package org.micro.tcc.demo.servicec;

import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
//    @RequestMapping(value = "/rpc" ,method = RequestMethod.GET)
    public String rpc(@RequestParam("value") String value) {
        return demoService.rpc(value);
    }
}
