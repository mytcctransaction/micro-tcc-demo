package org.micro.tcc.demo.servicec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@RestController
public class DemoController {

    @Autowired
    private DemoServiceImpl demoService;

    @GetMapping("/rpc")
    public String rpc(@RequestParam("value") String value) {
        return demoService.rpc(value);
    }
}
