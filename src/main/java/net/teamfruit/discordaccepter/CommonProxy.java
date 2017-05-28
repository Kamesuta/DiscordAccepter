package net.teamfruit.discordaccepter;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.teamfruit.discordaccepter.command.DiscordAccepterCommands;
import net.teamfruit.discordaccepter.discord.DiscordBase;

/**
 * 共通処理
 *
 * @author Kamesuta
 */
public class CommonProxy {
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		Config.init(event.getSuggestedConfigurationFile(), "1.0.0");
	}

	public void init(final @Nonnull FMLInitializationEvent event) {
		CoreHandler.instance.init();
	}

	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		Config.getConfig().save();
	}

	public void serverStarting(final @Nonnull FMLServerStartingEvent event) {
		event.registerServerCommand(DiscordAccepterCommands.instance);
		if (NumberUtils.toDouble(System.getProperty("java.specification.version"))<1.8)
			throw new RuntimeException("Java8 is Required!");
		DiscordBase.instance.init();
	}
}