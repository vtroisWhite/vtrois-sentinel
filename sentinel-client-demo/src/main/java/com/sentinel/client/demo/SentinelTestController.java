package com.sentinel.client.demo;

import cn.hutool.http.server.HttpServerRequest;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description sentinel演示
 */
@Slf4j
@RestController
public class SentinelTestController {

    /**
     * SentinelResource注解的使用文档
     *
     * https://sentinelguard.io/zh-cn/docs/annotation-support.html
     * @param request
     * @return
     */
    @GetMapping("/sentinel/test")
    @SentinelResource(value = "sentinelTest",
            blockHandlerClass = SentinelTestController.class, blockHandler = "sentinelBlock",
            fallbackClass = SentinelTestController.class, fallback = "sentinelFallback")
    public Map<String, Object> test(HttpServerRequest request) {
        if (new Random().nextInt(100) < 50) {
            throw new RuntimeException();
        }
        return buildMap(0, "success");
    }

    /**
     * 触发配置规则
     */
    public static Map<String, Object> sentinelBlock(HttpServerRequest request, BlockException ex) {
        if (ex instanceof com.alibaba.csp.sentinel.slots.block.degrade.DegradeException) {
            log.error("sentinel-熔断降级");
        } else if (ex instanceof com.alibaba.csp.sentinel.slots.block.flow.FlowException) {
            log.error("sentinel-限流");
        } else {
            log.error("sentinel-未知block,e:{}", ex.getMessage());
        }
        return buildMap(500, "block");
    }

    /**
     * 发生异常
     */
    public static Map<String, Object> sentinelFallback(HttpServerRequest request, Throwable ex) {
        log.error("sentinel-fallback,e:{}", ex.getMessage());
        return buildMap(500, "error");
    }

    private static Map<String, Object> buildMap(int code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }
}
