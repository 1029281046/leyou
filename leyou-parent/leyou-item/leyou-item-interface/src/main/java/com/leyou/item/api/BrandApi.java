package com.leyou.item.api;

import com.leyou.common.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {
    /**
     * 根据bid查询品牌名称
     */
    @GetMapping("{id}")
    public Brand queryNameById(@PathVariable("id")Long id);
}
