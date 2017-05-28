package net.teamfruit.discordaccepter.command;

import javax.annotation.Nonnull;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.teamfruit.discordaccepter.ChatBuilder;
import net.teamfruit.discordaccepter.Config;
import net.teamfruit.discordaccepter.command.base.RootCommand;
import net.teamfruit.discordaccepter.command.base.SubCommand;
import net.teamfruit.discordaccepter.discord.DiscordBase;

public class DiscordAccepterCommands extends RootCommand {
	public final static @Nonnull DiscordAccepterCommands instance = new DiscordAccepterCommands();

	private DiscordAccepterCommands() {
		super("discordaccepter", "discordaccepter");
		addChildCommand(new Reload());
		addChildCommand(new Setup());
	}

	public static class Reload extends SubCommand {
		public Reload() {
			super("reload");
			setPermLevel(PermLevel.ADMIN);
		}

		@Override
		public void processSubCommand(final ICommandSender sender, final String[] args) {
			Config.getConfig().reload();
			DiscordBase.instance.init();
			ChatBuilder.create("Config Reloaded!").sendPlayer(sender);
		}
	}

	public static class Setup extends SubCommand {
		public Setup() {
			super("setup");
			setPermLevel(PermLevel.ADMIN);
		}

		@Override
		public void processSubCommand(final ICommandSender sender, final String[] args) throws WrongUsageException {
			if (args.length<=0)
				throw new WrongUsageException("Missing argument /"+getFullCommandString()+" <token>");
			Config.getConfig().discordToken.set(args[0]);
			Config.getConfig().reload();
			final boolean status = DiscordBase.instance.init();
			ChatBuilder.create("Token has been set up! ["+(status ? "Enabled" : "Disabled")+"]").sendPlayer(sender);
		}
	}
}
