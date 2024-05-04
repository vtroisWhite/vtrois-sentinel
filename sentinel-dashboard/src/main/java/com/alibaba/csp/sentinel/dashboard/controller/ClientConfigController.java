package com.alibaba.csp.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.ClientConfigParam;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicClientConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 获取客户端配置
 */
@RestController
public class ClientConfigController {

    @Autowired
    private List<DynamicClientConfigService> dynamicClientConfigServiceList;
    private static final String projectNamePattern = "^[A-Za-z0-9-]+$";

    /**
     * 获取客户端的配置
     *
     * @param param
     * @return
     */
    @GetMapping("/client/config")
    public Result<ClientConfigEntity> getClientConfig(ClientConfigParam param) {
        if (param == null || !StringUtils.hasText(param.getProjectName())) {
            return Result.ofFail(500, "miss projectName");
        }
        if (!Pattern.compile(projectNamePattern).matcher(param.getProjectName()).matches()) {
            return Result.ofFail(500, "项目名称格式为:" + projectNamePattern);
        }
        try {
            ClientConfigEntity entity = new ClientConfigEntity();
            dynamicClientConfigServiceList.forEach(x -> x.initClientConfigEntity(param, entity));
            return Result.ofSuccess(entity);
        } catch (Exception e) {
            return Result.ofFail(-1, e.toString());
        }
    }
}
