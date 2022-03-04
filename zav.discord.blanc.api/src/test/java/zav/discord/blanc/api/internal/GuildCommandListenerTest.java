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

package zav.discord.blanc.api.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Command;

/**
 * Checks whether guild messages are able to produce (guild) commands.
 */
@ExtendWith(MockitoExtension.class)
public class GuildCommandListenerTest extends AbstractListenerTest {
  
  @Mock Command command;
  @Mock TextChannel textChannel;
  @Mock User author;
  @Mock MessageAction action;
  @Mock GuildMessageReceivedEvent event;

  GuildCommandListener listener;
  
  /**
   * Creates a guild command listener with a valid guild message received event.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    listener = injector.getInstance(GuildCommandListener.class);
  }
  
  @Test
  public void testExecuteCommand() throws Exception {
    when(event.getAuthor()).thenReturn(author);
    when(parser.parse(any(GuildMessageReceivedEvent.class))).thenReturn(Optional.of(command));
  
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
    
    listener.onGuildMessageReceived(event);
    
    verify(command, times(1)).postConstruct();
    verify(command, times(1)).validate();
    verify(command, times(1)).run();
  }
  
  @Test
  public void testExecuteCommandWithError() throws Exception {
    when(event.getAuthor()).thenReturn(author);
    when(event.getChannel()).thenReturn(textChannel);
    when(parser.parse(any(GuildMessageReceivedEvent.class))).thenReturn(Optional.of(command));
    when(textChannel.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(action);
  
    doThrow(new Exception("message", null)).when(command).run();
  
    doAnswer(invocation -> {
      Runnable job = invocation.getArgument(0);
      job.run();
      return null;
    }).when(queue).submit(any(Runnable.class));
  
    listener.onGuildMessageReceived(event);
  
    verify(textChannel, times(1)).sendMessageEmbeds(any(MessageEmbed.class));
  }
  
  @Test
  public void testIgnoreBotMessages() {
    when(event.getAuthor()).thenReturn(author);
    // Bot message -> ignore
    when(author.isBot()).thenReturn(true);
  
    listener.onGuildMessageReceived(event);
  
    verifyNoInteractions(queue);
  }
  
  @Test
  public void testIgnoreInvalidCommands() {
    when(event.getAuthor()).thenReturn(author);
    // Message not a command
    when(parser.parse(any(GuildMessageReceivedEvent.class))).thenReturn(Optional.empty());
  
    listener.onGuildMessageReceived(event);
  
    verifyNoInteractions(queue);
  }
}
