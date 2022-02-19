import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends BattlefyScraper {

    private CollegeNames collegeNames;

    public static void main(String[] args) throws IOException {
        // Insert your bot's token here
        BattlefyScraper battlefyScraper = new BattlefyScraper();
        battlefyScraper.getPlayers("https://battlefy.com/college-league-of-legends/2022-north-conference/6171f253947ed60d0abb9083/info?infoTab=details");
//        String token = new String(Files.readAllBytes( Paths.get("src/main/resources/token_key.txt")));
//
//        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
//
//        Main main = new Main();
//        main.collegeNames = new CollegeNames();
//
//        // Add a listener which answers with "Pong!" if someone writes "!ping"
//        api.addMessageCreateListener(event -> {
//            main.handleCommand(event);
//        });
//
//        // Print the invite url of your bot
//        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
//    }
//
//    public void handleCommand(MessageCreateEvent event) {
//        //If not an actual user (ie bot wrote the message)
//        if (!event.getMessageAuthor().isRegularUser()) {
//            return;
//        }
//
//        String messageLower = event.getMessageContent().toLowerCase();
//
//        if (messageLower.startsWith("jc search")) {
//
//            String[] output = messageLower.split("jc search", 2);
//            if (collegeNames.nameLookup.containsKey(output[1].trim())) {
//                int index = collegeNames.nameLookup.get(output[1].trim());
//                CollegeData currentData = collegeNames.collegeData.get(index);
//                System.out.println(currentData.getUniversity());
//                EmbedBuilder embed = new EmbedBuilder()
//                        .setAuthor(currentData.getName(), currentData.getWebsite(), currentData.getIcon())
//                        .setTitle(currentData.getUniversity())
//                        .setDescription(currentData.getDescription())
//                        .setThumbnail(currentData.getIcon())
//                        .addField("Twitter", currentData.getTwitter())
//                        .addField("Discord", currentData.getDiscord())
//                        .setColor(Color.BLUE);
//                event.getChannel().sendMessage(embed);
//            } else {
//                event.getChannel().sendMessage("Cannot find " + output[1].trim());
//            }
//        }
    }
}
