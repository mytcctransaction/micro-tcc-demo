package org.micro.tcc.demo.dubbo.servicec;


import org.micro.tcc.tc.component.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
//@EnableMicroTccTransaction
public class DubboServiceCApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServiceCApplication.class, args);

    }

}

