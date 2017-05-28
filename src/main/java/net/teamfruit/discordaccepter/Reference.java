package net.teamfruit.discordaccepter;

import javax.annotation.Nonnull;

/**
 * 参照情報
 * <p>
 * Gradleにより、このクラスの「${}」は書き換えられます
 *
 * @author Kamesuta
 */
public class Reference {
	/** Mod ID */
	public static final @Nonnull String MODID = "discordaccepter";
	/** Modの名前 */
	public static final @Nonnull String NAME = "DiscordAccepter";
	/** バージョン ※書き換えられます */
	public static final @Nonnull String VERSION = "${version}";
	/** Forgeバージョン ※書き換えられます */
	public static final @Nonnull String FORGE = "${forgeversion}";
	/** Minecraftバージョン ※書き換えられます */
	public static final @Nonnull String MINECRAFT = "${mcversion}";
	/** サーバープロキシ */
	public static final @Nonnull String PROXY_SERVER = "net.teamfruit.discordaccepter.CommonProxy";
	/** クライアントプロキシ */
	public static final @Nonnull String PROXY_CLIENT = "net.teamfruit.discordaccepter.ClientProxy";
}
