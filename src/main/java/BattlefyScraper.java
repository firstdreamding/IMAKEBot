import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class BattlefyScraper {

    private static JSONArray tournamentArray;
    private static JSONArray tournamentStatsArray;

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
    }

    public BattlefyScraper() throws IOException, ParseException {
        URL statsUrl = new URL("https://dtmwra1jsgyb0.cloudfront.net/stages/61d8c2ccb3ccd7231aa77fa0/stats");
        HttpURLConnection con2 = (HttpURLConnection) statsUrl.openConnection();
        con2.setRequestMethod("GET");

        int status2 = con2.getResponseCode();
        BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
        String inputLine2;
        StringBuilder content2 = new StringBuilder();
        while ((inputLine2 = in2.readLine()) != null) {
            content2.append(inputLine2);
        }
        in2.close();
        con2.disconnect();
        JSONParser parser2 = new JSONParser();
        tournamentStatsArray = (JSONArray) parser2.parse(content2.toString());
    }

    public static void main(String[] args) throws IOException, ParseException {
        // Insert your bot's token here
        //BattlefyScraper battlefyScraper = new
            //    BattlefyScraper("https://battlefy.com/college-league-of-legends/2022-north-conference/6171f253947ed60d0abb9083/info?infoTab=details");
        //battlefyScraper.getTeams();
        BattlefyScraper battlefyScraper = new BattlefyScraper("https://battlefy.com/college-league-of-legends/2022-east-conference/61720cd843d5751156d83bd2/info?infoTab=details");

        //System.out.println(tournamentStatsArray);
        //getKdas();
    }

    public ArrayList<String> getKdas() {
        ArrayList<Object> statsArray = new ArrayList<>();
        for (Object o : tournamentStatsArray) {
            statsArray.add(((JSONObject) o).get("stats"));
        }
        //System.out.println(statsArray.get(0));

        ArrayList<Object> teamssArray = new ArrayList<>();
        for (Object o : statsArray) {
            Object contents = ((JSONObject) o).get("teams");
            if (contents != null) {
                teamssArray.add(((JSONObject) o).get("teams"));
            }
        }
        //System.out.println(teamssArray.get(0));

        ArrayList<JSONArray> playersArray = new ArrayList<>();

        //System.out.println(teamssArray.get(teamssArray.size()-1));

        for (Object o : teamssArray) {
            playersArray.add((JSONArray) (((JSONObject) (((JSONArray) o).get(0))).get("players")));
            playersArray.add((JSONArray) (((JSONObject) (((JSONArray) o).get(1))).get("players")));
        }
        //System.out.println(playersArray.get(3).get(4));
        ArrayList<Object> playerStatsArray = new ArrayList<>();
        for (JSONArray o : playersArray) {
            playerStatsArray.add(((JSONObject)o.get(0)).get("stats"));
            playerStatsArray.add(((JSONObject)o.get(1)).get("stats"));
            playerStatsArray.add(((JSONObject)o.get(2)).get("stats"));
            playerStatsArray.add(((JSONObject)o.get(3)).get("stats"));
            playerStatsArray.add(((JSONObject)o.get(4)).get("stats"));
        }
        //System.out.println(playerStatsArray);

        ArrayList<KDA> playerKdas = new ArrayList<>();
        for (Object o : playerStatsArray) {
            String summonerName = ((JSONObject) o).get("summonerName").toString().toLowerCase().replace(" ", "");
            Object a = ((JSONObject) o).get("killDeathAssistsRatio");
            if (a != null) {
                boolean found = false;
                double kda = Double.parseDouble(a.toString());
                for (int i = 0; i < playerKdas.size(); i++) {
                    if (playerKdas.get(i).name.equalsIgnoreCase(summonerName)) {
                        playerKdas.get(i).counter++;
                        playerKdas.get(i).KDA = playerKdas.get(i).KDA / playerKdas.get(i).counter;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    playerKdas.add(new KDA(summonerName, kda));
                }
            }
        }

        Collections.sort(playerKdas, new KDAComparator());

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < playerKdas.size(); i++) {
            names.add(playerKdas.get(i).name + ": " + playerKdas.get(i).KDA);
        }

        return names;
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
