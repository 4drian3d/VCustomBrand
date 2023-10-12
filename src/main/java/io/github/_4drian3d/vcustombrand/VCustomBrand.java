package io.github._4drian3d.vcustombrand;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.vcustombrand.command.BrandCommand;
import io.github._4drian3d.vcustombrand.configuration.ConfigurationContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@Plugin(
        id = "vcustombrand",
        name = "VCustomBrand",
        description = "Customize the brand to be shown to players in Velocity",
        version = Constants.VERSION,
        authors = {"4drian3d"},
        dependencies = { @Dependency(id = "miniplaceholders", optional = true) }
)
public final class VCustomBrand {
    public static final Component PRESENTATION = miniMessage().deserialize(
            "<gradient:#E8C547:#C27114>VCustomBrand</gradient> <gray>|</gray> by <aqua>4drian3d"
    );
    @Inject
    private Injector injector;
    @Inject
    @DataDirectory
    private Path path;
    @Inject
    private ComponentLogger logger;
    private BrandManager brandManager;
    private ConfigurationContainer configuration;

    @Subscribe
    void onProxyInitialization(final ProxyInitializeEvent event) {
        configuration = ConfigurationContainer.load(logger, path);
        if (configuration == null) return;

        injector = injector.createChildInjector(
                binder -> binder.bind(ConfigurationContainer.class).toInstance(configuration)
        );

        brandManager = injector.getInstance(BrandManager.class);
        brandManager.start();

        injector.getInstance(BrandCommand.class).register();
        logger.info(PRESENTATION);
    }

    @Subscribe
    void onProxyShutdown(final ProxyShutdownEvent event) {
        if (brandManager != null) {
            brandManager.shutdown();
        }
    }

    @Subscribe
    void onProxyReload(final ProxyReloadEvent event) {
        reload();
    }

    public CompletableFuture<Boolean> reload() {
        return configuration.reload()
                .whenComplete((r, t) -> brandManager.reload());
    }
}