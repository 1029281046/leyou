package com.leyou.controller;

import com.leyou.common.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class GoodsController {

    @Autowired
    private GoodService goodService;
    @RequestMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> queryGoods(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
          PageResult<SpuBo> pageResult=  this.goodService.queryGoodsList(key,saleable,page,rows);
        if (pageResult==null || CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    @PostMapping("goods")
    public ResponseEntity<Void> addGoods(@RequestBody SpuBo spuBo){
        this.goodService.addGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> queryBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail spuDetail=  this.goodService.queryBySpuId(spuId);
        if (spuDetail==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querBySpu(@RequestParam("id")Long spuId){
      List<Sku> skuList=  this.goodService.queryBySku(spuId);
        if (skuList==null||CollectionUtils.isEmpty(skuList)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(skuList);
    }

    @PutMapping("goods")
    private ResponseEntity<Void> EditGoods(@RequestBody SpuBo spuBo){
        this.goodService.editGoods(spuBo);
        return ResponseEntity.noContent().build();
    }
    /***
     *
     */

    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
         Spu spu=   this.goodService.querySpuById(id);
        if (spu==null){
            return ResponseEntity.badRequest().build();
        }
         return ResponseEntity.ok(spu);
    }
    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querBySkuId(@PathVariable("skuId")Long skuId){
        Sku sku=  this.goodService.queryBySkuId(skuId);
        if (sku==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(sku);
    }

}
