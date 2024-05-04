package com.sentinel.client.demo.config;

import lombok.Data;

/**
 * @Description 客户端配置
 */
@Data
public class SentinelClientConfigEntity {
    /**
     * zookeeper的配置信息
     */
    private ZkConfig zkConfig;
    /**
     * nacos的配置信息
     */
    private NacosConfig nacosConfig;

    /**
     * zookeeper的配置
     */
    @Data
    public static class ZkConfig {
        private String address;
        private RulePath rulePath;
    }

    /**
     * nacos的配置
     */
    @Data
    public static class NacosConfig {
        private String address;
        private String namespace;
        private String username;
        private String password;
        private String groupId;
        private RulePath rulePath;
    }

    /**
     * 监听配置文件地址
     */
    @Data
    public static class RulePath {
        /**
         * 限流规则
         */
        private String flowPath;
        /**
         * 熔断降级规则地址
         */
        private String degradePath;
        /**
         * 热点规则
         */
        private String paramFlowPath;
        /**
         * 系统规则
         */
        private String systemRulePath;
        /**
         * 授权规则
         */
        private String authorityRulePath;
        /**
         * 集群配置-模式
         */
        private String clusterModePath;
        /**
         * 集群配置-客户端配置
         */
        private String clusterClientConfigPath;
        /**
         * 集群配置-服务端配置
         */
        private String clusterServerFlowPath;
        /**
         * 集群配置-服务端配置
         */
        private String clusterServerTransportPath;
        /**
         * 集群配置-命名空间配置
         */
        private String clusterServerNamespacePath;
    }
}
