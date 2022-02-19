import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BattlefyScraper {
    public ArrayList<String> getPlayers(String battlefyUrl) throws IOException {
        String[] splitBattlefyUrl = battlefyUrl.split("/");
        String tournamentId = splitBattlefyUrl[5];

        URL url = new URL("https://dtmwra1jsgyb0.cloudfront.net/tournaments/" + tournamentId + "/teams?");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        //System.out.println(content.toString());
        JSONParser object = new JSONParser();
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(content.toString());
            JSONArray playerArray = (JSONArray) ((JSONObject) jsonArray.get(0)).get("players");
            System.out.println(((JSONObject) playerArray.get(0)).get("inGameName"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int teamName = content.indexOf("name");
        //System.out.println(content.charAt(teamName) + content.charAt(teamName + 1));

//        for (int i = 0; i < 40; i++) {
//            System.out.print(content.charAt(teamName + 7 + i));
//
//        }
        in.close();
        con.disconnect();
        return null;
    }
}
