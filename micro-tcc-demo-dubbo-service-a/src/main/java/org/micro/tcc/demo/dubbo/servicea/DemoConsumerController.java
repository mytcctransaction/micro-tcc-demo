package org.micro.tcc.demo.dubbo.servicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
*@author jeff.liu
*   描述
*@date 2019/8/6
*/
@RestController
public class DemoConsumerController {


    @Autowired
    private DemoApiService demoApiService;


    @RequestMapping("/micro_tcc")
    public String sayHello(@RequestParam("value") String value,
                           @RequestParam(value = "ex", required = false) String exFlag) {
        return demoApiService.execute(value, exFlag);
    }

}
