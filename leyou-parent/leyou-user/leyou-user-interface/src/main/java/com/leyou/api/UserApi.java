package com.leyou.api;

import com.leyou.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    /**
     * 查询用户
     */
    @GetMapping("query")
    public User query(@RequestParam("username") String username, @RequestParam("password")String password);
}
