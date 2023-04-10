package io.github._4drian3d.vcustombrand;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import io.github._4drian3d.vcustombrand.configuration.Configuration;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "vcustombrand",
        name = "VCustomBrand",
        description = "Customize the brand to be shown to players in Velocity",
        version = Constants.VERSION,
        authors = {"4drian3d"},
        dependencies = { @Dependency(id = "miniplaceholders", optional = true) }
)
public final class VCustomBrand {
    @Inject
    private Logger logger;
    @Inject
    private Injector injector;
    @Inject
    @DataDirectory
    private Path path;
    private BrandManager brandManager;

    @Subscribe
    void onProxyInitialization(final ProxyInitializeEvent event) {
        final Configuration configuration;
        try {
            configuration = Configuration.loadConfig(path);
        } catch (Exception e) {
            logger.error("Cannot load configuration", e);
            return;
        }

        injector = injector.createChildInjector(
                binder -> binder.bind(Configuration.class).toInstance(configuration)
        );

        brandManager = injector.getInstance(BrandManager.class);
        brandManager.start();
    }

    @Subscribe
    void onProxyShutdown(final ProxyShutdownEvent event) {
        if (brandManager != null) {
            brandManager.shutdown();
        }
    }
}