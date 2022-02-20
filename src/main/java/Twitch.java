import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.GameTopList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Twitch {
    private String clientID = new String(Files.readAllBytes( Paths.get("src/main/resources/twitch_client_id.txt")));;
    private String clientSecret = new String(Files.readAllBytes( Paths.get("src/main/resources/twitch_client_secret.txt")));;
    private String clientToken;
    public ArrayList<String> streamerList;

    public TwitchClient twitchClient = TwitchClientBuilder.builder()
            .withEnableHelix(true)
            .build();

    public Twitch(CollegeNames collegeNames) throws IOException {
        clientToken = getToken();
        streamerList = createStreamerList(collegeNames);
    }

    private String getToken() throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/token?client_id=" + clientID + "&client_secret=" + clientSecret + "&grant_type=client_credentials");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject tokenObject = (JSONObject) parser.parse(content.toString());
            clientToken = tokenObject.get("access_token").toString();
            return clientToken;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public ArrayList<String> createStreamerList(CollegeNames collegeNames) {
        ArrayList<String> streamerList = new ArrayList<>();
        collegeNames.collegeData.forEach(college-> {
            streamerList.addAll(college.getStreamers());
        });

        return streamerList;
    }

    public StreamList getActiveStreamersFromSchool(ArrayList<String> streamerList) {
        StreamList resultList = twitchClient.getHelix().getStreams(clientToken, null, null, 5, null, null, null, streamerList).execute();
        resultList.getStreams().forEach(stream -> {
            System.out.println("ID: " + stream.getId() + " - Title: " + stream.getTitle());
        });

        return resultList;
    }

    public Stream getRandomStreamer() {
        ArrayList<String> shuffleStreamList = streamerList;
        StreamList resultList = twitchClient.getHelix().getStreams(clientToken, null, null, 1, null, null, null, shuffleStreamList).execute();
        if (resultList.getStreams().isEmpty()) {
            return null;
        } else {
            Random rand = new Random();
            return resultList.getStreams().get(rand.nextInt(resultList.getStreams().size()));
        }
    }
}
