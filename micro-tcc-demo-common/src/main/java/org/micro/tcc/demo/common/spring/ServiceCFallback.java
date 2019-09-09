package org.micro.tcc.demo.common.spring;


import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.common.core.Transaction;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Component
@Slf4j
public class ServiceCFallback implements ServiceCClient {

    @Override
    public String rpc(String name) {
        try {
            //手动回滚事务
            TransactionManager.getInstance().rollbackForClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.error("*******ServiceCFallback method called*******");
        return "fallback";
    }
}
