/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal;

import java.awt.Color;
import java.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import zav.discord.blanc.databind.message.FieldDto;
import zav.discord.blanc.databind.message.MessageEmbedDto;
import zav.discord.blanc.databind.message.PageDto;

/**
 * Utility class for creating humanly-readable embedded messages from data transfer objects.
 */
public final class MessageEmbedUtils {
  
  private MessageEmbedUtils() {
  
  }
  
  public static MessageEmbed forPage(PageDto page) {
    return forEmbed((MessageEmbedDto) page.getContent());
  }
  
  /**
   * Translates a message embed DTO into a JDA message embed.
   *
   * @param messageEmbed A message embed DTO.
   * @return A JDA message embed.
   */
  public static MessageEmbed forEmbed(MessageEmbedDto messageEmbed) {
    EmbedBuilder builder = new EmbedBuilder();
  
    messageEmbed.getThumbnail().ifPresent(builder::setThumbnail);
    messageEmbed.getContent().ifPresent(builder::setDescription);
    messageEmbed.getTimestamp().map(Date::toInstant).ifPresent(builder::setTimestamp);
    
    builder.setColor(messageEmbed.getColor().map(Color::getColor).orElse(Color.BLACK));
    
    messageEmbed.getTitle().ifPresent(title -> {
      builder.setTitle(title.getName(), title.getUrl().orElse(null));
    });
  
    messageEmbed.getAuthor().ifPresent(author -> {
      builder.setTitle(author.getName(), author.getUrl().orElse(null));
    });
  
    for (FieldDto field : messageEmbed.getFields()) {
      builder.addField(field.getName().toString(), field.getContent(), false);
    }
  
    return builder.build();
  }
}