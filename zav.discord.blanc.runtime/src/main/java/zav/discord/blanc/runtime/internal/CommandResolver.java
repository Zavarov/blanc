/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.runtime.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.runtime.command.core.MathCommand;
import zav.discord.blanc.runtime.command.core.SupportCommand;
import zav.discord.blanc.runtime.command.dev.FailsafeCommand;
import zav.discord.blanc.runtime.command.dev.KillCommand;
import zav.discord.blanc.runtime.command.dev.RankCommand;
import zav.discord.blanc.runtime.command.dev.SayCommand;
import zav.discord.blanc.runtime.command.dev.StatusCommand;
import zav.discord.blanc.runtime.command.mod.BlacklistCommand;
import zav.discord.blanc.runtime.command.mod.ForbiddenExpressionConfigurationCommand;
import zav.discord.blanc.runtime.command.mod.SubredditFeedConfigurationCommand;
import zav.discord.blanc.runtime.command.mod.TextChannelRedditCommand;
import zav.discord.blanc.runtime.command.mod.WebhookRedditCommand;

/**
 * Utility class for mapping each command class to their name.
 */
public class CommandResolver {
  /**
   * Initializes all supported commands.
   */
  public static void init() {
    Commands.bind("math", MathCommand.class);
    Commands.bind("support", SupportCommand.class);
    
    Commands.bind("mod.blacklist", BlacklistCommand.class);
    Commands.bind("mod.config.blacklist", ForbiddenExpressionConfigurationCommand.class);
    Commands.bind("mod.config.reddit", SubredditFeedConfigurationCommand.class);
    Commands.bind("mod.reddit.textchannel", TextChannelRedditCommand.class);
    Commands.bind("mod.reddit.webhook", WebhookRedditCommand.class);
  
    Commands.bind("dev.failsafe", FailsafeCommand.class);
    Commands.bind("dev.kill", KillCommand.class);
    Commands.bind("dev.rank", RankCommand.class);
    Commands.bind("dev.say", SayCommand.class);
    Commands.bind("dev.status", StatusCommand.class);
  }
  
  /**
   * Deserializes all available commands from disk.
   *
   * @return A list of all commands supported by the bot.
   */
  public static List<CommandData> getCommands() {
    ClassLoader cl = CommandResolver.class.getClassLoader();
    
    // Core Commands
    InputStream is = cl.getResourceAsStream("Commands.json");
    Objects.requireNonNull(is);
    List<CommandData> result = new ArrayList<>(CommandData.fromList(DataArray.fromJson(is)));
    
    // Developer Commands
    is = cl.getResourceAsStream("DeveloperCommands.json");
    Objects.requireNonNull(is);
    result.add(CommandData.fromData(DataObject.fromJson(is)));
    
    // Mod Commands
    is = cl.getResourceAsStream("ModCommands.json");
    Objects.requireNonNull(is);
    result.add(CommandData.fromData(DataObject.fromJson(is)));
    
    return result;
  }
}
