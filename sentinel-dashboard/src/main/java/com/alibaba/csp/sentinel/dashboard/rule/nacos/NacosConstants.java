package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.rule.RulePath;

/**
 * 配置常量
 */
public interface NacosConstants {

    String strategy = "nacos";

    String dataSource = "nacosClient";

    String GROUP_ID = "DEFAULT_GROUP";

    int TIME_OUT_MS = 30_000;

    RulePath rulePath = new NacosRulePath();


}
