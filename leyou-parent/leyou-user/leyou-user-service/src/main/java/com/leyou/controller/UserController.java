package com.leyou.controller;

import com.leyou.pojo.User;
import com.leyou.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean bool=this.userService.checkUser(data,type);
        if (bool==null){
         return   ResponseEntity.badRequest().build();
        }return ResponseEntity.ok(bool);
    }

    @PostMapping("code")
    public ResponseEntity<Void> send(@RequestParam("phone") String phone){
        this.userService.sendVefyCode(phone);
        return   ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 注册用户得接口
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        this.userService.register(user,code);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 查询用户
     */
    @GetMapping("query")
    public ResponseEntity<User> query(@RequestParam("username") String username,@RequestParam("password")String password){
      User user=  this.userService.query(username,password);
        if (user==null){
            return  ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }
}