package com.trileaf.entity.panel.mcsm.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 实例列表响应
 *
 * @author 徐亚松
 * 2025/3/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RemoteServiceInstancesResponse extends MCSManagerBaseResponse {

    private Data data;

    @lombok.Data
    public static class Data {
        /**
         * 当前页数
         */
        private int page;

        /**
         * 每页显示的实例数量
         */
        private int pageSize;

        /**
         * 总页数
         */
        private int maxPage;

        /**
         * 实例详情列表
         */
        private List<InstanceDetail> data;
    }

    @lombok.Data
    public static class InstanceDetail {
        /**
         * 实例配置信息
         */
        private InstanceConfig config;

        /**
         * 实例运行信息
         */
        private InstanceInfo info;

        /**
         * 实例唯一标识符
         */
        private String instanceUuid;

        /**
         * 进程信息
         */
        private ProcessInfo processInfo;

        /**
         * 磁盘空间占用（单位：字节）
         */
        private long space;

        /**
         * 启动次数
         */
        private int started;

        /**
         * 实例状态
         * -1 = 忙碌
         * 0  = 停止
         * 1  = 停止中
         * 2  = 启动中
         * 3  = 运行中
         */
        private int status;
    }

    @lombok.Data
    public static class ProcessInfo {
        /**
         * CPU 使用率（单位：百分比）
         */
        private double cpu;

        /**
         * 内存使用量（单位：字节）
         */
        private long memory;

        /**
         * 父进程 ID
         */
        private int ppid;

        /**
         * 进程 ID
         */
        private int pid;

        /**
         * 进程创建时间（单位：毫秒）
         */
        private long ctime;

        /**
         * 进程运行时间（单位：毫秒）
         */
        private long elapsed;

        /**
         * 采集数据的时间戳（单位：毫秒）
         */
        private long timestamp;
    }

    @lombok.Data
    public static class InstanceInfo {
        /**
         * 当前在线玩家数量
         */
        private int currentPlayers;

        /**
         * 最大玩家数量
         */
        private int maxPlayers;

        /**
         * 服务器版本
         */
        private String version;

        /**
         * 文件锁状态（未知用途）
         */
        private int fileLock;

        /**
         * 玩家数量统计图表
         */
        private List<PlayersChartItem> playersChart;

        /**
         * 是否启用 FRP 远程连接
         */
        private boolean openFrpStatus;
    }

    @lombok.Data
    public static class PlayersChartItem {
        /**
         * 统计值（玩家数量）
         */
        private int value;
    }


    /**
     * 实例配置信息
     */
    @lombok.Data
    public static class InstanceConfig {

        /**
         * 实例的昵称
         */
        private String nickname;

        /**
         * 实例的启动命令
         */
        private String startCommand;

        /**
         * 实例的停止命令
         */
        private String stopCommand;

        /**
         * 实例的工作目录
         */
        private String cwd;

        /**
         * 输入编码格式
         */
        private String ie;

        /**
         * 输出编码格式
         */
        private String oe;

        /**
         * 实例创建时间（格式：MM/dd/yyyy）
         */
        private String createDatetime;

        /**
         * 实例最后运行时间（格式：MM/dd/yyyy HH:mm）
         */
        private String lastDatetime;

        /**
         * 实例类型，例如 "minecraft/java"
         */
        private String type;

        /**
         * 实例的标签列表
         */
        private List<String> tag;

        /**
         * 实例的到期时间（格式：MM/dd/yyyy HH:mm:ss）
         */
        private String endTime;

        /**
         * 文件编码格式
         */
        private String fileCode;

        /**
         * 进程类型，例如 "docker"
         */
        private String processType;

        /**
         * 更新命令（可能为空）
         */
        private String updateCommand;

        /**
         * 换行符类型（1 代表 CRLF）
         */
        private int crlf;

        /**
         * 额外的自定义操作命令列表
         */
        private List<String> actionCommandList;

        /**
         * 终端选项配置
         */
        private TerminalOption terminalOption;

        /**
         * 事件任务配置
         */
        private EventTask eventTask;

        /**
         * Docker 配置
         */
        private DockerConfig docker;

        /**
         * 实例的网络 Ping 配置信息
         */
        private PingConfig pingConfig;

        /**
         * 额外的服务配置，如 FRP 隧道信息
         */
        private ExtraServiceConfig extraServiceConfig;

    }

    /**
     * 终端选项配置
     */
    @lombok.Data
    public static class TerminalOption {
        /**
         * 终端是否支持颜色
         */
        private boolean haveColor;

        /**
         * 是否启用 PTY（伪终端）
         */
        private boolean pty;

        /**
         * 终端窗口列数
         */
        private int ptyWindowCol;

        /**
         * 终端窗口行数
         */
        private int ptyWindowRow;
    }

    /**
     * 事件任务配置
     */
    @lombok.Data
    public static class EventTask {
        /**
         * 是否自动启动
         */
        private boolean autoStart;

        /**
         * 是否自动重启
         */
        private boolean autoRestart;

        /**
         * 是否忽略某些任务
         */
        private boolean ignore;
    }

    /**
     * Docker 配置
     */
    @lombok.Data
    public static class DockerConfig {
        /**
         * Docker 容器名称
         */
        private String containerName;

        /**
         * 使用的 Docker 镜像
         */
        private String image;

        /**
         * 容器映射的端口信息（格式：宿主端口:容器端口/协议）
         */
        private List<String> ports;

        /**
         * 额外的卷映射
         */
        private List<String> extraVolumes;

        /**
         * 分配的内存大小（MB）
         */
        private int memory;

        /**
         * 网络模式，例如 "bridge"
         */
        private String networkMode;

        /**
         * 网络别名列表
         */
        private List<String> networkAliases;

        /**
         * 指定的 CPU 核心编号（为空表示未限制）
         */
        private String cpusetCpus;

        /**
         * CPU 使用限制（单位：百分之一核，例如 800 代表 8 核）
         */
        private int cpuUsage;

        /**
         * 最大可用磁盘空间（单位：MB，可能为空）
         */
        private Integer maxSpace;

        /**
         * 额外的 IO 配置（可能为空）
         */
        private String io;

        /**
         * 网络相关配置（可能为空）
         */
        private String network;
    }

    /**
     * Ping 配置信息
     */
    @lombok.Data
    public static class PingConfig {
        /**
         * 服务器的 IP 地址
         */
        private String ip;

        /**
         * 服务器的端口号
         */
        private int port;

        /**
         * Ping 类型（1 代表标准 Minecraft 服务器）
         */
        private int type;
    }

    /**
     * 额外服务配置，如 FRP 隧道信息
     */
    @lombok.Data
    public static class ExtraServiceConfig {
        /**
         * FRP 隧道 ID
         */
        private String openFrpTunnelId;

        /**
         * FRP 访问令牌
         */
        private String openFrpToken;

        /**
         * 是否开启 FRP 隧道
         */
        private boolean isOpenFrp;
    }


}
