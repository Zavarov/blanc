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

package zav.discord.blanc.command.internal.resolver;

import net.dv8tion.jda.api.entities.Message;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.api.Rank;

/**
 * Derives a rank based on its (unique) name. Capitalization is ignored.
 */
@NonNullByDefault
public class RankResolver implements EntityResolver<Rank> {
  @Override
  public @Nullable Rank apply(Parameter parameter, Message message) {
    try {
      return parameter.asString().map(String::toUpperCase).map(Rank::valueOf).orElse(null);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}