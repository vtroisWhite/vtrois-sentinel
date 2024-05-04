package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.rule.RulePath;

/**
 * zk配置常量
 */
public interface ZookeeperConstants {

    String dataSource = "zkClient";

    String strategy = "zookeeper";

    int RETRY_TIMES = 3;

    int SLEEP_TIME = 1000;

    RulePath rulePath = new ZookeeperRulePath();

    String zkPath = "/sentinel_rules";
}
