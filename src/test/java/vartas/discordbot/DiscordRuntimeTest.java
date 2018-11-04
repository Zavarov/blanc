/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discordbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import javax.security.auth.login.LoginException;
import net.dean.jraw.http.NetworkAdapter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import vartas.OfflineInstance;
import vartas.OfflineJDA;
import vartas.OfflineJDABuilder;
import vartas.offlinejraw.OfflineNetworkAdapter;
import vartas.reddit.RedditBot;
import vartas.xml.XMLConfig;
import vartas.xml.XMLCredentials;

/**
 *
 * @author u/Zavarov
 */
public class DiscordRuntimeTest {
    static String xml = 
"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
"<server>\n" +
"    <entry row=\"reddit\" column=\"subreddit\">\n" +
"        <document>\n" +
"            <entry>1</entry>\n" +
"        </document>\n" +
"    </entry>\n" +
"</server>";
    static long time;
    static Function<XMLCredentials,JDABuilder> builder;
    static Function<XMLCredentials,NetworkAdapter> adapter;
    
    OfflineInstance instance;
    DiscordRuntime runtime;
    RedditBot reddit;
    
    @BeforeClass
    public static void setBuilder(){
        time = DiscordRuntime.SLEEP;
        builder = DiscordRuntime.BUILDER;
        adapter = DiscordRuntime.ADAPTER;
        DiscordRuntime.SLEEP = 1;
        DiscordRuntime.BUILDER = (c) -> new OfflineJDABuilder(AccountType.BOT);
        DiscordRuntime.ADAPTER = (c) -> new OfflineNetworkAdapter();
    }
    @AfterClass
    public static void tearDownBuilder(){
        DiscordRuntime.SLEEP = time;
        DiscordRuntime.BUILDER = builder;
        DiscordRuntime.ADAPTER = adapter;
    }
    @Before
    public void setUp() throws LoginException, InterruptedException, IOException, ClassNotFoundException{
        runtime = new DiscordRuntime();
        instance = new OfflineInstance();
        //Delete the guilds folder
        File file = new File("config/guilds");
        if(file.exists()){
            for(File guild : file.listFiles()){
                guild.delete();
            }
            file.delete();
        }
    }
    @After
    public void cleanUp(){
        //Remove any changes
        File file = new File("config/guilds");
        if(file.exists()){
            for(File guild : file.listFiles()){
                guild.delete();
            }
            file.delete();
        }
    }
    @Test
    public void createAdapterTest(){
        XMLCredentials credentials = XMLCredentials.create(new File("src/test/resources/credentials.xml"));
        NetworkAdapter a = adapter.apply(credentials);
        assertEquals(a.getUserAgent().getValue(),"platform:appid:version (by /u/user)");
    }
    @Test
    public void removeOldFilesTest() throws LoginException, IOException, InterruptedException, ClassNotFoundException{
        File file = new File("src/test/resources/guilds/1000.server");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(xml);
        writer.close();
        assertTrue(file.exists());
        file = new File("src/test/resources/guilds/0.server");
        writer = new FileWriter(file);
        writer.write(xml);
        writer.close();
        assertTrue(file.exists());
        
        
        
        runtime = new DiscordRuntime(){
            private static final long serialVersionUID = 1L;
            @Override
            protected DiscordBot createDiscordBot(int shard, JDABuilder builder, XMLConfig config){
                return new DiscordBot(null,instance.jda,config, runtime.reddit, null, null);
            }
        };
        
        file = new File("src/test/resources/guilds/0.server");
        assertTrue(file.exists());
        file.delete();
        file = new File("src/test/resources/1000.server");
        assertFalse(file.exists());
    }
    @Test
    public void getBotTest(){
        for(int i = 0 ; i < instance.config.getDiscordShards() ; ++i){
            runtime.add(new DiscordBot(runtime, new OfflineJDA(), instance.config, runtime.reddit, null, null));
        }
        DiscordBot bot = runtime.getBot(1 << 22);
        assertEquals(runtime.indexOf(bot),1);
    }
    @Test
    public void shutdownTest(){
        runtime.add(new DiscordBot(runtime, new OfflineJDA(), instance.config, runtime.reddit, null, null));
        assertFalse(runtime.isEmpty());
        
        runtime.shutdown();
        assertTrue(runtime.isEmpty());
    }
    @Test
    public void builderTest(){
        XMLCredentials credentials = XMLCredentials.create(new File("src/test/resources/credentials.xml"));
        assertNotNull(builder.apply(credentials).toString());
    }
    @Test
    public void requestBotFromFeedTest() throws IOException{
        File file = new File("src/test/resources/guilds/0.server");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(xml);
        writer.close();
        assertTrue(file.exists());
        
        runtime.feed.removeFeed("subreddit", instance.channel1);
        
        assertTrue(runtime.get(0).getServer(instance.guild).isEmpty());
    }
}