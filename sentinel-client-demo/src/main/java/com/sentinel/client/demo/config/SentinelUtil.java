package com.sentinel.client.demo.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * sentinel各配置加载
 */
@Slf4j
@Component
public class SentinelUtil {

    private static final String configUri = "/client/config";

    @Value("${project.name:}")
    private String projectName;
    @Value("${csp.sentinel.dashboard.server:}")
    private String sentinelServer;

    public SentinelClientConfigEntity queryClientConfigEntity() {
        if (StrUtil.hasEmpty(projectName, sentinelServer)) {
            log.error("容灾组件Sentinel,缺少配置,无法启动");
            return null;
        }
        String url = sentinelServer + configUri + "?projectName=" + projectName;
        try (HttpResponse response = HttpRequest.get(url).execute()) {
            SentinelClientConfigEntity data = Optional.ofNullable(response.body())
                    .map(JSONObject::parseObject)
                    .filter(x -> Boolean.TRUE.equals(x.getBoolean("success")))
                    .map(x -> x.getJSONObject("data"))
                    .map(x -> JSONObject.parseObject(x.toJSONString(), SentinelClientConfigEntity.class))
                    .orElse(null);
            if (data == null) {
                log.error("容灾组件Sentinel,获取配置失败,url:{} ,httpCode:{},response:{}", url, response.getStatus(), response.body());
            } else {
                log.info("容灾组件Sentinel,获取配置成功,url:{} ,配置:{}", url, JSON.toJSONString(data));
            }
            return data;
        } catch (Exception e) {
            log.error("容灾组件Sentinel,获取配置异常,url:{} ,e:", url, e);
            return null;
        }
    }

    public String getIp() {
        return HostNameUtil.getIp();
    }

    public int getApiPort() {
        return TransportConfig.getRuntimePort();
    }

    public String getCurrentMachineId() {
        return getIp() + "@" + getApiPort();
    }

    public boolean machineEqual(SentinelClusterConfigEntity.Common common) {
        return machineEqual(common.getIp(), common.getPort());
    }

    public boolean machineEqual(String ip, int port) {
        return getIp().equals(ip) && getApiPort() == port;
    }


    /**
     * 加载 限流规则、热点限流规则
     *
     * @param entity
     */
    public void loadRule_flow(SentinelClientConfigEntity entity) {
        //限流
        FlowRuleManager.register2Property(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getFlowPath,
                source -> {
                    List<FlowRule> list = JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    });
                    log.info("Sentinel配置-服务限流,配置:{}", JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());

        //热点规则
        ParamFlowRuleManager.register2Property(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getParamFlowPath,
                source -> {
                    List<ParamFlowRule> list = JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                    });
                    log.info("Sentinel配置-热点规则,配置:{}", JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());
    }


    /**
     * 加载 熔断降级规则
     *
     * @param entity
     */
    public void loadRule_degrade(SentinelClientConfigEntity entity) {
        //熔断降级
        DegradeRuleManager.register2Property(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getDegradePath,
                source -> {
                    List<DegradeRule> list = JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                    });
                    log.info("Sentinel配置-熔断降级,配置:{}", JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());
    }

    /**
     * 加载 集群 的 限流规则
     *
     * @param entity
     */
    public void loadRule_flow_cluster(SentinelClientConfigEntity entity) {
        this.loadClusterFlowConfig(entity);
        this.loadClusterConfig(entity);
    }

    /**
     * 加载 系统规则
     *
     * @param entity
     */
    public void loadRule_system(SentinelClientConfigEntity entity) {
        SystemRuleManager.register2Property(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getSystemRulePath,
                source -> {
                    List<SystemRule> list = JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                    });
                    log.info("Sentinel配置-系统规则,配置:{}", JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());
    }

    /**
     * 加载 授权规则
     *
     * @param entity
     */
    public void loadRule_authority(SentinelClientConfigEntity entity) {
        AuthorityRuleManager.register2Property(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getAuthorityRulePath,
                source -> {
                    List<AuthorityRule> list = JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {
                    });
                    log.info("Sentinel配置-授权规则,配置:{}", JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());
    }

    /**
     * 加载集群配置
     *
     * @param entity
     */
    private void loadClusterConfig(SentinelClientConfigEntity entity) {
        //当前客户端模式
        ClusterStateManager.registerProperty(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getClusterModePath,
                source -> {
                    List<SentinelClusterConfigEntity.Mode> list = JSON.parseObject(source, new TypeReference<List<SentinelClusterConfigEntity.Mode>>() {
                    });
                    Integer mode = Optional.ofNullable(list)
                            .flatMap(x -> x.stream().filter(this::machineEqual).findFirst())
                            .map(SentinelClusterConfigEntity.Mode::getMode)
                            .orElse(ClusterStateManager.CLUSTER_NOT_STARTED);
                    log.info("Sentinel配置-集群模式,配置:{},结果:{}", JSON.toJSONString(list), mode);
                    return mode;
                }).buildDataSource().getProperty());
        //client-配置请求server的 ip与端口，若本身不是server模式，则为null
        ClusterClientConfigManager.registerServerAssignProperty(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getClusterClientConfigPath,
                source -> {
                    List<SentinelClusterConfigEntity.ClientConfig> list = JSON.parseObject(source, new TypeReference<List<SentinelClusterConfigEntity.ClientConfig>>() {
                    });
                    ClusterClientAssignConfig config = Optional.ofNullable(list)
                            .flatMap(x -> x.stream().filter(this::machineEqual).findFirst())
                            .map(cfg -> new ClusterClientAssignConfig(cfg.getServerHost(), cfg.getServerPort()))
                            .orElse(null);
                    log.info("Sentinel配置-集群客户端,配置:{},结果:{}", JSON.toJSONString(list), JSON.toJSONString(config));
                    return config;
                }).buildDataSource().getProperty());

        //client-配置请求server的超时时间
        ClusterClientConfigManager.registerClientConfigProperty(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getClusterClientConfigPath,
                source -> {
                    List<SentinelClusterConfigEntity.ClientConfig> list = JSON.parseObject(source, new TypeReference<List<SentinelClusterConfigEntity.ClientConfig>>() {
                    });
                    ClusterClientConfig config = Optional.ofNullable(list)
                            .flatMap(x -> x.stream().filter(this::machineEqual).findFirst())
                            .map(cfg -> new ClusterClientConfig().setRequestTimeout(cfg.getRequestTimeout()))
                            .orElse(null);
                    log.info("Sentinel配置-集群客户端超时,配置:{},结果:{}", JSON.toJSONString(list), JSON.toJSONString(config));
                    return config;
                }).buildDataSource().getProperty());

        //server-设置端口
        ClusterServerConfigManager.registerServerTransportProperty(new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getClusterServerTransportPath,
                source -> {
                    List<SentinelClusterConfigEntity.ServerTransport> list = JSON.parseObject(source, new TypeReference<List<SentinelClusterConfigEntity.ServerTransport>>() {
                    });
                    ServerTransportConfig config = Optional.ofNullable(list)
                            .flatMap(x -> x.stream().filter(this::machineEqual).findFirst())
                            .map(cfg -> new ServerTransportConfig().setPort(cfg.getServerPort()).setIdleSeconds(cfg.getIdleSeconds()))
                            .orElse(null);
                    log.info("Sentinel配置-集群服务端配置,配置:{},结果:{}", JSON.toJSONString(list), JSON.toJSONString(config));
                    return config;
                }).buildDataSource().getProperty());
    }


    /**
     * 加载集群限流配置
     *
     * @param entity
     */
    private void loadClusterFlowConfig(SentinelClientConfigEntity entity) {
        //集群配置-流量控制
        ClusterFlowRuleManager.setPropertySupplier(namespace -> new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getFlowPath,
                source -> {
                    List<FlowRule> list = JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    });
                    log.info("Sentinel配置-集群流量控制,namespace:{},配置:{}", namespace, JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty()
        );
        //集群配置-热点流量控制
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> new SentinelDatasourceProperty<>(entity, SentinelClientConfigEntity.RulePath::getParamFlowPath,
                source -> {
                    List<ParamFlowRule> list = JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                    });
                    log.info("Sentinel配置-集群热点规则,namespace:{},配置:{}", namespace, JSON.toJSONString(list));
                    return list;
                }).buildDataSource().getProperty());
    }

}
