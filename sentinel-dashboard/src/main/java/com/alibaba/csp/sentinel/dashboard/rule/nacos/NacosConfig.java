/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigParam;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Slf4j
@Configuration
public class NacosConfig {

    @Autowired
    private NacosClientConfigServiceImpl configService;

    @Bean(name = NacosConstants.dataSource)
    public ConfigService nacosConfigService() throws Exception {
        Properties properties = new Properties();
        ClientConfigEntity.NacosConfig nacosConfig = configService.buildConnectConfig();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosConfig.getAddress());
        properties.setProperty(PropertyKeyConst.NAMESPACE, nacosConfig.getNamespace());
        properties.setProperty(PropertyKeyConst.USERNAME, nacosConfig.getUsername());
        properties.setProperty(PropertyKeyConst.PASSWORD, nacosConfig.getPassword());
        ConfigService configService = ConfigFactory.createConfigService(properties);
        log.info("nacosClient,加载完成");
        return configService;
    }


    public static String geDefRulePath(RuleParam param, String path) {
        return param.getApp() + path;
    }
}
