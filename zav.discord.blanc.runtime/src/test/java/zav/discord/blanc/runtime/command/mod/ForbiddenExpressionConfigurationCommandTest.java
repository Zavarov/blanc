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

package zav.discord.blanc.runtime.command.mod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.runtime.command.AbstractGuildCommandTest;

/**
 * Checks whether the interactive configuration message is returned.
 */
public class ForbiddenExpressionConfigurationCommandTest extends AbstractGuildCommandTest {
  private @Mock InteractionHook hook;
  private @Mock RestAction<Message> action;
  private @Mock Message message;
  
  @Test
  public void testRun() throws Exception {
    update(guildTable, guildEntity, e -> {});
    when(guild.getId()).thenReturn(Long.toString(guildEntity.getId()));
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    when(reply.complete()).thenReturn(hook);
    when(hook.retrieveOriginal()).thenReturn(action);
    when(action.complete()).thenReturn(message);
  
    run(ForbiddenExpressionConfigurationCommand.class);
  
    assertThat(siteCache.size()).isNotZero();
  }
}
