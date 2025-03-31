package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 概览数据响应
 *
 * @author 徐亚松
 * 2025/3/28 10:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OverviewResponse extends MCSManagerBaseResponse {
    /**
     * 返回值
     */
    private Data data;

    @lombok.Data
    public static class Data {
        /**
         * 当前应用的版本号
         */
        private String version;

        /**
         * 指定守护进程的版本号
         */
        private String specifiedDaemonVersion;

        /**
         * 进程信息
         */
        private org.springframework.boot.info.ProcessInfo process;

        /**
         * 记录统计信息
         */
        private Record record;

        /**
         * 系统信息
         */
        private SystemInfo system;

        /**
         * 面板上的内存和CPU使用情况（统计图）
         */
        private Chart chart;

        /**
         * 远程连接计数
         */
        private RemoteCount remoteCount;

        /**
         * 守护进程列表
         */
        private List<Remote> remote;
    }

    @lombok.Data
    public static class ProcessInfo {
        /**
         * CPU使用率
         */
        private long cpu;

        /**
         * 内存使用量
         */
        private long memory;

        /**
         * 当前工作目录
         */
        private String cwd;
    }

    @lombok.Data
    public static class Record {
        /**
         * 登录成功的次数
         */
        private int logined;

        /**
         * 非法访问次数
         */
        private int illegalAccess;

        /**
         * 被封禁IP的数量
         */
        private int banips;

        /**
         * 登录失败的次数
         */
        private int loginFailed;
    }

    @lombok.Data
    public static class SystemInfo {
        /**
         * 用户信息
         */
        private UserInfo user;

        /**
         * 时间戳
         */
        private long time;

        /**
         * 总内存大小
         */
        private long totalmem;

        /**
         * 可用内存大小
         */
        private long freemem;

        /**
         * 操作系统类型
         */
        private String type;

        /**
         * 操作系统版本
         */
        private String version;

        /**
         * Node.js版本
         */
        private String node;

        /**
         * 主机名
         */
        private String hostname;

        /**
         * 系统负载平均值（仅限Linux）
         */
        private List<Double> loadavg;

        /**
         * 平台标识符
         */
        private String platform;

        /**
         * 操作系统发行版
         */
        private String release;

        /**
         * 系统运行时间
         */
        private double uptime;

        /**
         * CPU使用率
         */
        private double cpu;
    }

    @lombok.Data
    public static class UserInfo {
        /**
         * 用户ID
         */
        private int uid;

        /**
         * 组ID
         */
        private int gid;

        /**
         * 用户名
         */
        private String username;

        /**
         * 用户主目录路径
         */
        private String homedir;

        /**
         * 用户shell, 可能是null或其他值
         */
        private Object shell;
    }

    @lombok.Data
    public static class Chart {
        /**
         * 系统的内存和CPU使用图表数据
         */
        private List<SystemChartItem> system;

        /**
         * 请求相关的统计数据
         */
        private List<RequestChartItem> request;
    }

    @lombok.Data
    public static class SystemChartItem {
        /**
         * CPU使用率
         */
        private double cpu;

        /**
         * 内存使用率
         */
        private double mem;
    }

    @lombok.Data
    public static class RequestChartItem {
        /**
         * 值
         */
        private int value;

        /**
         * 实例总数
         */
        private int totalInstance;

        /**
         * 正在运行的实例数
         */
        private int runningInstance;
    }

    @lombok.Data
    public static class RemoteCount {
        /**
         * 可用远程连接数量
         */
        private int available;

        /**
         * 总远程连接数量
         */
        private int total;
    }

    @lombok.Data
    public static class Remote {
        /**
         * 版本号
         */
        private String version;

        /**
         * 进程信息
         */
        private ProcessInfo process;

        /**
         * 实例信息
         */
        private InstanceInfo instance;

        /**
         * 守护进程系统信息
         */
        private DaemonSystemInfo system;

        /**
         * CPU和内存使用图表数据
         */
        private List<CpuMemChartItem> cpuMemChart;

        /**
         * UUID
         */
        private String uuid;

        /**
         * IP地址
         */
        private String ip;

        /**
         * 端口号
         */
        private int port;

        /**
         * 前缀
         */
        private String prefix;

        /**
         * 是否可用
         */
        private boolean available;

        /**
         * 备注
         */
        private String remarks;
    }

    @lombok.Data
    public static class InstanceInfo {
        /**
         * 正在运行的实例数量
         */
        private int running;

        /**
         * 实例总数
         */
        private int total;
    }

    @lombok.Data
    public static class DaemonSystemInfo {
        /**
         * 系统类型
         */
        private String type;

        /**
         * 主机名
         */
        private String hostname;

        /**
         * 平台标识符
         */
        private String platform;

        /**
         * 发行版本
         */
        private String release;

        /**
         * 系统运行时间
         */
        private double uptime;

        /**
         * 当前工作目录
         */
        private String cwd;

        /**
         * 负载平均值
         */
        private List<Double> loadavg;

        /**
         * 可用内存大小
         */
        private long freemem;

        /**
         * CPU使用率
         */
        private double cpuUsage;

        /**
         * 内存使用率
         */
        private double memUsage;

        /**
         * 总内存大小
         */
        private long totalmem;

        /**
         * 进程CPU使用率
         */
        private double processCpu;

        /**
         * 进程内存使用率
         */
        private double processMem;
    }

    @lombok.Data
    public static class CpuMemChartItem {
        /**
         * CPU使用率
         */
        private double cpu;

        /**
         * 内存使用率
         */
        private double mem;
    }
}
