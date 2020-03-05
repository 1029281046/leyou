package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {



    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupWithPramId(@PathVariable("cid") Long cid);

    @GetMapping("params")
    public List<SpecParam> querySpecParam(@RequestParam(value = "gid",required = false) Long gid,
                                                          @RequestParam(value = "cid",required = false)Long cid,
                                                          @RequestParam(value = "generic",required = false)Boolean generic,
                                                          @RequestParam(value = "searching",required = false)Boolean searching);


}
