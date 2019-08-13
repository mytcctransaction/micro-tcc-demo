package org.micro.tcc.demo.dubbo.servicec;

import com.alibaba.dubbo.config.annotation.Service;

import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.common.annotation.TccTransaction;
import org.micro.tcc.common.constant.Propagation;
import org.micro.tcc.common.core.FixSizeCacheMap;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.dubbo.DemoServiceC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

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
public class DefaultDemoServiceC implements DemoServiceC {

    @Autowired
    private DemoMapper demoMapper;
    //定长并定时清理缓存map
    private FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();

    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",propagation = Propagation.SUPPORTS)
    public String rpc(String name) {
        Demo demo = new Demo();
        demo.setContent(name);
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demo.setCreateTime(new Date());
        demoMapper.save(demo);
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());

        return "success--c";
    }

    public void cancelMethod( String value){
        log.info("****cancelMethod:value:{},exFlag:{}",value);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.deleteByKId(id);
        fixSizeCacheMap.del(TransactionManager.getInstance().getTransactionGlobalId());
    }
    public void confirmMethod( String value){
        log.info("*****confirmMethod:value:{},exFlag:{}",value);
        //int a=1/0;
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        log.info("*****confirmMethod:id:{}",id);
    }
}
