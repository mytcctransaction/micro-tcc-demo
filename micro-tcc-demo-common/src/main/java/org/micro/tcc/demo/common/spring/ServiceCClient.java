package org.micro.tcc.demo.common.spring;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */

@FeignClient(name= "micro-tcc-demo-springcloud-service-c", fallback =ServiceCFallback.class)
public interface ServiceCClient {

    @GetMapping("/rpc")
    String rpc( @RequestParam("value") String name);

}
