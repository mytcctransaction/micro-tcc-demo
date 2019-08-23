package org.micro.tcc.demo.servicea;


import org.micro.tcc.tc.component.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Configuration
@EnableDiscoveryClient
@SpringBootApplication()
@EnableFeignClients
@EnableMicroTccTransaction
public class SpringServiceAApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringServiceAApplication.class, args);

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
