package io.github._4drian3d.vcustombrand.command;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.audience.Audience;

import io.github._4drian3d.vcustombrand.VCustomBrand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public final class BrandCommand {
    @Inject
    private ComponentLogger logger;
    @Inject
    private CommandManager commandManager;
    @Inject
    private VCustomBrand plugin;

    public void register() {
        final var command = LiteralArgumentBuilder.<CommandSource>literal("vcustombrand")
                .requires(ctx -> ctx.hasPermission("vcustombrand.command"))
                .executes(context -> {
                    Audience audience = context.getSource();
                    if (audience instanceof ConsoleCommandSource) {
                        logger.info(VCustomBrand.PRESENTATION);
                    } else {
                        audience.sendMessage(VCustomBrand.PRESENTATION);
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .executes(ctx -> {
                            final Audience audience = ctx.getSource();
                            plugin.reload().thenAccept(result -> {
                               if (result) {
                                   audience.sendMessage(Component.text(
                                           "Configuration Reloaded", NamedTextColor.GREEN));
                               } else {
                                   audience.sendMessage(Component.text(
                                           "An error occurred reloading configuration, check console", NamedTextColor.RED));
                               }
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                );

        final BrigadierCommand brigadierCommand = new BrigadierCommand(command);
        final CommandMeta meta = commandManager.metaBuilder(brigadierCommand)
                .plugin(plugin)
                .aliases("vbrand")
                .build();
        commandManager.register(meta, brigadierCommand);
    }
}
