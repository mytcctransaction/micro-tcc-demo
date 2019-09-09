package org.micro.tcc.demo.dubbo.servicea;
;

import org.micro.tcc.tc.annotation.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


/**
 *@author jeff.liu
 *   描述
 *@date 2019/8/6
 */
@Configuration
@SpringBootApplication
@EnableMicroTccTransaction
public class DubboServiceAApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServiceAApplication.class, args);
    }

}

