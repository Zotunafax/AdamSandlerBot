import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.*;
import de.btobastian.javacord.entities.message.*;
import de.btobastian.javacord.entities.message.embed.Embed;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.entities.permissions.Permissions;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kajtorvaldgrey on 06/06/17.
 */
public class ASB_Start {

    //Channels
    private final static String privateChannelId = "306390617305382913";
    private final static String strategystatsChannelId = "328883213877379072";

    //Scores
    private int total_games = 1;
    private int score_grey = 1;
    private int score_lorddoge = 1;
    private int score_scot = 1;
    private int score_gorskinho = 1;
    private int score_xector = 0;
    private int score_yuuta = 0;

    //addScore main variables
    private String definiteWinnerList = "";
    private ArrayList<String> definiteWinners = new ArrayList<String>();
    private String winningReason = "-";
    private int winners;
    private String game;

    //Misc
    private final static String regexString = "(/addscore) ([1-6]{1,6})%(HoI|AoE|CK2|EU4|other)%.*";
    private final static Pattern pattern = Pattern.compile(regexString);
    private static String token = "";
    private final static DiscordAPI api = Javacord.getApi(token, true);
    private final static String helptext = "Who won?\nGrey -> 1\nLordDoge -> 2\nScot -> 3\nGorskinho -> 4\nXector -> 5\nYuuta -> 6" +
            "\n\nIf more than one person won, just write each number next to each other. (e.g. Grey & Scot won -> enter '13')" +
            "\n\nYou should also add what game you played. (HoI,AoE,EU4,CK2,other)" +
            "\n\nLast but not least if you want you can add a reason of your victory. (e.g. 'I just know how games work.')" +
            "\n\nThe whole command should look like this: '/addscore 13%AoE%I just know how games work.'";


    //saving scores and returning the scoreboard to strategystatsChannelId
    private void addscore(Message message) {

        String messageContent = message.getContent();
        total_games++;

        split(messageContent);

        message.reply("__**"+ total_games +". Game ("+ game +")**__\n" +
                "*Winner(s):*\n" + definiteWinnerList +
                "\n" +
                "*Winning method:*\n" +
                winningReason+
                "\n" +
                "\n" +
                "*Score*\n" +
                "```\n" +
                "Grey: "+ score_grey +"\n" +
                "LordDoge: "+ score_lorddoge +"\n" +
                "Scot: "+ score_scot +"\n" +
                "Gorskinho: "+ score_gorskinho +"\n" +
                "Xector: "+ score_xector +"\n" +
                "Yuuta: "+ score_yuuta +"```");

        save(message);
        clear();

    }

    private void split(String content){
        String[] parts = content.split("%");
        String[] parts2 = parts[0].split(" ");

        winners = Integer.parseInt(parts2[1]);
        game = parts[1];

        if(parts.length == 3){
            winningReason = parts[2];
        }

        while (winners > 0) {
            addWinner(winners % 10);
            winners = winners / 10;
        }

        for(int i = 0;i<definiteWinners.size();i++){
            definiteWinnerList = definiteWinnerList + definiteWinners.get(i) + "\n";
        }
    }

    private void addWinner(int referenceNumber){
        switch(referenceNumber){
            //Grey
            case 1:
                definiteWinners.add("Grey");
                score_grey++;
                break;
            //LordDoge
            case 2:
                definiteWinners.add("LordDoge");
                score_lorddoge++;
                break;
            //Scot
            case 3:
                definiteWinners.add("Scot");
                score_scot++;
                break;
            //Gorskinho
            case 4:
                definiteWinners.add("Gorskinho");
                score_gorskinho++;
                break;
            //Xector
            case 5:
                definiteWinners.add("Xector");
                score_xector++;
                break;
            case 6:
                definiteWinners.add("Yuuta");
                score_yuuta++;
            default:
                break;
        }
    }

    private void returnScore(Message message){

        message.reply("*Total games played:* "+ total_games +"\n" +
                "\n" +
                "\n" +
                "*Score*\n" +
                "```\n" +
                "Grey: "+ score_grey +"\n" +
                "LordDoge: "+ score_lorddoge +"\n" +
                "Scot: "+ score_scot +"\n" +
                "Gorskinho: "+ score_gorskinho +"\n" +
                "Xector: "+ score_xector +"\n" +
                "Yuuta: "+ score_yuuta +"```");


    }

    private void save(Message message){
        message.getAuthor().sendMessage("*Total games*: " +total_games);
        message.getAuthor().sendMessage("*Individual scores*:\n" +
                "Grey: "+ score_grey +"\n" +
                "LordDoge: "+ score_lorddoge +"\n" +
                "Scot: "+ score_scot +"\n" +
                "Gorskinho: "+ score_gorskinho +"\n" +
                "Xector: "+ score_xector +"\n" +
                "Yuuta: "+ score_yuuta +"\n");
    }

    private void clear(){
        definiteWinners.clear();
        definiteWinnerList = "";
    }

    public static void main(String args[]) {

        final ASB_Start asb_start = new ASB_Start();

        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI api) {

                //setting up
                api.setGame("with numbers and stats.");

                // register listener
                api.registerListener(new MessageCreateListener() {
                    @Override
                    public void onMessageCreate(DiscordAPI api, Message message) {

                        if (message.getChannelReceiver().getId().equals(strategystatsChannelId)) {

                            Matcher matcher = pattern.matcher(message.getContent());

                            if(matcher.find()&& !message.getContent().startsWith("Who won?")){
                                asb_start.addscore(message);
                                message.delete();
                            } else {
                                if (message.getContent().equalsIgnoreCase("/addscore")) {
                                    message.reply(helptext);
                                    message.delete();
                                }

                                if (message.getContent().equalsIgnoreCase("/score")) {
                                    asb_start.returnScore(message);
                                    message.delete();
                                }
                            }
                        }

                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
