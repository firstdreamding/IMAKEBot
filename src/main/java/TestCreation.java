import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCreation {
    public static void main(final String[] args) {

        // Create new YAML file with relative path
        final YamlFile yamlFile = new YamlFile("src/main/resources/CollegeData/IlliniEsports.yml");

        // Load the YAML file if is already created or create new one otherwise
        try {
            if (!yamlFile.exists()) {
                yamlFile.createNewFile(true);
                System.out.println("New file has been created: " + yamlFile.getFilePath() + "\n");
            } else {
                System.out.println(yamlFile.getFilePath() + " already exists, loading configurations...\n");
            }
            yamlFile.load(); // Loads the entire file
            // If your file has comments inside you have to load it with yamlFile.loadWithComments()
        } catch (final Exception e) {
            e.printStackTrace();
        }

        yamlFile.set("Name", "IlliniEsports");
        yamlFile.set("Description", "Its an org");

        // More additions, e.g. adding entire lists

        final List<String> list = Arrays.asList("IE Illini UIUC Illinois".split("\\s+"));
        yamlFile.set("IDs", list);
        // Finally, save changes!
        try {
            yamlFile.save();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // Now, you can restart this test and see how the file is loaded due to it's already created

        // You can delete the generated file uncommenting next line and catching the I/O Exception
        // yamlFile.deleteFile();
    }
}
