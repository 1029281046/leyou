package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据ids查询分类名称
     */
    @GetMapping
    public List<String> queryNameByids(@RequestParam("ids")List<Long> ids);

}

