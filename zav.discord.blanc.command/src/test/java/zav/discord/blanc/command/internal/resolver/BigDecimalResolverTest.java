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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Command;

@ExtendWith(MockitoExtension.class)
public class BigDecimalResolverTest extends AbstractResolverTest {
  
  @Test
  public void testInject() {
    when(parameter.asNumber()).thenReturn(Optional.of(BigDecimal.valueOf(number)));
    
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.field).isEqualTo(BigDecimal.valueOf(number));
  }
  
  @Test
  public void testInjectDefault() {
    PrivateCommand cmd = injector.getInstance(PrivateCommand.class);
    
    assertThat(cmd.defaultField).isNull();
  }
  
  @SuppressWarnings("unused")
  private static class PrivateCommand implements Command {
    @Argument(index = 0)
    private BigDecimal field;
    
    @Argument(index = 1)
    private BigDecimal defaultField;
    
    @Override
    public void run() {}
    
    @Override
    public void validate() {}
  }
}