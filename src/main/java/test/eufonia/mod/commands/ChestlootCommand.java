package test.eufonia.mod.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import test.eufonia.mod.commons.LootTableConfig;

public class ChestlootCommand {

    public ChestlootCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("chestloot").then(CommandManager.literal("reload").executes(this::reloadConfig)));
        });
    }

    private int reloadConfig(CommandContext<ServerCommandSource> context) {
        try {
            LootTableConfig.loadConfig();
        } catch (Exception e) {
            System.out.println("Error reloadConfig-ChestlootCommand: " + e.getMessage());
        }
        context.getSource().sendFeedback(Text.literal("Configuración para las loot table recargada!"), true);
        return 1;
    }
}
