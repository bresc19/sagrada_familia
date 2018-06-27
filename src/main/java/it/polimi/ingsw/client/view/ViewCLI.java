package it.polimi.ingsw.client.view;

import com.google.gson.Gson;
import it.polimi.ingsw.client.clientConnection.Connection;
import it.polimi.ingsw.client.clientConnection.rmi.RmiConnection;
import it.polimi.ingsw.client.clientConnection.socket.SocketConnection;
import it.polimi.ingsw.client.exceptions.WrongInputException;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.client.constants.MessageConstants.PAINT_ROW;
import static it.polimi.ingsw.client.constants.TimerConstants.LOBBY_TIMER_VALUE;

public class ViewCLI implements View {
    private Scanner input;
    private String username;
    private Connection connection;
    private Handler hand;
    private HashMap<String, Schema> schemas;
    private ArrayList<String> moves;
    private String privateObjective;
    private List<String> publicObjcective;
    private List<Integer> toolCard;
    private List<Dices> diceSpace;
    private boolean correct;
    private boolean myTurn = false;
    private int row;
    private int column;
    private int indexDiceSpace;
    private int nPlayer;
    private int round;
    private boolean gameRunning;
    private Thread schemaThread;
    private Dices pendingDice;
    private int opVal;
    private LoadImage load;
    private int oldRow;
    private int oldColumn;
    private int newRow;
    private int newColumn;
    private int diceValue;
    private Message console = new Message();

    public ViewCLI() {
        load = new LoadImage();
        opVal = 0;
        round = 0;
        username = "";
        gameRunning = true;
        moves = new ArrayList<String>();
        input = new Scanner(System.in);
        schemas = new HashMap<String, Schema>();
        privateObjective = "";
        publicObjcective = new ArrayList<String>();
        toolCard = new ArrayList<>();
        diceSpace = new ArrayList<Dices>();
    }


    // set method
    public void setScene(String scene) {
        if (scene.equals("connection")) this.setConnection();
        else if (scene.equals("login")) this.setLogin();
    }

    // used to connect with server
    private void setConnection() {
        correct = false;
        String choose;
        while (!correct) {
            console.print("Scegli la tua connessione",TypeMessage.INFO_MESSAGE);
            console.print("1 ----> Socket",TypeMessage.INFO_MESSAGE);
            console.print("2 ----> Rmi",TypeMessage.INFO_MESSAGE);
            choose = input.nextLine();
            if (choose.equals("1")) {
                try {
                    connection = new SocketConnection(hand);
                    Thread t = new Thread((SocketConnection) connection);
                    t.start();
                    correct = true;
                } catch (IOException e) {
                    console.print("Errore di connessione con il server, riprovare",TypeMessage.ERROR_MESSAGE);
                }
            } else if (choose.equals("2")) {
                try {
                    connection = new RmiConnection(hand);
                    correct = true;
                } catch (RemoteException e) {
                    console.print("Errore di connessione con il server, riprovare", TypeMessage.ERROR_MESSAGE);
                }

            }
        }
        this.startScene();
    }

    // used to login with server
    private void setLogin() {
        username = "";
        while (username.equals("")) {
            console.print("Inserisci il tuo username:",TypeMessage.INFO_MESSAGE);
            username = input.nextLine();
            if(username.length()>8){
                console.print("Errore, nickname troppo lungo",TypeMessage.ERROR_MESSAGE);
                username ="";
            }
        }
        connection.login(username);

    }

    // used to choose the own schema
    public void setSchemas(List<String> schemas) {
        console.print("Scrivi il nome dello schema che preferisci tra:",TypeMessage.INFO_MESSAGE);
        HashMap<String, Schema> selSchema = new HashMap<String, Schema>();
        for (String nameSchema : schemas) {
            selSchema.put(nameSchema, new Schema().InitSchema("SchemaClient/" + nameSchema));
            selSchema.get(nameSchema).splitImageSchema();
        }

        showSchemas(selSchema);

        console.print("\nOppure carica uno schema personalizzato(load)", TypeMessage.INFO_MESSAGE);

        schemaThread = new Thread(() -> {
            try {
                String nameSchema;
                nameSchema = input.nextLine();
                if (!schemaThread.isInterrupted())
                    if (!nameSchema.equals("load"))
                        connection.sendSchema(nameSchema);
                    else
                        loadSchema();
            } catch (Exception e) {
                console.print("Schema già inserito",TypeMessage.INFO_MESSAGE);
            }
        });
        schemaThread.start();
        System.out.println("\n");
    }

    // used to set round's diceSpace
    public void setDiceSpace(List<String> colours, List<Integer> values) {
        diceSpace.clear();
        for (int i = 0; i < colours.size(); i ++) {
            diceSpace.add(new Dices("", values.get(i), Colour.stringToColour(colours.get(i))));
        }
    }

    // invoked by server to accept InsertDice action
    public void insertDiceAccepted() {
        diceSpace.get(indexDiceSpace).setConstraint(this.schemas.get(username).getGrid()[row][column].getConstraint());
        this.schemas.get(username).getGrid()[row][column] = diceSpace.get(indexDiceSpace);
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
    }

    // used to remove Dice from DiceSpace
    public void pickDiceSpace(int index) {
        diceSpace.remove(index);
    }

    // used to notify the user of an insertDiceSpace error
    public void pickDiceSpaceError() {
        console.print("Indice della riserva non corretto",TypeMessage.ERROR_MESSAGE);
    }

    // used to place a Dice in Schema
    public void placeDiceSchema(String nickname, int row, int column, String colour, int value) {
        if (!nickname.equals(username)) {
            this.schemas.get(nickname).getGrid()[row][column] =
                    new Dices("", value, Colour.stringToColour(colour));
        }
    }

    // used to notify the user of an placeDiceSchema error
    public void placeDiceSchemaError() {
        console.print("Errore nell'inserimento del dado",TypeMessage.ERROR_MESSAGE);
    }

    // set private objective card
    public void setPrivateCard(String colour) {
        console.print("il tuo obiettivo privato sarà il colore: " + colour + "\n",TypeMessage.INFO_MESSAGE);
        privateObjective = colour;
    }

    // set public objective card used in the game
    public void setPublicObjectives(List<String> cards) {
        publicObjcective = cards;
        System.out.println("\n");
    }

    // set tool cards used in the game
    public void setToolCards(List<Integer> cards) {
        toolCard = cards;
        System.out.println("\n");
    }


    public void setHandler(Handler hand) {
        this.hand = hand;
    }

    // create your custom schema
    private void createSchema() {
        int nCostraint = 0;
        console.print(" Hai scelto di creare la tua griglia: ",TypeMessage.INFO_MESSAGE);
        Schema sc = new Schema();
        console.print("Scegli il nome della griglia:",TypeMessage.INFO_MESSAGE);
        sc.setName(input.nextLine());

        for (int i = 0; i < sc.getGrid().length; i++) {
            for (int j = 0; j < sc.getGrid()[0].length; j++) {
                console.print("Inserisci la restrizione della cella:" + "[" + i + "][" + j + "]",TypeMessage.INFO_MESSAGE);
                if (setConstraint(sc, i, j))
                    nCostraint++;
            }
        }
        sc.setDifficult(nCostraint);
        console.print("La griglia che hai creato è questa:",TypeMessage.INFO_MESSAGE);
        sc.splitImageSchema();
        sc.showImage();
        console.print("Desideri apportare modifiche? y si",TypeMessage.INFO_MESSAGE);
        if (input.nextLine().equals("y"))
            modifySchema(sc);
        console.print("Vuoi salvare su file il tuo schema? y si",TypeMessage.INFO_MESSAGE);
        try {
            saveSchema(sc);
        } catch (IOException e) {
            console.print("Errore con il salvataggio dello schema",TypeMessage.ERROR_MESSAGE);
        }
    }

    // set constraint of custom scheme
    private boolean setConstraint(Schema sc, int i, int j) {
        boolean isConstraint = true;
        String constraint;
        boolean correct = false;
        while (!correct) {
            correct = true;
            System.out.println("1) colore " + Colour.colorString("verde(g)-", Colour.ANSI_GREEN) + Colour.colorString("rosso(r)-", Colour.ANSI_RED) + Colour.colorString("blu(b)-", Colour.ANSI_BLUE) + Colour.colorString("giallo(y)", Colour.ANSI_YELLOW) + Colour.colorString("-viola(p)", Colour.ANSI_PURPLE));
            console.print("2) numero 1-6",TypeMessage.INFO_MESSAGE);
            console.print("3) nessuna restrizione (e)",TypeMessage.INFO_MESSAGE);
            constraint = input.nextLine();
            char word = constraint.charAt(0);
            switch (word) {
                case 'r':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_RED.escape());
                    break;
                case 'g':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_GREEN.escape());
                    break;
                case 'y':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_YELLOW.escape());
                    break;
                case 'b':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_BLUE.escape());
                    break;
                case 'p':
                    sc.getGrid()[i][j].setConstraint(Colour.ANSI_PURPLE.escape());
                    break;
                case '1':
                    sc.getGrid()[i][j].setConstraint("1");
                    break;
                case '2':
                    sc.getGrid()[i][j].setConstraint("2");
                    break;
                case '3':
                    sc.getGrid()[i][j].setConstraint("3");
                    break;
                case '4':
                    sc.getGrid()[i][j].setConstraint("4");
                    break;
                case '5':
                    sc.getGrid()[i][j].setConstraint("5");
                    break;
                case '6':
                    sc.getGrid()[i][j].setConstraint("6");
                    break;
                case 'e': {
                    sc.getGrid()[i][j].setConstraint("");
                    isConstraint = false;
                }
                break;
                default:
                    correct = false;

            }
            if (!sc.nearConstraint(i, j, sc.getGrid()[i][j].getConstraint())) {
                correct = false;
                console.print(" Restrizione già immessa nelle caselle adiacenti\nSelezionarne un'altra\n",TypeMessage.ERROR_MESSAGE);
            }
        }

        return isConstraint;
    }

    // used to modify custom schema
    private void modifySchema(Schema s) {
        int row, column;
        correct = false;
        while (!correct) {


            try {
                console.print("Inserisci la riga che vuoi modificare",TypeMessage.INFO_MESSAGE);
                row = Integer.parseInt(input.nextLine());

                console.print("Inserisci la colonna che vuoi modificare",TypeMessage.INFO_MESSAGE);
                column = Integer.parseInt(input.nextLine());

                console.print("Row" + row + "Column" + column,TypeMessage.INFO_MESSAGE);

                setConstraint(s, row - 1, column - 1);

                console.print("Modifica avvenuta!",TypeMessage.CONFIRM_MESSAGE);

                console.print("Questo è il tuo schema",TypeMessage.INFO_MESSAGE);
                s.splitImageSchema();
                s.showImage();

                console.print("Vuoi modificare ancora la griglia? y si",TypeMessage.INFO_MESSAGE);

                if (input.nextLine().equals("y"))
                    correct = false;
                else
                    correct = true;

            } catch (NumberFormatException e) {
                correct = false;
            }
        }
    }

    // used to save custom schema
    private void saveSchema(Schema s) throws IOException {
        String path = "src/main/resources/data/SchemaPlayer/"; // todo metterlo da file di configurazione
        String schema;
        Gson g = new Gson();
        s.setPaint(null);
        schema = g.toJson(s);
        correct = false;

        while (!correct) {
            String copyPath;
            copyPath = path + s.getName() + ".json";
            FileWriter fw;
            BufferedWriter b;
            File file = new File(copyPath);

            if (file.exists())
                console.print("Il file " + copyPath + " esiste già",TypeMessage.ERROR_MESSAGE);
            else if (file.createNewFile()) {
                console.print("Il file " + copyPath + " è stato creato",TypeMessage.CONFIRM_MESSAGE);
                fw = new FileWriter(file);
                b = new BufferedWriter(fw);
                b.write(schema);
                b.flush();
                fw.close();
                b.close();
                correct = true;
            } else
                console.print("Il file " + path + " non può essere creato",TypeMessage.ERROR_MESSAGE);

        }

    }

    // used to set opponents schemas
    public void setOpponentsSchemas(List<String> s) {
        clearScreen();
        Schema temp = new Schema();

        for (int i = 0; i < s.size(); i = i + 2) {
            if (!s.get(i).equals(this.getName()))
                schemas.put(s.get(i), temp.InitSchema("SchemaClient/" + s.get(i + 1)));
        }
    }

    // used to set the number of player in the game
    public void setNumberPlayer(int nPlayer) {
        this.nPlayer = nPlayer;
    }

    // invoked when start the rounds
    public void startRound() {
        round++;
        if (round == 1) {
            Thread thread = new Thread(() -> {
                while (gameRunning)
                    chooseMoves();
            });
            thread.start();
        }
    }

    // used to set legal action
    public void setActions(List<String> actions) {
        String move;
        moves.clear();
        moves.add("Mostra l'obiettivo privato");
        moves.add("Mostra gli obiettivi pubblici");
        moves.add("Mostra schemi avversari");
        moves.add("Mostra le carte utensili");
        moves.add("Mostra il tracciato dei round");
        moves.add("Mostra il tuo schema");
        if (actions == null)
            return;
        for (String action : actions) {
            move = action;
            switch (action) {
                case "InsertDice":
                    move = "Inserisci un dado";
                    break;
                case "DraftDice":
                    move = "Prendi un dado dalla riserva";
                    break;
                case "CancelUseToolCard":
                    move = "Annulla uso della carta utensile";
                    break;
                case "UseToolCard":
                    move = "Utilizza la carta utensile";
                    break;
                case "EndTurn":
                    move = "Passa il turno";
                    break;
                case "RollDice":
                    move = "Tira il dado";
                    break;
                case "PlaceDice":
                    move = "Piazza il dado";
                    break;
                case "RollDiceSpace":
                    move = "Tira i dadi della riserva";
                    break;
                case "MoveDice":
                    move = "Muovi il dado";
                    break;
                case "FlipDice":
                    move = "Gira il dado sulla faccia opposta";
                    break;
                case "SwapDice":
                    move = "Scambia il dado con un dado nel tracciato dei round";
                    break;
                case "PlaceDiceSpace":
                    move = "Lascia il dado nella riserva";
                    break;
                case "SwapDiceBag":
                    move = "Pesca un dado dalla borsa dei dadi";
                    break;
                case "ChangeValue":
                    move = "Aumenta/Diminuisci il valore del dado di 1";
                    break;
            }
            moves.add(move);
        }
        showMoves();
    }

    // show method


    private void showDiceSpace() {
        for (Dices d : diceSpace)
            System.out.print(d.toString());

        System.out.println("\n");
    }

    private void showOpponentsSchemas(HashMap<String, Schema> schema) {
        schema.remove(username);
        for (String key : schema.keySet()) {
            schema.get(key).splitImageSchema();
            schema.get(key).getPaint()[0] = key;
        }
        showSchemas(schema);
    }

    private void showSchemas(HashMap<String, Schema> schema) {
        for (int i = 0; i < PAINT_ROW; i++) {
            for (String key : schema.keySet()) {
                System.out.print(schema.get(key).getPaint()[i]);
                if (i < 3 || i > 6) {
                    for (int j = schema.get(key).getPaint()[i].length(); j < 45; j++)
                        System.out.print(" ");
                } else {
                    for (int x = 32; x < 46; x++)
                        System.out.print(" ");
                }
            }
            System.out.print("\n");
        }
    }

    private void showPrivateObjective() {
        console.print("Il tuo obiettivo privato è:" + privateObjective,TypeMessage.INFO_MESSAGE);
    }

    private void showPublicObjective() {
        console.print("Gli obiettivi pubblici della partita sono:",TypeMessage.INFO_MESSAGE);
        for (String pub : publicObjcective)
            console.print(pub,TypeMessage.INFO_MESSAGE);
    }

    private void showToolCard() {
        console.print("Le carte utensili sono:",TypeMessage.INFO_MESSAGE);
        for (Integer tool : toolCard)
            try {
                switch (tool) {
                    case 1:
                        console.print("1)Dopo aver scelto un dado,aumenta o diminuisci il valore del dado scelto di 1", TypeMessage.INFO_MESSAGE);
                        break;
                    case 2:
                        console.print("2)Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore", TypeMessage.INFO_MESSAGE);
                        break;
                    case 3:
                        console.print("3)Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore", TypeMessage.INFO_MESSAGE);
                        break;
                    case 4:
                        console.print("4)Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento", TypeMessage.INFO_MESSAGE);
                        break;
                    case 5:
                        console.print("5)Dopo aver scelto un dado,scambia quel dado con un dado sul tracciato dei round", TypeMessage.INFO_MESSAGE);
                        break;
                    case 6:
                        console.print("6)Dopo aver scelto un dado, tira nuovamente quel dado", TypeMessage.INFO_MESSAGE);
                        break;
                    case 7:
                        console.print("7)Tira nuovamente tutti i dadi della riserva", TypeMessage.INFO_MESSAGE);
                        break;
                    case 8:
                        console.print("8)Dopo il tuo primo turno scegli immediatamente un altro dado", TypeMessage.INFO_MESSAGE);
                        break;
                    case 9:
                        console.print("9)Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado", TypeMessage.INFO_MESSAGE);
                        break;
                    case 10:
                        console.print("10)Dopo aver scelto un dado, giralo sulla faccia opposta", TypeMessage.INFO_MESSAGE);
                        break;
                    case 11:
                        console.print("11)Dopo aver scelto un dado, riponilo nel sacchetto, poi pescane uno dal sacchetto", TypeMessage.INFO_MESSAGE);
                        break;
                    default:
                        console.print("12)Muovi fino a due dadi dello stesso colore di un solo dado sul tracciato dei round", TypeMessage.INFO_MESSAGE);
                }
            } catch (NumberFormatException e) {
                console.print("Errore con la visualizzazione della carta utensile",TypeMessage.ERROR_MESSAGE);
            }
    }

    private void showMoves() {
        console.print("Scegli una tra le seguenti opzioni:", TypeMessage.INFO_MESSAGE);
        for (int i = 0; i < moves.size(); i++)
            console.print((i + 1) + ")" + moves.get(i),TypeMessage.INFO_MESSAGE);
    }

    private void showRoundTrack() {
        if (roundTrack.isEmpty())
            console.print("Tracciato dei round vuoto",TypeMessage.ERROR_MESSAGE);

        for (int i = 0; i < roundTrack.size(); i++) {
            console.print("Round" + (i + 1),TypeMessage.INFO_MESSAGE);
            for (Dices dices : roundTrack.get(i))
                System.out.print(dices.toString());
            System.out.println("\n");
        }
    }

    // action method

    public void startScene() {
        int choose;

        console.print("Prima di cominciare...",TypeMessage.INFO_MESSAGE);
        while (!correct) {
            try {
                correct = true;
                console.print("Desideri:",TypeMessage.INFO_MESSAGE);
                console.print("1)Costruire il tuo schema",TypeMessage.INFO_MESSAGE);
                console.print("2)Utilizzare schemi predefiniti",TypeMessage.INFO_MESSAGE);
                choose = Integer.parseInt(input.nextLine());
                switch (choose) {
                    case 1:
                        createSchema();
                        break;
                    case 2:
                        break;
                    default:
                        correct = false;
                }
            } catch (NumberFormatException e) {
                console.print("Scelta non valida",TypeMessage.ERROR_MESSAGE);
            }
        }

    }

    // used to load a custom schema
    private void loadSchema() {
        String name;
        final String path = "src/main/resources/data/SchemaPlayer";

        File f = new File(path);
        Schema sc = new Schema();

        console.print("Scegli il nome dello schema da caricare",TypeMessage.INFO_MESSAGE);
        if (f.list().length == 0) {
            console.print(" Nessuno schema da caricare, te ne verrà assegnato uno allo scadere del tempo ",TypeMessage.ERROR_MESSAGE);
            return;
        }

        for (String file : f.list())
            console.print(file.substring(0, file.length() - 5),TypeMessage.INFO_MESSAGE);
        name = input.nextLine();
        try {
            connection.sendCustomSchema(sc.getGson("SchemaPlayer/" + name));
            console.print("Hai caricato questo schema",TypeMessage.CONFIRM_MESSAGE);
            schemas.put(username, sc.InitSchema("SchemaPlayer/" + name));
            schemas.get(username).splitImageSchema();
            schemas.get(username).showImage();
            correct = true;
        } catch (IOException e) {
            console.print("Errore con lo schema da caricare,te ne verrà assegnato uno allo scadere del tempo",TypeMessage.ERROR_MESSAGE);
        }

    }


    // invoked by server to log the player
    public void login(String str) {
        if (str.equals("Welcome")) {
            console.print("La partita inizierà a breve",TypeMessage.INFO_MESSAGE);
            console.print("Aspettando altri giocatori...",TypeMessage.INFO_MESSAGE);
        } else if (str.equals("Login_error-username")) {
            console.print("Errore, nickname già in uso", TypeMessage.ERROR_MESSAGE);
            this.setLogin();
        } else if (str.equals("Login_error-game")) {
            console.print("Errore, partita già in corso", TypeMessage.ERROR_MESSAGE);
            console.print("Riprovare più tardi",TypeMessage.INFO_MESSAGE);
        }
    }

    // notifies the user that another player has joined the game
    public void playerConnected(String name) {
        console.print(" \r"+name + " si è aggiunto alla lobby\n",TypeMessage.INFO_MESSAGE);
        nPlayer++;
    }

    // notifies the user that another player has left the game
    public void playerDisconnected(String name) {
        console.print("\r"+name + " si è disconnesso\n",TypeMessage.INFO_MESSAGE);
        nPlayer--;
    }

    // display timer connection
    public void timerPing(String time) {
        Colour colour ;
        int percent = (int) (((LOBBY_TIMER_VALUE - Double.parseDouble(time)) / LOBBY_TIMER_VALUE) * 100);
        System.out.print("\r" + Colour.colorString("L", Colour.ANSI_GREEN) + Colour.colorString("o", Colour.ANSI_RED) + Colour.colorString("a", Colour.ANSI_BLUE) + Colour.colorString("d", Colour.ANSI_YELLOW) + Colour.colorString("i", Colour.ANSI_PURPLE) + Colour.colorString("n", Colour.ANSI_GREEN) + Colour.colorString("g", Colour.ANSI_BLUE));
        for (int i = 0; i < percent; i++) {
            switch (i / 20) {
                case 0:
                    colour = Colour.ANSI_GREEN;
                    break;
                case 1:
                    colour = Colour.ANSI_RED;
                    break;
                case 2:
                    colour = Colour.ANSI_BLUE;
                    break;
                case 3:
                    colour = Colour.ANSI_YELLOW;
                    break;
                default:
                    colour = Colour.ANSI_PURPLE;
            }

            System.out.print(Colour.colorString("▋", colour));
        }
        for (int i = percent; i < 100; i++) {
            System.out.print(" ");
        }
        System.out.print(Colour.colorString(percent + "%", Colour.ANSI_BLUE));
    }

    // invoked when the game starts
    public void createGame() {
        clearScreen();
        console.print("\nPartita creata\n",TypeMessage.CONFIRM_MESSAGE);
    }


    // invoked when user's schema has been accepted
    public void chooseSchema(String name) {
        Schema s = new Schema();
        schemas.put(this.getName(), s.InitSchema("SchemaClient/" + name));
        console.print("schema approvato: " + name,TypeMessage.CONFIRM_MESSAGE);


        console.print("Aspettando la scelta degli altri giocatori...\n",TypeMessage.INFO_MESSAGE);
        if (schemaThread.isAlive()) {
            console.print("Tempo scaduto! Schema scelto dal server!", TypeMessage.CONFIRM_MESSAGE);
            console.print("Premi invio!",TypeMessage.CONFIRM_MESSAGE);
            schemaThread.interrupt();
            try {
                schemaThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // invoked when the turn starts
    public void startTurn(String name) {
        clearScreen();
        try {
            load.displayImage("Round" + round + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        console.print("\nIl tuo schema:",TypeMessage.INFO_MESSAGE);
        showMyschema();
        showDiceSpace();
        if (!name.equals(username)) {
            myTurn = false;
            console.print("Turno iniziato, tocca a: " + name,TypeMessage.CONFIRM_MESSAGE);
            setActions(null);
            showMoves();
        } else {
            myTurn = true;
            console.print("Tocca a te!",TypeMessage.CONFIRM_MESSAGE);
        }
    }


    private void chooseMoves() {
        String move;
        int choose;
        boolean right = false;
        //to be modified later
        while (!right) {
            right = true;
            try {
                move = input.nextLine();
                if (move.equals(""))
                    throw new WrongInputException();

                choose = Integer.parseInt(move);
                if (choose > moves.size() || choose < 1)
                    throw new WrongInputException();
                switch(moves.get(choose - 1))
                {
                    case "Inserisci un dado": insertDice(); break;
                    case "Utilizza la carta utensile": useToolCard(); break;
                    case "Muovi il dado":  moveDice(); break;
                    case "Tira il dado":rollDice(); break;
                    case "Passa il turno" : passTurn(); break;
                    case "Prendi un dado dalla riserva": draftDice(); break;
                    case "Piazza il dado": placeDice(); break;
                    case "Aumenta/Diminuisci il valore del dado di 1": changeValue(); break;
                    case "Scambia il dado con un dado nel tracciato dei round" : swapDice(); break;
                    case "Annulla uso della carta utensile": cancelToolCard(); break;
                    case "Gira il dado sulla faccia opposta" : flipDice(); break;
                    case "Lascia il dado nella riserva" : placeDiceSpace(); break;
                    case "Tira i dadi della riserva" : rollDiceSpace(); break;
                    case "Pesca un dado dalla borsa dei dadi": swapDiceBag(); break;
                    case "ChooseValue": chooseValue(); break;
                }

                switch (choose - 1) {
                    case 0: showPrivateObjective();
                        break;
                    case 1: showPublicObjective();
                        break;
                    case 2: showOpponentsSchemas((HashMap<String, Schema>) schemas.clone());
                        break;
                    case 3: showToolCard();
                        break;
                    case 4: showRoundTrack();
                        break;
                    case 5: showMyschema();
                        break;
                    default:
                        right = false;
                }


            } catch (WrongInputException e) {
                e.getMessage();
                right = false;
            } catch (NumberFormatException e) {
                console.print("Inserisci un numero",TypeMessage.INFO_MESSAGE);
                right = false;
            }
        }
    }

    private void rollDiceSpace() {
        console.print("Rolliamo 'sti dadi",TypeMessage.CONFIRM_MESSAGE);
        connection.rollDiceSpace();
    }

    public void placeDiceSpace() {
        connection.placeDiceSpace();
    }

    private void cancelToolCard() {
        connection.cancelUseToolCard();
    }

    private void showMyschema() {
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
    }

    public void rollDice() {
        console.print("Vuoi davvero rollare il dado?",TypeMessage.INFO_MESSAGE);
        input.nextLine();
        console.print("Era retorico.",TypeMessage.CONFIRM_MESSAGE);
        console.print("Rolliamo sto dado",TypeMessage.CONFIRM_MESSAGE);
        connection.rollDice();
    }

    private void changeValue() {
        console.print("Vuoi incrementare(I) o decrementare(D) il dado?",TypeMessage.INFO_MESSAGE);

        boolean right = false;
        while (!right) {

            String choose = input.nextLine();
            switch (choose)
            {
                case "I": {
                    opVal = 1;
                    connection.changeValue("Increment");
                    right = true;
                    break;
                }
                case "D": {
                    opVal = -1;
                    connection.changeValue("Decrement");
                    right = true;
                    break;
                }
                default: {
                    console.print("Parametro non corretto",TypeMessage.ERROR_MESSAGE);
                    right = false;
                }
            }

        }
    }

    private void placeDice() {
        boolean right = false;
        while (!right) {
            try {
                console.print("Inserisci la riga in cui inserire il dado",TypeMessage.INFO_MESSAGE);
                row = Integer.parseInt(input.nextLine()) - 1;
                console.print("Inserisci la colonna in cui inserire il dado",TypeMessage.INFO_MESSAGE);
                column = Integer.parseInt(input.nextLine()) - 1;
                if (row < 0 || row > 3 || column < 0 || column > 4)
                    throw new NumberFormatException();
                right = true;
                connection.sendPlaceDice(row, column);
            } catch (NumberFormatException ex) {
                console.print("Inserimento non valido",TypeMessage.ERROR_MESSAGE);
                right = false;
            }
        }
    }

    private void draftDice() {
        console.print("Inserisci l'indice del dado della riserva:(Da 1 a " + diceSpace.size() + ")",TypeMessage.INFO_MESSAGE);
        indexDiceSpace = Integer.parseInt(input.nextLine()) - 1;
        connection.sendDraft(indexDiceSpace);
    }

    private void passTurn() {
        this.myTurn = false;
        connection.sendEndTurn();
    }

    public void insertDice() {
        correct = false;
        while (!correct) {
            try {
                console.print("Inserisci l'indice del dado della riserva:(Da 1 a " + diceSpace.size() + ")",TypeMessage.INFO_MESSAGE);
                indexDiceSpace = Integer.parseInt(input.nextLine());
                indexDiceSpace--;
                if (indexDiceSpace < 0 || indexDiceSpace > diceSpace.size())
                    throw new NumberFormatException();

                console.print("Inserisci la riga",TypeMessage.INFO_MESSAGE);
                row = Integer.parseInt(input.nextLine());
                row--;
                if (row < 0 || row > 3)
                    throw new NumberFormatException();

                console.print("Inserisci la colonna",TypeMessage.INFO_MESSAGE);
                column = Integer.parseInt(input.nextLine());
                column--;
                if (column < 0 || column > 4)
                    throw new NumberFormatException();

                correct = true;
                connection.insertDice(indexDiceSpace, row, column);

            } catch (NumberFormatException e) {
                console.print("Formato non valido", TypeMessage.ERROR_MESSAGE);
            }
        }

    }

    private void clearScreen() {
        for (int i = 0; i < 10; i++)
            System.out.print("\n");
    }

    private void useToolCard() {
        correct = false;
        console.print("Scegli la tool card da utilizzare:",TypeMessage.INFO_MESSAGE);
        for (Integer s : toolCard)
            console.print(s.toString(),TypeMessage.INFO_MESSAGE);
        int tool = Integer.parseInt(input.nextLine());
        connection.useToolCard(tool);
    }

    public void useToolCardAccepted(int favor) {
        console.print("Carta utilizzata! Favori rimanenti" + favor,TypeMessage.CONFIRM_MESSAGE);
    }

    public void useToolCardError() {
        console.print("Non hai abbastanza favori oppure non è permesso l'azione svolta dalla toolCard",TypeMessage.ERROR_MESSAGE);
    }

    public void changeValueAccepted() {
        console.print(opVal+"",TypeMessage.INFO_MESSAGE);
        pendingDice.setNumber(pendingDice.getNumber() + opVal);
        console.print("Cambiamento valore accettato " + pendingDice,TypeMessage.CONFIRM_MESSAGE);
    }

    public void changeValueError() {
        console.print("Errore di cambiamento del valore",TypeMessage.ERROR_MESSAGE);
    }

    public void placeDiceAccepted() {
        console.print("Dado inserito correttamente",TypeMessage.INFO_MESSAGE);
        schemas.get(username).getGrid()[row][column].setNumber(pendingDice.getNumber());
        schemas.get(username).getGrid()[row][column].setColour(pendingDice.getColour());
        pendingDice = null;
    }

    public void rollDiceAccepted(int value) {
        pendingDice.setNumber(value);
        console.print("Valore del dado " + pendingDice,TypeMessage.INFO_MESSAGE);
    }

    private List<List<Dices>> roundTrack = new ArrayList<List<Dices>>();

    public void pickDiceRoundTrack(int nRound, int nDice) {
        pendingDice = roundTrack.get(nRound).get(nDice);
        roundTrack.get(nRound).remove(nDice);
    }

    public void pickDiceRoundTrackError() {
        console.print("Errore,dado non trovato",TypeMessage.ERROR_MESSAGE);
    }

    public void placeDiceRoundTrack(int nRound, List<String> colours, List<Integer> values) {
        int roundNumber = nRound;
        if (roundNumber > roundTrack.size() - 1)
            roundTrack.add(new ArrayList<Dices>());
        for (int i = 0; i < colours.size(); i ++) {
            roundTrack.get(roundNumber).add(new Dices("", values.get(i), Colour.stringToColour((colours.get(i)))));
        }
    }

    public void swapDiceAccepted() {
        console.print("Dado scambiato correttamente",TypeMessage.INFO_MESSAGE);
    }

    public void cancelUseToolCardAccepted(int favor) {
        console.print("Azione annullata,favori rimanenti " + favor,TypeMessage.CONFIRM_MESSAGE);
    }

    public void flipDiceAccepted(int value) {
        console.print("Dado flippato",TypeMessage.CONFIRM_MESSAGE);
        pendingDice.setNumber(value);
    }

    public void placeDiceSpaceAccepted() {
        console.print("Dado inserito correttamente",TypeMessage.CONFIRM_MESSAGE);
        pendingDice = null;
    }

    public void placeDiceSpace(String colour, int value) {
        diceSpace.add(new Dices("", value, Colour.stringToColour((colour))));
    }

    public void rollDiceSpaceAccepted() {
        console.print("Abbiamo rollato 'sti dadi",TypeMessage.CONFIRM_MESSAGE);
        showDiceSpace();
    }

    public void swapDiceBagAccepted(String colour, int value) {
        pendingDice.setColour(Colour.stringToColour(colour));
        pendingDice.setNumber(value);
        console.print("valore nuovo dado: " + pendingDice,TypeMessage.INFO_MESSAGE);
    }

    public void chooseValueAccepted() {
        pendingDice.setNumber(diceValue);
        console.print("valore del dado cambiato: " + pendingDice.getNumber(),TypeMessage.INFO_MESSAGE);
    }

    public void chooseValueError() {
        console.print("errore nel cambiamento del valore del dado",TypeMessage.ERROR_MESSAGE);
    }

    private void swapDice() {
        correct = false;
        while (!correct) {
            try {
                console.print("Inserisci il numero del round da cui prendere il dado",TypeMessage.INFO_MESSAGE);
                int nRound = Integer.parseInt(input.nextLine()) - 1;
                console.print(roundTrack.get(nRound)+"",TypeMessage.INFO_MESSAGE);
                console.print("Inserisci il numero del dado che vuoi prendere dalla RoundTrack",TypeMessage.INFO_MESSAGE);
                int index = Integer.parseInt(input.nextLine()) - 1;
                connection.swapDice(nRound, index);
                correct = true;
            } catch (NumberFormatException e) {
                console.print("Parametro non valido",TypeMessage.ERROR_MESSAGE);
            } catch (IndexOutOfBoundsException ex) {
                console.print("Indice non valido",TypeMessage.ERROR_MESSAGE);
            }
        }
    }

    private void moveDice() {
        correct = false;
        while (!correct) {
            try {
                console.print("Inserisci l'indice della riga da cui prendere il dado:",TypeMessage.INFO_MESSAGE);
                oldRow = Integer.parseInt(input.nextLine()) - 1;
                console.print("Inserisci l'indice della colonna da cui prendere il dado:",TypeMessage.INFO_MESSAGE);
                oldColumn = Integer.parseInt(input.nextLine()) - 1;
                console.print("Inserisci l'indice della riga in cui spostare il dado:",TypeMessage.INFO_MESSAGE);
                newRow = Integer.parseInt(input.nextLine()) - 1;
                console.print("Inserisci l'indice della colonna in cui spostare il dado:",TypeMessage.INFO_MESSAGE);
                newColumn = Integer.parseInt(input.nextLine()) - 1;
                connection.moveDice(oldRow, oldColumn, newRow, newColumn);
                correct = true;
            } catch (NumberFormatException e) {
                console.print("Parametro non valido",TypeMessage.ERROR_MESSAGE);
            }
        }
    }

    public void draftDiceAccepted() {
        console.print("Azione accettata",TypeMessage.CONFIRM_MESSAGE);
        pendingDice = diceSpace.get(indexDiceSpace);
        console.print(pendingDice.toString(),TypeMessage.INFO_MESSAGE);
    }

    public void moveDiceAccepted() {
        this.schemas.get(username).getGrid()[newRow][newColumn].setNumber(this.schemas.get(username).getGrid()[oldRow][oldColumn].getNumber());
        this.schemas.get(username).getGrid()[newRow][newColumn].setColour(this.schemas.get(username).getGrid()[oldRow][oldColumn].getColour());
        this.schemas.get(username).getGrid()[oldRow][oldColumn].setNumber(0);
        this.schemas.get(username).getGrid()[oldRow][oldColumn].setColour(null);

        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
        console.print("Dado spostato correttamente",TypeMessage.CONFIRM_MESSAGE);
    }

    public void pickDiceSchema(String nickname, int row, int column) {
        if (!nickname.equals(username))
            this.schemas.get(nickname).getGrid()[row][column] = new Dices("", 0, null);
    }

    public void pickDiceSchemaError() {
        console.print("Dado non trovato",TypeMessage.ERROR_MESSAGE);
    }

    public String getName() {
        return this.username;
    }

    private void flipDice() {
        console.print("Flippa 'sto dado",TypeMessage.INFO_MESSAGE);
        connection.flipDice();
    }

    private void swapDiceBag() {
        console.print("Lo swappo tutto 'sto dado",TypeMessage.INFO_MESSAGE);
        connection.swapDiceBag();
    }

    private void chooseValue() {
        console.print("Scegli il valore dal dado: ",TypeMessage.INFO_MESSAGE);
        diceValue = Integer.parseInt(input.nextLine());
        connection.chooseValue(diceValue);
    }

    public void schemaCustomAccepted(String name) {
        Schema s = new Schema();
        schemas.put(this.getName(), s.InitSchema("SchemaPlayer/" + name));
        console.print("schema approvato: " + name,TypeMessage.CONFIRM_MESSAGE);
        console.print("Aspettando la scelta degli altri giocatori...\n",TypeMessage.INFO_MESSAGE);
    }

    public void setOpponentsCustomSchemas(List<String> s) {
        Gson g = new Gson();
        clearScreen();
        Schema temp;

        for (int i = 0; i < s.size(); i = i + 2) {
            if (!s.get(i).equals(this.getName())) {
                temp = g.fromJson(s.get(i + 1), Schema.class);
                schemas.put(s.get(i), temp);
            }
        }
    }

    public void setWinner(String nickname) {
        clearScreen();
        if(nickname.equals(username))
            console.print("Hai vinto!Complimenti!",TypeMessage.CONFIRM_MESSAGE);
        else
            console.print("Hai perso, gioca di nuovo per rifarti!",TypeMessage.CONFIRM_MESSAGE);

    }

    public void setRankings(List<String> players, List<Integer> scores) {
        //todo;
        System.out.println("Classifica: ");
        System.out.println("Pos    Giocatore     Punteggio");
        for(int i = 0; i < players.size(); i++){
            console.print("┏---┓--------------┓-----------┓",TypeMessage.INFO_MESSAGE);
            console.print("║ "+(i+1) + " ║  "+ players.get(i),TypeMessage.INFO_MESSAGE);

            for(int j = players.get(i).length();j<12;j++)
            {
                System.out.print(" ");
            }
            System.out.print("║    ");
            System.out.print(scores.get(i));
            for(int j = scores.get(i).toString().length();j<7;j++)
            {
                System.out.print(" ");
            }
            console.print("║",TypeMessage.INFO_MESSAGE);
            console.print("┗---┛--------------┛-----------┛",TypeMessage.INFO_MESSAGE);
        }
    }

}
enum TypeMessage{
    ERROR_MESSAGE("\u001B[31m"),
    CONFIRM_MESSAGE("\u001B[33m"),
    INFO_MESSAGE("");

    private String colorString;
     TypeMessage(String colorString){this.colorString = colorString;}
     public String colorString(){return colorString;}
}
class Message {
    public void print(String message, TypeMessage type) {
        System.out.println(type.colorString() + message + Colour.RESET);
    }
}