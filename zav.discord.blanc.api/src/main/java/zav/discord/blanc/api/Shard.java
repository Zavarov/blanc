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

package zav.discord.blanc.api;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Base interface for all functions that are performed on this application.
 */
public interface Shard {
  /**
   * Returns a view over all guilds in this shard.
   *
   * @return An immutable list of guild views.
   */
  Collection<? extends Guild> getGuilds();
  
  /**
   * Returns the Discord user corresponding to this application.
   *
   * @return A user view over this application.
   */
  SelfUser getSelfUser();
  
  Guild getGuild(Argument argument);
  
  User getUser(Argument argument);
  
  Presence getPresence();
  
  void shutdown();
  
  <T extends Runnable> void submit(T job);
  
  <T extends Runnable> void schedule(T job, int period, TimeUnit timeUnit);
}
