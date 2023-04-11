package io.github._4drian3d.vcustombrand.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("UnstableApiUsage")
public class ConfigurationContainer {
    private static final TypeToken<Configuration> token = TypeToken.of(Configuration.class);
    private final AtomicReference<Configuration> config;
    private final HoconConfigurationLoader loader;
    private final Logger logger;

    private ConfigurationContainer(
            final Configuration config,
            final HoconConfigurationLoader loader,
            final Logger logger
    ) {
        this.config = new AtomicReference<>(config);
        this.loader = loader;
        this.logger = logger;
    }

    public CompletableFuture<Boolean> reload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final CommentedConfigurationNode node = loader.load();
                Configuration newConfig = Configuration.loadFrom(node);
                node.setValue(token, newConfig);
                loader.save(node);
                config.set(newConfig);
                return true;
            } catch (IOException | ObjectMappingException exception) {
                logger.error("Could not reload configuration file", exception);
                return false;
            }
        });
    }

    public Configuration get() {
        return this.config.get();
    }

    public static ConfigurationContainer load(
            final Logger logger,
            final Path path
    ) {
        try {
            final Path configPath = loadFiles(path);
            final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .setDefaultOptions(options ->
                            options.setHeader("VCustomBrand | by 4drian3d")
                                    .withShouldCopyDefaults(true)
                    )
                    .setPath(configPath)
                    .build();

            final CommentedConfigurationNode node = loader.load();
            final Configuration configuration = Configuration.loadFrom(node);
            node.setValue(token, configuration);
            loader.save(node);
            return new ConfigurationContainer(configuration, loader, logger);
        } catch (IOException | ObjectMappingException exception){
            logger.error("Could not load configuration file", exception);
            return null;
        }
    }

    private static Path loadFiles(final Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
        final Path configPath = path.resolve("config.conf");
        if (Files.notExists(configPath)) {
            Files.createFile(configPath);
        }
        return configPath;
    }
}
