package cn.sanyeyun.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 哈希工具类
 *
 * @author 徐亚松
 * 2025-05-07 14:49
 **/
public class HashUtil {

    /**
     * 计算文件的哈希值,并且过滤空白字符,forge接口专属,类型为 murmurHash2
     *
     * @param filePath 文件绝对路径
     * @return 哈希值十六进制字符串
     * @author 徐亚松
     * <p>2025/5/7 14:53</p>
     */
    public static long murmurHash2(String filePath) throws IOException {
        // 读取并过滤字节
        List<Byte> dataList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int b;
            while ((b = fis.read()) != -1) {
                if (b == 0x09 || b == 0x0A || b == 0x0D || b == 0x20) {
                    continue;
                }
                dataList.add((byte) b);
            }
        }
        int length = dataList.size();
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = dataList.get(i);
        }

        int seed = 1;
        int m = 0x5BD1E995;
        int h = seed ^ length;
        int i = 0;
        while (i + 4 <= length) {
            int k = (data[i] & 0xFF)
                    | ((data[i + 1] & 0xFF) << 8)
                    | ((data[i + 2] & 0xFF) << 16)
                    | ((data[i + 3] & 0xFF) << 24);
            k *= m;
            k &= 0xFFFFFFFF;
            k ^= (k >>> 24);
            k *= m;
            k &= 0xFFFFFFFF;

            h *= m;
            h &= 0xFFFFFFFF;
            h ^= k;

            i += 4;
        }
        int remaining = length - i;
        if (remaining == 3) {
            h ^= (data[i] & 0xFF) | ((data[i + 1] & 0xFF) << 8);
            h ^= (data[i + 2] & 0xFF) << 16;
            h *= m;
            h &= 0xFFFFFFFF;
        } else if (remaining == 2) {
            h ^= (data[i] & 0xFF) | ((data[i + 1] & 0xFF) << 8);
            h *= m;
            h &= 0xFFFFFFFF;
        } else if (remaining == 1) {
            h ^= (data[i] & 0xFF);
            h *= m;
            h &= 0xFFFFFFFF;
        }
        h ^= (h >>> 13);
        h *= m;
        h &= 0xFFFFFFFF;
        h ^= (h >>> 15);

        return h & 0xFFFFFFFFL;
    }

    /**
     * 计算文件的哈希值（支持 SHA-1 / SHA-256 / SHA-512）
     *
     * @param filePath  文件绝对路径
     * @param algorithm 哈希算法（"SHA-1"、"SHA-256"、"SHA-512"）
     * @return 哈希值十六进制字符串，如计算失败则返回错误信息
     * @author 徐亚松
     * <p>2025/5/7 14:53</p>
     */
    public static String hashFile(String filePath, String algorithm) {
        try (InputStream is = new FileInputStream(filePath)) {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] hash = digest.digest();
            return bytesToHex(hash);
        } catch (Exception e) {
            return "哈希计算失败: " + e.getMessage();
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
