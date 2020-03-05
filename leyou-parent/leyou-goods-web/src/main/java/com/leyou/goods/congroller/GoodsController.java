package com.leyou.goods.congroller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")
    public String redirect(@PathVariable("id")Long id, Model model){
        Map<String, Object> map = this.goodsService.loadDate(id);
        model.addAllAttributes(map);
        goodsHtmlService.createHtml(id);
        return "item.html";
    }
}
