package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.rule.RulePath;

/**
 * nacos配置路径
 */
public class ZookeeperRulePath implements RulePath {
    @Override
    public String getFlowRule() {
        return "/flow";
    }

    @Override
    public String getDegradeRule() {
        return "/degrade";
    }

    @Override
    public String getParamFlowRule() {
        return "/paramFlow";
    }

    @Override
    public String getSystemRule() {
        return "/systemRules";
    }

    @Override
    public String getAuthorityRule() {
        return "/authorityRules";
    }

    @Override
    public String getGatewayFlowRule() {
        return "/gatewayFlowRules";
    }

    @Override
    public String getGatewayApiFlowRule() {
        return "/gatewayApiGroupRules";
    }

    @Override
    public String getClusterModeRule() {
        return "/clusterModeRules";
    }

    @Override
    public String getClusterClientConfigRule() {
        return "/clusterClientConfigRules";
    }

    @Override
    public String getClusterServerFlowRule() {
        return "/clusterServerFlowRules";
    }

    @Override
    public String getClusterServerTransportRule() {
        return "/clusterServerTransportRules";
    }

    @Override
    public String getClusterServerNamespaceRule() {
        return "/clusterServerNamespaceRules";
    }

}
