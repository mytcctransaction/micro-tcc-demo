package org.micro.tcc.demo.serviceb;


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
import org.micro.tcc.demo.common.spring.ServiceCClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *@author jeff.liu
 *   描述
 *@date 2019/7/31
 */
@Service
@Slf4j
public class DemoServiceImpl implements DemoService {

    private final DemoMapper demoMapper;
    @Autowired
    private  ServiceCClient serviceCClient;
    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }
    //定长并定时清理缓存map
    private volatile FixSizeCacheMap fixSizeCacheMap= FixSizeCacheMap.get();
    private volatile Map<String,Long> myMap=new HashMap<String, Long>();
    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",propagation = Propagation.SUPPORTS,rollbackFor = Throwable.class)
    public String rpc(String value) throws Exception {

        //String c=serviceCClient.rpc(value);
        Demo demo = new Demo();

        demo.setContent(value);
        demo.setAppName(appName);
        demo.setGroupId(TransactionManager.getInstance().getTransactionGlobalId());
        demo.setCreateTime(new Date());
        demoMapper.save(demo);
        log.debug("**********saved b********");
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());
        //int a=1/0;
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
        int a=1/0;
        Long id=(Long)fixSizeCacheMap.peek(TransactionManager.getInstance().getTransactionGlobalId());
        demoMapper.updateByKId(id);
        log.debug("*****confirmMethod:id:{}",id);
    }
}
