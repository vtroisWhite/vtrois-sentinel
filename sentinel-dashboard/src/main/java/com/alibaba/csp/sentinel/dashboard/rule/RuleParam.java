package com.alibaba.csp.sentinel.dashboard.rule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleParam {

    private String app;
    private String ip;
    private Integer port;

    public RuleParam(String app) {
        this(app, null, null);
    }

}
