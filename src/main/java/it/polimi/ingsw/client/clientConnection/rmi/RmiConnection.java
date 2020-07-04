package it.polimi.ingsw.client.clientConnection.rmi;


import it.polimi.ingsw.client.clientConnection.Connection;
import it.polimi.ingsw.client.setUp.TakeDataFile;
import it.polimi.ingsw.client.view.Handler;
import it.polimi.ingsw.client.view.Message;
import it.polimi.ingsw.client.view.TypeMessage;
import it.polimi.ingsw.server.connection.rmi.RmiServerMethodInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static it.polimi.ingsw.client.constants.NameConstants.SERVER_IP;
import static it.polimi.ingsw.client.constants.NameConstants.RMI_PORT;
import static it.polimi.ingsw.client.constants.printCostants.SERVER_CONNECTION_ERROR;
import static it.polimi.ingsw.client.constants.printCostants.THREAD_ERROR;
import static java.lang.Thread.sleep;

public class RmiConnection implements Connection {
    private RmiServerMethodInterface server;
    private RmiClientMethod client;
    private Handler hand;
    private String host;
    private int port;

    public RmiConnection(Handler hand) throws RemoteException {
        this.hand = hand;
        try {
            setConnection();
            Registry registry = LocateRegistry.getRegistry(host, port);
            client = new RmiClientMethod(hand);
            server = (RmiServerMethodInterface) registry.lookup("RmiServerMethodInterface");
        } catch (NotBoundException e3) {
            System.out.println(e3.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
        }
        pingServer();
    }

        /**
         * sets the connection
         */
        private void setConnection() throws IOException {
            TakeDataFile config = new TakeDataFile();
            host = config.getParameter(SERVER_IP);
            port = Integer.parseInt(config.getParameter(RMI_PORT));
        }


        /**
         * check connection with the server
         */
        private void pingServer()
        {
            Thread t = new Thread(() -> {
                boolean  isRunning = true;
                while(isRunning) {
                    try {
                        server.ping();
                        sleep(5000);
                    } catch (RemoteException e) {
                        Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
                        isRunning = false;
                        System.exit(1);
                    } catch (InterruptedException ex){
                        Message.print(THREAD_ERROR, TypeMessage.ERROR_MESSAGE);
                        Thread.currentThread().interrupt();
                    }
                }
            });
            t.start();
        }

    /**
     * @param str is the name of scheme
     */
    public void sendSchema(final String str) {
        Thread t = new Thread(() -> {
            try {
                server.sendSchema(str, hand.getView().getName());
            } catch (RemoteException e) {
                Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
            }
        });
        t.start();

    }

    /**
     * @param nickname is the name of player
     */
    public void login(String nickname) {
        try {
            server.login(client, nickname);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * disconnect the player
     */
    public void disconnect() {
        Thread t = new Thread(()-> {
            try {
                server.disconnected(hand.getView().getName());
            } catch (RemoteException e) {
                Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
            }

        }); t.start();

    }

    /**
     * used to insert dice to scheme from diceSpace
     * @param indexDiceSpace is index of dice space
     * @param row is index of row
     * @param column is index of column
     */
    public void insertDice(final int indexDiceSpace, final int row, final int column) {
        Thread t = new Thread(() -> {
            try {
                server.insertDice(hand.getView().getName(), indexDiceSpace, row, column);
            } catch (RemoteException e) {
                Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
            }
        });
        t.start();
    }

    /**
     * invoked when you want use tool card
     * @param toolNumber is tool card's number
     */
    public void useToolCard(int toolNumber) {
        try {
            server.useToolCard(hand.getView().getName(), toolNumber);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * invoked when you want move dice in scheme
     * @param oldRow is the row from take dice
     * @param oldColumn is the column from take dice
     * @param newRow is the row to move dice
     * @param newColumn is the column to move dice
     */
    public void moveDice(final int oldRow, final int oldColumn, final int newRow, final int newColumn) {
        Thread t = new Thread(() -> {
            try {
                server.moveDice(hand.getView().getName(), oldRow, oldColumn, newRow, newColumn);
            } catch (RemoteException e) {
                Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
            }
        });
        t.start();

    }

    /**
     * is invoked when use draft dice
     * @param indexDiceSpace is index of dice space
     */
    public void sendDraft(int indexDiceSpace) {
        try {
            server.draftDice(hand.getView().getName(), indexDiceSpace);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * is invoked when use place dice
     * @param row is row index of scheme
     * @param column is column index of scheme
     */
    public void sendPlaceDice(int row, int column) {
        try {
            server.placeDice(hand.getView().getName(), row, column);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * is invoked when use changeValue
     @param change is "decrement" or "increment"
     */
    public void changeValue(String change) {
        try {
            server.changeValue(hand.getView().getName(), change);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * is invoked when use roll dice
     */
    public void rollDice() {
        try {
            server.rollDice(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * take a dice from round track
     * @param numRound is the number of round
     * @param indexDice is index of dice
     */
    public void swapDice(int numRound, int indexDice) {
        try {
            server.swapDice(hand.getView().getName(), numRound, indexDice);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * invoked when use cancel tool card
     */
    public void cancelUseToolCard() {
        try {
            server.cancelUseToolCard(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * turn to opposite face of dice
     */
    public void flipDice() {
        try {
            server.flipDice(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * place dices in dice space
     */
    public void placeDiceSpace() {
        try {
            server.placeDiceSpace(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * roll dices in dice space
     */
    public void rollDiceSpace() {
        try {
            server.rollDiceSpace(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * exchange dice with dice bag
     */
    public void swapDiceBag() {
        try {
            server.swapDiceBag(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * is invoked when choose value of dice
     * @param value is the new value of dice
     */
    public void chooseValue(int value) {
        try {
            server.chooseValue(hand.getView().getName(), value);
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * send end turn message
     */
    public void sendEndTurn() {
        try {
            server.sendEndTurn(hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

    /**
     * send custom scheme to server
     * @param schema is the name of custom schema
     */
    public void sendCustomSchema(String schema) {
        try {
            server.sendCustomSchema(schema, hand.getView().getName());
        } catch (RemoteException e) {
            Message.print(SERVER_CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }
    }

}

