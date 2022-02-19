import org.simpleyaml.configuration.file.YamlFile;

public class CollegeData {
    private String name;
    private String description;

    public CollegeData() {

    }

    public CollegeData(YamlFile yamlFile) {
        name = yamlFile.getString("Name");
        description = yamlFile.getString("Description");
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
}
