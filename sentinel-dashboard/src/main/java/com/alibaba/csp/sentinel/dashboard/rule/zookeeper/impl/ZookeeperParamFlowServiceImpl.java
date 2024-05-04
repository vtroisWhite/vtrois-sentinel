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
package com.alibaba.csp.sentinel.dashboard.rule.zookeeper.impl;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.RuleConstants;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.ZookeeperConfig;
import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.ZookeeperConstants;
import com.alibaba.csp.sentinel.dashboard.rule.zookeeper.ZookeeperRuleAbstract;
import org.springframework.stereotype.Component;

/**
 * push熔断降级规则
 */
@Component(ZookeeperConstants.strategy + RuleConstants.PARAM_RULE_SERVICE)
public class ZookeeperParamFlowServiceImpl extends ZookeeperRuleAbstract<DegradeRuleEntity> {

    @Override
    public String getRulePath(RuleParam param) {
        return ZookeeperConfig.geDefRulePath(param, rulePath.getParamFlowRule());
    }
}