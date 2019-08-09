package org.micro.tcc.demo.servicec;


import org.micro.tcc.tc.component.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMicroTccTransaction
public class SpringServiceCApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServiceCApplication.class, args);
    }
}
