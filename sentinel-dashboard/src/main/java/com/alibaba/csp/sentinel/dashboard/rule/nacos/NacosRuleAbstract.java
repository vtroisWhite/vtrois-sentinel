package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

public abstract class NacosRuleAbstract<T extends RuleEntity> implements DynamicRuleService<List<T>>, NacosConstants {

    @Autowired
    @Qualifier(NacosConstants.dataSource)
    protected ConfigService configService;
    @Autowired
    protected Converter<String, List<T>> converter1;
    @Autowired
    protected Converter<List<T>, String> converter2;

    @SneakyThrows
    @Override
    public List<T> getRules(RuleParam param) {
        String rules = configService.getConfig(getRulePath(param), getGroupId(param), NacosConstants.TIME_OUT_MS);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter1.convert(rules);
    }

    @SneakyThrows
    @Override
    public boolean publish(RuleParam param, List<T> rules) {
        String app = param.getApp();
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return false;
        }
        configService.publishConfig(getRulePath(param), getGroupId(param), converter2.convert(rules));
        return true;
    }

    @Override
    public abstract String getRulePath(RuleParam param);


    public String getGroupId(RuleParam param) {
        return NacosConstants.GROUP_ID;
    }
}
