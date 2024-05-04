package com.alibaba.csp.sentinel.dashboard.rule;

/**
 * 获取路径
 */
public interface RulePath {

    /**
     * 限流
     */
    String getFlowRule();

    /**
     * 熔断降级
     */
    String getDegradeRule();

    /**
     * 热点规则限流
     */
    String getParamFlowRule();

    /**
     * 系统规则
     */
    String getSystemRule();

    /**
     * 授权规则
     */
    String getAuthorityRule();

    /**
     * 网关限流
     */
    String getGatewayFlowRule();

    /**
     * 网关api管理
     */
    String getGatewayApiFlowRule();

    /**
     * 集群配置
     */
    String getClusterModeRule();

    /**
     * 集群配置
     */
    String getClusterClientConfigRule();

    /**
     * 集群配置
     */
    String getClusterServerFlowRule();

    /**
     * 集群配置
     */
    String getClusterServerTransportRule();

    /**
     * 集群配置
     */
    String getClusterServerNamespaceRule();


}
