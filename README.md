# Just Collegiate Bot
A Discord bot focused on collecting information about collegiate: Esports programs, competitive league stats, social media interaction, and streamer promotion.
Created by Team JustChilln (Geoff, Sparsh, and Xin) as their IMake submission for 2022.
[Presentation](https://docs.google.com/presentation/d/1xg04bJXnu8tJKvJO9RmYQvbkKVyr-La-J7Zo2xcVS50/edit?usp=sharing)

## Up and Running
Add these custom API keys to their respective files in the resources folder.
- Discord API Key: token_key.txt
- Twitch Client ID: twitch_client_id.txt
- Twitch Client Secret: twitch_client_secret.txt
- Twitter Token: twitter_token_key.txt

## Bot Commands (JC Help)
### Overall Info (╯°□°）╯︵ ┻━┻
jc search <University Name or Team Name>
- Gets information on search query esports organization.

jc news <University Name or Team Name>
- Gets info if there on Universities Announcementt channel.

### Twitter (☞ﾟヮﾟ)☞
jc trecent <University Name or Team Name>
- Get the most recent tweet from query organization (last week)

jc twitter <University Name or Team Name>
- Get the Twitter account of the university and the JustChilln Score (scored via last week's likes, retweets, and replies)

jc tstats <University Name or Team Name>
- Get the Twitter rankings of all the database Universities Twitter account (last week)

### Twitch ༼ つ ◕_◕ ༽つ
jc twitch random
- Gets a random collegiate stream that is live.

jc twitch <University/Team Name>
- Gets a list of the top streams from a university/team that are live.

jc twitch <Game/Category Name>
- Gets a list of the top streams from a game/category that are live.

### Battlefy ╰(°▽°)╯
jc team
- Returns a list of all teams in the channel's region.

jc op <Team Name (jc team for list of teams)>
- Returns an opgg link of the inputted team.

jc stats
- Returns a list of all KDAs sorted in descending order.

jc region <Region Name>
- Switches channel region to inputted region.

jc regions
- Gets a list of all collegiate conference regions.
