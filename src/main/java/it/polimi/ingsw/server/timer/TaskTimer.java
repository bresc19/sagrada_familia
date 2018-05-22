package it.polimi.ingsw.server.timer;

import it.polimi.ingsw.server.model.game.Session;

import java.util.TimerTask;

import static it.polimi.ingsw.costants.LoginMessages.timerElapsed;
import static it.polimi.ingsw.costants.LoginMessages.timerPing;

public class TaskTimer extends TimerTask{
    private Long startingTime = 0L;
    private int waitTime;
    private Session session;

    public TaskTimer(int waitTime, Session session) {
        this.waitTime = waitTime ;
        this.session = session;
    }

    @Override
    public void run() {
        if(startingTime == 0)
            startingTime = System.currentTimeMillis();
        if (System.currentTimeMillis() < startingTime + waitTime * 1000) {
            session.notify(timerPing);
        }
        else{
            session.notify(timerElapsed);
            this.cancel();
        }
    }
}