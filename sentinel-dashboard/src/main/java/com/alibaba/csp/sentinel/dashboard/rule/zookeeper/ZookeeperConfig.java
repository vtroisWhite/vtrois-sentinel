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
package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class ZookeeperConfig {

    @Autowired
    private ZookeeperClientConfigServiceImpl configService;

    @Bean(name = ZookeeperConstants.dataSource)
    public CuratorFramework zkClient() {
        CuratorFramework zkClient =
                CuratorFrameworkFactory.newClient(configService.buildConnectConfig().getAddress(),
                        new ExponentialBackoffRetry(ZookeeperConstants.SLEEP_TIME, ZookeeperConstants.RETRY_TIMES));
        zkClient.start();
        log.info("zookeeperClient,加载完成");
        return zkClient;
    }

    public static String geDefRulePath(RuleParam param, String path) {
        String appName = param.getApp();
        return ZookeeperConstants.zkPath + (appName.startsWith("/") ? "" : "/") + appName + path;
    }
}