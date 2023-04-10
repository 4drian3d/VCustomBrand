package io.github._4drian3d.vcustombrand;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public final class Constants {
	public static final String VERSION = "{version}";
	public static final ChannelIdentifier MODERN_CHANNEL = MinecraftChannelIdentifier
			.forDefaultNamespace("brand");
	public static final ChannelIdentifier LEGACY_CHANNEL = new LegacyChannelIdentifier("MC|Brand");
	
	private Constants() {}
}