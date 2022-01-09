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

import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Role;
import zav.discord.blanc.databind.RoleDto;

/**
 * Implementation of a role view, backed by JDA.
 */
public class JdaRole implements zav.discord.blanc.api.Role {
  @Inject
  private Role jdaRole;
  
  @Override
  public RoleDto getAbout() {
    return new RoleDto().withId(jdaRole.getIdLong())
          .withName(jdaRole.getName());
  }
  
  @Override
  public String getAsMention() {
    return jdaRole.getAsMention();
  }
}
