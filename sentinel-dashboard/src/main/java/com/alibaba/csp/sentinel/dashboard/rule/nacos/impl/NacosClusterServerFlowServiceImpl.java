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
package com.alibaba.csp.sentinel.dashboard.rule.nacos.impl;

import com.alibaba.csp.sentinel.dashboard.rule.RuleConstants;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.dashboard.rule.entity.SentinelClusterConfigEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConstants;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosRuleAbstract;
import org.springframework.stereotype.Component;

/**
 * 集群规则
 */
@Component(NacosConstants.strategy + RuleConstants.CLUSTER_SERVER_FLOW_SERVICE)
public class NacosClusterServerFlowServiceImpl extends NacosRuleAbstract<SentinelClusterConfigEntity.ServerFlow> {

    @Override
    public String getRulePath(RuleParam param) {
        return NacosConfig.geDefRulePath(param, rulePath.getClusterServerFlowRule());
    }
}