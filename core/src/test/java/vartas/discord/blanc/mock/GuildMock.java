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

package vartas.discord.blanc.mock;

import vartas.discord.blanc.Guild;
import vartas.discord.blanc.Member;
import vartas.discord.blanc.Role;
import vartas.discord.blanc.TextChannel;

import javax.annotation.Nonnull;

public class GuildMock extends Guild {
    @Override
    public Member getMembers(Long key){
        return null;
    }
    @Override
    public TextChannel getChannels(Long key){
        return null;
    }
    @Override
    public void leave(){
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull Role role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull TextChannel textChannel) {
        throw new UnsupportedOperationException();
    }
}