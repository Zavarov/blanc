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
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * Test case for the User database.<br>
 * Verifies that entries are written and read correctly.
 */
public class UserTableTest extends AbstractTableTest {
  
  UserTable db;
  UserEntity entity;
  
  @Mock User user;
  
  /**
   * Deserializes Discord user and initializes database.
   *
   * @throws Exception If the database couldn't be initialized.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  
    db = new UserTable(query);
    db.postConstruct();
    
    entity = read("User.json", UserEntity.class);
  }
  
  @Test
  public void testPut() throws SQLException {
    when(user.getIdLong()).thenReturn(entity.getId());
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(user)).map(UserEntity::getName).contains(entity.getName());
  
    entity.setName("NotUser");
  
    assertEquals(db.put(entity), 1);
    assertThat(db.get(user)).map(UserEntity::getName).contains(entity.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    when(user.getIdLong()).thenReturn(entity.getId());
  
    assertEquals(db.put(entity), 1);
    assertEquals(db.delete(user), 1);
    assertEquals(db.delete(user), 0);
  }
  
  @Test
  public void testGet() throws SQLException {
    db.put(entity);
  
    assertThat(db.get(user)).isEmpty();
    when(user.getIdLong()).thenReturn(entity.getId());
    assertThat(db.get(user)).contains(entity);
  }
}
