package org.micro.tcc.demo.serviceb;


import lombok.extern.slf4j.Slf4j;
import org.micro.tcc.tc.component.TransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.micro.tcc.common.annotation.TccTransaction;
import org.micro.tcc.common.core.FixSizeCacheMap;
import org.micro.tcc.common.constant.Propagation;

import org.micro.tcc.demo.common.db.domain.Demo;
import org.micro.tcc.demo.common.spring.ServiceCClient;

import java.util.Date;

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
    private FixSizeCacheMap fixSizeCacheMap=FixSizeCacheMap.get();
    @Override
    @Transactional
    @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod",propagation = Propagation.SUPPORTS)
    public String rpc(String value) throws Exception {

        //serviceCClient.rpc(value);
        Demo demo = new Demo();
        demo.setGroupId("b-gid");
        demo.setContent(value);
        demo.setAppName("b-appname");
        demo.setCreateTime(new Date());
        demoMapper.save(demo);
        log.info("**********saved b********");
        fixSizeCacheMap.add(TransactionManager.getInstance().getTransactionGlobalId(),demo.getId());

        //if(1==1)
        //throw new Exception("dff");
        //int a=1/0;
        return "ok-service-b";
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
