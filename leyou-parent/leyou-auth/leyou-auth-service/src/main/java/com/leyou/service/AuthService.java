package com.leyou.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.client.UserClient;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.nio.cs.US_ASCII;

@Service
public class AuthService {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserClient userClient;

    public String accredit(String username, String password) {
        User user = userClient.query(username, password);
        if (user==null){
            return null;
        }
        UserInfo userInfo=new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        try {
           return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
