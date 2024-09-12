open module zav.discord.blanc.databind {
  requires static com.fasterxml.jackson.annotation;
  requires static org.jetbrains.annotations;
  requires static lombok;

  requires transitive jakarta.persistence;
  requires transitive net.dv8tion.jda;

  exports zav.discord.blanc.databind;
}