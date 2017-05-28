package net.teamfruit.discordaccepter;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.teamfruit.discordaccepter.discord.AcceptData;
import net.teamfruit.discordaccepter.discord.MetaIO;

public class Config extends ConfigBase {
	private static @Nullable Config instance;

	public static @Nonnull Config getConfig() {
		if (instance!=null)
			return instance;
		throw new IllegalStateException("config not initialized");
	}

	public static void init(final @Nonnull File cfgFile, final @Nonnull String version) {
		instance = new Config(cfgFile, version);
	}

	public final MetaIO<AcceptData> additional;

	private Config(final @Nonnull File configFile, final @Nonnull String version) {
		super(configFile, version);
		final File additionalFile = new File(getConfigFile().getParentFile(), Reference.MODID+".json");
		this.additional = new MetaIO<AcceptData>(additionalFile, AcceptData.class) {
			@Override
			public AcceptData createBlank() {
				return new AcceptData();
			}
		};
		this.additional.get();
		if (!additionalFile.exists())
			this.additional.save();
		loadAdditional();
	}

	public AcceptData data;

	public AcceptData loadAdditional() {
		return this.data = this.additional.get();
	}

	@Override
	public void reload() {
		final Config config = new Config(getConfigFile(), getDefinedConfigVersion());
		config.loadAdditional();
		instance = config;
	}

	public final ConfigProperty<String> minecraftRunCommand = propertyString(get("Minecraft", "RunCommand", "/whitelist add %playername%"));
	public final ConfigProperty<String> minecraftNoticeMessage = propertyString(get("Minecraft", "NoticeMessage", ""));
	public final ConfigProperty<String> minecraftAcceptedMessage = propertyString(get("Minecraft", "AcceptedMessage", ""));
	public final ConfigProperty<String> discordToken = propertyString(get("Discord", "Token", ""));
	public final ConfigProperty<Integer> discordRequiredPower = propertyInteger(get("Discord", "PowerLevel", 10));
	public final ConfigProperty<String> discordVoteReaction = propertyString(get("Discord", "VoteReactionID", "\u2705"));
	public final ConfigProperty<String> discordPrefix = propertyString(get("Discord", "Prefix", "/req "));
	public final ConfigProperty<String> discordNoticeMessage = propertyString(get("Discord", "NoticeMessage", "Whitelist request from %playername% (%username%)."));
	public final ConfigProperty<String> discordAcceptedMessage = propertyString(get("Discord", "AcceptedMessage", "%playername% (%username%) has been accepted!"));
}
