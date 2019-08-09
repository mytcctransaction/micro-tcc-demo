package org.micro.tcc.demo.dubbo.serviceb;


import org.micro.tcc.tc.component.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@EnableMicroTccTransaction
public class DubboServiceBApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboServiceBApplication.class, args);

    }

}

