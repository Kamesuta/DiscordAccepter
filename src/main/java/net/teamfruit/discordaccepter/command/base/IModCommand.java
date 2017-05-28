package net.teamfruit.discordaccepter.command.base;

import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public interface IModCommand extends ICommand {
	@Nonnull
	String getFullCommandString();

	@Override
	@Nullable
	List<String> getCommandAliases();

	int getRequiredPermissionLevel();

	@Nonnull
	SortedSet<SubCommand> getChildren();

	@Nullable
	RootCommand getRoot();

	void printHelp(@Nonnull ICommandSender arg0);
}