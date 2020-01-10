package com.kking.dao.service;

import com.aliyuncs.exceptions.ClientException;

/**
 * @author zhaoliancan
 * @version 1.0.0
 * @ClassName SendSmsService.java
 * @Description 短信发送服务
 * @createTime 2019年11月13日 17:44:00
 */
public interface SendSmsService {

    /**
     * 发送短信
     * @return
     */
    String  sendSms() throws ClientException;
}
