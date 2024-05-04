package com.alibaba.csp.sentinel.dashboard.config;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleConstants;
import com.alibaba.csp.sentinel.dashboard.rule.entity.SentinelClusterConfigEntity;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 不同环境下获取器
 */
@Slf4j
@Component
public class EnvObjectConfig {
    /**
     * 熔断降级
     */
    @Getter
    private DynamicRuleService<List<FlowRuleEntity>> flowRuleService;
    /**
     * 限流
     */
    @Getter
    private DynamicRuleService<List<DegradeRuleEntity>> degradeRuleService;
    /**
     * 授权规则
     */
    @Getter
    private DynamicRuleService<List<AuthorityRuleEntity>> authorityRuleService;
    /**
     * 热点规则
     */
    @Getter
    private DynamicRuleService<List<ParamFlowRuleEntity>> paramFlowRuleService;
    /**
     * 系统规则
     */
    @Getter
    private DynamicRuleService<List<SystemRuleEntity>> systemRuleService;
    /**
     * 网关限流
     */
    @Getter
    private DynamicRuleService<List<GatewayFlowRuleEntity>> gatewayRuleService;
    /**
     * 网关api限流
     */
    @Getter
    private DynamicRuleService<List<ApiDefinitionEntity>> gatewayApiRuleService;
    /**
     * 集群流控
     */
    @Getter
    private DynamicRuleService<List<SentinelClusterConfigEntity.Mode>> clusterClientModeService;
    /**
     * 集群流控
     */
    @Getter
    private DynamicRuleService<List<SentinelClusterConfigEntity.ClientConfig>> clusterClientConfigService;
    /**
     * 集群流控
     */
    @Getter
    private DynamicRuleService<List<SentinelClusterConfigEntity.ServerFlow>> clusterServerFlowService;
    /**
     * 集群流控
     */
    @Getter
    private DynamicRuleService<List<SentinelClusterConfigEntity.ServerTransport>> clusterServerTransportService;
    /**
     * 集群流控
     */
    @Getter
    private DynamicRuleService<List<SentinelClusterConfigEntity.ServerNamespace>> clusterServerNamespaceService;

    @Autowired
    private ApplicationContext context;
    @Value("${environment.plugin}")
    private String envPlugin;

    @PostConstruct
    public void init() {
        this.flowRuleService = context.getBean(envPlugin + RuleConstants.FLOW_RULE_SERVICE, DynamicRuleService.class);
        this.degradeRuleService = context.getBean(envPlugin + RuleConstants.DEGRADE_RULE_SERVICE, DynamicRuleService.class);
        this.authorityRuleService = context.getBean(envPlugin + RuleConstants.AUTHORITY_RULE_SERVICE, DynamicRuleService.class);
        this.paramFlowRuleService = context.getBean(envPlugin + RuleConstants.PARAM_RULE_SERVICE, DynamicRuleService.class);
        this.systemRuleService = context.getBean(envPlugin + RuleConstants.SYSTEM_RULE_SERVICE, DynamicRuleService.class);
        this.gatewayRuleService = context.getBean(envPlugin + RuleConstants.GATEWAY_FLOW_RULE_SERVICE, DynamicRuleService.class);
        this.gatewayApiRuleService = context.getBean(envPlugin + RuleConstants.GATEWAY_API_FLOW_RULE_SERVICE, DynamicRuleService.class);
        this.clusterClientModeService = context.getBean(envPlugin + RuleConstants.CLUSTER_MODE_SERVICE, DynamicRuleService.class);
        this.clusterClientConfigService = context.getBean(envPlugin + RuleConstants.CLUSTER_CLIENT_CONFIG_SERVICE, DynamicRuleService.class);
        this.clusterServerFlowService = context.getBean(envPlugin + RuleConstants.CLUSTER_SERVER_FLOW_SERVICE, DynamicRuleService.class);
        this.clusterServerTransportService = context.getBean(envPlugin + RuleConstants.CLUSTER_SERVER_TRANSPORT_SERVICE, DynamicRuleService.class);
        this.clusterServerNamespaceService = context.getBean(envPlugin + RuleConstants.CLUSTER_SERVER_NAMESPACE_SERVICE, DynamicRuleService.class);
        log.info("处理器加载成功:{}", getDynamicRuleServiceImplMap());
    }

    private JSONObject getDynamicRuleServiceImplMap() {
        Field[] fields = ReflectUtil.getFields(EnvObjectConfig.class);
        JSONObject res = new JSONObject();
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(this, field);
            if (fieldValue instanceof DynamicRuleService) {
                res.put(field.getName(), fieldValue.getClass().getName());
            }
        }
        return res;
    }
}
