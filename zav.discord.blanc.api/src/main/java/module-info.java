open module zav.discord.blanc.api {
  requires static org.eclipse.jdt.annotation;
  requires static org.jetbrains.annotations;
  
  requires com.google.common;
  requires com.google.guice;
  requires java.inject;
  requires java.sql;
  requires net.dv8tion.jda;
  requires org.apache.commons.lang3;
  requires org.slf4j;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  
  exports zav.discord.blanc.api;
  exports zav.discord.blanc.api.guice;
}