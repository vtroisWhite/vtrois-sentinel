package com.sentinel.client.demo.config;

import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 集群机器配置
 */
@Data
public class SentinelClusterConfigEntity {
    @Data
    public static class Common {

        private String ip;

        private Integer port;

        private Date gmtCreate;
    }

    @Data
    public static class Mode extends Common {

        /**
         * 0-client
         * 1-server
         * -1 未指定
         * {@link  ClusterStateManager#CLUSTER_CLIENT}
         */
        private Integer mode;

        @JSONField(serialize = false)
        public boolean isClientMode() {
            return Objects.equals(this.mode, ClusterStateManager.CLUSTER_CLIENT);
        }

        @JSONField(serialize = false)
        public boolean isServerMode() {
            return Objects.equals(this.mode, ClusterStateManager.CLUSTER_SERVER);
        }
    }

    @Data
    public static class ClientConfig extends Common {
        /**
         * server的 ip
         */
        private String serverHost;
        /**
         * server的 端口
         */
        private Integer serverPort;
        /**
         * 请求超时时间 ms
         */
        private Integer requestTimeout;

        private Integer connectTimeout;
    }

    @Data
    public static class ServerFlow extends Common {
        /**
         * 最大允许qps
         */
        private Double maxAllowedQps;
    }

    @Data
    public static class ServerTransport extends Common {
        /**
         * server的 端口
         */
        private Integer serverPort;

        private Integer idleSeconds;
    }

    @Data
    public static class ServerNamespace extends Common {

        private Set<String> namespaceSet;
    }
}