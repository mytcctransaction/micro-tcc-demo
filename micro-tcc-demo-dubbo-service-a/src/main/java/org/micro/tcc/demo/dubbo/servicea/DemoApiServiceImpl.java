package org.micro.tcc.demo.dubbo.servicea;

import com.alibaba.dubbo.config.annotation.Reference;

import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.demo.common.util.FixSizeCacheMap;
import org.micro.tcc.tc.annotation.TccTransaction;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.dubbo.DemoServiceB;
import org.micro.tcc.demo.common.dubbo.DemoServiceC;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
            retries = -1,timeout = 3000,
            check = false)
    private DemoServiceB demoServiceB;

    @Reference(version = "${demo.service.version}",
            application = "${dubbo.application.c}",
            retries = -1,
            check = false,timeout = 3000,
            registry = "${dubbo.registry.address}")
    private DemoServiceC demoServiceC;

    @Autowired
    private DemoMapper demoMapper;

    //定长并定时清理缓存map
    private volatile FixSizeCacheMap fixSizeCacheMap=org.micro.tcc.demo.common.util.FixSizeCacheMap.get();

    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional(rollbackFor = {Throwable.class,Exception.class})
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",rollbackFor = Throwable.class)
    public String execute(String name, String exFlag) {
        long a=System.currentTimeMillis();
        String bResp = demoServiceB.rpc(name);
        String cResp = demoServiceC.rpc(name);
        Demo demo = new Demo();
        demo.setContent(name);
        demo.setCreateTime(new Date());
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        if(Objects.isNull(demo.getId())){
            log.error("gid:{},tid:{}",TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        }

        if (Objects.nonNull(exFlag)) {
            long b=System.currentTimeMillis();
            log.error("execute time:{}",b-a);
            throw new IllegalStateException("by exFlag");
        }
        long b=System.currentTimeMillis();
        log.error("execute time:{}",b-a);
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
        if(id==null){
            log.error("id is null:{},group id:{}",id,TransactionManager.getInstance().getTransactionGlobalId());
        }
        demoMapper.updateByKId(id);
        log.debug("*****confirmMethod:id:{}",id);

    }

}
