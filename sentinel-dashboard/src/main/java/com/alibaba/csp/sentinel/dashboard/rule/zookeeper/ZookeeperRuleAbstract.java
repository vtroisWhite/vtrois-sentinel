package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleService;
import com.alibaba.csp.sentinel.dashboard.rule.RuleParam;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ZookeeperRuleAbstract<T extends RuleEntity> implements DynamicRuleService<List<T>> ,ZookeeperConstants{

    @Autowired
    @Qualifier(ZookeeperConstants.dataSource)
    protected CuratorFramework zkClient;
    @Autowired
    protected Converter<String, List<T>> converter1;
    @Autowired
    protected Converter<List<T>, String> converter2;

    @SneakyThrows
    @Override
    public List<T> getRules(RuleParam param) {
        byte[] bytes = zkClient.getData().forPath(getRulePath(param));
        if (null == bytes || bytes.length == 0) {
            return new ArrayList<>();
        }
        String s = new String(bytes);
        return converter1.convert(s);
    }

    @SneakyThrows
    @Override
    public boolean publish(RuleParam param, List<T> rules) {
        AssertUtil.notEmpty(param.getApp(), "app name cannot be empty");
        String path = getRulePath(param);
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat == null) {
            zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, null);
        }
        byte[] data = CollectionUtils.isEmpty(rules) ? "[]".getBytes() : converter2.convert(rules).getBytes();
        zkClient.setData().forPath(path, data);
        return true;
    }

    @Override
    public abstract String getRulePath(RuleParam param);

}
