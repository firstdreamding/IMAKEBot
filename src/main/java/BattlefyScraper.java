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

    private static JSONArray tournamentArray;
    private final ArrayList<String> teamsArray;

    public BattlefyScraper(String battlefyUrl) throws ParseException, IOException {
        String[] splitBattlefyUrl = battlefyUrl.split("/");
        String tournamentId = splitBattlefyUrl[5];

        URL url = new URL("https://dtmwra1jsgyb0.cloudfront.net/tournaments/" + tournamentId + "/teams?");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        JSONParser parser = new JSONParser();
        tournamentArray = (JSONArray) parser.parse(content.toString());
        teamsArray = getTeams();

    }

    public static void main(String[] args) throws IOException, ParseException {
        // Insert your bot's token here
        BattlefyScraper battlefyScraper = new
                BattlefyScraper("https://battlefy.com/college-league-of-legends/2022-north-conference/6171f253947ed60d0abb9083/info?infoTab=details");
        battlefyScraper.getTeams();
        //System.out.println(tournamentArray);
    }

    public ArrayList<String> getTeams() {
        ArrayList<String> teamArray = new ArrayList<>();
        for (Object o : tournamentArray) {
            teamArray.add(((JSONObject) o).get("name").toString());
        }
        return teamArray;
    }

    public String getPlayersOpgg(String teamName) throws IOException, ParseException {
        JSONArray playerArray = new JSONArray();
        for (Object o : tournamentArray) {
            if (((JSONObject) o).get("name").toString().equalsIgnoreCase(teamName)) {
                playerArray = ((JSONArray) ((JSONObject) o).get("players"));
            }
        }
        if (playerArray.isEmpty()) {
            return teamName + " does not exist!";
        }
        //playerArray = (JSONArray) ((JSONObject) tournamentArray.get(0)).get("players");
        ArrayList<String> playerNames = new ArrayList<>();
        for (Object o : playerArray) {
            if (((JSONObject) o).get("isStaff") != null && ((JSONObject) o).get("isStaff").toString().equalsIgnoreCase("false")) {
                String playerName = ((JSONObject) o).get("inGameName").toString().toLowerCase().replace(" ", "");
                playerNames.add(playerName);
            } else if (((JSONObject) o).get("onTeam") != null && ((JSONObject) o).get("onTeam").toString().equalsIgnoreCase("true")) {
                String playerName = ((JSONObject) o).get("inGameName").toString().toLowerCase().replace(" ", "");
                playerNames.add(playerName);
            } else {
                String playerName = ((JSONObject) o).get("inGameName").toString().toLowerCase().replace(" ", "");
                playerNames.add(playerName);
            }
        }
        StringBuilder opggLink = new StringBuilder("https://na.op.gg/multisearch/na?summoners=");
        for (String playerName : playerNames) {
            opggLink.append(playerName).append("%2C");
        }

        return opggLink.toString();
    }
}
