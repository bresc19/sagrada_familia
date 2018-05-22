package it.polimi.ingsw.client.clientConnection;


import it.polimi.ingsw.client.view.Handler;
import it.polimi.ingsw.server.serverConnection.RmiServerMethodInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiConnection implements Connection {
    RmiServerMethodInterface server;
    RmiClientMethod client;
    Handler hand; // used to manage graphic
    public RmiConnection(Handler hand)
    {
        this.hand= hand;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            client = new RmiClientMethod(hand);
            server = (RmiServerMethodInterface) registry.lookup("RmiServerMethodInterface");
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        } catch(NotBoundException e3){
            System.out.println(e3.getMessage());
        }

    }


    public void sendMessage(String str) {
        try {
            server.forwardAction(str,client);
        }catch(RemoteException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void login(String nickname) {
        try {
            server.login(client, nickname);
        }catch(RemoteException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() {
        try {
            server.disconnected(client);
        }catch(RemoteException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
