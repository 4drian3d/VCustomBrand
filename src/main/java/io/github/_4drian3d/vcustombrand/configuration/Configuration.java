package io.github._4drian3d.vcustombrand.configuration;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.concurrent.TimeUnit;

@ConfigSerializable
public final class Configuration {
    private static final ObjectMapper<Configuration> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.forClass(Configuration.class);
        } catch (ObjectMappingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Configuration loadFrom(ConfigurationNode node) throws ObjectMappingException {
        return MAPPER.bindToNew().populate(node);
    }


    @Setting(comment = """
            Sets the brand to display
            Supports MiniPlaceholders""",
            value = "custom-brand")
    private String customBrand = "<rainbow>MyServer <green><player_name>";

    @Setting(comment = "Unit of time in which the brand will be updated", value = "time-unit")
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    @Setting(comment = """
            Amount of time according to the unit of time
            in which the brand will be updated""")
    private int value = 2;

    public String customBrand() {
        return this.customBrand;
    }

    public TimeUnit timeUnit() {
        return this.timeUnit;
    }

    public int timeValue() {
        return this.value;
    }
}
