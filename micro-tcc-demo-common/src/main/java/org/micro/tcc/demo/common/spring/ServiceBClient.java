package org.micro.tcc.demo.common.spring;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */

@FeignClient(name= "micro-tcc-demo-springcloud-service-b",fallback = ServiceBFallback.class)
public interface ServiceBClient {

    @GetMapping("/rpc")
    String rpc( @RequestParam("value") String name);
//    @RequestLine("GET /rpc")
//    String rpc( @PathVariable("value") String name);


}
