package org.micro.tcc.demo.common.spring;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        //DTXUserControls.rollbackGroup(TracingContext.tracing().groupId());
        log.error("TCC:service c fallback");
        return "fallback";
    }
}
