package org.micro.tcc.demo.servicea;

;
import lombok.extern.slf4j.Slf4j;

import org.micro.tcc.demo.common.util.FixSizeCacheMap;
import org.micro.tcc.tc.annotation.TccTransaction;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private volatile FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();

    private final RestTemplate restTemplate;
    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper, ServiceBClient serviceBClient, ServiceCClient serviceCClient, RestTemplate restTemplate) {
        this.demoMapper = demoMapper;
        this.serviceBClient = serviceBClient;
        this.serviceCClient = serviceCClient;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",rollbackFor = Throwable.class)
    public String execute( String value, String exFlag) {
        long start=System.currentTimeMillis();
        // step1
        Demo demo = new Demo();
        demo.setContent(value);
        demo.setCreateTime(new Date());
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());

        // step2.
        String bResp = serviceBClient.rpc(value);
        //String bResp = restTemplate.getForObject("http://127.0.0.1:8882/rpc?value=" + value, String.class);

        // step3.
        String cResp = serviceCClient.rpc(value);
        //String cResp = restTemplate.getForObject("http://127.0.0.1:8883/rpc?value=" + value, String.class);
        //String cResp="cResp";
        long end=System.currentTimeMillis();
        log.error("execute time:{}",end-start);
        // 置异常标志，事务回滚
        if (Objects.nonNull(exFlag)) {
            throw new IllegalStateException("by exFlag");
        }

        return bResp + " > " + cResp + " > " + "success--a";
    }
    public void cancelMethod( String value, String exFlag){
        log.debug("****cancelMethod:value:{},exFlag:{}",value,exFlag);

        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.deleteByKId(id);
        fixSizeCacheMap.del(TransactionManager.getInstance().getTransactionGlobalId());
    }
    public void confirmMethod( String value, String exFlag){
        log.debug("*****confirmMethod:value:{},exFlag:{}",value,exFlag);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.updateByKId(id);
        log.debug("*****confirmMethod:id:{}",id);

    }
}
