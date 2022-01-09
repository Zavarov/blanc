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

package zav.discord.blanc.jda.api;

import static org.assertj.core.api.Assertions.assertThat;
import static zav.discord.blanc.api.Permission.MANAGE_MESSAGES;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectMember;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.jda.AbstractTest;

/**
 * JUnit test for checking whether guild members are properly instantiated using Guice.
 */
public class JdaMemberTest extends AbstractTest {
  private JdaMember member;
  
  /**
   * Initializes {@link #member} and mocks the required JDA components.
   */
  @BeforeEach
  public void setUp() {
    member = injectMember(jdaMember);
  }
  
  @Test
  public void testGetPermissions() {
    assertThat(member.getPermissions()).containsExactly(MANAGE_MESSAGES);
  }
}
