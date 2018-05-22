package it.polimi.ingsw.client.clientConnection;

import it.polimi.ingsw.client.view.Handler;
import it.polimi.ingsw.client.view.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static it.polimi.ingsw.costants.GameCreationMessages.*;
import static it.polimi.ingsw.costants.LoginMessages.*;

public class SocketConnection implements Connection,Runnable {

    Socket socket;
    PrintWriter out;
    Scanner in;
    Handler hand;  // used to manage graphic
    private boolean stopThread = false;

    public SocketConnection(Handler hand) throws IOException {
        socket = new Socket("localhost", 1666);
        out = new PrintWriter(socket.getOutputStream());
        in = new Scanner(socket.getInputStream());
        this.hand = hand;
    }


    public void sendMessage(String str) {
        out.println(str);
        out.flush();
    }


    public void login(String nickname) {
        out.println("Login-" + nickname);
        out.flush();
    }


    public void disconnect() {
        stopRunning();
        out.println("Disconnected");
        out.flush();
        out.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        in.close();
    }

    public void stopRunning() {
        stopThread = true;
    }


    public void run() {
        while (!stopThread) {
            try {
                String str = in.nextLine();
                List <String> action =new ArrayList<String>();
                StringTokenizer token = new StringTokenizer(str, "-");
                while(token.hasMoreTokens())
                    action.add(token.nextToken());
                deliverGI(action);
            } catch (NoSuchElementException e) {
                System.out.println("disconnesso");
                stopThread = true;
            }
        }
    }

    // deliver action on GUI or CLI
    public void deliverGI(List<String> action) {
        View v = hand.getView();
        if(action.get(0).equals(loginSuccessful)) {
            if (action.get(1).equals(v.getName()))
                v.login(action.get(0));
            else
                v.playerConnected(action.get(1));
        }else if(action.get(0).equals(loginError)) {
            v.login(action.get(0) + "-" + action.get(1));
        }else if(action.get(0).equals(logout)){
            v.playerDisconnected(action.get(1));
        }else if(action.get(0).equals(timerPing)){
            v.timerPing(action.get(1));
        }else if(action.get(0).equals(startingGameMsg)) {
            v.createGame();
        }else if(action.get(0).equals(setSchemas)) {
            v.setSchemas(action.subList(1,5));
        }else if(action.get(0).equals(setPrivateCard)) {
            v.setPrivateCard(action.get(1));
        }else if(action.get(0).equals(setPublicObjectives)) {
            v.setPublicObjectives(action.subList(1,4));
        }else if(action.get(0).equals(setToolCards)) {
            v.setToolCards(action.subList(1,4));
        }
    }
}