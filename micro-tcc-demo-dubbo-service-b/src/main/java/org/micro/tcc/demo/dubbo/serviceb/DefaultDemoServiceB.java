package org.micro.tcc.demo.dubbo.serviceb;

import com.alibaba.dubbo.config.annotation.Service;

import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.common.constant.Propagation;

import org.micro.tcc.demo.common.util.FixSizeCacheMap;
import org.micro.tcc.tc.annotation.TccTransaction;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.dubbo.DemoServiceB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/8/6
 */
@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}"
)
@Slf4j
public class DefaultDemoServiceB implements DemoServiceB {

    @Autowired
    private DemoMapper demoMapper;

    //定长并定时清理缓存map
    private  volatile FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();

    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",propagation = Propagation.SUPPORTS,rollbackFor = Throwable.class)
    public String rpc(String name) {
        long a=System.currentTimeMillis();
        Demo demo = new Demo();
        demo.setContent(name);
        demo.setGroupId(name);
        demo.setCreateTime(new Date());
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        long b=System.currentTimeMillis();
        log.error("execute time:{}",b-a);
        return "success--b";
    }

    public void cancelMethod( String value){
        log.debug("****cancelMethod:value:{},exFlag:{}",value);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.deleteByKId(id);
        fixSizeCacheMap.del(TransactionManager.getInstance().getTransactionGlobalId());
    }
    public void confirmMethod( String value){
        log.debug("*****confirmMethod:value:{},exFlag:{}",value);
        //int a=1/0;
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.updateByKId(id);
        log.debug("*****confirmMethod:id:{}",id);
    }

}
