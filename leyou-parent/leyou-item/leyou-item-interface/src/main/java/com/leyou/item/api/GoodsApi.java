package com.leyou.item.api;

import com.leyou.common.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface GoodsApi {

    @RequestMapping("spu/page")
    public PageResult<SpuBo> queryGoods(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );


    @GetMapping("spu/detail/{spuId}")
    public SpuDetail queryBySpuId(@PathVariable("spuId")Long spuId);

    @GetMapping("sku/list")
    public List<Sku> querBySpu(@RequestParam("id")Long spuId);

    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id") Long id);
    @GetMapping("sku/{skuId}")
    public Sku querBySkuId(@PathVariable("skuId")Long skuId);
}
