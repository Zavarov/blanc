package vartas.discord.bot.io.guild;

import com.google.common.collect.Multimap;
import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.io.guild._ast.ASTGuildArtifact;
import vartas.discord.bot.io.guild._parser.GuildParser;
import vartas.discord.bot.io.guild._symboltable.GuildLanguage;
import vartas.discord.bot.io.guild._symboltable.GuildSymbolTableCreator;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/*
 * Copyright (C) 2019 Zavarov
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
public class GuildConfigurationTest {
    ASTGuildArtifact ast;
    GuildSymbolTableCreator symbolTableCreator;
    GuildConfiguration config;

    @Before
    public void setUp(){
        String source = "src/test/resources/guild.gld";
        File reference = new File("target/test/resources/guild.gld");
        config = GuildHelper.parse(source, reference);
    }

    @Test
    public void testPrefix(){
        assertThat(config.getPrefix()).contains("prefix");
    }

    @Test
    public void testFilter(){
        assertThat(config.getFilter()).containsExactlyInAnyOrder("expression0","expression1","expression2","expression3");
    }

    @Test
    public void testSubreddit(){
        Multimap<String, Long> multimap = config.getRedditFeeds();

        assertThat(multimap.keySet()).hasSize(3);
        assertThat(multimap.keySet()).containsExactlyInAnyOrder("x","y","z");

        assertThat(multimap.get("x")).containsExactlyInAnyOrder(0L, 1L);
        assertThat(multimap.get("y")).containsExactlyInAnyOrder(2L, 3L);
        assertThat(multimap.get("z")).containsExactlyInAnyOrder(4L);
    }

    @Test
    public void testGroup(){
        Multimap<String, Long> multimap = config.getTags();

        assertThat(multimap.keySet()).hasSize(3);
        assertThat(multimap.keySet()).containsExactlyInAnyOrder("a","b","c");

        assertThat(multimap.get("a")).containsExactlyInAnyOrder(5L, 6L);
        assertThat(multimap.get("b")).containsExactlyInAnyOrder(7L, 8L);
        assertThat(multimap.get("c")).containsExactlyInAnyOrder(9L);
    }

    @Test
    public void testUpdatedFile(){
        String source = "target/test/resources/guild.gld";
        File reference = new File("target/test/resources/guild.gld");
        config = GuildHelper.parse(source, reference);

        testFilter();
        testGroup();
        testPrefix();
        testSubreddit();
    }
}