package org.micro.tcc.demo.servicea;

;
import lombok.extern.slf4j.Slf4j;

import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.micro.tcc.common.annotation.TccTransaction;

import org.micro.tcc.common.core.FixSizeCacheMap;
import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.spring.ServiceBClient;
import org.micro.tcc.demo.common.spring.ServiceCClient;

import java.util.Date;
import java.util.Objects;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Service
@Slf4j
public class DemoServiceImpl implements DemoService {

    private final DemoMapper demoMapper;

    private final ServiceBClient serviceBClient;

    private final ServiceCClient serviceCClient;

    //定长并定时清理缓存map
    private FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();

    private final RestTemplate restTemplate;

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper, ServiceBClient serviceBClient, ServiceCClient serviceCClient, RestTemplate restTemplate) {
        this.demoMapper = demoMapper;
        this.serviceBClient = serviceBClient;
        this.serviceCClient = serviceCClient;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod")
    public String execute( String value, String exFlag) {
        // step1
        String bResp = serviceBClient.rpc(value);

        //String bResp = restTemplate.getForObject("http://127.0.0.1:8882/rpc?value=" + value, String.class);

        // step2.
        String cResp = serviceCClient.rpc(value);
        //String cResp = restTemplate.getForObject("http://127.0.0.1:8883/rpc?value=" + value, String.class);
        //String cResp="test";
        // step3.
        Demo demo = new Demo();
        demo.setGroupId("a-gid");
        demo.setContent(value);
        demo.setCreateTime(new Date());
        demo.setAppName("a-appname");
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());

        // 置异常标志，事务回滚
        if (Objects.nonNull(exFlag)) {
            throw new IllegalStateException("by exFlag");
        }

        return bResp + " > " + cResp + " > " + "ok-service-a";
    }
    public void cancelMethod( String value, String exFlag){
        log.info("****cancelMethod:value:{},exFlag:{}",value,exFlag);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.deleteByKId(id);
        fixSizeCacheMap.del(TransactionManager.getInstance().getTransactionGlobalId());
    }
    public void confirmMethod( String value, String exFlag){
        log.info("*****confirmMethod:value:{},exFlag:{}",value,exFlag);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        log.info("*****confirmMethod:id:{}",id);

    }
}
