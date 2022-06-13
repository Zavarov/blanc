open module zav.discord.blanc.reddit {
  requires static org.eclipse.jdt.annotation;
  requires static com.github.spotbugs.annotations;
  
  requires com.google.common;
  requires com.google.guice;
  requires discord.webhooks;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.slf4j;
  requires net.dv8tion.jda;
  requires zav.discord.blanc.api;
  requires zav.discord.blanc.databind;
  requires zav.discord.blanc.db;
  requires zav.jrc.api;
  requires zav.jrc.databind;
  requires zav.jcr.listener;
  requires zav.jrc.client;
  requires zav.jrc.endpoint.subreddit;
  
  requires java.desktop;
  requires java.sql;
  
  exports zav.discord.blanc.reddit;
}