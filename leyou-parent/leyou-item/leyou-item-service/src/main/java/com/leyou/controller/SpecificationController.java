package com.leyou.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroyo(@PathVariable("cid") Long cid){
      List<SpecGroup> categoryList=   specificationService.queryByCategoryId(cid);
      if (CollectionUtils.isEmpty(categoryList)){
          return ResponseEntity.notFound().build();
      }
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value = "gid",required = false) Long gid,
                                                          @RequestParam(value = "cid",required = false)Long cid,
                                                          @RequestParam(value = "generic",required = false)Boolean generic,
                                                          @RequestParam(value = "searching",required = false)Boolean searching){
        List<SpecParam> specParamList =  specificationService.queryBySpecParam(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(specParamList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParamList);
    }

    @PostMapping(value = "param")
    public ResponseEntity<Void> EditBrand(@RequestBody SpecParam specParam){
        this.specificationService.addSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "param")
    public ResponseEntity<Void> EditSpecParam(@RequestBody SpecParam specParam){
        this.specificationService.editSpecSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping(value = "param/{id}")
    public ResponseEntity<Void> DeleteSpecParam(@PathVariable("id") Long id){
        this.specificationService.deleteSpecParam(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
