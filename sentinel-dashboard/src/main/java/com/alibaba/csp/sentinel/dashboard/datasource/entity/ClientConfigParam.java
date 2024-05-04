package com.alibaba.csp.sentinel.dashboard.datasource.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ClientConfigParam {

    /**
     * 项目名称
     */
    private String projectName;

}
