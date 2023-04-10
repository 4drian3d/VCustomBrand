package io.github._4drian3d.vcustombrand;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github._4drian3d.vcustombrand.configuration.Configuration;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

public final class BrandManager {
    @Inject
    private ProxyServer proxyServer;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private Configuration configuration;
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
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    public void start() {
        final boolean miniPlaceholders = pluginManager.isLoaded("miniplaceholders");
        EXECUTOR.scheduleAtFixedRate(() -> proxyServer.getAllPlayers()
                .parallelStream()
                .forEach(player -> {
                    final String brand = configuration.customBrand();
                    final TagResolver resolver = miniPlaceholders
                            ? MiniPlaceholders.getAudienceGlobalPlaceholders(player)
                            : TagResolver.empty();
                    final Component brandParsed = MiniMessage.miniMessage().deserialize(brand, resolver);
                    final String legacyBrand = LEGACY_SERIALIZER.serialize(brandParsed);
                    final byte[] brandBytes = legacyBrand.getBytes(StandardCharsets.UTF_8);
                    if (!player.sendPluginMessage(Constants.MODERN_CHANNEL, brandBytes)) {
                        player.sendPluginMessage(Constants.LEGACY_CHANNEL, brandBytes);
                    }
                }), 0,5, TimeUnit.SECONDS);
    }

    public void shutdown() {
        EXECUTOR.shutdown();
    }
}
