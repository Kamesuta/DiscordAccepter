package net.teamfruit.discordaccepter;

import java.util.Map;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * FruitLib
 *
 * @author Kamesuta
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, useMetadata = true)
public class DiscordAccepter {
	@Instance(Reference.MODID)
	public static DiscordAccepter instance;

	@SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_SERVER)
	public static CommonProxy proxy;

	@NetworkCheckHandler
	public boolean checkModList(final @Nonnull Map<String, String> versions, final @Nonnull Side side) {
		return true;
	}

	@EventHandler
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		if (proxy!=null)
			proxy.preInit(event);
	}

	@EventHandler
	public void init(final @Nonnull FMLInitializationEvent event) {
		if (proxy!=null)
			proxy.init(event);
	}

	@EventHandler
	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		if (proxy!=null)
			proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(final @Nonnull FMLServerStartingEvent event) {
		if (proxy!=null)
			proxy.serverStarting(event);
	}
}