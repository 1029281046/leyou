package com.leyou.goods.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);
    public void createHtml(Long spuId) {

        PrintWriter printWriter=null;
        try {
            //初始化上下文对象
            Context context = new Context();
            context.setVariables(this.goodsService.loadDate(spuId));
            File file = new File("D:\\nginx\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            this.templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter!=null){
                printWriter.close();;
            }
        }

    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\nginx\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
