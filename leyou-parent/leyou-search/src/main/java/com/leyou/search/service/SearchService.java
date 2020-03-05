package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.catalina.mapper.Mapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    private static  final ObjectMapper MAPPER=new ObjectMapper();

    public Goods goodsBuilds(Spu spu) throws IOException {
        //创建要返回的Goods对象 第一步必须要创建Goods对象这个后面是为了给Goods对象里面设置属性及其相应的值
        Goods goods=new Goods();
        //查询品牌      根据Spu的ID去查询出来对应的品牌
        Brand brand = this.brandClient.queryNameById(spu.getBrandId());
        //查询分类         根据Spu的ID去查询出来对应的分类名称
        List<String> categoryList = this.categoryClient.queryNameByids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询SPu下的所有 Sku     根据SpuID去查询出来对应的下属的Sku的集合
        List<Sku> skus = this.goodsClient.querBySpu(spu.getId());
        //因为一个Spu下面可能有多个Sku,所以多个Sku,就可能有多个价格，定义价格price
        List<Long> prices=new ArrayList<>();
        //定义一个SkuMpa的集合，目的是把sku转成对应的固定的四个属性，之后在同一放到一起
        List<Map<String,Object>> skuMapList=new ArrayList<>();
        //遍历每个skus
        skus.forEach(sku -> {
            //把每个sku的价格放进去
            prices.add(sku.getPrice());
            //开始把每个Sku转换成Map
            Map<String,Object> skuMap=new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages())?StringUtils.split(sku.getImages(),",")[0]:"");
            skuMapList.add(skuMap);
        });
        //查询出所有的规格参数
        List<SpecParam> specParamList = this.specificationClient.querySpecParam(null, spu.getCid3(), null, true);
        //查询出所有的SpuDetail，获取规格参数值
        SpuDetail spuDetail = this.goodsClient.queryBySpuId(spu.getId());
        //获取通用的规格参数反序列化为Map对象  将获取的通用规格参数，反序为Map对象
        Map<Long,Object> genericScap=MAPPER.readValue(spuDetail.getGenericSpec(),new TypeReference<Map<Long,Object>>(){});
       //获取特殊规格参数反序列化为Map对象
        Map<Long,Object> specialSpecMap=MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<Long,List<Object>>>(){});
        //定义map接收{规格参数名，规格参数值}
        Map<String,Object> paramap=new HashMap<>();
        specParamList.forEach(specParam -> {
            //检验是否是通用的规格参数
            if (specParam.getGeneric()){
                String value = genericScap.get(specParam.getId()).toString();
                //判断是否是数值类型
                if (specParam.getNumeric()){
                    //如果是数值类型，判断落在那个区间
                    value=chooseSegment(value,specParam);
                }
                paramap.put(specParam.getName(),value);
            }else{
                paramap.put(specParam.getName(),specialSpecMap.get(specParam.getId()));
            }
        });
        //设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle()+brand.getName()+StringUtils.join(categoryList," "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramap);
        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public SearchResult queryByPage(SearchRequest searchRequest) {
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)){
            return null;
        }
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        // 1、对key进行全文检索查询
      // QueryBuilder baseQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder baseQuery=buildBoolQueryBuid(searchRequest);
        queryBuilder.withQuery(baseQuery);
        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        // 3、分页准备分页参数
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));
        //添加分类和品牌的聚合
        String categoryAggName="categories";
        String brandAggName="brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 4、查询，获取结果
        AggregatedPage<Goods> goods = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        //获取聚合的结果集并解析
        List<Brand> brands= gerBrandAggResult(goods.getAggregation(brandAggName));
        List<Map<String,Object>> categories= getCategoryAggResult(goods.getAggregation(categoryAggName));
        List<Map<String,Object>> specs=null;
        //是否是一个分类，只有一个分类时才做规格参数的聚合
        if (!CollectionUtils.isEmpty(categories) && categories.size()==1){
            //规格参数的聚合
            specs  = getParamAggResult((Long)categories.get(0).get("id"),baseQuery);
        }

        // 5.封装结果并返回
        return new SearchResult(goods.getTotalElements(),goods.getTotalPages(),goods.getContent(),categories,brands,specs);
    }

    private BoolQueryBuilder buildBoolQueryBuid(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌",key)){
                key="brandId";
            }else  if (StringUtils.equals("分类",key)){
                key="cid3";
            }else {
                key="specs."+key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     * @param id
     * @param baseQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder baseQuery) {
        //构建查询对象
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //保留关键字查询
        queryBuilder.withQuery(baseQuery);
        List<Map<String, Object>>specs=new ArrayList<>();
        //查询要执行聚合的规格参数
        List<SpecParam> specParamList = this.specificationClient.querySpecParam(null, id, null, true);
        //将每个规格参数放入聚合从查询条件中
        specParamList.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        //排除字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //解析聚合查询
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        //遍历聚合查询出来的条件开始进行封装
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String,Object> map=new HashMap<>();
            map.put("k",entry.getKey());
            StringTerms options = (StringTerms) entry.getValue();
            List<String> ops=new ArrayList<>();
            options.getBuckets().forEach(bucket -> {
                ops.add(bucket.getKeyAsString());
            });
            map.put("options",ops);
            specs.add(map);
        }


        return specs;
    }

    /**
     * 解析品牌的聚合结果集合
     * @param aggregation
     * @return
     */
    private List<Brand> gerBrandAggResult(Aggregation aggregation) {
        LongTerms terms= (LongTerms) aggregation;
        List<Brand> brands=new ArrayList<>();
        terms.getBuckets().forEach(bucket -> {
            Brand brand = this.brandClient.queryNameById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;
    }

    /**
     * 解析分类的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms= (LongTerms) aggregation;
        List<Map<String,Object>> maps=new ArrayList<>();
        terms.getBuckets().forEach(bucket -> {
            Map<String,Object> map=new HashMap<>();
            long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNameByids(Arrays.asList(id));
            map.put("id",id);
            for (String name : names) {
                map.put("name",name);
            }
            maps.add(map);
        });
        return maps;
    }

    public void save(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.goodsBuilds(spu);
        this.goodsRepository.save(goods);
    }
}

