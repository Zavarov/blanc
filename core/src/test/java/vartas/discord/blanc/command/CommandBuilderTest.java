/*
 * Copyright (c) 2020 Zavarov
 *
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

package vartas.discord.blanc.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vartas.discord.blanc.*;
import vartas.discord.blanc.factory.*;
import vartas.discord.blanc.mock.*;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandBuilderTest extends AbstractTest {
    Member author;
    Guild guild;
    ParserMock parser;
    Message privateMessage;
    Message guildMessage;
    PrivateChannel privateChannel;
    TextChannel textChannel;
    IntermediateCommandMock privateIntermediateCommand;
    IntermediateCommandMock guildIntermediateCommand;
    MessageCommandMock privateCommand;
    GuildCommandMock guildCommand;
    CommandBuilderMock commandBuilder;
    @BeforeEach
    public void setUp(){
        parser = new ParserMock();
        commandBuilder = new CommandBuilderMock(parser, "!!");

        author = MemberFactory.create(Rank.USER, 0, "User");
        guild = GuildFactory.create(0, "Guild");

        privateChannel = PrivateChannelFactory.create(0, "PrivateChannel");
        textChannel = TextChannelFactory.create(0, "TextChannel");

        guildMessage = MessageFactory.create(0, Instant.now(), author);
        privateMessage = MessageFactory.create(1, Instant.now(), author);

        privateCommand = new MessageCommandMock(author, privateChannel);
        guildCommand = new GuildCommandMock(author, textChannel, guild);

        privateIntermediateCommand = new IntermediateCommandMock("!!", "private", Collections.emptyList());
        guildIntermediateCommand = new IntermediateCommandMock("!!", "guild", Collections.emptyList());

        commandBuilder.commandTable.put("private", Collections.emptyList(), privateCommand);
        commandBuilder.commandTable.put("guild", Collections.emptyList(), guildCommand);
        parser.commandMap.put(privateMessage, privateIntermediateCommand);
        parser.commandMap.put(guildMessage, guildIntermediateCommand);
    }

    @Test
    public void testBuildInvalidGuildCommand(){
        parser.commandMap.remove(guildMessage);
        assertThat(commandBuilder.build(guildMessage, guild)).isEmpty();

    }

    @Test
    public void testBuildGuildCommand(){
        assertThat(commandBuilder.build(guildMessage, guild)).contains(guildCommand);
    }

    @Test
    public void testBuildGuildCommandWithGuildPrefix(){
        guild.setPrefix("*");
        guildIntermediateCommand.setPrefix("*");
        assertThat(commandBuilder.build(guildMessage, guild)).contains(guildCommand);
    }

    @Test
    public void testBuildGuildCommandWithoutPrefix(){
        guildIntermediateCommand.setPrefix(null);
        assertThat(commandBuilder.build(guildMessage, guild)).isEmpty();
    }

    @Test
    public void testBuildInvalidPrivateCommand(){
        parser.commandMap.remove(privateMessage);
        assertThat(commandBuilder.build(privateMessage)).isEmpty();
    }

    @Test
    public void testBuildPrivateCommand(){
        assertThat(commandBuilder.build(privateMessage)).contains(privateCommand);
    }

    @Test
    public void testBuildPrivateCommandWithoutPrefix(){
        privateIntermediateCommand.setPrefix(null);
        assertThat(commandBuilder.build(privateMessage)).isEmpty();
    }
}