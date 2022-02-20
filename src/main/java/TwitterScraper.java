import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TwitterScraper {

    //Channel ID, Instance
    private HashMap<String, PageHandler> instances;
    private String key_;

    public TwitterScraper() {
        instances = new HashMap<>();
        try {
            key_ = new String(Files.readAllBytes( Paths.get("src/main/resources/twitter_token_key.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRecentTweet(String user, TextChannel channel, CollegeData currentData) throws IOException {
        String command_ss = "curl -X GET \"https://api.twitter.com/2/tweets/search/recent?query=from:" + user + "%20-is:retweet&expansions=attachments.media_keys&media.fields=duration_ms,height,media_key,preview_image_url,public_metrics,type,url,width,alt_text\" -H \"Authorization: Bearer " + key_ + "\"";

        Process process = Runtime.getRuntime().exec(command_ss);


        BufferedReader in = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        //System.out.println(content.toString());
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(content.toString());
            System.out.println(obj);
            JSONArray data = (JSONArray) obj.get("data");
            JSONObject recent_tweet = (JSONObject) data.get(0);
            System.out.println(obj);
            System.out.println(recent_tweet.get("text"));

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(currentData.getName(), currentData.getTwitter(), currentData.getIcon())
                    .setTitle("Latest Tweet")
                    .setDescription(recent_tweet.get("text").toString())
                    .setColor(Color.BLUE);
            try {
                JSONObject includesObj = (JSONObject) obj.get("includes");
                JSONArray media = (JSONArray) includesObj.get("media");
                JSONObject url = (JSONObject) media.get(0);
                String urlS = url.get("url").toString();
                embed.setImage(urlS);
            } catch (Exception e) {

            }
            channel.sendMessage(embed);
            System.out.println(recent_tweet.toString());
            return;
        } catch (Exception e) {
            channel.sendMessage("No Recent Tweets");
        }

        in.close();
    }

    public void getAllStats(TextChannel channel, ArrayList<CollegeData> currentData) {

        ArrayList<CollegeRank> stats = new ArrayList<>();
        for (CollegeData i : currentData) {
            try {
                System.out.println(i.getName());
                stats.add(getLikes(i.getTwitter().split("https://twitter.com/", 2)[1], i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(stats, new RankComparator());

        ArrayList<String> stats_string = new ArrayList<>();
        for (CollegeRank i : stats) {
            stats_string.add(i.data.getName());
        }

        if (instances.containsKey(channel.getIdAsString())) {
            instances.remove(channel.getIdAsString());
        }

        instances.put(channel.getIdAsString(), new PageHandler(channel, stats_string, "Twitter Ranking: This Week", stats.get(0).data.getName(), stats.get(0).data.getTwitter(), stats.get(0).data.getIcon(), null));
    }

    public CollegeRank getLikes(String user, CollegeData currentData) throws IOException {

        String command_ss = "curl -X GET \"https://api.twitter.com/2/tweets/search/recent?query=from:" + user + "%20-is:retweet&tweet.fields=public_metrics\" -H \"Authorization: Bearer " + key_ + "\"";

        Process process = Runtime.getRuntime().exec(command_ss);

        CollegeRank instance = new CollegeRank();
        instance.data = currentData;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        //System.out.println(content.toString());
        instance.score = 0;
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(content.toString());
            System.out.println(obj);
            JSONArray data = (JSONArray) obj.get("data");
            for (Object i : data) {
                JSONObject recent_tweet = (JSONObject) i;
                JSONObject publicMetrics = (JSONObject) recent_tweet.get("public_metrics");
                instance.score += Integer.parseInt(publicMetrics.get("like_count").toString());
                instance.score += 2 * Integer.parseInt(publicMetrics.get("reply_count").toString());
                instance.score += 2 * Integer.parseInt(publicMetrics.get("retweet_count").toString());
            }
        } catch (Exception e) {
            //channel.sendMessage("No Recent Tweets");
        }
        in.close();
        return instance;
    }
}