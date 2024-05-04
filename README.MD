# 将sentinel配置持久化到nacos、zookeeper
> 基于[sentinel-dashboard](https://github.com/alibaba/Sentinel/tree/1.8.6/sentinel-dashboard)二次开发, 版本1.8.6

## [sentinel-dashboard](sentinel-dashboard) 二次开发的sentinel配置后台

### 实现的功能
* 后端配置持久化到nacos、zookeeper，可通过[application.properties](sentinel-dashboard%2Fsrc%2Fmain%2Fresources%2Fapplication.properties) `spring.profiles.active` 进行配置
* 新增了 `client/config` 接口，返回客户端需要监控的配置文件路径，防止出现客户端手动配置的不一致的问题，配置示例：
```json
{
  "success": true,
  "code": 0,
  "msg": "success",
  "data": {
    "zkConfig": {
      "address": "test.zk.localhost",
      "rulePath": {
        "flowPath": "/sentinel_rules/sentinel-client-demo/flow",
        "degradePath": "/sentinel_rules/sentinel-client-demo/degrade",
        "paramFlowPath": "/sentinel_rules/sentinel-client-demo/paramFlow",
        "systemRulePath": "/sentinel_rules/sentinel-client-demo/systemRules",
        "authorityRulePath": "/sentinel_rules/sentinel-client-demo/authorityRules",
        "gatewayFlowRuleRulePath": "/sentinel_rules/sentinel-client-demo/gatewayFlowRules",
        "gatewayApiDefinitionPath": "/sentinel_rules/sentinel-client-demo/gatewayApiGroupRules",
        "clusterModePath": "/sentinel_rules/sentinel-client-demo/clusterModeRules",
        "clusterClientConfigPath": "/sentinel_rules/sentinel-client-demo/clusterClientConfigRules",
        "clusterServerFlowPath": "/sentinel_rules/sentinel-client-demo/clusterServerFlowRules",
        "clusterServerTransportPath": "/sentinel_rules/sentinel-client-demo/clusterServerTransportRules",
        "clusterServerNamespacePath": "/sentinel_rules/sentinel-client-demo/clusterServerNamespaceRules"
      }
    },
    "nacosConfig": {
      "address": "test.nacos.localhost:80",
      "namespace": "sentinel-config",
      "username": "nacos",
      "password": "nacos",
      "groupId": "DEFAULT_GROUP",
      "rulePath": {
        "flowPath": "sentinel-client-demo-flow-rules",
        "degradePath": "sentinel-client-demo-degrade-rules",
        "paramFlowPath": "sentinel-client-demo-param-rules",
        "systemRulePath": "sentinel-client-demo-system-rules",
        "authorityRulePath": "sentinel-client-demo-authority-rules",
        "gatewayFlowRuleRulePath": "sentinel-client-demo-gateway-flow-rules",
        "gatewayApiDefinitionPath": "sentinel-client-demo-gateway-api-group-rules",
        "clusterModePath": "sentinel-client-demo-cluster-mode-rules",
        "clusterClientConfigPath": "sentinel-client-demo-cluster-client-config-rules",
        "clusterServerFlowPath": "sentinel-client-demo-cluster-serve-flow-rules",
        "clusterServerTransportPath": "sentinel-client-demo-cluster-server-transport-rules",
        "clusterServerNamespacePath": "sentinel-client-demo-cluster-server-namespace-rules"
      }
    }
  }
}
```
### 改造说明 ([git修改记录](https://github.com/vtroisWhite/vtrois-sentinel/commit/9eb9562b3999e5805bdb4d2c78c8494cea3f2577))
*  将原本官方的获取配置接口DynamicRuleProvider，发布配置接口DynamicRulePublisher，聚合为了一个接口：[DynamicRuleService.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Frule%2FDynamicRuleService.java)，
*  并以此接口为基础，实现了通过
[nacos](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Frule%2Fnacos)
和 [zookeeper](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Frule%2Fzookeeper)
对配置进行获取与发布的实现
* 新增[SentinelCustomApiClient.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Fservice%2FSentinelCustomApiClient.java) 
并继承覆盖了
[SentinelApiClient.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Fclient%2FSentinelApiClient.java) 
将原本调用client客户端进行获取配置、更新配置的操作，改为修改nacos、zookeeper中的配置，并且客户端监控中间件的配置文件变化，同时更新配置
* 新增api接口[ClientConfigController.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Fcontroller%2FClientConfigController.java) 获取当前服务端中间件的配置信息，客户端可以直接连接配置更新配置文件，无需在客户端再维护一套配置
* 项目启动时[EnvObjectConfig.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Fconfig%2FEnvObjectConfig.java)会通过`environment.plugin`配置来加载使用nacos或者zookeeper为中间件

## [sentinel-client-demo](sentinel-client-demo) 客户端加载使用sentinel Demo
 项目启动需要配置以下启动参数
> -Dproject.name=sentinel-client-demo -Dcsp.sentinel.dashboard.server=127.0.0.1:8901 -Dcsp.sentinel.api.port=8719 -Dcsp.sentinel.log.dir=./logs/sentinel/ -Dcsp.sentinel.log.use.pid=true -Dcsp.sentinel.log.output.type=console
### 代码说明
* [SentinelClientConfigEntity.java](sentinel-client-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsentinel%2Fclient%2Fdemo%2Fconfig%2FSentinelClientConfigEntity.java) 
序列化服务端配置接口返回的配置内容，与服务端的
[ClientConfigEntity.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Fdatasource%2Fentity%2FClientConfigEntity.java)
一致
* [SentinelClusterConfigEntity.java](sentinel-client-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsentinel%2Fclient%2Fdemo%2Fconfig%2FSentinelClusterConfigEntity.java)
集群配置中的一些实体类，与服务端的
[SentinelClusterConfigEntity.java](sentinel-dashboard%2Fsrc%2Fmain%2Fjava%2Fcom%2Falibaba%2Fcsp%2Fsentinel%2Fdashboard%2Frule%2Fentity%2FSentinelClusterConfigEntity.java)
一致
* [SentinelUtil.java](sentinel-client-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsentinel%2Fclient%2Fdemo%2Fconfig%2FSentinelUtil.java)
调用服务端接口获取配置，并加载`限流规则`，`热点规则`，`熔断规则`，`集群配置`，`系统规则`，`授权规则`的代码示例
* [SentinelDatasourceProperty.java](sentinel-client-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsentinel%2Fclient%2Fdemo%2Fconfig%2FSentinelDatasourceProperty.java)
在`buildDataSource()`方法中选择监控nacos或者zookeeper配置
* [SentinelClientInit.java](sentinel-client-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Fsentinel%2Fclient%2Fdemo%2Fconfig%2FSentinelClientInit.java) 初始化Sentinel，并加载以上配置类