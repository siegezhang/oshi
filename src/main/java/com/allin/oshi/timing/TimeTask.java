package com.allin.oshi.timing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.allin.oshi.config.MyProperties;
import com.allin.oshi.server.WebSocketServer;
import com.allin.oshi.util.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author xiaosheng.zhang
 */
@Component
@EnableScheduling
public class TimeTask {

    @Autowired
    private MyProperties myProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Scheduled(cron = "${name.timer}")
    public void info() throws InterruptedException {
        if (myProperties.isNeedWebsocket()) {
            CopyOnWriteArraySet<WebSocketServer> webSocketSet = WebSocketServer.getWebSocketSet();
            webSocketSet.forEach(
                    c -> {
                        try {
                            c.sendMessage(getAllInfo());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            Info info = getLocalServerInfo();
            setRedis(myProperties.getServiceNameEn(), info);
        }
    }

    private String getAllInfo() throws InterruptedException {
        List<Info> allInfo = new ArrayList<>();
        allInfo.add(getLocalServerInfo());
        myProperties.getServiceNameList().forEach(serviceName ->
                allInfo.add(JSONObject.parseObject(JSONObject.toJSONString(redisTemplate.opsForValue().get(serviceName)), Info.class)));
        return JSON.toJSONString(allInfo);
    }

    public Info getLocalServerInfo() throws InterruptedException {
        Info info = new Info();
        Info.UsageData usageData = new Info.UsageData();
        info.setUsageData(usageData);
        info.setServiceName(myProperties.getServiceName());
        info.setStatus(1);
        oshi.SystemInfo si = new oshi.SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        //内存信息
        GlobalMemory memory = hal.getMemory();
        long availableMemory = memory.getAvailable();
        long totalMemory = memory.getTotal();
        usageData.setMemoryUsage((totalMemory - availableMemory) * 100 / totalMemory + 1);

        //磁盘信息
        long totalStoreUsable =
                os.getFileSystem().getFileStores().stream().mapToLong(OSFileStore::getUsableSpace).reduce(0,
                        (acc,
                         item) -> {
                            acc += item;
                            return acc;
                        });
        long totalStoreSpace =
                os.getFileSystem().getFileStores().stream().mapToLong(OSFileStore::getTotalSpace).reduce(0,
                        (acc,
                         item) -> {
                            acc += item;
                            return acc;
                        });
        usageData.setDiskUsage(totalStoreUsable * 100 / totalStoreSpace + 1);
        //CPU信息
        Thread.sleep(1000);
        usageData.setCpuUsage((long) (processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100) + 1);
        return info;
    }

    private void setRedis(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

}