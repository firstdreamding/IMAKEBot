import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryNames {

    public HashMap<String, Integer> nameLookup;
    public ArrayList<CategoryData> categoryData;

    private final String DIR_LOCATION = "src/main/resources/CategoryData";

    public CategoryNames() {
        nameLookup = new HashMap<>();
        categoryData = new ArrayList<>();
        setUp();
    }

    private void setUp() {
        try {
            List<File> files = Files.list(Paths.get(DIR_LOCATION))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for (int i = 0; i < files.size(); i++) {
                String pathFiles = files.get(i).toString();
                YamlFile yamlFile = new YamlFile(pathFiles);
                yamlFile.load();

                categoryData.add(new CategoryData(yamlFile));

                System.out.println(i);
                List<String> stringIDs = yamlFile.getStringList("IDs");
                for (String j : stringIDs) {
                    nameLookup.put(j.toLowerCase(), i);
                    System.out.println(j);
                }
            }

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
