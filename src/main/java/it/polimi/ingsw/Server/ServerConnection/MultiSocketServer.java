package it.polimi.ingsw.Server.ServerConnection;

import it.polimi.ingsw.Server.VirtualView.VirtualView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiSocketServer{
    private int port;
    private VirtualView virtual;
    private Connected connection;
    // ricordarsi di togliere il socket quando viene chiusa la connessione

    public MultiSocketServer(int port,VirtualView virtual,Connected connection)
    {
        this.port = port;
        this.virtual = virtual;
        this.connection = connection;
    }

    public void StartServer()
    {
        ExecutorService execute = Executors.newCachedThreadPool();
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server ready");
            while(true)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    System.out.println("Accettato");
                   SocketConnection sock = new SocketConnection(socket,virtual,connection);
                    execute.submit(sock);

                }catch(Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }

        }catch(IOException e)
        {
            System.out.println(e.getMessage());
            return;
        }finally {
            try {
                serverSocket.close();
            }catch(IOException ex2){}
        }


    }




   /* public void update(Observable o, Object arg) {
        SocketConnection s = (SocketConnection) arg;

        if(sockets.get(arg).equals(""))
         sockets.put(s,s.getName());
        else {
            sockets.remove(s);
            System.out.println("Disconnesso"+s.getName());
            this.publish(s.getName()+" si è disconnesso");
        }
    }

    public void publish(String str) {
        if(!sockets.isEmpty()){
            for(SocketConnection sock:sockets.keySet())
                sock.sendMessage(str);
        }
    }

    */
}
