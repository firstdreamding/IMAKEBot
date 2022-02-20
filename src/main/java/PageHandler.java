import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;

public class PageHandler {

    private TextChannel channelMessaging;
    private Message message;
    private int index;
    private ArrayList<String> content;

    private String user;
    private String website;
    private String icon;
    private String title;
    private String thumbnail;

    private final int ELEMENT_PAGE = 10;

    public PageHandler(TextChannel channel, ArrayList<String> content, String title, String user, String website, String icon, String thumbnail) {

        this.user = user;
        this.website = website;
        this.icon = icon;
        this.title = title;

        this.content = content;

        index = 0;
        this.thumbnail = thumbnail;
        channelMessaging = channel;
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(user, website, icon)
                    .setTitle(title);
            String contentString = "";

            int max_i = Math.min(index * ELEMENT_PAGE + ELEMENT_PAGE, content.size());
            for (int i = index * ELEMENT_PAGE; i < index * ELEMENT_PAGE + max_i; i++) {
                contentString += (i + 1) + ": " + content.get(i) + "\n";
            }
            embed.setDescription(contentString);
            if (thumbnail != null) {
                embed.setThumbnail(thumbnail);
            }
            message = channelMessaging.sendMessage(embed).get();
            update();
        } catch (Exception e) {

        }
        message.addReaction("⬅");
        message.addReaction("➡");
        message.addReactionAddListener(event -> {
            if (!event.getUser().get().isBot() && event.getEmoji().equalsEmoji("⬅")) {
                index = Math.max(0, index - 1);
                update();
            } else if (!event.getUser().get().isBot() && event.getEmoji().equalsEmoji("➡")) {
                index = Math.min((content.size() - 1) / ELEMENT_PAGE, index + 1);
                update();
            }
        });
        message.addReactionRemoveListener(event -> {
            if (!event.getUser().get().isBot() && event.getEmoji().equalsEmoji("⬅")) {
                index = Math.max(0, index - 1);
                update();
            } else if (!event.getUser().get().isBot() && event.getEmoji().equalsEmoji("➡")) {
                index = Math.min((content.size() - 1) / ELEMENT_PAGE, index + 1);
                update();
            }
        });
    }

    private void update() {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(user, website, icon)
                .setTitle(title);
        String contentString = "";

        int max_i = Math.min(index * ELEMENT_PAGE + ELEMENT_PAGE, content.size());
        for (int i = index * ELEMENT_PAGE; i < max_i; i++) {
            contentString += (i + 1) + ": " + content.get(i) + "\n";
        }
        embed.setDescription(contentString);
        embed.setFooter("Page: " + (index + 1) + " / " + (((content.size() - 1) / ELEMENT_PAGE) + 1));
        if (thumbnail != null) {
            embed.setThumbnail(thumbnail);
        }
        message.edit(embed);
    }
}
