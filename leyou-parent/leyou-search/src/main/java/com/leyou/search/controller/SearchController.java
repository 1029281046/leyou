package com.leyou.search.controller;

import com.leyou.common.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> queryByPage(@RequestBody SearchRequest searchRequest){
        SearchResult goodsList= this.searchService.queryByPage(searchRequest);
        if (goodsList.getItems().size()==0|| CollectionUtils.isEmpty(goodsList.getItems())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(goodsList);
    }

}
