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
package com.alibaba.csp.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.dashboard.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author hisenyuan
 * @since 1.7.0
 */
@Slf4j
@RestController
public class VersionController {

    private static final String VERSION_PATTERN = "-";

    @Value("${sentinel.dashboard.version:}")
    private String sentinelDashboardVersion;
    @Value("${environment.plugin}")
    private String plugin;
    @Value("${environment.info}")
    private String dc;

    private static String fullDesc;

    @PostConstruct
    public void initFullDesc() {
        StringBuilder sb = new StringBuilder(sentinelDashboardVersion);
        sb.append(" ").append(plugin).append("-").append(dc);
        fullDesc = sb.toString();
        log.info("当前服务信息说明:{}", fullDesc);
    }

    @GetMapping("/version")
    public Result<String> apiGetVersion() {
        return Result.ofSuccess(fullDesc);
    }
}
