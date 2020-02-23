package com.leyou.controller;

import com.leyou.common.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.service.BrandService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;
    @GetMapping("page")//page?key=&page=1&rows=5&sortBy=id&desc=false
    public ResponseEntity<PageResult<Brand>> queryByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc", defaultValue = "false")Boolean desc
            ){
         PageResult<Brand> list=  brandService.queryByPage(key,page,rows,sortBy,desc);
        if (list==null|| CollectionUtils.isEmpty(list.getItems())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.addBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> EditBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.editBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("delete/bid/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid")Long bid){
        this.brandService.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryByCid(@PathVariable("cid")Long cid){
        List<Brand> brandList=   this.brandService.queryByCid(cid);
        if (brandList==null||CollectionUtils.isEmpty(brandList)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(brandList);
    }

}
