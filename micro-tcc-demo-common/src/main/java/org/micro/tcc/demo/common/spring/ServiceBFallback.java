package org.micro.tcc.demo.common.spring;

import lombok.extern.slf4j.Slf4j;

import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.stereotype.Component;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Component
@Slf4j
public class ServiceBFallback implements ServiceBClient {

    @Override
    public String rpc(String name) {
        log.error("*******fallback method called*******");
        try {
            TransactionManager.getInstance().rollbackForClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fallback";
    }
}
