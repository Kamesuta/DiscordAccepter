package net.teamfruit.discordaccepter;

import java.util.Deque;

import javax.annotation.Nonnull;

import com.google.common.collect.Queues;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class CoreHandler {
	public static final CoreHandler instance = new CoreHandler();

	private CoreHandler() {
	}

	public void init() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		Config.getConfig().onConfigChanged(eventArgs);
	}

	@SubscribeEvent
	public void onDraw(final @Nonnull RenderGameOverlayEvent.Post event) {
	}

	private final Deque<Runnable> taskLater = Queues.newArrayDeque();

	@SubscribeEvent
	public void onTick(final @Nonnull ServerTickEvent event) {
		Runnable task;
		while ((task = this.taskLater.poll())!=null)
			task.run();
	}

	public void invokeLater(final @Nonnull Runnable task) {
		this.taskLater.offer(task);
	}
}
