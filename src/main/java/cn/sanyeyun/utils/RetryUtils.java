package cn.sanyeyun.utils;

import java.util.function.Supplier;

/**
 * @author 徐亚松
 * 2025/5/8 16:17
 */
public class RetryUtils {
    /**
     * 重试机制工具方法
     *
     * @param supplier    执行逻辑（返回非 null 即可认为成功）
     * @param maxAttempts 最大重试次数
     * @param delayMillis 每次重试间隔（毫秒）
     * @param <T>         返回值类型
     * @return 第一次成功获取的值，或 null（超过次数仍失败）
     */
    public static <T> T retryUntilNotNull(Supplier<T> supplier, int maxAttempts, long delayMillis) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = supplier.get();
                if (result != null) {
                    return result;
                }
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (Exception e) {
                // 可选日志：记录每次重试失败
            }
        }
        return null;
    }
}
