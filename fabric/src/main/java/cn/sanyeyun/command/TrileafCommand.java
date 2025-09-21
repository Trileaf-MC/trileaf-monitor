package cn.sanyeyun.command;

import cn.sanyeyun.cache.GlobalCache;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 指令
 * @author 徐亚松
 * 2025-04-15 15:36
 **/
public class TrileafCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("trileaf")
                    .requires(source -> source.hasPermissionLevel(4)) // 限制只有 OP 等级 4 可用
                    .then(CommandManager.literal("key")
                            .executes(TrileafCommand::handleKeyCommand)));
        });
    }


    private static int handleKeyCommand(CommandContext<ServerCommandSource> context) {
        try {
            var runtimeInfo = GlobalCache.getServerRuntimeInfo();
            Boolean claim = runtimeInfo.getClaim();
            String url = "https://www.baidu.com"; // 默认地址

            Text message;

            if (Boolean.TRUE.equals(claim)) {
                // 已认领：只显示 URL
                Text urlText = Text.literal(url)
                        .setStyle(Style.EMPTY
                                .withColor(Formatting.AQUA)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击打开网页"))));

                message = Text.literal("服务器已被认领，访问后台管理：").formatted(Formatting.GREEN)
                        .append(urlText);

            } else {
                // 未认领：显示验证码、过期时间和 URL
                String code = runtimeInfo.getVerificationCode();
                Long expireMillis = runtimeInfo.getVerificationExpireTime();

                String expireStr = "未知";
                if (expireMillis != null) {
                    expireStr = Instant.ofEpochMilli(expireMillis)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }

                Text codeText = Text.literal(code)
                        .setStyle(Style.EMPTY
                                .withColor(Formatting.GREEN)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击复制验证码"))));

                Text urlText = Text.literal(url)
                        .setStyle(Style.EMPTY
                                .withColor(Formatting.AQUA)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击打开网页"))));

                message = Text.literal("你当前需要认领服务器：\n")
                        .append(Text.literal("验证码：").formatted(Formatting.YELLOW))
                        .append(codeText)
                        .append(Text.literal("\n过期时间：").formatted(Formatting.YELLOW))
                        .append(Text.literal(expireStr).formatted(Formatting.RED))
                        .append(Text.literal("\n请前往后台管理进行认领，点击访问："))
                        .append(urlText);
            }

            context.getSource().sendFeedback(() -> message, false);

        } catch (Exception e) {
            context.getSource().sendError(Text.literal("获取验证码失败：" + e.getMessage()));
            e.printStackTrace();
        }

        return 1;
    }
}
