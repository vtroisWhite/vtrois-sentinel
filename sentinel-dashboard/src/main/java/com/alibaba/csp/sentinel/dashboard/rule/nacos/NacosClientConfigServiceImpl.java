package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigParam;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicClientConfigService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.impl.*;
import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 获取各个中间件规则存储path
 */
@Service
public class NacosClientConfigServiceImpl implements DynamicClientConfigService, NacosConstants {

    @Value("${nacos.server-addr}")
    private String address;
    @Value("${nacos.namespace}")
    private String namespace;
    @Value("${nacos.username}")
    private String user;
    @Value("${nacos.password}")
    private String password;
    @Autowired
    private ApplicationContext context;

    @Override
    public void initClientConfigEntity(ClientConfigParam param, ClientConfigEntity clientConfigEntity) {
        clientConfigEntity.setNacosConfig(this.buildConfig(param));
    }

    @Override
    public ClientConfigEntity.NacosConfig buildConfig(ClientConfigParam param) {
        ClientConfigEntity.NacosConfig config = this.buildConnectConfig();
        config.setRulePath(this.buildRulePath(param));
        return config;
    }

    @Override
    public ClientConfigEntity.NacosConfig buildConnectConfig() {
        ClientConfigEntity.NacosConfig config = new ClientConfigEntity.NacosConfig();
        config.setAddress(address);
        config.setNamespace(namespace);
        config.setUsername(user);
        config.setPassword(password);
        config.setGroupId(GROUP_ID);
        return config;
    }

    @Override
    public ClientConfigEntity.RulePath buildRulePath(ClientConfigParam param) {
        ClientConfigEntity.RulePath rulePath = new ClientConfigEntity.RulePath();
        RuleParam ruleParam = new RuleParam(param.getProjectName());
        rulePath.setFlowPath(context.getBean(NacosFlowRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setDegradePath(context.getBean(NacosDegradeRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setParamFlowPath(context.getBean(NacosParamFlowRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setSystemRulePath(context.getBean(NacosSystemRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setGatewayFlowRuleRulePath(context.getBean(NacosGatewayFlowRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setGatewayApiDefinitionPath(context.getBean(NacosGatewayApiRulesServiceImpl.class).getRulePath(ruleParam));
        rulePath.setAuthorityRulePath(context.getBean(NacosAuthorityRuleServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterModePath(context.getBean(NacosClusterModeServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterClientConfigPath(context.getBean(NacosClusterClientConfigServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerFlowPath(context.getBean(NacosClusterServerFlowServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerNamespacePath(context.getBean(NacosClusterServerNamespaceServiceImpl.class).getRulePath(ruleParam));
        rulePath.setClusterServerTransportPath(context.getBean(NacosClusterServerTransportServiceImpl.class).getRulePath(ruleParam));
        return rulePath;
    }

}
