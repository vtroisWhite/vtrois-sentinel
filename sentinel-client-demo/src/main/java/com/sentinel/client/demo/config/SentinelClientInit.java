package com.sentinel.client.demo.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.init.InitExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Description 初始化Sentinel配置
 */
@Slf4j
@Configuration
public class SentinelClientInit {

    @Autowired
    private SentinelUtil sentinelUtil;

    /**
     * 初始化aop
     *
     * @return
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    /**
     * 初始化sentinel配置
     * 需要在{@link SentinelDatasourceProperty}中配置数据源
     */
    @PostConstruct
    public void init() {
        SentinelClientConfigEntity entity = sentinelUtil.queryClientConfigEntity();
        if (entity == null) {
            return;
        }
        //初始化
        InitExecutor.doInit();

        //============加载配置项============
        //单机流控
        sentinelUtil.loadRule_flow(entity);
        //熔断
        sentinelUtil.loadRule_degrade(entity);
        //集群流控
        sentinelUtil.loadRule_flow_cluster(entity);
        //系统规则
        sentinelUtil.loadRule_system(entity);
        //授权规则
        sentinelUtil.loadRule_authority(entity);

        log.info("---------- 容灾组件Sentinel Init Success:{} ----------", sentinelUtil.getCurrentMachineId());
    }
}