package com.allin.oshi.util;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 服务器信息
 */
@Getter
@Setter
public class Info implements Serializable {
    /**
     * 服务器名称
     */
    private String serviceName;


    /**
     * 服务器状态
     */
    private int status;

    /**
     * 服务器信息
     */
    private UsageData usageData;


    @Getter
    @Setter
    public static class UsageData implements Serializable {
        /**
         * 磁盘使用率
         */
        private long diskUsage;
        /**
         * CPU使用率
         */
        @JSONField(name = "CPUUsage")
        private long cpuUsage;
        /**
         * 内存使用率
         */
        private long memoryUsage;

    }


}
