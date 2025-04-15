package cn.sanyeyun.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.FileReader;
import java.nio.file.Paths;

/**
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
            var configPath = Paths.get("config", "TrileafCertification.json").toFile();
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(new FileReader(configPath)).getAsJsonObject();

            String monitorAuth = json.get("monitorAuth").getAsString();
            Text clickableText = Text.literal("点击复制 monitorAuth")
                    .setStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, monitorAuth))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("点击将 monitorAuth 复制到剪贴板")))
                            .withColor(Formatting.GREEN)
                            .withUnderline(true)
                    );
            context.getSource().sendFeedback(() -> clickableText, false);
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("读取配置失败：" + e.getMessage()));
            e.printStackTrace();
        }
        return 1;
    }
}
