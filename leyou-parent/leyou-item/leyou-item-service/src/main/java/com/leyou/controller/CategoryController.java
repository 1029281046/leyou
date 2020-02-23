package com.leyou.controller;

import com.leyou.item.pojo.Category;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> CategoryqueryByPid(@RequestParam(value = "pid",defaultValue = "0")Long pid){
        if (pid==null || pid<0){
            return ResponseEntity.badRequest().build();
        }
        List<Category> categoryList = categoryService.CategoryqueryByPid(pid);
        if (CollectionUtils.isEmpty(categoryList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryList);
    }

    @PostMapping(value = "add" )
    public ResponseEntity<Integer> add(@RequestBody Category category ){
        Integer categoryadd = categoryService.Categoryadd(category);
        return ResponseEntity.ok(categoryadd);
    }
    @GetMapping(value = "edit")
    public ResponseEntity<Integer> edit(@RequestParam(value = "id")Long id,@RequestParam(value = "name")String name){
     Integer editInt=   categoryService.editCategory(id,name);
        return ResponseEntity.ok(editInt);
    }

    @GetMapping(value = "delete")
    public ResponseEntity<Integer> delete(@RequestParam(value = "id")Long id){
       Integer deleteInt=  categoryService.delete(id);
        return ResponseEntity.ok(deleteInt);
    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategory(@PathVariable("bid")Long bid){
        List<Category> categoryList=  this.categoryService.queryCategoryBid(bid);
        if (categoryList==null || categoryList.size()<0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(categoryList);
    }
}

