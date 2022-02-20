import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Main {

    private TwitterScraper twitterScraper;
    private CollegeNames collegeNames;
    private DiscordApi api;
    private Twitch twitch;

    static BattlefyScraper battlefyScraper;
    static BattlefyScraper battlefyScrapperStats;

    private HashMap<String, PageHandler> instances_teams;

    public static void main(String[] args) throws IOException, ParseException {
        // Insert your bot's token here
        battlefyScraper = new BattlefyScraper("https://battlefy.com/college-league-of-legends/2022-north-conference/6171f253947ed60d0abb9083/info?infoTab=details");
        battlefyScrapperStats = new BattlefyScraper();

        String token = new String(Files.readAllBytes( Paths.get("src/main/resources/token_key.txt")));
        Main main = new Main();
        main.instances_teams = new HashMap<>();
        main.api = new DiscordApiBuilder().setToken(token).login().join();
        main.twitterScraper = new TwitterScraper();

        main.collegeNames = new CollegeNames();

        //main.twitch = new Twitch(main.collegeNames);

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        main.api.addMessageCreateListener(event -> {
            try {
                main.handleCommand(event);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + main.api.createBotInvite());
    }

    public void handleCommand(MessageCreateEvent event) throws IOException, ParseException {
        //If not an actual user (ie bot wrote the message)
        if (!event.getMessageAuthor().isRegularUser()) {
            return;
        }

        String messageLower = event.getMessageContent().toLowerCase();
        if (messageLower.startsWith("jc help")) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("JC Help", "", "https://www.iconsdb.com/icons/preview/red/question-mark-xxl.png")
                    .setTitle("JC Help")
                    .setDescription("**OverallInfo**\n" +
                            "jc search <University Name or Team Name>\n" +
                            "    - Gets information on search query esports organization.\n" +
                            "\n" +
                            "jc news <University Name or Team Name>\n" +
                            "    - Gets info if there on Universities Announcementt channel. \n" +
                            "\n" +
                            "**Twitter**\n" +
                            "jc trecent <University Name or Team Name>\n" +
                            "    - Get the most recent tweet from query organization (last week)  \n" +
                            "\n" +
                            "jc twitter <University Name or Team Name>\n" +
                            "    - Get the Twitter account of the university and the JustChilln Score (scored via last week's likes, retweets, and replies)\n" +
                            "\n" +
                            "jc tstats <University Name or Team Name>\n" +
                            "    - Get the Twitter rankings of all the database Universities Twitter account (last week)");

            event.getChannel().sendMessage(embed);
        }else if (messageLower.startsWith("jc search")) {
            String[] output = messageLower.split("jc search", 2);

            if (output[1].trim().length() == 0) {
                event.getChannel().sendMessage("Syntax: jc search <University or program name searched for>");
                return;
            }

            //Search to see if keywords is contained in lookup table
            if (collegeNames.nameLookup.containsKey(output[1].trim())) {
                int index = collegeNames.nameLookup.get(output[1].trim());
                CollegeData currentData = collegeNames.collegeData.get(index);
                currentData.sendSummaryMessage(event.getChannel());
            } else {
                event.getChannel().sendMessage("Cannot find " + output[1].trim());
            }
        } else if (messageLower.startsWith("jc news")) {
            String[] output = messageLower.split("jc news", 2);
            if (collegeNames.nameLookup.containsKey(output[1].trim())) {
                int index = collegeNames.nameLookup.get(output[1].trim());
                CollegeData currentData = collegeNames.collegeData.get(index);
                //Tries to access channel to copy paste from
                try {
                    String content_scraped = api.getChannelById(currentData.getTextChannelID()).get().asTextChannel().get().getMessages(1).get().getNewestMessage().get().getContent();
                    EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor(currentData.getName(), currentData.getWebsite(), currentData.getIcon())
                            .setTitle("Latest News")
                            .setDescription(content_scraped);
                    event.getChannel().sendMessage(embed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else if (messageLower.startsWith("jc twitch")) {
            String[] output = messageLower.split("jc twitch", 2);
            if (output[1].trim().equalsIgnoreCase("random")) {
                Stream stream = twitch.getRandomStreamer();
                if (stream == null) {
                    event.getChannel().sendMessage("No streamers are online! Check back another time.");
                } else {
                    EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor(stream.getUserName() + " 🔴", "https://www.twitch.tv/" + stream.getUserLogin(), "https://static.twitchcdn.net/assets/favicon-32-e29e246c157142c94346.png")
                        .setTitle(stream.getTitle())
                        .setDescription("Viewers: " + stream.getViewerCount().toString())
                        .setImage(stream.getThumbnailUrl().replace("{height}", "900").replace("{width}", "1600"))
                        .setUrl("https://www.twitch.tv/" + stream.getUserLogin());

                    event.getChannel().sendMessage(embed);
                }
            } else if (collegeNames.nameLookup.containsKey(output[1].trim())) {
                int index = collegeNames.nameLookup.get(output[1].trim());
                CollegeData currentData = collegeNames.collegeData.get(index);
                ArrayList<String> streamerList = currentData.getStreamers();

                StreamList active = twitch.getActiveStreamersFromSchool(streamerList);
                if (active.getStreams().isEmpty()) {
                    event.getChannel().sendMessage("No streamers are online! Check back another time.");
                } else {
                    active.getStreams().forEach(stream -> {
//                        EmbedBuilder embed = new EmbedBuilder()
//                            .setAuthor(stream.getUserName(), "https://www.twitch.tv/" + stream.getUserLogin(), "https://static.twitchcdn.net/assets/favicon-32-e29e246c157142c94346.png")
//                            .setTitle(stream.getTitle())
//                            .setDescription("Viewers: " + stream.getViewerCount().toString())
//                            .setImage(stream.getThumbnailUrl().replace("{height}", "900").replace("{width}", "1600"))
//                            .setUrl("https://www.twitch.tv/" + stream.getUserLogin());
//
//                        event.getChannel().sendMessage(embed);
                        event.getChannel().sendMessage("https://www.twitch.tv/" + stream.getUserLogin());
                    });
                }
            }
        } else if (messageLower.startsWith("jc trecent")) {
            String[] output = messageLower.split("jc trecent", 2);
            if (collegeNames.nameLookup.containsKey(output[1].trim())) {
                int index = collegeNames.nameLookup.get(output[1].trim());
                CollegeData currentData = collegeNames.collegeData.get(index);
                System.out.println(index);
                //Tries to access channel to copy paste from
                try {
                    twitterScraper.getRecentTweet(currentData.getTwitter().split("https://twitter.com/", 2)[1], event.getChannel(), currentData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (messageLower.startsWith("jc tstats")) {
            twitterScraper.getAllStats(event.getChannel(), collegeNames.collegeData);
        } else if (messageLower.startsWith("jc twitter")) {
            String[] output = messageLower.split("jc twitter", 2);
            if (collegeNames.nameLookup.containsKey(output[1].trim())) {
                int index = collegeNames.nameLookup.get(output[1].trim());
                CollegeData currentData = collegeNames.collegeData.get(index);

                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor(currentData.getName(), currentData.getTwitter(), currentData.getIcon())
                        .setTitle(currentData.getName() + " Twitter Stats")
                        .setThumbnail(currentData.getIcon())
                        .setDescription("Last week points: " + twitterScraper.getLikes(currentData.getTwitter().split("https://twitter.com/", 2)[1], currentData).score);
                event.getChannel().sendMessage(embed);
            }
        } else if (messageLower.startsWith("jc teams")) {
            ArrayList<String> teamData = battlefyScraper.getTeams();

            teamData.sort(String::compareToIgnoreCase);

            if (instances_teams.containsKey(event.getChannel().getIdAsString())) {
                instances_teams.remove(event.getChannel().getIdAsString());
            }

            instances_teams.put(event.getChannel().getIdAsString(), new PageHandler(event.getChannel(), teamData, "Battlefly Teams", "Sparsh", "https://www.linkedin.com/in/sparshdeep-singh-08a07b221", "https://media-exp1.licdn.com/dms/image/C5603AQH7TMwhExoPjA/profile-displayphoto-shrink_800_800/0/1631757790166?e=1651104000&v=beta&t=d-G8wpbVq4DexYtHJz_WSvWnIXk0Tpi2LYkARHRIEoI", null));

        } else if (messageLower.startsWith("jc op")) {
            String teamName = messageLower.substring(6).trim();
            //System.out.println(teamName);
            String teamOpgg = battlefyScraper.getPlayersOpgg(teamName);
            EmbedBuilder embed = new EmbedBuilder().setDescription(teamOpgg);
            event.getChannel().sendMessage(embed);
        } else if (messageLower.startsWith("jc stats")) {
        Object[] kdaArray = battlefyScrapperStats.getKdas().toArray();
        StringBuilder description = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder().setTitle("Top KDAs").setThumbnail("https://cdn.battlefy.com/helix/images/leagues-v2/collegelol/clol-logo.png");
        for (int i = 0; i < 50; i++) {
            description.append("#").append(i + 1).append(" - ").append(kdaArray[kdaArray.length - 1 - i]).append("\n");
        }
        embed.setDescription(description.toString());
        event.getChannel().sendMessage(embed);
    }
    }

}
