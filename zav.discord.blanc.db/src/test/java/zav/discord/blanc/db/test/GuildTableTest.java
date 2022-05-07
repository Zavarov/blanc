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

package zav.discord.blanc.db.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;

/**
 * Test case for the Guild database.<br>
 * Verifies that entries are written and read correctly.
 */
public class GuildTableTest extends AbstractTableTest {
  
  GuildTable db;
  GuildEntity entity;
  
  @Mock Guild guild;
  
  /**
   * Deserializes Discord guild and initializes database.
   *
   * @throws Exception If the database couldn't be initialized.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    db = new GuildTable();
    db.setSqlQuery(query);
    db.postConstruct();
  
    entity = read("Guild.json", GuildEntity.class);
  }
  
  @Test
  public void testPut() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getId());
    
    assertEquals(db.put(entity), 1);
    assertThat(db.get(guild)).map(GuildEntity::getName).contains(entity.getName());
    
    entity.setName("NotGuild");
    
    assertEquals(db.put(entity), 1);
    assertThat(db.get(guild)).map(GuildEntity::getName).contains(entity.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    when(guild.getIdLong()).thenReturn(entity.getId());
    
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(guild), 1);
    assertEquals(db.delete(guild), 0);
  }
  
  @Test
  public void testGet() throws SQLException {
    db.put(entity);
    
    assertThat(db.get(guild)).isEmpty();
    when(guild.getIdLong()).thenReturn(entity.getId());
    assertThat(db.get(guild)).contains(entity);
  }
}