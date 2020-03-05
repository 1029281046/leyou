package com.leyou.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.service.AuthService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private  JwtProperties jwtProperties;
    @Autowired
    private AuthService authService;

    @PostMapping("accredit")
    public ResponseEntity<Void> accredt(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletResponse response, HttpServletRequest request){
        String token=   authService.accredit(username,password);
        if (StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
        return ResponseEntity.ok(null);
    }

    @GetMapping("verify")
    public  ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token,HttpServletRequest request,HttpServletResponse response){
        try {
            UserInfo user = JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
            if (user==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            token=JwtUtils.generateToken(user,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
           CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

            System.out.println("你还没有登录呢，哈哈哈哈哈！！！！！！");
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

