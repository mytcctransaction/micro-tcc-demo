package org.micro.tcc.demo.dubbo.servicea;

import com.alibaba.dubbo.config.annotation.Reference;

import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.common.annotation.TccTransaction;
import org.micro.tcc.common.core.FixSizeCacheMap;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.dubbo.DemoServiceB;
import org.micro.tcc.demo.common.dubbo.DemoServiceC;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
*@author jeff.liu
*   描述
*@date 2019/8/6
*/
@Service
@Slf4j
public class DemoApiServiceImpl implements DemoApiService {

    @Reference(version = "${demo.service.version}",
            application = "${dubbo.application.b}",
            registry = "${dubbo.registry.address}",
            retries = -1,
            check = false)
    private DemoServiceB demoServiceB;

    @Reference(version = "${demo.service.version}",
            application = "${dubbo.application.c}",
            retries = -1,
            check = false,
            registry = "${dubbo.registry.address}")
    private DemoServiceC demoServiceC;

    @Autowired
    private DemoMapper demoMapper;

    //定长并定时清理缓存map
    private FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();
    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod")
    public String execute(String name, String exFlag) {
        String bResp = demoServiceB.rpc(name);
        String cResp = demoServiceC.rpc(name);
        Demo demo = new Demo();
        demo.setContent(name);
        demo.setCreateTime(new Date());
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        if (Objects.nonNull(exFlag)) {
            throw new IllegalStateException("by exFlag");
        }
        return bResp + " > " + cResp + " > " + "success--a";
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
