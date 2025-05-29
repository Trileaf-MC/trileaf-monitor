package cn.sanyeyun.command;

import cn.sanyeyun.TrileafMonitorForgeMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.FileReader;
import java.nio.file.Paths;

/**
 * @author 徐亚松
 * 2025-04-15 15:36
 **/
@Mod.EventBusSubscriber(modid = TrileafMonitorForgeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrileafCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("trileaf")
            .requires(source -> source.hasPermission(2))  // 需要权限等级2（相当于op）
            .executes(context -> {
                context.getSource().sendSuccess(() -> 
                    Component.literal("TrileafMonitor 命令帮助：\n" +
                                   "/trileaf - 显示此帮助信息"), false);
                return 1;
            });
            
        dispatcher.register(command);
    }

    private static int handleKeyCommand(CommandSourceStack source) {
        try {
            var configPath = Paths.get("config", "TrileafCertification.json").toFile();
            JsonObject json = JsonParser.parseReader(new FileReader(configPath)).getAsJsonObject();

            String monitorAuth = json.get("monitorAuth").getAsString();
            Component clickableText = Component.literal("点击复制 monitorAuth")
                    .setStyle(Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, monitorAuth))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("点击将 monitorAuth 复制到剪贴板")))
                            .withColor(ChatFormatting.GREEN)
                            .withBold(true)
                    );
            source.sendSuccess(() -> clickableText, false);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("读取配置失败：" + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }
}
