import org.simpleyaml.configuration.file.YamlFile;

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
        System.out.println(name);
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
}
