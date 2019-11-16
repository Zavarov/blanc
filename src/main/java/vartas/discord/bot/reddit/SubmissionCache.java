/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.reddit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.BotGuild;
import vartas.discord.bot.entities.DiscordEnvironment;
import vartas.discord.bot.message.SubmissionMessage;
import vartas.discord.bot.visitor.DiscordEnvironmentVisitor;
import vartas.reddit.SubmissionInterface;
import vartas.reddit.UnresolvableRequestException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SubmissionCache {
    /**
     * The log for this class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     *  Contains the most recent received submissions and
     *  their corresponding Discord message.
     */
    protected Cache<SubmissionInterface, MessageBuilder> cache;
    /**
     * The subreddit the submissions belong to.
     */
    protected String subreddit;
    /**
     * The global environment.
     */
    protected DiscordEnvironment environment;

    /**
     * @param subreddit the subreddit submissions are requested from.
     */
    public SubmissionCache(String subreddit, DiscordEnvironment environment){
        //We only need a few minutes to avoid duplicates
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
        this.subreddit = subreddit;
        this.environment = environment;
    }

    public List<MessageBuilder> retrieve(Instant start, Instant end){
        return cache
                .asMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().getCreated().toInstant().isBefore(end))
                .filter(entry -> !entry.getKey().getCreated().toInstant().isBefore(start))
                .sorted(Comparator.comparing(entry -> entry.getKey().getCreated()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    /**
     * Requests submissions betwee the given intervals and stores them in the local cache.
     * @param start the (exclusive) minimum age of the submissions.
     * @param end the (exclusive) maximum age of the submissions.
     */
    public void request(Instant start, Instant end){
        try{
            log.info("Request submissions from '"+subreddit+"'.");
            Set<SubmissionInterface> submissions = environment.submission(subreddit, start, end).orElseGet(TreeSet::new);
            //Register/Update the new submission and replace any older ones
            submissions.forEach(submission -> cache.put(submission, SubmissionMessage.create(submission)));
        //Submissions are impossible to acccess
        }catch(UnresolvableRequestException e){
            log.error(e.getMessage());
            new RemoveSubredditVisitor().accept();
        }
    }

    private class RemoveSubredditVisitor implements DiscordEnvironmentVisitor{
        public void accept(){
            environment.accept(this);
        }
        @Override
        public void handle(BotGuild group){
            environment.schedule(() -> {
                group.remove(BotGuild.SUBREDDIT, subreddit);
                group.store();
            });
        }
    }
}
