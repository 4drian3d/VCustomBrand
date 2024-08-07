package io.github._4drian3d.vcustombrand;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.network.ProtocolState;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.github._4drian3d.vcustombrand.configuration.ConfigurationContainer;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public final class BrandManager {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final String RESET = LegacyComponentSerializer.SECTION_CHAR + "r";

    @Inject
    private ProxyServer proxyServer;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private ConfigurationContainer configuration;
    @Inject
    private Logger logger;
    private final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setNameFormat("VCustomBrand")
                    .setUncaughtExceptionHandler(
                            (thread, throwable) -> logger.error("An error occurred in thread {}", thread.getName(), throwable)
                    )
                    .build()
    );
    private ScheduledFuture<?> actualTask;


    public void start() {
        final boolean miniPlaceholders = pluginManager.isLoaded("miniplaceholders");
        this.actualTask = EXECUTOR.scheduleAtFixedRate(() -> proxyServer.getAllPlayers()
                .parallelStream()
                .forEach(player -> {
                    if (player.getProtocolState() != ProtocolState.PLAY) {
                        return;
                    }
                    final String brand = configuration.get().customBrand;
                    final TagResolver resolver = miniPlaceholders
                            ? MiniPlaceholders.getAudienceGlobalPlaceholders(player)
                            : TagResolver.empty();
                    final Component brandParsed = MiniMessage.miniMessage().deserialize(brand, resolver);
                    final String legacyBrand = LEGACY_SERIALIZER.serialize(brandParsed) + RESET;

                    final ProtocolVersion protocolVersion = player.getProtocolVersion();
                    final ByteBuf buf = Unpooled.buffer();

                    if (protocolVersion.compareTo(ProtocolVersion.MINECRAFT_1_8) >= 0) {
                        ProtocolUtils.writeString(buf, legacyBrand);
                    } else {
                        buf.writeCharSequence(legacyBrand, StandardCharsets.UTF_8);
                    }
                    // This cannot be converted to the new Velocity API because it has to be sent to older version clients
                    player.sendPluginMessage(protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)
                            ? Constants.MODERN_CHANNEL : Constants.LEGACY_CHANNEL, buf.array());
                }), 0, configuration.get().value, configuration.get().timeUnit);
    }

    public void reload() {
        if (actualTask != null) {
            actualTask.cancel(false);
        }
        start();
    }

    public void shutdown() {
        EXECUTOR.shutdown();
    }
}
