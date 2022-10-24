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

package zav.discord.blanc.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This test case checks whether the pattern cache is able to retrieve the persisted entity as well
 * as update then when necessary.
 */
@ExtendWith(MockitoExtension.class)
public class PatternCacheTest {
  
  EntityManagerFactory factory;
  EntityManager entityManager;
  GuildEntity entity;
  PatternCache cache;
  @Mock Guild guild;
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  /**
   * Initializes the pattern cache. The cache will load a single guild entity, containing both the
   * words {@code banana} and {@code pizza} as blacklisted expressions.
   */
  @BeforeEach
  public void setUp() {
    factory = Persistence.createEntityManagerFactory("discord-entities");
    cache = new PatternCache(factory);
    entityManager = factory.createEntityManager();
    entity = new GuildEntity();
    entity.setId(1000L);
    entity.setBlacklist(List.of("banana", "pizza"));
    
    entityManager.getTransaction().begin();
    entityManager.merge(entity);
    entityManager.getTransaction().commit();
  }
  
  @AfterEach
  public void tearDown() {
    entityManager.close();
  }
  
  /**
   * Use Case: The cached entity should be invalidated whenever the entity has changed.
   */
  @Test
  public void testInvalidate() {
    when(guild.getIdLong()).thenReturn(entity.getId());

    // Modify entity and store it in the database
    entity.setBlacklist(Collections.emptyList());
    entityManager.getTransaction().begin();
    entityManager.merge(entity);
    entityManager.getTransaction().commit();
    
    // Invalidate the cached entity
    cache.invalidate(guild);
    
    assertTrue(cache.get(guild).isEmpty());
  }
  
  /**
   * Use Case: Fetch the pattern from the database, if available.
   */
  @Test
  public void testGet() {
    when(guild.getIdLong()).thenReturn(entity.getId());
    assertTrue(cache.get(mock(Guild.class)).isEmpty());
    assertEquals(cache.get(guild).orElseThrow().toString(), "banana|pizza");
  }
}