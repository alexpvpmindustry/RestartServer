package alexRestart;

import arc.Events;
import arc.util.Log;
import arc.util.Timer;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.mod.Plugin;

import java.util.concurrent.atomic.AtomicInteger;


public class alexRestart extends Plugin {
    public int checkInterval = 5*60; // 5 mins
    public int[] array_for_checking = new int[]{1, 1, 1, 1, 1, 1};
    // checks 6 times, and if the array is all zero, fire gameover event
    // means if there are no players in the server for 30mins (rolling window) the game will restart.

    public int counter = 0 ;

    @Override
    public void init() {
        Timer.schedule(() -> {
            // checks the number of players in the server,
            // if empty for longer than 30mins, send gameover command
            AtomicInteger current_players = new AtomicInteger(0);
            Groups.player.each(p -> p.con != null,p->{
                current_players.getAndIncrement();
            });
            array_for_checking[counter] = current_players.get();
            //Log.info("counter "+counter+" curr num players: "+current_players.get());
            counter++;
            if (counter>=array_for_checking.length){
                counter=0;
            }
            int totalNumberOfPlayers = 0;
            for (int j : array_for_checking) {
                totalNumberOfPlayers = totalNumberOfPlayers + j;
            }
            if(totalNumberOfPlayers==0){
                Log.info("no players found for 30mins, sending gameover event");
                array_for_checking = new int[]{1, 1, 1, 1, 1, 1};
                Events.fire(new EventType.GameOverEvent(Team.crux));
            }
        }, checkInterval, checkInterval);
    }
}