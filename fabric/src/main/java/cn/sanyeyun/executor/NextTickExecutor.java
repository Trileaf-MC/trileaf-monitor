package cn.sanyeyun.executor;

import lombok.Getter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * 延迟调度器 延迟指定Tick
 *
 * @author 徐亚松
 * 2025-04-14 21:57
 **/
public class NextTickExecutor {
    // 存储需要延迟执行的任务，每个任务有对应的延迟 Tick 数量
    private static final List<TickTask> tasks = new ArrayList<>();

    static {
        // 每个 Tick 结束后检查是否有任务需要执行
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!tasks.isEmpty()) {
                List<TickTask> toExecute = new ArrayList<>();
                for (TickTask task : tasks) {
                    task.decrementTick();
                    if (task.getRemainingTicks() <= 0) {
                        toExecute.add(task);
                    }
                }
                tasks.removeAll(toExecute);  // 移除已执行的任务
                toExecute.forEach(t -> t.getRunnable().run());  // 执行任务
            }
        });
    }

    /**
     * 在指定 Tick 后执行任务。
     *
     * @param runnable   执行的任务
     * @param delayTicks 延迟的 Tick 数量
     * @author 徐亚松
     * <p>2025/4/14 22:05</p>
     */
    public static void runNextTick(Runnable runnable, int delayTicks) {
        tasks.add(new TickTask(runnable, delayTicks));
    }

    /**
     * 包装任务和延迟 Tick 数量 实体类
     */
    @Getter
    private static class TickTask {
        private final Runnable runnable;
        private int remainingTicks;

        public TickTask(Runnable runnable, int delayTicks) {
            this.runnable = runnable;
            this.remainingTicks = delayTicks;
        }

        public void decrementTick() {
            this.remainingTicks--;
        }
    }
}
