package cn.sanyeyun.executor;

import cn.sanyeyun.TrileafMonitorForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 延迟调度器 延迟指定Tick
 *
 * @author 徐亚松
 * 2025-04-14 21:57
 **/
@Mod.EventBusSubscriber(modid = TrileafMonitorForgeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NextTickExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NextTickExecutor.class);
    // 存储需要延迟执行的任务，每个任务有对应的延迟 Tick 数量
    private static final List<TickTask> tasks = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !tasks.isEmpty()) {
            List<TickTask> toExecute = new ArrayList<>();
            for (TickTask task : tasks) {
                task.decrementTick();
                if (task.getRemainingTicks() <= 0) {
                    toExecute.add(task);
                }
            }
            tasks.removeAll(toExecute);  // 移除已执行的任务
            toExecute.forEach(t -> {
                try {
                    t.getRunnable().run();
                } catch (Exception e) {
                    LOGGER.error("执行延迟任务时发生错误", e);
                }
            });
        }
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
        if (runnable == null) {
            throw new IllegalArgumentException("任务不能为空");
        }
        if (delayTicks < 0) {
            throw new IllegalArgumentException("延迟Tick数不能为负数");
        }
        tasks.add(new TickTask(runnable, delayTicks));
    }

    /**
     * 包装任务和延迟 Tick 数量 实体类
     */
    private static class TickTask {
        private final Runnable runnable;
        private int remainingTicks;

        public TickTask(Runnable runnable, int delayTicks) {
            this.runnable = runnable;
            this.remainingTicks = delayTicks;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public int getRemainingTicks() {
            return remainingTicks;
        }

        public void decrementTick() {
            this.remainingTicks--;
        }
    }
}
