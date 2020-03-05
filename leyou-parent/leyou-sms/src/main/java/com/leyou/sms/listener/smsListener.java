package com.leyou.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class smsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties smsProperties;


 @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "leyou.sms.queue",durable = "true"),exchange = @Exchange(value = "leyou.sms.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),key = {"sms.verify.code"}))
    public void sendMsg(Map<String,String> msg) throws ClientException {
        if (msg==null || msg.size()<0){
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)){
            return;
        }
        this.smsUtils.sendSms(phone,code,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate());
    }

}
