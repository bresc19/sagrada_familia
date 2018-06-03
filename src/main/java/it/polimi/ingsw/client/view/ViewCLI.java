package it.polimi.ingsw.client.view;

import com.google.gson.Gson;
import it.polimi.ingsw.client.clientConnection.Connection;
import it.polimi.ingsw.client.clientConnection.RmiConnection;
import it.polimi.ingsw.client.clientConnection.SocketConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static it.polimi.ingsw.costants.GameConstants.PAINT_ROW;
import static it.polimi.ingsw.costants.TimerCostants.LOBBY_TIMER_VALUE;


public class ViewCLI implements View{
    private Scanner input;
    private String username;
    private Connection connection;
    private Handler hand;
    private HashMap<String,Schema> schemas ;

    private ArrayList <String> moves;
    private String privateObjective;
    private List <String> publicObjcective ;
    private List <String> toolCard;
    private List <Dices> diceSpace;
    private boolean correct;
    private boolean myTurn=false;
    private int row;
    private int column;
    private int indexDiceSpace;
    private int nPlayer;
    private int round;
    private boolean gameRunning;
    Thread schemaThread ;
    private static final String operatingSystem = System.getProperty("os.name");
    public ViewCLI()
    {
        round = 0;
        username = "";
        gameRunning = true;
        moves = new ArrayList<String>();
        input = new Scanner(System.in);
        schemas = new HashMap<String, Schema>();
        privateObjective = "";
        publicObjcective = new ArrayList<String>();
        toolCard = new ArrayList<String>();
        diceSpace = new ArrayList<Dices>();
    }





// set method
    public void setScene(String scene) {
        if(scene.equals("connection"))this.setConnection();
        else if(scene.equals("login"))this.setLogin();
    }

    public void setConnection()
    {
         correct= false;
        String choose;
        while(!correct) {
            System.out.println("Scegli la tua connessione");
            System.out.println("1 ----> Socket");
            System.out.println("2 ----> Rmi");
            choose = input.nextLine();
            if (choose.equals("1"))
            {
                try {
                    connection = new SocketConnection(hand);
                    Thread t = new Thread((SocketConnection)connection);
                    t.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                correct = true;
            }
            else if (choose.equals("2"))
            {
                connection = new RmiConnection(hand);
                correct = true;
            }
            else
                correct = false;
        }
        this.startScene();
    }

    public void setLogin()
    {
        username = "";
        while(username.equals("")) {
            System.out.println("Inserisci il tuo username:");
            username = input.nextLine();
        }
        connection.login(username);

    }

    public void setSchemas(List<String> schemas){
        System.out.println("scrivi il nome dello schema che preferisci tra:");
        HashMap<String,Schema> selSchema = new HashMap<String, Schema>();
        for(String nameSchema:schemas)
        {
            try {
                selSchema.put(nameSchema,new Schema().InitSchema("SchemaClient/"+nameSchema));
                selSchema.get(nameSchema).splitImageSchema();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            showSchemas(selSchema);


        schemaThread = new Thread(new Runnable() {
            public void run() {
                try {
                    String nameSchema;
                    nameSchema = input.nextLine();
                    if(!schemaThread.isInterrupted())
                    connection.sendSchema(nameSchema);
                }catch(Exception e)
                {
                    System.out.println("Schema già inserito");
                }
            }
        });
        schemaThread.start();
        System.out.println("\n");
    }

    public void stopTakeSchema()
    {
        schemaThread.interrupt();
    }
    public void setDiceSpace(List<String> dice)
    {
        diceSpace.clear();
        for(int i=0;i<dice.size();i=i+2)
        {
            diceSpace.add(new Dices("",Integer.parseInt(dice.get(i+1)), Colour.stringToColour(dice.get(i))));
        }
    }

    public void insertDiceAccepted() {

        this.schemas.get(username).getGrid()[row][column]= diceSpace.get(indexDiceSpace);
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
    }

    public void pickDiceSpace(List action) {
        diceSpace.remove(Integer.parseInt((String)action.get(0)));
    }

    public void pickDiceSpaceError() {
        System.out.println("Indice della riserva non corretto");
     }

    public void placeDiceSchema(List action) {
        if(!action.get(0).equals(username)){
            this.schemas.get(action.get(0)).getGrid()[Integer.parseInt((String)action.get(1))][Integer.parseInt((String)action.get(2))]=
                    new Dices("",Integer.parseInt((String) action.get(4)),Colour.stringToColour((String)action.get(3)));
        }
    }


    public void placeDiceSchemaError() {
        System.out.println("errore nell'inserimento del dado");
        //todo ristampare le azioni;
    }


    public void setPrivateCard(String colour){
        System.out.println("il tuo obiettivo privato sarà il colore: " + colour + "\n");
        privateObjective = colour;
    }

    public void setPublicObjectives(List<String> cards){
        System.out.println("gli obiettivi publici per questa partita saranno:");
        for(String s: cards)
            System.out.println(s);
        publicObjcective = cards;
        System.out.println("\n");
    }

    public void setToolCards(List<String> cards){
        System.out.println("le carte utensili per questa partita saranno:");
        for(String s: cards)
            System.out.println("la carta numero " + s + ",");
        toolCard = cards;
        System.out.println("\n");
    }

    public void setHandler(Handler hand) {
        this.hand = hand;
    }

    public void createSchema() {
        int nCostraint=0;
        System.out.println(Colour.ANSI_BLUE.escape()+" Hai scelto di creare la tua griglia: "+Colour.RESET);
        Schema sc = new Schema();
        System.out.println("Scegli il nome della griglia:");
        sc.setName(input.nextLine());

        for (int i = 0; i < sc.getGrid().length; i++)
        {
            for (int j = 0; j < sc.getGrid()[0].length; j++) {
                System.out.println("Inserisci la restrizione della cella:" + "[" + i + "][" + j + "]");
                if(setCostraint(sc,i,j))
                    nCostraint++;
            }
        }
        setDifficult(sc,nCostraint);
        System.out.println("La griglia che hai creato è questa:");
        System.out.println(sc);
        System.out.println("Desideri apportare modifiche? y si");
        if(input.nextLine().equals("y"))
            modifySchema(sc);
        System.out.println("Vuoi salvare su file il tuo schema? y si");
        try {
            saveSchema(sc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        schemas.put(username,sc);
    }

    public boolean setCostraint(Schema sc,int i,int j)
    {
        boolean isCostraint=true;
        String costraint;
        correct = false;
        while (!correct) {
            correct = true;
            System.out.println("1) colore "+Colour.ANSI_GREEN.escape()+"verde(g)-"+Colour.ANSI_RED.escape()+"rosso(r)-"+Colour.ANSI_BLUE.escape()+"blu(b)-"+Colour.ANSI_YELLOW.escape()+"giallo(y)"+Colour.ANSI_PURPLE.escape()+"-viola(p)"+Colour.RESET);
            System.out.println("2) numero 1-6");
            System.out.println("3) nessuna restrizione (e)");
            costraint = input.nextLine();
            char word = costraint.charAt(0);
            switch (word) {
                case 'r':
                    sc.getGrid()[i][j].setCostraint(Colour.ANSI_RED.escape());
                    break;
                case 'g':
                    sc.getGrid()[i][j].setCostraint(Colour.ANSI_GREEN.escape());
                    break;
                case 'y':
                    sc.getGrid()[i][j].setCostraint(Colour.ANSI_YELLOW.escape());
                    break;
                case 'b':
                    sc.getGrid()[i][j].setCostraint(Colour.ANSI_BLUE.escape());
                    break;
                case 'p':
                    sc.getGrid()[i][j].setCostraint(Colour.ANSI_PURPLE.escape());
                    break;
                case '1':
                    sc.getGrid()[i][j].setCostraint("1");
                    break;
                case '2':
                    sc.getGrid()[i][j].setCostraint("2");
                    break;
                case '3':
                    sc.getGrid()[i][j].setCostraint("3");
                    break;
                case '4':
                    sc.getGrid()[i][j].setCostraint("4");
                    break;
                case '5':
                    sc.getGrid()[i][j].setCostraint("5");
                    break;
                case '6':
                    sc.getGrid()[i][j].setCostraint("6");
                    break;
                case 'e': {
                    sc.getGrid()[i][j].setCostraint("");
                    isCostraint = false;
                }
                    break;
                default:
                    correct = false;

            }
        if(!sc.nearCostraint(i,j,sc.getGrid()[i][j].getCostraint()))
        {
            correct = false;
            System.out.println(Colour.ANSI_RED.escape()+" Restrizione già immessa nelle caselle adiacenti "+Colour.RESET);
            System.out.println("Selezionarne un'altra");
        }
        }

        return isCostraint;
    }

    public void setDifficult(Schema sc,int nCostraint)
    {
        int difficult;

        if(nCostraint<8)
            difficult = 1;
        else if(nCostraint<11)
            difficult = 2;
        else if(nCostraint<12)
            difficult=3;
        else if(nCostraint<13)
            difficult=4;
        else if(nCostraint<14)
            difficult=5;
        else
            difficult=6;

        sc.setDifficult(difficult);
    }
    public void modifySchema(Schema s)
    {
        int row, column;
        correct = false;
        while(!correct) {


            try {
                System.out.println("Inserisci la riga che vuoi modificare");
                row = Integer.parseInt(input.nextLine());

                System.out.println("Inserisci la colonna che vuoi modificare");
                column = Integer.parseInt(input.nextLine());

                setCostraint(s,row,column);

                System.out.println("Modifica avvenuta!");

                System.out.println("Questo è il tuo schema");
                System.out.println(s);

                System.out.println("Vuoi modificare ancora la griglia? y si");

                if (input.nextLine().equals("y"))
                    correct = false;
                else
                    correct = true;

            } catch (NumberFormatException e) {
                correct = false;
            }
        }
    }

    public void saveSchema(Schema s) throws IOException
    {
        String path = "src/main/data/SchemaPlayer/";
        String name,schema;
        Gson g = new Gson();
        schema = g.toJson(s);
        correct = false;
        while(!correct)
        {
            String copyPath;
            System.out.println("Inserisci il nome che vorrai dare allo schema:");
            name = input.nextLine();
            copyPath = path + name + ".json";
            FileWriter fw=null;
            BufferedWriter b;
                File file = new File(copyPath);

                if (file.exists())
                    System.out.println("Il file " + copyPath + " esiste già");
                else if (file.createNewFile())
                {
                    System.out.println("Il file " + copyPath + " è stato creato");
                    fw = new FileWriter(file);
                    b = new BufferedWriter(fw);
                    b.write(schema);
                    b.flush();
                    fw.close();
                    b.close();
                    correct = true;
                }
                else
                    System.out.println("Il file " + path + " non può essere creato");

        }

    }


    public void setChangeGrid(int row,int column,Colour c,int number,String player)
    {
        schemas.get(player).getGrid()[row][column].setColour(c);
        schemas.get(player).getGrid()[row][column].setNumber(number);
    }

    public void setOpponentsSchemas(List <String> s)
    {
        clearScreen();
        Schema temp= new Schema();
        for(int i=0;i<s.size();i=i+2) {
            try {
                if(!s.get(i).equals(this.getName()))
                    schemas.put(s.get(i),temp.InitSchema("SchemaClient/"+s.get(i+1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setNumberPlayer(int nPlayer) {
        this.nPlayer = nPlayer;
    }

    public void startRound() {
        round++;
        if(round == 1) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (gameRunning)
                        chooseMoves();
                }
            });
            thread.start();
        }
    }

    public void setActions(List<String> actions) {
        moves.clear();
        moves.add("Mostra l'obiettivo privato");
        moves.add("Mostra gli obiettivi pubblici");
        moves.add("Mostra schemi avversari");
        moves.add("Mostra le tool card");
        if(actions == null)
            return;
        for(String action: actions)
            moves.add(action);
        showMoves();
    }

    // show method
    public void showSchemas(String nome)
    {
        Schema schema = new Schema();
        try {
            schema = schema.InitSchema("SchemaClient/"+nome);
            schema.splitImageSchema();
            schema.showImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showDiceSpace()
    {
        for(Dices d:diceSpace)
        {
            System.out.print(d.toString());
        }
        System.out.println("\n");
    }

    public void showOpponentsSchemas(HashMap<String,Schema> schema)
    {
        schema.remove(username);
        for(String key:schema.keySet()) {
            schema.get(key).splitImageSchema();
            schema.get(key).paint[0] = key;
        }
            showSchemas(schema);
         }

    public void showSchemas(HashMap<String,Schema> schema)
    {
        for(int i=0;i<PAINT_ROW;i++)
        {
            for(String key:schema.keySet()) {
                System.out.print(schema.get(key).paint[i]);
                if(i<3 || i>6) {
                    for (int j = schema.get(key).paint[i].length(); j < 45; j++)
                        System.out.print(" ");
                }
                else {
                    for (int x = 32; x < 46; x++)
                        System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }
    public void showPrivateObjective()
    {
        System.out.println("Il tuo obiettivo privato è:"+privateObjective);
    }

    public void showPublicObjective()
    {
        System.out.println("Gli obiettivi pubblici della partita sono:");
        for(String pub: publicObjcective)
            System.out.println(pub);
    }

    public void showToolCard()
    {
        System.out.println("Le toolcard sono:");
        for(String tool:toolCard)
            System.out.println(tool);
    }




    public void showMoves()
    {

        System.out.println("Scegli una tra le seguenti opzioni:");
        for(int i=0;i<moves.size();i++)
            System.out.println((i+1)+")"+moves.get(i));
    }


    // action method

    public void startScene() {
        int choose;
        System.out.println(Colour.ANSI_GREEN.escape() + "W E L C O M E "+Colour.RESET);
        System.out.println("Prima di cominciare...");
        while(!correct) {
            try {
                correct = true;
                System.out.println("Desideri:");
                System.out.println("1)Costruire il tuo schema");
                System.out.println("2)Caricarlo da file");
                System.out.println("3)Utilizzare schemi predefiniti");
                choose = Integer.parseInt(input.nextLine());
                switch(choose)
                {
                    case 1: createSchema(); break;
                    case 2: loadSchema(); break;
                    case 3: break;
                    default: correct = false;
                }
            }catch(NumberFormatException e)
            {
                System.out.println("Scelta non valida");
            }
        }

    }

    public void loadSchema()
    {
        String name;
        boolean correct=false;
        final String path = "src/main/data/SchemaPlayer";
        File f = new File(path);
        Schema sc = new Schema();
        while(!correct) {
            System.out.println("Scegli il nome dello schema da caricare");
            if(f.list().length==0)
            {
                System.out.println(Colour.ANSI_RED.escape()+" Nessuno schema da caricare "+Colour.RESET);
                System.out.println("Crearne uno? y per si");
                if(input.nextLine().equals("y"))
                    createSchema();
                break;
            }
            for(String file:f.list())
            System.out.println(file.substring(0,file.length()-5));
            name = input.nextLine();
            try {
                schemas.put(username,sc.InitSchema("SchemaPlayer/"+name));
                correct = true;
            } catch (IOException e) {
                System.out.println("Errore con lo schema da caricare");
                correct = false;
            }
        }
    }



    public void login(String str) {
        if (str.equals("Welcome"))
        {
            System.out.println("La partita inizierà a breve");
            System.out.println("Aspettando altri giocatori...");
        }else if(str.equals("Login_error-username")) {
            System.out.println(Colour.ANSI_RED.escape()+"Errore, nickname già in uso"+Colour.RESET);
            this.setLogin();
        }else if(str.equals("Login_error-game")) {
            System.out.println(Colour.ANSI_RED.escape()+"Errore, partita già in corso");
            System.out.println("Riprovare più tardi"+Colour.RESET);
        }
    }

    public void playerConnected(String name){
        System.out.print("\r");
        System.out.println(name + " si è aggiunto alla lobby\n");
        nPlayer++;
    }

    public void playerDisconnected(String name){
        System.out.print("\r");
        System.out.println(name + " si è disconnesso\n");
        nPlayer--;
    }

    public void timerPing(String time) {
        //System.out.println("Caricamento");
       // for(int i=0;i<(LOBBY_TIMER_VALUE-Integer.parseInt(time))/5;i++) {
        Colour colour = null;
            int percent =(int)(((LOBBY_TIMER_VALUE -Double.parseDouble(time))/ LOBBY_TIMER_VALUE)*100);
            System.out.print("\r"+Colour.ColorString("L",Colour.ANSI_GREEN)+Colour.ColorString("o",Colour.ANSI_RED)+Colour.ColorString("a",Colour.ANSI_BLUE)+Colour.ColorString("d",Colour.ANSI_YELLOW)+Colour.ColorString("i",Colour.ANSI_PURPLE)+Colour.ColorString("n",Colour.ANSI_GREEN)+Colour.ColorString("g",Colour.ANSI_BLUE));
            for(int i=0;i<percent;i++)
            {
                switch(i/20)
                {
                    case 0:  colour = Colour.ANSI_GREEN;break;
                    case 1: colour = Colour.ANSI_RED;break;
                    case 2:colour = Colour.ANSI_BLUE;break;
                    case 3: colour = Colour.ANSI_YELLOW;break;
                    default:colour = Colour.ANSI_PURPLE;
                }

                System.out.print(Colour.ColorString("▋",colour));
            }
            for(int i=percent;i<100;i++)
            {
                System.out.print(" ");
            }
        System.out.print(Colour.ColorString(percent+"%",Colour.ANSI_BLUE));
       // }
       // System.out.println("la partita inizierà tra " + time + " secondi\n");
    }

    public void createGame(){
        clearScreen();
        System.out.println("\npartita creata\n");
    }




    public void chooseSchema(String name)
    {
        if(schemaThread.isAlive())
            schemaThread.interrupt();

        try {
            Schema s = new Schema();
            schemas.put(this.getName(),s.InitSchema("SchemaClient/"+name));
            System.out.println("schema approvato: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Aspettando la scelta degli altri giocatori...\n");
    }


    public void startTurn(String name)
    {
        clearScreen();
        System.out.println(Colour.ANSI_BLUE.escape()+"Round"+round+Colour.RESET);
        System.out.println("Il tuo schema:");
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
        showDiceSpace();
        if(!name.equals(username)) {
            myTurn = false;
            System.out.println("turno iniziato, tocca a: " + name);
            setActions(null);
            showMoves();
        }
        else
            myTurn = true;
    }

    public void chooseMoves()
    {
        String move="";
        int choose;
        boolean correct = false;
        //to be modified later

        while(!correct)
        {
            correct = true;
            try
            {
                move = input.nextLine();

                choose = Integer.parseInt(move);

                if (moves.get(choose - 1).equals("InsertDice")) {
                    insertDice();
                    // se ricevo una risposta positiva dal server allora tolgo dalle azioni la possibilità di inserire un dado
                    //moves.remove(choose-1);
                } else if (moves.get(choose - 1).equals("UseToolCard")) {
                    useToolCard();
                    // se ricevo una risposta positiva dal server allora tolgo dalle azioni la possibilità di utilizzare la toolcard
                    // moves.remove(choose-1);
                } else if (moves.get(choose - 1).equals("MoveDice")) {
                    moveDice();
                    // se ricevo una risposta positiva dal server allora tolgo dalle azioni la possibilità di utilizzare la toolcard
                    // moves.remove(choose-1);
                } else if (moves.get(choose - 1).equals("EndTurn")) {
                    passTurn();
                    clearScreen();
                }
                switch (choose - 1) {
                    case 0:
                        showPrivateObjective();
                        break;
                    case 1:
                        showPublicObjective();
                        break;
                    case 2:
                        showOpponentsSchemas((HashMap<String, Schema>) schemas.clone());
                        break;
                    case 3:
                        showToolCard();
                        break;
                    default: correct = false;
                }



            }catch (Exception e) {
               // e.printStackTrace();
               // System.out.println("Errore divinooooo");
                correct= false;
            }
        }
    }



    public void passTurn()
    {
        this.myTurn = false;
        connection.sendEndTurn();
    }

    public void insertDice()
    {
        // devo controllare la diceSpace
        correct = false;
        while(!correct) {
            try {
                System.out.println("Inserisci l'indice del dado della riserva:(Da 1 a "+diceSpace.size()+")");
                indexDiceSpace = Integer.parseInt(input.nextLine());
                indexDiceSpace--;
                if(indexDiceSpace<0 || indexDiceSpace>diceSpace.size())
                    throw new NumberFormatException();

                System.out.println("Inserisci la riga");
                row = Integer.parseInt(input.nextLine());
                row--;
                if(row<0 || row>3)
                    throw  new NumberFormatException();

                System.out.println("Inserisci la colonna");
                column = Integer.parseInt(input.nextLine());
                column --;
                if (column<0 || column>4)
                    throw  new NumberFormatException();

                correct = true;
                connection.insertDice(indexDiceSpace,row,column);

            }catch(NumberFormatException e) {
                e.printStackTrace();
                System.out.println(Colour.ANSI_RED.escape()+"Formato non valido"+Colour.RESET);
            }/*catch(IndexException ex)
            {
                System.out.println("Indice errato");
            }
           */
        }

    }


    public void clearScreen()
    {
        for(int i= 0 ;i<10;i++)
        System.out.println("\n");
    }

    public void useToolCard()
    {
        correct = false;
        System.out.println("Scegli la tool card da utilizzare:");
        for(String s:toolCard)
            System.out.println(s);
        int tool = Integer.parseInt(input.nextLine());
        //connection.sendMessage(tool);
        connection.useToolCard(tool);
    }

    public void useToolCardAccepted() {
        System.out.println("bravissimo, però dobbiamo concordare come gestire lo scalare dei favori");
    }

    public void useToolCardError() {
        System.out.println("non hai abbastanza favori");
    }

    int oldRow;
    int oldColumn;
    int newRow;
    int newColumn;

    public void moveDice() {
        correct = false;
        System.out.println("Riga da dove vuoi prendere il dado:");
        oldRow = Integer.parseInt(input.nextLine()) -1;
        System.out.println("Colonna da dove vuoi prendere il dado:");
        oldColumn = Integer.parseInt(input.nextLine()) -1;
        System.out.println("Riga dove vuoi spostare il dado:");
        newRow = Integer.parseInt(input.nextLine()) -1;
        System.out.println("Colonna dove vuoi spostare il dado:");
        newColumn = Integer.parseInt(input.nextLine()) -1;
        connection.moveDice(oldRow,oldColumn,newRow,newColumn);
    }
    //problem: quando togli un dado dallo schema come recuperi la restrizione precedente?
    public void pickDiceAccepted(){}
    public void moveDiceAccepted(){
        this.schemas.get(username).getGrid()[newRow][newColumn] = this.schemas.get(username).getGrid()[oldRow][oldColumn];
        this.schemas.get(username).getGrid()[oldRow][oldColumn] = new Dices("",0,null);
        schemas.get(username).splitImageSchema();
        schemas.get(username).showImage();
        System.out.println("sei riuscito a spostare il dado, complimenti");
    }

    Dices pendingDice;

    public void pickDiceSchema(List action){
        if(!action.get(0).equals(username)){
            this.schemas.get(action.get(0)).getGrid()[Integer.parseInt((String)action.get(1))][Integer.parseInt((String)action.get(2))] =
                    new Dices("",0,null);

        }
    }

    public void pickDiceSchemaError(){
        System.out.println("non ci sono dadi qui");
    }

    public String getName(){ return this.username;}
}

