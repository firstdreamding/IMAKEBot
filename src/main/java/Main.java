import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {

    private CollegeNames collegeNames;
    private DiscordApi api;
    private static BattlefyScraper battlefyScraper;

    public static void main(String[] args) throws IOException, ParseException {
        // Insert your bot's token here
        battlefyScraper = new BattlefyScraper("https://battlefy.com/college-league-of-legends/2022-north-conference/6171f253947ed60d0abb9083/info?infoTab=details");

        String token = new String(Files.readAllBytes( Paths.get("src/main/resources/token_key.txt")));
        Main main = new Main();
        main.api = new DiscordApiBuilder().setToken(token).login().join();
        main.twitterScraper = new TwitterScraper();

        main.collegeNames = new CollegeNames();

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

        if (messageLower.startsWith("jc search")) {
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
        } else if (messageLower.startsWith("jc twitter")) {
            String[] output = messageLower.split("jc twitter", 2);
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
        } else if (messageLower.startsWith("jc teams")) {
            ArrayList<String> teamData = battlefyScraper.getTeams();
            teamData.sort(String::compareToIgnoreCase);
            EmbedBuilder embed = new EmbedBuilder().setTitle("Teams").setThumbnail("https://cdn.battlefy.com/helix/images/leagues-v2/collegelol/clol-logo.png");

            ArrayList<Button> buttons = new ArrayList<>();
            buttons.add(new Button("<"));
            buttons.add(new Button(">"));

            StringBuilder description = new StringBuilder();
            for (int i = 0; i < teamData.size(); i++) {
                description.append("#").append(i + 1).append(" - ").append(teamData.get(i)).append("\n");

            }
            embed.setDescription(description.toString());
            event.getChannel().sendMessage(embed);
        } else if (messageLower.startsWith("jc op")) {
            String teamName = messageLower.substring(6).trim();
            //System.out.println(teamName);
            String teamOpgg = battlefyScraper.getPlayersOpgg(teamName);
            EmbedBuilder embed = new EmbedBuilder().setDescription(teamOpgg);
            event.getChannel().sendMessage(embed);
        }
    }

}
