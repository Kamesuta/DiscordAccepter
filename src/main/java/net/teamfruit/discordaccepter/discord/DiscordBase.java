package net.teamfruit.discordaccepter.discord;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.teamfruit.discordaccepter.ChatBuilder;
import net.teamfruit.discordaccepter.Config;
import net.teamfruit.discordaccepter.CoreHandler;
import net.teamfruit.discordaccepter.Log;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.ReactionRemoveEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

public class DiscordBase {
	private IDiscordClient client;
	public static final DiscordBase instance = new DiscordBase();

	public DiscordBase() {
	}

	public boolean init() {
		if (this.client!=null)
			return true;
		String token = Config.getConfig().discordToken.get();
		if (StringUtils.isEmpty(token)) {
			Config.getConfig().reload();
			token = Config.getConfig().discordToken.get();
			if (StringUtils.isEmpty(token)) {
				Log.log.warn("Invalid Token! Please set token in Config File!");
				// ChatBuilder.sendServer(ChatBuilder.create("[\"\",{\"text\":\"The token has not been set yet.\",\"color\":\"red\"},{\"text\":\" [\"},{\"text\":\"Set token\",\"color\":\"yellow\",\"insertion\":\"/discordaccepter setup\"},{\"text\":\"]\"}]")
				// 		.useJson());
				return false;
			}
		}
		this.client = createClient(token, true);
		if (this.client!=null) {
			final EventDispatcher dispatcher = this.client.getDispatcher();
			dispatcher.registerListener(this);
			return true;
		}
		Log.log.warn("Could not connect to Discord with token: "+token);
		return false;
	}

	public static IDiscordClient createClient(final String token, final boolean login) { // Returns a new instance of the Discord client
		final ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
		clientBuilder.withToken(token); // Adds the login info to the builder
		try {
			if (login)
				return clientBuilder.login(); // Creates the client instance and logs the client in
			else
				return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
		} catch (final DiscordException e) { // This is thrown if there was a problem building the client
			e.printStackTrace();
			return null;
		}
	}

	@EventSubscriber
	public void onMessageReceivedEvent(final MessageReceivedEvent event) { // This method is NOT called because it doesn't have the @EventSubscriber annotation
		try {
			final IMessage msg = event.getMessage();
			final IChannel channel = msg.getChannel();

			final String prefix = Config.getConfig().discordPrefix.get();
			final String msgstr = msg.getContent();

			if (StringUtils.startsWith(msg.getContent(), Config.getConfig().discordPrefix.get())) {
				final Set<String> acceptChannel = Config.getConfig().data.acceptChannel;
				if (acceptChannel.isEmpty()||acceptChannel.contains(channel.getID())) {
					final String playername = StringUtils.trimToEmpty(StringUtils.substringAfter(msgstr, prefix));
					final String username = msg.getAuthor().getName();
					final String discordMsg = Config.getConfig().discordNoticeMessage.get();
					RetryRunner.retry(() -> msg.addReaction(Config.getConfig().discordVoteReaction.get()));
					if (!StringUtils.isEmpty(discordMsg))
						RetryRunner.retry(() -> channel.sendMessage(
								StringUtils.replaceEach(discordMsg,
										new String[] { "%playername%", "%username%" },
										new String[] { playername, username })));
					final String minecraftMsg = Config.getConfig().minecraftNoticeMessage.get();
					if (!StringUtils.isEmpty(minecraftMsg))
						CoreHandler.instance.invokeLater(() -> ChatBuilder.sendServer(ChatBuilder.create(
								StringUtils.replaceEach(minecraftMsg,
										new String[] { "%playername%", "%username%" },
										new String[] { playername, username }))));
				}
			}
		} catch (final Exception e) {
			Log.log.error(e.getMessage(), e);
		}
	}

	private static final Map<Integer, String> nums = Maps.newTreeMap();
	static {
		nums.put(0, "\u0030\u20E3");
		nums.put(1, "\u0031\u20E3");
		nums.put(2, "\u0032\u20E3");
		nums.put(3, "\u0033\u20E3");
		nums.put(4, "\u0034\u20E3");
		nums.put(5, "\u0035\u20E3");
		nums.put(6, "\u0036\u20E3");
		nums.put(7, "\u0037\u20E3");
		nums.put(8, "\u0038\u20E3");
		nums.put(9, "\u0039\u20E3");
		nums.put(10, "\u2611");
	}

	@EventSubscriber
	public void onReactionAdded(final ReactionAddEvent event) {
		try {
			final IReaction reaction = event.getReaction();

			if (reaction.isCustomEmoji()&&reaction.getCustomEmoji()==null)
				return;

			if (!StringUtils.equals(reaction.toString(), Config.getConfig().discordVoteReaction.get()))
				return;

			final IMessage msg = reaction.getMessage();
			final IChannel channel = msg.getChannel();
			final IUser user = event.getClient().getOurUser();

			final String prefix = Config.getConfig().discordPrefix.get();
			final String msgstr = msg.getContent();

			if (StringUtils.startsWith(msgstr, prefix)) {
				final Set<String> acceptChannel = Config.getConfig().data.acceptChannel;
				if (acceptChannel.isEmpty()||acceptChannel.contains(channel.getID())) {
					final int maxpower = Config.getConfig().discordRequiredPower.get();
					final int power = getPower(reaction);
					if (power>=maxpower) {
						updateNums(msg, user, DiscordBase.nums.get(10));

						final String command = Config.getConfig().minecraftRunCommand.get();
						final String playername = StringUtils.trimToEmpty(StringUtils.substringAfter(msgstr, prefix));
						final String username = msg.getAuthor().getName();
						if (!StringUtils.isEmpty(command)&&!StringUtils.isEmpty(playername))
							CoreHandler.instance.invokeLater(new Runnable() {
								@Override
								public void run() {
									final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
									server.getCommandManager().executeCommand(server,
											StringUtils.replaceEach(command,
													new String[] { "%playername%", "%username%" },
													new String[] { playername, username }));
								}
							});
						final String discordMsg = Config.getConfig().discordAcceptedMessage.get();
						if (!StringUtils.isEmpty(discordMsg))
							RetryRunner.retry(() -> channel.sendMessage(
									StringUtils.replaceEach(discordMsg,
											new String[] { "%playername%", "%username%" },
											new String[] { playername, username })));
						final String minecraftMsg = Config.getConfig().minecraftAcceptedMessage.get();
						if (!StringUtils.isEmpty(minecraftMsg))
							CoreHandler.instance.invokeLater(() -> ChatBuilder.sendServer(ChatBuilder.create(
									StringUtils.replaceEach(minecraftMsg,
											new String[] { "%playername%", "%username%" },
											new String[] { playername, username }))));
					} else {
						final String emoji = DiscordBase.nums.get(Math.max(Math.min(power*10/maxpower, 10), 0));
						updateNums(msg, user, emoji);
					}
				}
			}

		} catch (final RateLimitException e) {
			Log.log.warn(e);
		} catch (final Exception e) {
			Log.log.error(e.getMessage(), e);
		}
	}

	@EventSubscriber
	public void onReactionRemoved(final ReactionRemoveEvent event) {
		try {
			final IReaction reaction = event.getReaction();

			if (reaction.isCustomEmoji()&&reaction.getCustomEmoji()==null)
				return;

			if (!StringUtils.equals(reaction.toString(), Config.getConfig().discordVoteReaction.get()))
				return;

			final IMessage msg = reaction.getMessage();
			final IUser user = event.getClient().getOurUser();

			final int maxpower = Config.getConfig().discordRequiredPower.get();
			final int power = getPower(reaction);
			final String emoji = DiscordBase.nums.get(Math.max(Math.min(power*10/maxpower, 10), 0));
			updateNums(msg, user, emoji);
		} catch (final RateLimitException e) {
			Log.log.warn(e);
		} catch (final Exception e) {
			Log.log.error(e.getMessage(), e);
		}
	}

	@EventSubscriber
	public void onReadyEvent(final ReadyEvent event) {
		try {
		} catch (final Exception e) {
			Log.log.error(e.getMessage(), e);
		}
	}

	public static int getPower(final IReaction reaction) throws RateLimitException, DiscordException {
		int power = 0;
		final AcceptData data = Config.getConfig().data;
		for (final IUser user : reaction.getUsers()) {
			final String userid = user.getID();
			for (final Entry<String, Integer> entry : data.userPower.entrySet())
				if (StringUtils.equalsIgnoreCase(userid, entry.getKey()))
					power += entry.getValue();
			for (final IRole role : user.getRolesForGuild(reaction.getMessage().getGuild())) {
				final String roleid = role.getID();
				for (final Entry<String, Integer> entry : data.rolePower.entrySet())
					if (StringUtils.equalsIgnoreCase(roleid, entry.getKey()))
						power += entry.getValue();
			}
		}
		return power;
	}

	public static boolean removeNums(final IMessage msg, final IUser user, final String except) {
		boolean ignored = false;
		for (final IReaction r : msg.getReactions()) {
			if (StringUtils.equals(except, r.toString())) {
				ignored = true;
				continue;
			}
			if (r.getUserReacted(user)&&nums.containsValue(r.toString()))
				RetryRunner.retry(() -> msg.removeReaction(user, r));
		}
		return ignored;
	}

	public static void updateNums(final IMessage msg, final IUser user, final String newemoji) {
		if (!removeNums(msg, user, newemoji)&&newemoji!=null)
			RetryRunner.retry(() -> msg.addReaction(newemoji));
	}

	public static class RetryRunner {
		private static RetryRunner instance = new RetryRunner();

		private RetryRunner() {
			new Thread() {
				@Override
				public void run() {
					try {
						RetryRunner.this.run();
						Thread.sleep(100);
					} catch (final Exception e) {
					}
				}
			}.start();
		}

		private ConcurrentLinkedQueue<RetryRunnable> queue = Queues.newConcurrentLinkedQueue();

		private void run() throws Exception {
			RetryRunnable task = null;
			while ((task = this.queue.poll())!=null)
				try {
					task.run();
				} catch (final RateLimitException e) {
					addTask(task);
				} catch (final Exception e) {
					break;
				}
		}

		private void addTask(final RetryRunnable task) {
			this.queue.offer(task);
		}

		public static void retry(final RetryRunnable task) {
			try {
				task.run();
			} catch (final Exception e) {
				instance.addTask(task);
			}
		}

		public static interface RetryRunnable {
			void run() throws Exception;

			default int retryCount() {
				return 4;
			}
		}
	}
}
