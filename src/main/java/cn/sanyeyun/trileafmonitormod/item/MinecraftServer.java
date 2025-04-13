package cn.sanyeyun.trileafmonitormod.item;

import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public final class MinecraftServer {
    // Minecraft服务器的地址和端口
    private String address = "localhost";
    private int port = 25565;

    // 请求超时设置
    private int timeout = 1500;

    // 服务器的详细信息
    private int pingVersion = -1;
    private int protocolVersion = -1;
    private String gameVersion;
    private String motd; // message of the day (服务器信息)
    private int playersOnline = -1; // 当前在线玩家数
    private int maxPlayers = -1; // 最大玩家数

    // 默认构造函数
    public MinecraftServer() {
    }

    // 构造函数：指定服务器地址
    public MinecraftServer(String address) {
        this();
        this.setAddress(address);
    }

    // 构造函数：指定服务器地址和端口
    public MinecraftServer(String address, int port) {
        this(address);
        this.setPort(port);
    }

    // 构造函数：指定服务器地址、端口和超时时间
    public MinecraftServer(String address, int port, int timeout) {
        this(address, port);
        this.setTimeout(timeout);
    }

    // 设置服务器地址
    public void setAddress(String address) {
        this.address = address;
    }

    // 获取服务器地址
    public String getAddress() {
        return this.address;
    }

    // 设置服务器端口
    public void setPort(int port) {
        this.port = port;
    }

    // 获取服务器端口
    public int getPort() {
        return this.port;
    }

    // 设置超时时间（毫秒）
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    // 获取超时时间
    public int getTimeout() {
        return this.timeout;
    }

    // 设置服务器Ping协议版本
    private void setPingVersion(int pingVersion) {
        this.pingVersion = pingVersion;
    }

    // 获取Ping协议版本
    public int getPingVersion() {
        return this.pingVersion;
    }

    // 设置协议版本
    private void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    // 获取协议版本
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    // 设置游戏版本
    private void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    // 获取游戏版本
    public String getGameVersion() {
        return this.gameVersion;
    }

    // 设置服务器信息（MotD）
    private void setMotd(String motd) {
        this.motd = motd;
    }

    // 获取服务器信息（MotD）
    public String getMotd() {
        return this.motd;
    }

    // 设置在线玩家数量
    private void setPlayersOnline(int playersOnline) {
        this.playersOnline = playersOnline;
    }

    // 获取在线玩家数量
    public int getPlayersOnline() {
        return this.playersOnline;
    }

    // 设置最大玩家数量
    private void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    // 获取最大玩家数量
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * 通过Socket连接Minecraft服务器，获取服务器信息（Ping协议）
     *
     * @return 如果成功获取信息则返回true，否则返回false
     */
    public JsonObject fetchServerData() {
        JsonObject serverData = new JsonObject();
        try {
            // 创建Socket连接
            Socket socket = new Socket();
            socket.setSoTimeout(this.timeout);
            socket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), this.getTimeout());

            // 获取输入输出流
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-16BE"));

            // 向服务器发送Ping请求
            dataOutputStream.write(new byte[]{(byte) 0xFE, (byte) 0x01});

            // 读取返回的包ID
            int packetId = socket.getInputStream().read();
            if (packetId != 0xFF) {
                serverData.addProperty("error", "Invalid packet ID");
                return serverData;
            }

            // 读取字符串长度
            int length = inputStreamReader.read();
            if (length == -1) {
                serverData.addProperty("error", "Premature end of stream.");
                return serverData;
            }

            // 读取字符串内容
            char[] chars = new char[length];
            inputStreamReader.read(chars, 0, length);
            String string = new String(chars);

            // 解析返回的数据
            if (string.startsWith("§")) {
                String[] data = string.split("\0");
                this.setPingVersion(Integer.parseInt(data[0].substring(1)));
                this.setProtocolVersion(Integer.parseInt(data[1]));
                this.setGameVersion(data[2]);
                this.setMotd(data[3]);
                this.setPlayersOnline(Integer.parseInt(data[4]));
                this.setMaxPlayers(Integer.parseInt(data[5]));
            } else {
                String[] data = string.split("§");
                this.setMotd(data[0]);
                this.setPlayersOnline(Integer.parseInt(data[1]));
                this.setMaxPlayers(Integer.parseInt(data[2]));
            }

            // 填充返回的JsonObject
            serverData.addProperty("gameVersion", this.getGameVersion());
            serverData.addProperty("playersOnline", this.getPlayersOnline());
            serverData.addProperty("maxPlayers", this.getMaxPlayers());
            serverData.addProperty("motd", this.getMotd());
            serverData.addProperty("pingVersion", this.getPingVersion());
            serverData.addProperty("protocolVersion", this.getProtocolVersion());

            // 关闭连接
            socket.close();
        } catch (IOException exception) {
            serverData.addProperty("error", "Unable to fetch server data: " + exception.getMessage());
        }

        return serverData;
    }
}
