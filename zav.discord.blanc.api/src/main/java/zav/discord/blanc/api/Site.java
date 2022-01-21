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

package zav.discord.blanc.api;

import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;

/**
 * Implementation of site.
 */
@NonNullByDefault
public class Site {
  private final List<Page> pages;
  private final User owner;
  private Page currentPage;
  
  private Site(List<Page> pages, User owner) {
    this.pages = pages;
    this.owner = owner;
    this.currentPage = pages.get(0);
  }
  
  /**
   * Creates a new instance of a site over the provided arguments.<br>
   * The argument has to contain at least one element. Furthermore, all sites need to contain at
   * least one page.<br>
   * Only the owner should be allowed to interact with this site.
   *
   * @param pages All sites of this object.
   * @param owner The user for which this site was created.
   * @return A new site instance over the argument.
   */
  public static Site create(List<Page> pages, User owner) {
    Validate.validIndex(pages, 0);
    return new Site(List.copyOf(pages), owner);
  }
  
  public void moveLeft(Consumer<MessageEmbed> consumer) {
    currentPage.index = Math.floorMod(currentPage.index - 1, currentPage.entries.size());
    consumer.accept(currentPage.entries.get(currentPage.index));
  }
  
  public void moveRight(Consumer<MessageEmbed> consumer) {
    currentPage.index = Math.floorMod(currentPage.index + 1, currentPage.entries.size());
    consumer.accept(currentPage.entries.get(currentPage.index));
  }
  
  @Contract(pure = true)
  public User getOwner() {
    return owner;
  }
  
  /**
   * Changes the current page of this site to the one specified by the label.
   *
   * @param label The name of the newly selected page.
   * @param consumer The consumer, updating the MessageEmbed.
   */
  public void changeSelection(String label, Consumer<MessageEmbed> consumer) {
    currentPage = pages.stream()
          .filter(page -> page.label.equals(label))
          .findFirst()
          .orElseThrow();
    
    consumer.accept(currentPage.entries.get(currentPage.index));
  }
  
  /**
   * A page within a given site. Each page contains of multiple entries through which the user
   * can flip.
   */
  public static class Page {
    private final List<MessageEmbed> entries;
    private final String label;
    private int index;
    
    private Page(String label, List<MessageEmbed> entries) {
      this.label = label;
      this.entries = entries;
    }
  
    /**
     * A page must contain at least one entry.
     *
     * @param label The (unique) name of this page.
     * @param entries All entries of this page.
     * @return A new page instance.
     */
    public static Page create(String label, List<MessageEmbed> entries) {
      // Page needs at least one entry
      Validate.validIndex(entries, 0);
      return new Page(label, List.copyOf(entries));
    }
  }
}
