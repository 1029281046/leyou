package com.leyou.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.mapper.UserMapper;
import com.leyou.pojo.User;
import com.leyou.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import sun.nio.cs.US_ASCII;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


    private static final String KEY_PREFIX="user:verify";


    public Boolean checkUser(String data, Integer type) {
        User user=new User();
        if (type==1){
            user.setUsername(data);
        }else if (type==2){
            user.setPhone(data);
        }else {
            return null;
        }
        return this.userMapper.selectCount(user)==0;

    }

    public void sendVefyCode(String phone) {
        String code = NumberUtils.generateCode(6);
        if (StringUtils.isBlank(phone)){
            return;
        }
        Map<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        this.amqpTemplate.convertAndSend("leyou.sms.exchange","sms.verify.code",msg);
        this.redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5,TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        String redisCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(redisCode,code)){
            return;
        }
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insert(user);
    }

    public User query(String username, String password) {
        User record=new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if (user==null){
            return null;
        }
        password=CodecUtils.md5Hex(password,user.getSalt());
        if (StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;

    }
}