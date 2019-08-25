package org.micro.tcc.demo.common.spring;

import feign.Contract;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 采用Feign自己的API注解
 */
@Configuration
@EnableFeignClients
@ConditionalOnMissingBean(Contract.class)
public class FeignApiConfiguration {
    @Bean
    public Contract feignApiContract() {
       return new Contract.Default();
    }
}
