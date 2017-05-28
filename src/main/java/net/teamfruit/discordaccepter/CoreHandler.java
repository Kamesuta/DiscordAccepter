package net.teamfruit.discordaccepter;

import java.util.Deque;

import javax.annotation.Nonnull;

import com.google.common.collect.Queues;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CoreHandler {
	public static final CoreHandler instance = new CoreHandler();

	private CoreHandler() {
	}

	public void init() {
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
