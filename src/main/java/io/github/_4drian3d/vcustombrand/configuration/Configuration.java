package io.github._4drian3d.vcustombrand.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.concurrent.TimeUnit;

@ConfigSerializable
public final class Configuration {
    private static final ObjectMapper<Configuration> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.factory().get(Configuration.class);
        } catch (SerializationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Configuration loadFrom(ConfigurationNode node) throws SerializationException {
        return MAPPER.load(node);
    }


    @Comment("""
            Sets the brand to display
            Supports MiniPlaceholders""")
    @Setting(value = "custom-brand")
    public String customBrand = "<rainbow>MyServer <green><player_name>";

    @Comment("Unit of time in which the brand will be updated")
    @Setting(value = "time-unit")
    public TimeUnit timeUnit = TimeUnit.SECONDS;

    @Comment("""
            Amount of time according to the unit of time
            in which the brand will be updated""")
    public int value = 2;
}
