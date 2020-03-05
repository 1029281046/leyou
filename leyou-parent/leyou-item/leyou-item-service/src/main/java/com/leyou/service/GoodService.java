package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.mapper.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class GoodService {

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> queryGoodsList(String key, Boolean saleable, Integer page, Integer rows) {
        //创建查询条件
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //检验搜索框是否为空
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //检查上下架
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //执行分页
        PageHelper.startPage(page,rows);
        //查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        //创建分页对象
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        List<SpuBo> spuBoList=new ArrayList<>();
        //将Spu转换为SpuBo,并设置Bname和Cname
        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
          //赋值属性
            BeanUtils.copyProperties(spu,spuBo);
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
           List<String> names= this.categoryService.queryCategoryByNames(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
            spuBo.setCname(StringUtils.join(names,"/"));
            spuBoList.add(spuBo);
        });
        return new PageResult<>(pageInfo.getTotal(),spuBoList);
    }


    public void addGoods(SpuBo spuBo) {
        //先增加Spu表中的内容
        spuBo.setId(null);
        spuBo.setValid(true);
        spuBo.setSaleable(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);
        //在增加Spu_Detao表中的内容
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);
        //在增加Sku和Sku_Stock表中的内容
        saveSkuAndStock(spuBo);
        sendMsg("insert",spuBo.getId());

    }

    private void sendMsg(String type,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail queryBySpuId(Long spuId) {
        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(spuId);
        return spuDetail;
    }

    public List<Sku> queryBySku(Long spuId) {
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = this.skuMapper.select(sku);
        skuList.forEach(sku1 -> {
            Stock stock=new Stock();
            stock.setSkuId(sku1.getId());
            Stock stock1 = this.stockMapper.selectByPrimaryKey(stock);
            sku1.setStock(stock1.getStock());
        });
        return skuList;
    }

    public void editGoods(SpuBo spuBo) {
        //删除Stock
        Sku record=new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skuList = this.skuMapper.select(record);
        skuList.forEach(sku -> {
            //删除Stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        //删除Sku
        Sku sku=new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);

        //更新Sku和Stock
        saveSkuAndStock(spuBo);
        //新增Spu
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(null);
        spuBo.setValid(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        //新增SpuDetail
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
        sendMsg("update",spuBo.getId());

    }

    public Spu querySpuById(Long id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        return spu;
    }

    public Sku queryBySkuId(Long skuId) {
        return  this.skuMapper.selectByPrimaryKey(skuId);
    }
}
