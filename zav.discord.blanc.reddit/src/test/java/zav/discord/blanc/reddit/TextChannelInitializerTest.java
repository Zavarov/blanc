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

package zav.discord.blanc.reddit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.TextChannelTable;

/**
 * Checks whether listeners are created for all valid registered text channels in the database.
 */
@ExtendWith(MockitoExtension.class)
public class TextChannelInitializerTest {
  
  @Mock TextChannelTable db;
  @Mock SubredditObservable observable;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  @Mock TextChannelEntity entity;
  TextChannelInitializer initializer;
  
  @BeforeEach
  public void setUp() {
    initializer = new TextChannelInitializer(db, observable);
  }
  
  @Test
  public void testLoad() throws SQLException {
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    when(db.get(textChannel)).thenReturn(Optional.of(entity));
    when(entity.getSubreddits()).thenReturn(List.of("RedditDev"));
    
    initializer.load(guild);
    
    verify(observable).addListener("RedditDev", textChannel);
  }
}
