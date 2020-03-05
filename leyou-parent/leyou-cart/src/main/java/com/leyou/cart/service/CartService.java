package com.leyou.cart.service;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interpetor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static  final String PRY_KEY="user:cart:";


    /**
     * 添加购物车方法
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        Integer num = cart.getNum();
        String key = cart.getSkuId().toString();
        //查询购物车中得记录
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(PRY_KEY + userInfo.getId());
        if(ops.hasKey(key)){
            //如果商品已存在在购物车中
            String cartJson = ops.hasKey(key).toString();
             cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum()+num);
        }else {
            //商品部在购物车中就新增
            cart.setUserId(userInfo.getId());
            Sku sku = this.goodsClient.querBySkuId(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),"")[0]);
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setTitle(sku.getTitle());
        }
        ops.put(key,JsonUtils.serialize(cart));
    }

    /**
     * 查询购物车方法
     * @return
     */
    public List<Cart> query() {
        UserInfo user = LoginInterceptor.getLoginUser();
       if (!redisTemplate.hasKey(PRY_KEY+user.getId())){
           return null;
       }
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(PRY_KEY + user.getId());
        List<Object> objectList = ops.values();
        return  objectList.stream().map(cart->JsonUtils.parse(cart.toString(),Cart.class)).collect(Collectors.toList());
    }

    public void update(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        if (!redisTemplate.hasKey(PRY_KEY+userInfo.getId())){
            return ;
        }
        //获取购物车数据
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(PRY_KEY + userInfo.getId());
        Integer num = cart.getNum();
        String cartJson = ops.get(cart.getSkuId().toString()).toString();
        cart=JsonUtils.parse(cartJson,Cart.class);
        cart.setNum(num);
        //重新放入缓从
        ops.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }
}
