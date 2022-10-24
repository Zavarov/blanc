package zav.discord.blanc.runtime.command.mod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * Checks whether automatic responses can be removed to the database.
 */
@ExtendWith(MockitoExtension.class)
public class ResponseRemoveCommandTest extends AbstractDatabaseTest<GuildEntity> {
  @Mock OptionMapping index;
  GuildCommandManager manager;
  ResponseRemoveCommand command;
  AutoResponseEntity responseEntity;
  
  /**
   * Initializes the command with a single response.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new GuildEntity());
    
    responseEntity = new AutoResponseEntity();
    responseEntity.setPattern("Hello There");
    responseEntity.setAnswer("General Kenobi");
    
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(entity);
    
    entity.add(responseEntity);
    
    manager = new GuildCommandManager(client, event);
    command = new ResponseRemoveCommand(event, manager);
  }

  /**
   * Use Case: A valid index should be removed from the list of auto responses..
   */
  @Test
  public void testRemoveResponse() {
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(0L);
    
    command.run();
    
    assertEquals(entity.getAutoResponses().size(), 0);

    verify(responseCache).invalidate(guild);
  }

  /**
   * Use Case: The database should be modified when an invalid index is selected.
   */
  @ParameterizedTest
  @ValueSource(longs = {-1, 2})
  public void testIgnoreInvalidIndex(long realIndex) {
    when(event.getOption("index")).thenReturn(index);
    when(index.getAsLong()).thenReturn(realIndex);
    
    command.run();
    
    assertEquals(entity.getAutoResponses().size(), 1);
    assertEquals(entity.getAutoResponses().get(0).getPattern(), "Hello There");
    assertEquals(entity.getAutoResponses().get(0).getAnswer(), "General Kenobi");

    verify(responseCache, times(0)).invalidate(guild);
  }
}