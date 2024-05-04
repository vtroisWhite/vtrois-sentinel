package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.rule.entity.SentinelClusterConfigEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RuleConfig {

    @Bean
    public Converter<List<FlowRuleEntity>, String> encoderFlowRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<FlowRuleEntity>> decoderFlowRuleEntity() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }


    @Bean
    public Converter<List<DegradeRuleEntity>, String> encoderDegradeRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<DegradeRuleEntity>> decoderDegradeRuleEntity() {
        return s -> JSON.parseArray(s, DegradeRuleEntity.class);
    }


    @Bean
    public Converter<List<SystemRuleEntity>, String> encoderSystemRuleEntity() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<SystemRuleEntity>> decoderSystemRuleEntity() {
        return s -> JSON.parseArray(s, SystemRuleEntity.class);
    }


    @Bean
    public Converter<List<ParamFlowRuleEntity>, String> encoderParamFlowRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<ParamFlowRuleEntity>> decoderParamFlowRuleEntity() {
        return s -> JSON.parseArray(s, ParamFlowRuleEntity.class);
    }


    @Bean
    public Converter<List<AuthorityRuleEntity>, String> encoderAuthorityRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<AuthorityRuleEntity>> decoderAuthorityRuleEntity() {
        return s -> JSON.parseArray(s, AuthorityRuleEntity.class);
    }


    @Bean
    public Converter<List<ApiDefinitionEntity>, String> encoderApiDefinitionEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<ApiDefinitionEntity>> decoderApiDefinitionEntity() {
        return s -> JSON.parseArray(s, ApiDefinitionEntity.class);
    }


    @Bean
    public Converter<List<GatewayFlowRuleEntity>, String> encoderGatewayFlowRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<GatewayFlowRuleEntity>> decoderGatewayFlowRuleEntity() {
        return s -> JSON.parseArray(s, GatewayFlowRuleEntity.class);
    }

    @Bean
    public Converter<List<SentinelClusterConfigEntity.Mode>, String> encoderClusterModeRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<SentinelClusterConfigEntity.Mode>> decoderClusterModeRuleEntity() {
        return s -> JSON.parseArray(s, SentinelClusterConfigEntity.Mode.class);
    }

    @Bean
    public Converter<List<SentinelClusterConfigEntity.ClientConfig>, String> encoderClusterClientConfigRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<SentinelClusterConfigEntity.ClientConfig>> decoderClusterClientConfigRuleEntity() {
        return s -> JSON.parseArray(s, SentinelClusterConfigEntity.ClientConfig.class);
    }

    @Bean
    public Converter<List<SentinelClusterConfigEntity.ServerFlow>, String> encoderClusterServerFlowRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<SentinelClusterConfigEntity.ServerFlow>> decoderClusterServerFlowRuleEntity() {
        return s -> JSON.parseArray(s, SentinelClusterConfigEntity.ServerFlow.class);
    }

    @Bean
    public Converter<List<SentinelClusterConfigEntity.ServerTransport>, String> encoderClusterServerTransportRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<SentinelClusterConfigEntity.ServerTransport>> decoderClusterServerTransportRuleEntity() {
        return s -> JSON.parseArray(s, SentinelClusterConfigEntity.ServerTransport.class);
    }

    @Bean
    public Converter<List<SentinelClusterConfigEntity.ServerNamespace>, String> encoderClusterServerNamespaceRuleEntity() {
        return JSON::toJSONString;
    }
    @Bean
    public Converter<String, List<SentinelClusterConfigEntity.ServerNamespace>> decoderClusterServerNamespaceRuleEntity() {
        return s -> JSON.parseArray(s, SentinelClusterConfigEntity.ServerNamespace.class);
    }
}
