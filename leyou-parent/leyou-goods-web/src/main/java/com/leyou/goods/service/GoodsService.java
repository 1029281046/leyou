package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    public Map<String,Object> loadDate(Long spuId){
        Map<String,Object> map=new HashMap<>();
        //1.根据SpuId查询Spu
        Spu spu = this.goodsClient.querySpuById(spuId);
        //2.根据SpuID查询SpuDetail
        SpuDetail spuDetail = this.goodsClient.queryBySpuId(spuId);
        //3.根据SpuID查询Skus
        List<Sku> skus = this.goodsClient.querBySpu(spuId);
        //4.根据Spu查询品牌
        Brand brand = this.brandClient.queryNameById(spu.getBrandId());
        //5.根据Spu查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNameByids(cids);
        List<Map<String,Object>> categories=new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> maps=new HashMap<>();
            maps.put("id",cids.get(i));
            maps.put("name",names.get(i));
            categories.add(maps);
        }
        //6.根据Spu查询规格参数组
        List<SpecGroup> groups = this.specificationClient.queryGroupWithPramId(spu.getCid3());
        //7.根据Spu查询规格参数
        List<SpecParam> specParamList = this.specificationClient.querySpecParam(null, spu.getCid3(), false, null);
        Map<Long,Object> paramMap=new HashMap<>();
        specParamList.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });
        map.put("spu",spu);
        map.put("spuDetail",spuDetail);
        map.put("skus",skus);
        map.put("brand",brand);
        map.put("categories",categories);
        map.put("groups",groups);
        map.put("paramMap",paramMap);
        return map;
    }
}
