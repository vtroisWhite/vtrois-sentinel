package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigParam;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicClientConfigService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 获取各个中间件规则存储path
 */
@Service
public class ZookeeperClientConfigServiceImpl implements DynamicClientConfigService, ZookeeperConstants {

    @Value("${zk.address}")
    private String zkAddress;

    @Autowired
    private ApplicationContext context;

    @Override
    public void initClientConfigEntity(ClientConfigParam param, ClientConfigEntity clientConfigEntity) {
        clientConfigEntity.setZkConfig(this.buildConfig(param));
    }

    @Override
    public ClientConfigEntity.ZkConfig buildConfig(ClientConfigParam param) {
        ClientConfigEntity.ZkConfig config = this.buildConnectConfig();
        config.setRulePath(this.buildRulePath(param));
        return config;
    }

    @Override
    public ClientConfigEntity.ZkConfig buildConnectConfig() {
        ClientConfigEntity.ZkConfig config = new ClientConfigEntity.ZkConfig();
        config.setAddress(zkAddress);
        return config;
    }

    @Override
    public ClientConfigEntity.RulePath buildRulePath(ClientConfigParam param) {
        ClientConfigEntity.RulePath rulePath = new ClientConfigEntity.RulePath();
        RuleParam ruleParam = new RuleParam(param.getProjectName());
        rulePath.setFlowPath(context.getBean(ZookeeperFlowRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setDegradePath(context.getBean(ZookeeperDegradeRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setParamFlowPath(context.getBean(ZookeeperParamFlowServiceImpl.class).getRulePath(ruleParam));
        rulePath.setSystemRulePath(context.getBean(ZookeeperSystemRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setGatewayFlowRuleRulePath(context.getBean(ZookeeperGatewayFlowServiceImpl.class).getRulePath(ruleParam));
        rulePath.setGatewayApiDefinitionPath(context.getBean(ZookeeperGatewayApiRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setAuthorityRulePath(context.getBean(ZookeeperAuthorityRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterModePath(context.getBean(ZookeeperClusterModeServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterClientConfigPath(context.getBean(ZookeeperClusterClientConfigServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerFlowPath(context.getBean(ZookeeperClusterServerFlowServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerNamespacePath(context.getBean(ZookeeperClusterServerNamespaceServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerTransportPath(context.getBean(ZookeeperClusterServerTransportServiceImpl.class).getRulePath(ruleParam));
        return rulePath;
    }

}
