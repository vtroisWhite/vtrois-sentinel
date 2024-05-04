package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigParam;

/**
 * 获取各个中间件规则存储path
 */
public interface DynamicClientConfigService {

    /**
     * 初始化配置
     * @param param
     * @param clientConfigEntity
     */
    void initClientConfigEntity(ClientConfigParam param, ClientConfigEntity clientConfigEntity);

    /**
     * 构建nacosConfig或者zkConfig
     * @return
     */
    Object buildConfig(ClientConfigParam param);

    /**
     * 构建nacosConfig或者zkConfig的连接配置
     * @return
     */
    Object buildConnectConfig();

    /**
     * 构建RulePath
     * @param param
     * @return
     */
    ClientConfigEntity.RulePath buildRulePath(ClientConfigParam param);

}
