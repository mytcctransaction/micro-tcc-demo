package org.micro.tcc.demo.servicec;

import lombok.extern.slf4j.Slf4j;

import org.micro.tcc.demo.common.util.FixSizeCacheMap;
import org.micro.tcc.tc.annotation.TccTransaction;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.micro.tcc.common.constant.Propagation;
import org.micro.tcc.demo.common.db.domain.Demo;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Service
@Slf4j
public class DemoServiceImpl  {

    private final DemoMapper demoMapper;

    private ConcurrentHashMap<String, Set<Long>> ids = new ConcurrentHashMap<>();

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }

    //定长并定时清理缓存map
    private volatile FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();
    private volatile Map<String,Long> myMap=new HashMap<String, Long>();
    @Value("${spring.application.name}")
    private String appName;

    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",propagation = Propagation.SUPPORTS,rollbackFor = Throwable.class)
    public String rpc(String value) {
        Demo demo = new Demo();
        demo.setContent(value);
        demo.setCreateTime(new Date());
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.save(demo);
        log.debug("**********saved c");
        //int a=1/0;
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        return "success--c";
    }

    public void cancelMethod( String value){
        log.debug("****cancelMethod:value:{},exFlag:{}",value);
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.deleteByKId(id);
        fixSizeCacheMap.del(TransactionManager.getInstance().getTransactionGlobalId());
    }
    public void confirmMethod( String value){
        log.debug("***confirmMethod:value:{},exFlag:{}",value);
        int a=1/0;
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.updateByKId(id);
        log.debug("*****confirmMethod:id:{}",id);
    }
}
