package org.micro.tcc.demo.servicea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@RestController
public class DemoController {

    private final DemoService demoService;

    @Autowired
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @RequestMapping("/micro_tcc")
    public String execute(@RequestParam("value") String value, @RequestParam(value = "ex", required = false) String exFlag) {
        return demoService.execute(value, exFlag);
    }


}
