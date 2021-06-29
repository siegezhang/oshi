package com.allin.oshi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("myProperties")
@ConfigurationProperties(prefix = "name")
@Data
public class MyProperties {

    /**
     * 服务器中文名称
     */
    private String serviceName;
    /**
     * 服务器英文名称
     */
    private String serviceNameEn;
    /**
     * 是否是websocket应用
     */
    private boolean needWebsocket;
    /**
     * 服务器列表
     */
    private List<String> serviceNameList;

}
