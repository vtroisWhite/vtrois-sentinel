package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.rule.RulePath;

/**
 * nacos配置路径
 */
public class NacosRulePath implements RulePath {
    @Override
    public String getFlowRule() {
        return "-flow-rules";
    }

    @Override
    public String getDegradeRule() {
        return "-degrade-rules";
    }

    @Override
    public String getParamFlowRule() {
        return "-param-rules";
    }

    @Override
    public String getSystemRule() {
        return "-system-rules";
    }

    @Override
    public String getAuthorityRule() {
        return "-authority-rules";
    }

    @Override
    public String getGatewayFlowRule() {
        return "-gateway-flow-rules";
    }

    @Override
    public String getGatewayApiFlowRule() {
        return "-gateway-api-group-rules";
    }

    @Override
    public String getClusterModeRule() {
        return "-cluster-mode-rules";
    }

    @Override
    public String getClusterClientConfigRule() {
        return "-cluster-client-config-rules";
    }

    @Override
    public String getClusterServerFlowRule() {
        return "-cluster-serve-flow-rules";
    }

    @Override
    public String getClusterServerTransportRule() {
        return "-cluster-server-transport-rules";
    }

    @Override
    public String getClusterServerNamespaceRule() {
        return "-cluster-server-namespace-rules";
    }

}
