package org.micro.tcc.demo.common.spring;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;

/**
 * 采用Feign自己的API注解
 */
@Configuration
@EnableFeignClients
//@ConditionalOnMissingBean(Contract.class)
public class FeignApiConfiguration {
//    @Bean
//    public Contract feignContract() {
//       return new Contract.Default();
//    }
}
