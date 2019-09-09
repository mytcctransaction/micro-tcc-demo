package org.micro.tcc.demo.servicea;



import org.micro.tcc.tc.annotation.EnableMicroTccTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

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
    public RestTemplate restTemplate(ClientHttpRequestInterceptor clientHttpRequestInterceptor){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(clientHttpRequestInterceptor));
        return restTemplate;
    }
}
