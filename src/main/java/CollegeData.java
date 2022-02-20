import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.awt.*;

public class CollegeData {
    private String name;
    private String university;
    private String description;
    private String icon;
    private String website;
    private String twitch;
    private String twitter;
    private String discord;
    private long TextChannelID;
    private ArrayList<String> streamers;

    public CollegeData() {

    }

    public CollegeData(YamlFile yamlFile) {
        name = yamlFile.getString("Name");
        university = yamlFile.getString("University");
        description = yamlFile.getString("Description");
        website = yamlFile.getString("Website");
        twitch = yamlFile.getString("Twitch");
        twitter = yamlFile.getString("Twitter");
        discord = yamlFile.getString("Discord");
        icon = yamlFile.getString("Icon");
        TextChannelID = yamlFile.getLong("TextChannelID");
        streamers = (ArrayList<String>) yamlFile.getStringList("Streamers");
        System.out.println(name);
    }

    public void sendSummaryMessage(TextChannel channel) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(name, website, icon)
                .setTitle(university)
                .setDescription(description)
                .setThumbnail(icon)
                .addField("Twitter", twitter)
                .addField("Discord", discord)
                .setColor(Color.BLUE);
        channel.sendMessage(embed);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getTwitch() {
        return twitch;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

    public long getTextChannelID() {
        return TextChannelID;
    }

    public void setTextChannelID(long textChannelID) {
        TextChannelID = textChannelID;
    }

    public ArrayList<String> getStreamers() { return streamers; }
}
