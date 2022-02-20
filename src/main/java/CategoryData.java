import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;

public class CategoryData {
    private String name;
    private String description;
    private String icon;
    private ArrayList<String> streamers;

    public CategoryData() {

    }

    public CategoryData(YamlFile yamlFile) {
        name = yamlFile.getString("Name");
        description = yamlFile.getString("Description");
        icon = yamlFile.getString("Icon");
        streamers = (ArrayList<String>) yamlFile.getStringList("Streamers");
        System.out.println(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<String> getStreamers() {
        return streamers;
    }

    public void setStreamers(ArrayList<String> streamers) {
        this.streamers = streamers;
    }
}
