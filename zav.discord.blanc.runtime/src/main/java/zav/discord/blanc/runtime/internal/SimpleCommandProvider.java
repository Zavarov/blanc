package zav.discord.blanc.runtime.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandProvider;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.runtime.command.core.MathCommand;
import zav.discord.blanc.runtime.command.core.SupportCommand;
import zav.discord.blanc.runtime.command.dev.FailsafeCommand;
import zav.discord.blanc.runtime.command.dev.KillCommand;
import zav.discord.blanc.runtime.command.dev.SayCommand;
import zav.discord.blanc.runtime.command.dev.StatusCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistAddCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistInfoCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistRemoveCommand;
import zav.discord.blanc.runtime.command.mod.LegacyRedditInfoCommand;
import zav.discord.blanc.runtime.command.mod.LegacyRedditRemoveCommand;
import zav.discord.blanc.runtime.command.mod.RedditAddCommand;
import zav.discord.blanc.runtime.command.mod.RedditRemoveCommand;

/**
 * Implementation for all available commands. Commands are grouped into three categories. Normal
 * user commands, which can be executed by everyone. Moderator commands which can only be executed
 * within a guild by members with the required permissions. Developer commands, which can only be
 * executed by bot developers.
 */
public class SimpleCommandProvider implements CommandProvider {
  
  @Override
  public Optional<Command> create(Client client, SlashCommandEvent event) {
    if (event.isFromGuild()) {
      return Optional.ofNullable(createGuildCommand(client, event));
    } else {
      return Optional.ofNullable(createCommand(client, event));
    }
  }
  
  /**
   * Creates the guild-less command corresponding to the slash event.
   *
   * @param client The Discord instance.
   * @param event The event from which the command is created.
   * @return The created command or {@code null}, if no matching command is found.
   */
  private static Command createCommand(Client client, SlashCommandEvent event) {
    CommandManager manager = new CommandManager(client, event);
    
    switch (getQualifiedName(event)) {
      case "math":
        return new MathCommand(event, manager);
      case "support":
        return new SupportCommand(event, manager);
      case "dev.failsafe":
        return new FailsafeCommand(event, manager);
      case "dev.kill":
        return new KillCommand(event, manager);
      case "dev.say":
        return new SayCommand(event, manager);
      case "dev.status":
        return new StatusCommand(event, manager);
      default:
        return null;
    }
  }
  
  /**
   * Creates the guild command corresponding to the slash event. If no matching guild command is
   * found, a guild-less command is created.
   *
   * @param client The Discord instance.
   * @param event The event from which the command is created.
   * @return The created command or {@code null}, if no matching command is found.
   */
  private static Command createGuildCommand(Client client, SlashCommandEvent event) {
    GuildCommandManager manager = new GuildCommandManager(client, event);
    
    switch (getQualifiedName(event)) {
      case "mod.blacklist.add":
        return new BlacklistAddCommand(event, manager);
      case "mod.blacklist.remove":
        return new BlacklistRemoveCommand(event, manager);
      case "mod.blacklist.info":
        return new BlacklistInfoCommand(event, manager);
      case "mod.reddit.add":
        return new RedditAddCommand(event, manager);
      case "mod.reddit.remove":
        return new RedditRemoveCommand(event, manager);
      case "mod.reddit_legacy.remove":
        return new LegacyRedditRemoveCommand(event, manager);
      case "mod.reddit_legacy.info":
        return new LegacyRedditInfoCommand(event, manager);
      default:
        // Guild commands are also normal commands
        return createCommand(client, event);
    }
  }
  
  /**
   * The qualified name is derived from the command group, subcommand group and subcommand name.
   *
   * @param event The event from which the command is created.
   * @return The fully qualified command name.
   */
  @Contract(pure = true)
  private static String getQualifiedName(SlashCommandEvent event) {
    List<String> parts = new ArrayList<>(3);
    
    parts.add(event.getName());
  
    Optional.ofNullable(event.getSubcommandGroup()).ifPresent(parts::add);
    Optional.ofNullable(event.getSubcommandName()).ifPresent(parts::add);
    return String.join(".", parts);
  }
}