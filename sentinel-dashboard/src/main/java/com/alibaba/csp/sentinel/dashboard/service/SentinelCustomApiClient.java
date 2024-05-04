package com.alibaba.csp.sentinel.dashboard.service;

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.config.EnvObjectConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.dashboard.repository.gateway.InMemApiDefinitionStore;
import com.alibaba.csp.sentinel.dashboard.repository.gateway.InMemGatewayFlowRuleStore;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.dashboard.rule.entity.SentinelClusterConfigEntity;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 重写 SentinelApiClient 的实现，改为由中间件持久化配置
 */
@Slf4j
@Component
public class SentinelCustomApiClient extends SentinelApiClient {

    @Autowired
    private AppManagement appManagement;
    @Autowired
    private EnvObjectConfig envObjectConfig;
    @Autowired
    private RuleRepository<DegradeRuleEntity, Long> repositoryDegrade;
    @Autowired
    private InMemoryRuleRepositoryAdapter<FlowRuleEntity> repositoryFlowRule;
    @Autowired
    private RuleRepository<ParamFlowRuleEntity, Long> repositoryParamFlowRule;
    @Autowired
    private InMemApiDefinitionStore repositoryApiDefinition;
    @Autowired
    private InMemGatewayFlowRuleStore repositoryGatewayFlow;
    @Autowired
    private RuleRepository<SystemRuleEntity, Long> repositorySystemRule;
    @Autowired
    private RuleRepository<AuthorityRuleEntity, Long> repositoryAuthorityRule;

    public static final String LOG_1 = "自定义处理器-服务限流";
    public static final String LOG_2 = "自定义处理器-熔断降级";
    public static final String LOG_3 = "自定义处理器-热点规则";
    public static final String LOG_4 = "自定义处理器-权限规则";
    public static final String LOG_5 = "自定义处理器-授权规则";
    public static final String LOG_6 = "自定义处理器-网关规则";
    public static final String LOG_7 = "自定义处理器-网关api配置";
    public static final String LOG_8 = "自定义处理器-集群-模式";
    public static final String LOG_9 = "自定义处理器-集群-客户端配置";
    public static final String LOG_10 = "自定义处理器-集群-服务端qps";
    public static final String LOG_11 = "自定义处理器-集群-服务端端口号";
    public static final String LOG_12 = "自定义处理器-集群-服务端namespace";

    /**
     * 限流-获取配置
     */
    @Override
    public synchronized List<FlowRuleEntity> fetchFlowRuleOfMachine(String app, String ip, int port) {
        log.info("{},配置获取", LOG_1);
        return envObjectConfig.getFlowRuleService().getRules(new RuleParam(app));
    }

    public boolean setFlowRuleOfMachine(String app, String ip, int port, List<FlowRuleEntity> rules) {
        this.setFlowRuleOfMachineAsync(app, ip, port, rules);
        log.info("{},配置修改1", LOG_1);
        return true;
    }

    /**
     * 限流-修改配置
     */
    @Override
    public synchronized CompletableFuture<Void> setFlowRuleOfMachineAsync(String app, String ip, int port, List<FlowRuleEntity> rules) {
        rules = repositoryFlowRule.findAllByApp(app);
        envObjectConfig.getFlowRuleService().publish(new RuleParam(app), rules);
        log.info("{},配置修改2,app:{},修改后内容:{}", LOG_1, app,  JSON.toJSONString(rules));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 熔断降级-获取配置
     */
    @Override
    public synchronized List<DegradeRuleEntity> fetchDegradeRuleOfMachine(String app, String ip, int port) {
        log.info("{},配置获取", LOG_2);
        return envObjectConfig.getDegradeRuleService().getRules(new RuleParam(app, ip, port));
    }

    /**
     * 熔断降级-修改配置
     */
    @Override
    public synchronized boolean setDegradeRuleOfMachine(String app, String ip, int port, List<DegradeRuleEntity> rules) {
        rules = repositoryDegrade.findAllByApp(app);
        boolean flag = envObjectConfig.getDegradeRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_2, app, ip, port,  JSON.toJSONString(rules));
        return flag;
    }

    /**
     * 热点规则
     */
    @Override
    public CompletableFuture<List<ParamFlowRuleEntity>> fetchParamFlowRulesOfMachine(String app, String ip, int port) {
        log.info("{},配置获取", LOG_3);
        List<ParamFlowRuleEntity> rules = envObjectConfig.getParamFlowRuleService().getRules(new RuleParam(app, ip, port));
        return CompletableFuture.completedFuture(rules);
    }

    /**
     * 热点规则
     */
    @Override
    public CompletableFuture<Void> setParamFlowRuleOfMachine(String app, String ip, int port, List<ParamFlowRuleEntity> rules) {
        rules = repositoryParamFlowRule.findAllByApp(app);
        envObjectConfig.getParamFlowRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_3, app, ip, port,  JSON.toJSONString(rules));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 网关api
     */
    @Override
    public CompletableFuture<List<ApiDefinitionEntity>> fetchApis(String app, String ip, int port) {
        log.info("{},配置获取", LOG_7);
        List<ApiDefinitionEntity> rules = envObjectConfig.getGatewayApiRuleService().getRules(new RuleParam(app, ip, port));
        return CompletableFuture.completedFuture(rules);
    }

    /**
     * 网关api
     */
    @Override
    public boolean modifyApis(String app, String ip, int port, List<ApiDefinitionEntity> rules) {
        rules = repositoryApiDefinition.findAllByApp(app);
        envObjectConfig.getGatewayApiRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_7, app, ip, port,  JSON.toJSONString(rules));
        return true;
    }

    /**
     * 网关规则
     */
    @Override
    public CompletableFuture<List<GatewayFlowRuleEntity>> fetchGatewayFlowRules(String app, String ip, int port) {
        log.info("{},配置获取", LOG_6);
        List<GatewayFlowRuleEntity> rules = envObjectConfig.getGatewayRuleService().getRules(new RuleParam(app, ip, port));
        return CompletableFuture.completedFuture(rules);
    }

    /**
     * 网关规则
     */
    @Override
    public boolean modifyGatewayFlowRules(String app, String ip, int port, List<GatewayFlowRuleEntity> rules) {
        rules = repositoryGatewayFlow.findAllByApp(app);
        envObjectConfig.getGatewayRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_6, app, ip, port,  JSON.toJSONString(rules));
        return true;
    }

    /**
     * 权限规则
     */
    @Override
    public List<SystemRuleEntity> fetchSystemRuleOfMachine(String app, String ip, int port) {
        log.info("{},配置获取", LOG_4);
        return envObjectConfig.getSystemRuleService().getRules(new RuleParam(app, ip, port));
    }

    /**
     * 权限规则
     */
    @Override
    public boolean setSystemRuleOfMachine(String app, String ip, int port, List<SystemRuleEntity> rules) {
        rules = repositorySystemRule.findAllByApp(app);
        envObjectConfig.getSystemRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_4, app, ip, port,  JSON.toJSONString(rules));
        return true;
    }

    /**
     * 授权规则
     */
    @Override
    public List<AuthorityRuleEntity> fetchAuthorityRulesOfMachine(String app, String ip, int port) {
        log.info("{},配置获取", LOG_5);
        return envObjectConfig.getAuthorityRuleService().getRules(new RuleParam(app, ip, port));
    }

    /**
     * 授权规则
     */
    @Override
    public boolean setAuthorityRuleOfMachine(String app, String ip, int port, List<AuthorityRuleEntity> rules) {
        rules = repositoryAuthorityRule.findAllByApp(app);
        envObjectConfig.getAuthorityRuleService().publish(new RuleParam(app, ip, port), rules);
        log.info("{},配置修改,app:{},ip:{},port:{},修改后内容:{}", LOG_5, app, ip, port,  JSON.toJSONString(rules));
        return true;
    }

    /**
     * 集群配置-修改服务器的模式为：server、client
     */
    @Override
    public synchronized CompletableFuture<Void> modifyClusterMode(String ip, int port, int mode) {
        String app = getAppByIpPort(ip, port);
        RuleParam ruleParam = new RuleParam(app);
        DynamicRuleService<List<SentinelClusterConfigEntity.Mode>> service = envObjectConfig.getClusterClientModeService();
        List<SentinelClusterConfigEntity.Mode> rules = service.getRules(ruleParam);
        String before = JSON.toJSONString(rules);
        Optional<SentinelClusterConfigEntity.Mode> optional = rules.stream().filter(x -> machineEqual(x, ip, port)).findFirst();
        SentinelClusterConfigEntity.Mode rule = optional.orElseGet(SentinelClusterConfigEntity.Mode::new);
        rule.setMode(mode);
        rule.setGmtCreate(new Date());
        if (!optional.isPresent()) {
            rule.initIpPort(ip, port);
            rules.add(rule);
        }
        log.info("{},app:{},修改前:{},修改后:{}", LOG_8, app,  before, JSON.toJSONString(rules));
        service.publish(ruleParam, rules);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 集群配置-为client指定其server
     */
    @Override
    public synchronized CompletableFuture<Void> modifyClusterClientConfig(String app, String ip, int port, ClusterClientConfig config) {
        DynamicRuleService<List<SentinelClusterConfigEntity.ClientConfig>> service = envObjectConfig.getClusterClientConfigService();
        RuleParam ruleParam = new RuleParam(app);
        List<SentinelClusterConfigEntity.ClientConfig> rules = service.getRules(ruleParam);
        String before = JSON.toJSONString(rules);
        Optional<SentinelClusterConfigEntity.ClientConfig> optional = rules.stream().filter(x -> machineEqual(x, ip, port)).findFirst();
        SentinelClusterConfigEntity.ClientConfig rule = optional.orElseGet(SentinelClusterConfigEntity.ClientConfig::new);
        rule.setServerHost(config.getServerHost());
        rule.setServerPort(config.getServerPort());
        rule.setRequestTimeout(config.getRequestTimeout());
        rule.setConnectTimeout(config.getConnectTimeout());
        rule.setGmtCreate(new Date());
        if (!optional.isPresent()) {
            rule.initIpPort(ip, port);
            rules.add(rule);
        }
        log.info("{},app:{},修改前:{},修改后:{}", LOG_9, app,  before, JSON.toJSONString(rules));
        service.publish(ruleParam, rules);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 集群配置-设置server的qps
     */
    @Override
    public synchronized CompletableFuture<Void> modifyClusterServerFlowConfig(String app, String ip, int port, ServerFlowConfig config) {
        DynamicRuleService<List<SentinelClusterConfigEntity.ServerFlow>> service = envObjectConfig.getClusterServerFlowService();
        RuleParam ruleParam = new RuleParam(app);
        List<SentinelClusterConfigEntity.ServerFlow> rules = service.getRules(ruleParam);
        String before = JSON.toJSONString(rules);
        Optional<SentinelClusterConfigEntity.ServerFlow> optional = rules.stream().filter(x -> machineEqual(x, ip, port)).findFirst();
        SentinelClusterConfigEntity.ServerFlow rule = optional.orElseGet(SentinelClusterConfigEntity.ServerFlow::new);
        rule.setMaxAllowedQps(config.getMaxAllowedQps());
        rule.setGmtCreate(new Date());
        if (!optional.isPresent()) {
            rule.initIpPort(ip, port);
            rules.add(rule);
        }
        log.info("{},app:{},修改前:{},修改后:{}", LOG_10, app,  before, JSON.toJSONString(rules));
        service.publish(ruleParam, rules);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 集群配置-设置server的port
     */
    @Override
    public synchronized CompletableFuture<Void> modifyClusterServerTransportConfig(String app, String ip, int port, ServerTransportConfig config) {
        DynamicRuleService<List<SentinelClusterConfigEntity.ServerTransport>> service = envObjectConfig.getClusterServerTransportService();
        RuleParam ruleParam = new RuleParam(app);
        List<SentinelClusterConfigEntity.ServerTransport> rules = service.getRules(ruleParam);
        String before = JSON.toJSONString(rules);
        Optional<SentinelClusterConfigEntity.ServerTransport> optional = rules.stream().filter(x -> machineEqual(x, ip, port)).findFirst();
        SentinelClusterConfigEntity.ServerTransport rule = optional.orElseGet(SentinelClusterConfigEntity.ServerTransport::new);
        rule.setServerPort(config.getPort());
        rule.setIdleSeconds(config.getIdleSeconds());
        rule.setGmtCreate(new Date());
        if (!optional.isPresent()) {
            rule.initIpPort(ip, port);
            rules.add(rule);
        }
        log.info("{},app:{},修改前:{},修改后:{}", LOG_11, app,  before, JSON.toJSONString(rules));
        service.publish(ruleParam, rules);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 集群配置- 设置server的namespace
     */
    @Override
    public synchronized CompletableFuture<Void> modifyClusterServerNamespaceSet(String app, String ip, int port, Set<String> set) {
        DynamicRuleService<List<SentinelClusterConfigEntity.ServerNamespace>> service = envObjectConfig.getClusterServerNamespaceService();
        RuleParam ruleParam = new RuleParam(app);
        List<SentinelClusterConfigEntity.ServerNamespace> rules = service.getRules(ruleParam);
        String before = JSON.toJSONString(rules);
        Optional<SentinelClusterConfigEntity.ServerNamespace> optional = rules.stream().filter(x -> machineEqual(x, ip, port)).findFirst();
        SentinelClusterConfigEntity.ServerNamespace rule = optional.orElseGet(SentinelClusterConfigEntity.ServerNamespace::new);
        rule.setNamespaceSet(set);
        rule.setGmtCreate(new Date());
        if (!optional.isPresent()) {
            rule.initIpPort(ip, port);
            rules.add(rule);
        }
        log.info("{},app:{},修改前:{},修改后:{}", LOG_12, app,  before, JSON.toJSONString(rules));
        service.publish(ruleParam, rules);
        return CompletableFuture.completedFuture(null);
    }

    private String getAppByIpPort(String ip, int port) {
        return appManagement.getBriefApps().stream()
                .flatMap(appInfo -> appInfo.getMachines().stream())
                .filter(machine -> machine.getPort() == port && machine.getIp().equals(ip))
                .findAny()
                .map(MachineInfo::getApp)
                .orElseThrow(() -> new RuntimeException("无法找到app"));
    }

    private boolean machineEqual(RuleEntity rule, String ip, int port) {
        return rule.getIp().equals(ip) && rule.getPort() == port;
    }
}
