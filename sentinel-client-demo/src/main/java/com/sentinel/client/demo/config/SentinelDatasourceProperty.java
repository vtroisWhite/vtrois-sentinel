package com.sentinel.client.demo.config;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Properties;
import java.util.function.Function;

/**
 * sentinel的数据源选择
 */
@Getter
@AllArgsConstructor
public class SentinelDatasourceProperty<T> {
    private SentinelClientConfigEntity configEntity;
    private Function<SentinelClientConfigEntity.RulePath, String> pathFunc;
    private Converter<String, T> parser;

    public AbstractDataSource<String, T> buildDataSource() {
            return buildDataSourceZookpeer();
//        return buildZkSourceNacos();
    }

    private AbstractDataSource<String, T> buildDataSourceZookpeer() {
        SentinelClientConfigEntity.ZkConfig config = this.getConfigEntity().getZkConfig();
        Function<SentinelClientConfigEntity.RulePath, String> pathFunc = this.getPathFunc();

        return new ZookeeperDataSource<>(config.getAddress(), pathFunc.apply(config.getRulePath()), this.getParser());
    }

    private AbstractDataSource<String, T> buildZkSourceNacos() {
        SentinelClientConfigEntity.NacosConfig config = this.getConfigEntity().getNacosConfig();
        Function<SentinelClientConfigEntity.RulePath, String> pathFunc = this.getPathFunc();

        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, config.getAddress());
        properties.setProperty(PropertyKeyConst.NAMESPACE, config.getNamespace());
        properties.setProperty(PropertyKeyConst.USERNAME, config.getUsername());
        properties.setProperty(PropertyKeyConst.PASSWORD, config.getPassword());
        return new NacosDataSource<>(properties, config.getGroupId(), pathFunc.apply(config.getRulePath()), this.getParser());
    }
}
