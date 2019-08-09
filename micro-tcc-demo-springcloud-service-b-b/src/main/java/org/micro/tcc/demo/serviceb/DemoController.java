package org.micro.tcc.demo.serviceb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@RestController
@Slf4j
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/rpc")
    public String rpc(@RequestParam("value") String value, HttpServletRequest servletRequest) throws Exception {

        return demoService.rpc(value);
    }
}
