package it.polimi.ingsw.client.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.client.clientConnection.Connection;
import it.polimi.ingsw.client.clientConnection.rmi.RmiConnection;
import it.polimi.ingsw.client.clientConnection.socket.SocketConnection;
import it.polimi.ingsw.client.constants.NameConstants;
import it.polimi.ingsw.client.setUp.TakeDataFile;
import it.polimi.ingsw.client.view.*;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static it.polimi.ingsw.client.constants.MessageConstants.CANCEL_USE_TOOL_CARD;
import static it.polimi.ingsw.client.constants.MessageConstants.DRAFT_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.END_TURN;
import static it.polimi.ingsw.client.constants.MessageConstants.FLIP_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.MOVE_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.PLACE_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.PLACE_DICE_SPACE;
import static it.polimi.ingsw.client.constants.MessageConstants.ROLL_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.ROLL_DICE_SPACE;
import static it.polimi.ingsw.client.constants.MessageConstants.SWAP_DICE;
import static it.polimi.ingsw.client.constants.MessageConstants.SWAP_DICE_BAG;
import static it.polimi.ingsw.client.constants.MessageConstants.USE_TOOL_CARD;
import static it.polimi.ingsw.client.constants.NameConstants.*;
import static it.polimi.ingsw.client.constants.NameConstants.NICKNAME_ALREADY_USE;
import static it.polimi.ingsw.client.constants.printCostants.*;
import static it.polimi.ingsw.client.view.gui.GameMessage.DISCONNECTED;
import static it.polimi.ingsw.client.view.gui.GameMessage.WAIT_CHOOSE_SCHEMA;
import static java.lang.Integer.parseInt;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import static sun.management.AgentConfigurationError.FILE_NOT_FOUND;


/**
 * It's the FXML controller (for GUI). It contains:
 *  -Attributes : they're mainly javaFX components (@FXML);
 *  -Event (MouseClicked, DragEvent, ActionEvent);
 *  -GUI's method
 */
public class ControllerGUI implements View {

    //attributes used to communicate outside GUI
    private Connection connection;
    private Handler hand;
    private TakeDataFile config;
    private MediaPlayer mediaPlayer;

    private String schemaChoosen;
    private Integer roundNumber;
    private Integer roundIndex;
    private List<String> diceExtract;
    private static String text;
    private int currentTool;
    private List<Object> schemaPlayers;
    private int indexDiceSpace;
    private Image dragImage;
    private final Object lock = new Object();
    private List<String> schemasClient;
    private ImageView imageMoved;
    private ImageView schemaCell;
    private ImageView roundDice;

    //attributes used during ToolCard aciton
    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;
    private boolean diceChanged;
    private boolean isFirst;
    private boolean decrement;
    private boolean correctInsertion;
    private String colorMoved;
    private int numberMoved;

    //fx-Id declared in  @FXML
    private Font ea;
    @FXML
    private Button repeatLogin;
    @FXML
    private Button loginAction;
    @FXML
    private GridPane schemaConstrain;
    @FXML
    private ImageView cancelButton;
    @FXML
    private ImageView pendingDice;
    @FXML
    private ImageView use1;
    @FXML
    private ImageView use2;
    @FXML
    private ImageView use3;
    @FXML
    private GridPane constrain2;
    @FXML
    private GridPane constrain3;
    @FXML
    private GridPane constrain4;
    @FXML
    private ImageView iconTool;
    @FXML
    private GridPane gridPane;
    @FXML
    private Text serverMessage;
    @FXML
    private Text textflow;
    @FXML
    private ImageView endTurn;
    @FXML
    private ImageView imageZoomed;
    @FXML
    private Text nickname2;
    @FXML
    private Text nickname3;
    @FXML
    private Text nickname4;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button closeButton;
    @FXML
    private ImageView playButton;
    @FXML
    private ImageView rmiButton;
    @FXML
    private ImageView socketButton;
    @FXML
    private TextField nickname;
    @FXML
    private ImageView schemaA;
    @FXML
    private ImageView schemaB;
    @FXML
    private ImageView schemaC;
    @FXML
    private ImageView schemaD;
    @FXML
    private ImageView toolCard1;
    @FXML
    private ImageView toolCard2;
    @FXML
    private ImageView toolCard3;
    @FXML
    private ImageView publObj1;
    @FXML
    private ImageView publObj2;
    @FXML
    private ImageView publObj3;
    @FXML
    private ImageView schema1;
    @FXML
    private ImageView privateCard;
    @FXML
    private Text waitingMessage;
    @FXML
    private Text nFavour;
    @FXML
    private GridPane diceSpace;
    @FXML
    private GridPane roundTrack;
    @FXML
    private GridPane score;
    @FXML
    private Text timer;
    @FXML
    private ImageView mute;
    private boolean moveCorrect;

    /**
     ** Controller builder, in order to set default paramaters used by Event
     * @param hand : is about what type of view interface was choosen; in this case handler was set on GUI
     */
    public ControllerGUI(Handler hand) {
        this.config = new TakeDataFile();
        this.isFirst = true;
        this.hand = hand;
        this.diceChanged = false;
        diceExtract = new ArrayList<>();
    }
    /**
     * After close current scene,  it will open login scene.
     * @param actionEvent  select RMI connection (on Imageview Clicked).
     */

    @FXML
    public void goRMI(MouseEvent actionEvent) {

        ImageView imageView = (ImageView) actionEvent.getTarget();
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
        try {
            connection = new RmiConnection(hand);
        } catch (RemoteException e) {
            Message.println(CONNECTION_ERROR, TypeMessage.ERROR_MESSAGE);
        }


        changeScene(config.getParameter(GO_TO_LOGIN));
        loginAction.setDefaultButton(true);
    }


    /**
     ** After close current scene,  it will open login scene.
     * @param actionEvent  select Socket connection (on Imageview Clicked).
     * */
    @FXML
    public void goSocket(MouseEvent actionEvent) {

        ImageView imageView = (ImageView) actionEvent.getTarget();
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
        try {
            connection = new SocketConnection(hand);
        } catch (IOException e) {
            Message.println(ERROR_SAVE_SCHEMA,TypeMessage.ERROR_MESSAGE);
        }
        Thread t = new Thread((SocketConnection) connection);
        t.start();


        changeScene(config.getParameter(GO_TO_LOGIN));
        loginAction.setDefaultButton(true);

    }

    /**
     ** After close current scene,  it will open the connection choose scene.
     * @param event selected PLAY action.
     */
    @FXML
    void playAction(MouseEvent event) {
        Stage stage = (Stage) playButton.getScene().getWindow();
        stage.close();

        changeScene(config.getParameter(CHOOSE_CONNECTION));
    }


    /**
     *Player uses this scene to create and save his own schema (in .json file).
     * Schema created it could be choosen by player when he plays.
     * @param event it will open Schema Editor scene.
     *
     *
     */
    @FXML
    void goSchemaEditor(MouseEvent event) {
        Stage stage = new Stage();
        Pane p = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FXML/" +
                    config.getParameter(GO_TO_SCHEMA_EDITOR) + ".fxml"));
            p = loader.load();
        } catch (IOException e) {
            Message.println(ERROR_SAVE_SCHEMA,TypeMessage.ERROR_MESSAGE);
        }
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("SAGRADA GAME");
        Image image = new Image(config.getParameter(ICON_GAME));
        stage.getIcons().add(image);
        stage.setResizable(false);

        stage.show();
    }


    /**
     * @return own nickname with a String type
     */
    public String getName() {
        return nickname.getText();
    }


    /**Get Nickname put and makes login
     * @param actionEvent controlls that nickname if is empty and has length longer then 8
     *                    nickname too long is not accepted)
     */
    @FXML
    public void captureNickname(ActionEvent actionEvent) {

        if (getName().equals(GameMessage.EMPTY) || getName().length() > 8 )
            setNotice(config.getParameter(NICKNAME_IS_EMPTY));
        else {
            try {
                connection.login(getName());

            } catch (Exception e) {
                Message.println(SCHEMA_ALREADY_INSERT,TypeMessage.INFO_MESSAGE);
            }

        }
    }

    /**close error pane
     * @param actionEvent close Error Scene generated by an error (nickname empty for ex),
     *                    on "OK" button clicked
     */
    @FXML
    public void repeatAction(ActionEvent actionEvent) {
        Stage stage = (Stage) repeatLogin.getScene().getWindow();
        stage.close();

    }


    /**
     * -Welcome: login accepted. It will be opened the lobby scene;
     * -Login_error-username: another player has already used the own nickname choosen;
     * -"Login_error-game": a new Game has already started, and the player cannot connect;
     * @param src after got nickname send nickname to server. Server has 3 different answer:
     */
    public void login(String src) {

        Platform.runLater(() -> {
            switch (src) {
                case GameMessage.CONNECTION_SUCCEDED:
                    Stage stage;
                    stage = (Stage) loginAction.getScene().getWindow();
                    stage.close();
                    changeScene(config.getParameter(GO_TO_LOBBY));
                    break;
                case GameMessage.NICKNAME_USED:
                    setNotice(config.getParameter(NICKNAME_ALREADY_USE));
                    break;
                case GameMessage.GAME_STARTED:
                    setNotice(config.getParameter(PLAYER_LIMIT));
                    break;
            }

        });
    }


    /**set Textflow's text when player connects
     * @param name in the lobby scene it set a text in order to notify that a new player entered in lobby;
     */
    public void playerConnected(final String name) {
        Platform.runLater(() -> waitingMessage.setText(name + GameMessage.NEW_PLAYER));
    }

    /**set Textflow's text when player disconnects
     * @param name in the lobby scene it set a text in order to notify that a new player disconnect and give up lobby;
     */
    public void playerDisconnected(final String name) {
        Platform.runLater(() -> {
            waitingMessage.setText(name + GameMessage.PLAYER_DISCONNECTED);
            if (textflow != null) {
                if(name.equals(nickname.getText()))
                    serverMessage.setText(DISCONNECTED);
                else
                    serverMessage.setText(name + GameMessage.PLAYER_DISCONNECTED);
            }
        });

    }

    /**set timer value set the progressbar
     * @param time is the second about the time passed after the lobby creation. when time/LOBBY_TIME == 1. the game starder.
     *             time/LOBBY_TIME was used like a double  also to fill the progress bar.
     */
    public void timerPing(final String time) {

        Platform.runLater(() -> {
            int lobbyTimer = Integer.parseInt(config.getParameter(LOBBY_TIMER));
            double result = ((lobbyTimer - Double.parseDouble(time)) / lobbyTimer);
                    progressBar.setProgress(result);
                }
        );

    }

    /**
     * set text for remaining time
     * @param time which remain on turn
     */
    public void turnTimerPing(int time) {
        Platform.runLater(() -> timer.setText(String.valueOf(time)));
    }

    /**
     * A new game begins. This method opens the FXML of game and set font of textflow, used to show message sent from
     * server during game.
     */
    public void createGame() {
        Platform.runLater(() -> {
            String musicPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            if (width > 1500 && height > 1000)
                changeScene(config.getParameter(GAMESCENE_15));
            else changeScene(config.getParameter(NEW_GAME));
            ea = Font.loadFont(getClass().getResourceAsStream(config.getParameter(FONT)), 17);
            textflow.setFont(ea);
            serverMessage.setFont(ea);
            textflow.setTextAlignment(TextAlignment.LEFT);
            textflow.setText("Seleziona il tuo schema prima di iniziare");


            if (progressBar != null) {
                Stage stage = (Stage) progressBar.getScene().getWindow();
                stage.close();
            }

        });
    }


    /**
     * It set image of schemas (from schemas.get(i))
     @param schemas are the name of schemas from which player choose his own schema
     */
    public void setSchemas(final List<String> schemas) {
        this.schemasClient = new ArrayList<>(schemas);
        Platform.runLater(() -> {
            String path = config.getParameter(SCHEMI);

            openUndecoreted(config.getParameter(NameConstants.CHOOSE_SCHEMA));
            List<ImageView> setSchemas = Arrays.asList(schemaA, schemaB, schemaC, schemaD);

            IntStream.range(0, 4)
                    .forEach(i -> {
                        Image image = new Image(path + schemas.get(i) + ".png");
                        setSchemas.get(i).setImage(image);
                    });
            textflow.setText(GameMessage.EMPTY);
        });

    }

    /**
     * It set image of Private Objective card
     * and its cursor (magnifier)
     @param colour is the color of Private Objective Card
     */
    public void setPrivateCard(final String colour) {

        Platform.runLater(() -> {
            Image cursor = new Image(config.getParameter(ZOOM_CURSOR));
            Image image = new Image(config.getParameter(PRIVATE_OBJ_PATH) + colour + ".png");
            privateCard.setImage(image);
            privateCard.setCursor(new ImageCursor(cursor));
        });
    }

    /**
     * It set image of Public Objective Card
     * and their cursor (magnifier)
     @param cards are the name of cards.
     */
    public void setPublicObjectives(final List<String> cards) {
        Platform.runLater(() -> {
            Image cursor = new Image(config.getParameter(ZOOM_CURSOR));
            List<ImageView> imagePubl = Arrays.asList(publObj1, publObj2, publObj3);
            String path = config.getParameter(PUBLIC_OBJ_PATH);
            IntStream.range(0, 3)
                    .forEach(i -> {
                        Image image = new Image(path + cards.get(i) + ".png");
                        imagePubl.get(i).setImage(image);
                        imagePubl.get(i).setCursor(new ImageCursor(cursor));
                    });

        });

    }

    /**
     * It set image of Tool Card
     * and their cursor (magnifier)
     @param cards are the name of cards.
     */
    public void setToolCards(final List<Integer> cards) {

        Platform.runLater(() -> {

            String path = config.getParameter(TOOLCARD_PATH);
            Image cursor = new Image(config.getParameter(ZOOM_CURSOR));
            List<ImageView> tool = Arrays.asList(toolCard1, toolCard2, toolCard3);
            List<ImageView> useTool = Arrays.asList(use1, use2, use3);

            IntStream.range(0, 3)
                    .forEach(i -> {
                        Image toolImage = new Image(path + cards.get(i) + ".png");
                        tool.get(i).setImage(toolImage);
                        tool.get(i).setCursor(new ImageCursor(cursor));
                        useTool.get(i).setId(cards.get(i).toString());

                    });

        });


    }

    /**
     * @param hand handler of view
     */
    public void setHandler(Handler hand) {
        this.hand = hand;
    }

    /**method loaded stage UNDECORATED with UTILITY style and no resizable
     * @param src name of fxml loaded
     */
    public void openUndecoreted(String src){

    Stage stage = new Stage();
    Pane p = null;

    p = loadFXML(src, p);
    Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("Sagrada");
    Image image = new Image(config.getParameter(ICON_GAME));
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.resizableProperty().setValue(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        putCloseEvent(stage);
        stage.show();

}

    /**it's set event on close request of stage
     * @param stage  where put event
     *
     */
    private void putCloseEvent(Stage stage) {
        stage.setOnCloseRequest(event -> {
            if (connection != null)
                connection.disconnect();

            Platform.exit();
            exit(0);
        });
    }


    /**
     * method used to open a new scene.
     * It was also defined setOnCloseRequest in order to disconnect() client from server after closing JavaFx application
     * @param src is the name of FXML which it loads
     */
    private void changeScene(String src) {

        Stage stage = new Stage();
        Pane p = null;

        p = loadFXML(src, p);
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("Sagrada");
        Image image = new Image(config.getParameter(ICON_GAME));
        stage.getIcons().add(image);


        putCloseEvent(stage);
        stage.show();
    }

    /**load FXML in pane
     * @param src name of fxml file
     * @param p pane used setted with fxml
     * @return return Pane setted with FXML
     */
    private Pane loadFXML(String src, Pane p) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(getClass().getResource("/FXML/" + src + ".fxml"));
            p = loader.load();

        } catch (IOException e) {
            Message.println(ERROR_SAVE_SCHEMA,TypeMessage.ERROR_MESSAGE);
        }
        return p;
    }

    /**
     * method used to open a new scene.
     * @param src is the name of FXML which it loads
     */
    private void setNotice(String src) {

        Stage stage = new Stage();
        Pane p = null;
        p = loadFXML(src, p);
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("SAGRADA GAME");
        stage.resizableProperty().setValue(false);



        stage.show();

    }

    @Override
    public void setScene(String scene) {
        //not implemented on gui
    }

    public void startScene() {
        //not implemented on gui
    }


    /**it opens closeRequest during lobby
     * @param actionEvent open the closeRequest during lobby, after clicked the "Disconnetti" button
     */
    @FXML
    public void QuitPaneAction(ActionEvent actionEvent) {
        setNotice(config.getParameter(CLOSE_MESSAGE));

    }


    /** After open scene with QuitPaneAction, the application was closed and client disconnect from server
     * @param actionEvent kill application
     */
    @FXML
    public void disconnectAction(ActionEvent actionEvent) {

        connection.disconnect();

        Platform.exit();
        exit(0);
    }


    /**it closes little scene opened before
     * @param actionEvent close Error Scene generated by an error (nickname empty for ex),
     *                    on "OK" button clicked
     */
    @FXML
    public void quitPannel(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

    }


    /**
     * after clicked the schema choosen, client send the request to server
     * @param event clicks on schema which player choose.
     */
    @FXML
    void sendSchema(MouseEvent event) {
        ImageView imageView = (ImageView) event.getTarget();

        connection.sendSchema(schemasClient.get(parseInt(imageView.getId())));
        serverMessage.setText(WAIT_CHOOSE_SCHEMA);
    }

    /**
     * with the file Chooser the player could choose his own schema (created before)
     * @param event open the File Chooser
     */
    @FXML
    void openSchema(MouseEvent event) {
        Platform.runLater(() -> {

            Stage stage = new Stage();

            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Scanner in = null;
                try {
                    in = new Scanner(new FileReader(file));
                } catch (FileNotFoundException e) {
                    Message.println(FILE_NOT_FOUND, TypeMessage.ERROR_MESSAGE);
                }

                StringBuilder sb = new StringBuilder();


                while (in.hasNext()) {
                    sb.append(in.next());
                }
                in.close();
                schemaChoosen = sb.toString();


                connection.sendCustomSchema(schemaChoosen);

            } else setNotice(config.getParameter(OPEN_SCHEMA_ERROR));

        });
    }


    /**
     * it load schema from the name of file given and print its constrain on the gridPane @schemaConstrain dynamically
     * (advanced functionality)
     * @param name name of schema choose
     */
    public void chooseSchema(final String name) {

        Platform.runLater(() -> {
            Schema schema = new Schema();
            printSchema(schemaConstrain, name);
            schema = schema.InitSchema(config.getParameter(PATH_INIT_DEFAULT_SCHEMA) + name);
            nFavour.setText("x " + schema.getDifficult());
            if(schemaA != null) {
                Stage stage = (Stage) schemaA.getScene().getWindow();
                stage.close();
            }
        });
    }
     /**
     * it load schema from the name of file given and print its constrain on the gridPane @schemaConstrain dynamically
      * of opponents Players
     * (advanced functionality)
     * @param schemas name of schemas choose by the other player
     */

    public void setOpponentsSchemas(final List<String> schemas) {
        List<String> stringList = new ArrayList<>(schemas);
        Platform.runLater(() -> {
            schemaPlayers = new ArrayList<>();
            schemaPlayers = Arrays.asList(nickname2, constrain2,
                    nickname3, constrain3, nickname4, constrain4);

            if (stringList.contains(nickname.getText())) {
                stringList.remove(stringList.indexOf(nickname.getText()) + 1);
                stringList.remove(nickname.getText());
            }
            IntStream.iterate(0, i -> i + 2)
                    .limit(stringList.size() / 2)
                    .forEach(i -> {
                        ((Text) (schemaPlayers.get(i))).setText(stringList.get(i));
                        printSchema((GridPane) schemaPlayers.get(i + 1), stringList.get(i + 1));
                        ((GridPane) schemaPlayers.get(i + 1)).setId(stringList.get(i));
                    });
        });

    }

    /**
     *
     * @param event firs step of drag & drop use to insert dice from DiceSpace to own Schema
     *              in the drag step the image dragged it's placed in the DragBoard
     */
    @FXML
    void handleDragDetection(MouseEvent event) {

        ImageView imageView = (ImageView) event.getTarget();
        dragImage = imageView.getImage();
        Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);
        indexDiceSpace = parseInt(imageView.getId());
        gridPane.setDisable(false);

        ClipboardContent cb = new ClipboardContent();
        cb.putImage(imageView.getImage());
        db.setContent(cb);
        event.consume();

    }


    /**
     * @param event firs step of drag and drop use to insert dice from DiceSpace to own Schema
     *              in the drag step the image dragged it's placed in the DragBoard     */
    @FXML
    void handleDiceDrag(DragEvent event) {
        if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }


    /**
     * @param event second step of drag and drop use to insert dice from DiceSpace to own Schema
     *              in the drop step client send x, y and index of DiceSpace to server and wait().
     *              After that if correctInsertion = true place the dice, else gives error
     */
    @FXML
    public void handleImageDropped(DragEvent event) {
        final DragEvent drag = event;

        Thread t = new Thread(() -> {

            ImageView imageView = (ImageView) drag.getTarget();
            Node source = ((Node) drag.getTarget());

            if (source != schema1) {
                Node parent;
                while ((parent = source.getParent()) != gridPane) {
                    source = parent;
                }
            }
            Integer col = GridPane.getColumnIndex(source);

            Integer row = GridPane.getRowIndex(source);

            if (col == null)
                col = 0;

            if (row == null)
                row = 0;

            int rowIndex = row;
            int colIndex = col;
            synchronized (lock) {
                connection.insertDice(indexDiceSpace, rowIndex, colIndex);

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (correctInsertion) {
                imageView.setImage(dragImage);
                imageView.setId(GameMessage.FULL);
                textflow.setText(GameMessage.EMPTY);

                gridPane.setDisable(true);
                diceSpace.setDisable(true);

            } else {
                textflow.setText(GameMessage.WRONG_INSERTION);
            }
        });

        if (currentTool == 0 || currentTool == 9)
            t.start();

    }

    /**
     * @param event
     *if the insertion (drag & drop) was correct set null the image of dice dragged
     */
    @FXML
    void handleDragDone(DragEvent event) {
        ImageView imageView = (ImageView) event.getTarget();
        gridPane.setDisable(true);
        if (!(event.getDragboard().hasImage()))
            imageView.setImage(null);

    }


    public void setNumberPlayer(int nPlayer) {
        //not used by gui
    }

    /**
     * @param event
     * open the ImageView clicked (tool Card, Public Obj Card, Private Obj Card zommed in a little scene
     */
    @FXML
    void imageZoom(MouseEvent event) {

        Platform.runLater(() -> {
            ImageView image = (ImageView) event.getTarget();
            openUndecoreted(config.getParameter(ZOOM_CARD));
            imageZoomed.setImage(image.getImage());
        });
    }

    /**
     * @param event
     * used by player to pass his turn
     */
    @FXML
    void nextPlayer(MouseEvent event) {
        disableAll();
        x1 = null;
        x2 = null;
        textflow.setText(GameMessage.EMPTY);
        connection.sendEndTurn();


    }

    /**
     * set the textFlow with "è iniziato il Round n°.."
     */
    public void startRound() {
        serverMessage.setText(GameMessage.NEW_ROUND);
    }

    /**
     * if nickname is the same of player is his turn, else is not his turn and wait
     * @param name nickname of the current player
     */
    public void startTurn(String name) {

        if (!name.equals(nickname.getText())) {
            serverMessage.setText(GameMessage.NOT_MY_TURN + name);
            disableAll();
            timer.setVisible(false);
            timer.setText(GameMessage.EMPTY);
        } else {
            timer.setVisible(true);
            timer.setText(GameMessage.EMPTY);
            serverMessage.setText(GameMessage.MY_TURN);

        }
    }

    /**
     *   If actions contains an "PossibileAction" it set disable(false) the object refered to its use,
     * else setDisable(true) because it couldn't be used
     * @param actions are the possibile action taht a player can do during a turn. It could be change during the turn
     *
     */
    public void setActions(final List<String> actions) {



        Platform.runLater(() -> {
            if (actions.contains(USE_TOOL_CARD) && !actions.contains(ROLL_DICE_SPACE)) {
                disableTool(false);
            } else if (actions.contains(ROLL_DICE_SPACE) && !actions.contains(USE_TOOL_CARD)) {
                disableTool(true);
                disableToolNumber("7", false);
            } else disableTool(true);


            if (actions.contains(INSERT_DICE) || actions.contains(PLACE_DICE_SPACE) || actions.contains(DRAFT_DICE)) {
                diceSpace.setDisable(false);
                if (actions.contains(INSERT_DICE))
                    currentTool = 0;
            } else diceSpace.setDisable(true);


            if (actions.contains(END_TURN))
                endTurn.setDisable(false);
            else endTurn.setDisable(true);


            if (actions.contains(MOVE_DICE) || actions.contains(PLACE_DICE))
                gridPane.setDisable(false);
            else gridPane.setDisable(true);


            if (actions.contains(ROLL_DICE) || actions.contains(FLIP_DICE) || actions.contains(SWAP_DICE_BAG))
                pendingDice.setDisable(false);
            else pendingDice.setDisable(true);

            if (actions.contains(SWAP_DICE))
                roundTrack.setDisable(false);
            else roundTrack.setDisable(true);

            if (actions.contains(CANCEL_USE_TOOL_CARD)) {
                cancelButton.setVisible(true);
                cancelButton.setDisable(false);
            } else {
                cancelButton.setVisible(false);
                cancelButton.setDisable(true);
            }
            if (actions.contains(CANCEL_USE_TOOL_CARD) || actions.contains(SWAP_DICE)
                    || actions.contains(MOVE_DICE) || actions.contains(PLACE_DICE) ||
                   actions.contains(PLACE_DICE_SPACE))
                iconTool.setVisible(true);
            else iconTool.setVisible(false);


        });
    }


    /**set Dice in DiceSpace
     * @param colours colors of every dice in diceSpace ordered
     * @param values values of every dice in diceSpace ordered
     *               It set ImageView of every dice present in DiceSpace
     */
    public void setDiceSpace(final List<String> colours, List<Integer> values) {
        Platform.runLater(() -> {
            diceExtract = new ArrayList<>();

            final int[] j = {0};
            IntStream.iterate(0, i -> i + 1)
                    .limit(colours.size())
                    .forEach(i -> {
                        diceExtract.add(colours.get(i));
                        diceExtract.add(values.get(i).toString());
                        ImageView imageView = (ImageView) diceSpace.getChildren().get(j[0]);
                        String color = colours.get(i);
                        String number = values.get(i).toString();
                        setDice(imageView, color, number);
                        j[0]++;
                    });
        });

    }


    /**
     * Method launched if InsertDice was correct.
     *
     */
    public void insertDiceAccepted() {
        correctInsertion = true;
        synchronized (lock) {
            lock.notify();
        }

    }

    /**
     Method launched if DraftDice was accepted.
    show message/scene to carry on the use of tool card
     */
    public void draftDiceAccepted() {
        Platform.runLater(() -> {
            diceChanged = true;

            if (currentTool == 1) {
                openUndecoreted(config.getParameter(NameConstants.CHANGE_VALUE));
            } else if (currentTool == 6) {
                textflow.setText(GameMessage.USE_TOOL_6);
            } else if (currentTool == 5) {
                textflow.setText(GameMessage.USE_TOOL_5);

            } else if (currentTool == 11)
                textflow.setText(GameMessage.USE_TOOL_11);
        });

    }


    /**
     *  Method launched if MoveDice was correct (during he use of tool n° 2-3-4)
     */
    public void moveDiceAccepted() {
        moveCorrect = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void moveDiceError() {
      moveCorrect = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * it set null ImageView of Dice picked
     * @param index index of dice picked from DiceSpace
     *
     */
    public void pickDiceSpace(final int index) {

        Platform.runLater(() -> {
            if (currentTool != 7){
                ImageView imageView = (ImageView) diceSpace.getChildren().get(index);
                imageView.setImage(null);
                diceExtract.remove(index * 2);
                diceExtract.remove(index * 2);


                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                diceSpaceSort();
            }
        });
    }


    /**
     * Launch message error during pick Dice action
     */
    public void pickDiceSpaceError() {

        textflow.setText(GameMessage.PICK_DICE_ERROR);

    }

    /**
     * place a dice in Schema of player
     * @param nickname nickname of player who placed dice in his schema
     * @param row row index
     * @param column column index
     * @param colour color of dice placed
     * @param value number of dice placed
     */
    public void placeDiceSchema(final String nickname, int row, int column, String colour, int value) {

        Platform.runLater(() -> {

            for (int i = 1; i < schemaPlayers.size(); i = i + 2) {
                GridPane schemaGrid = (GridPane) schemaPlayers.get(i);
                if (schemaGrid.getId().equals(nickname)) {
                    ImageView imageView = (ImageView) getNodeFromGridPane(schemaGrid, column, row);
                    setDice(imageView, colour, ((Integer) value).toString());
                }
            }
        });


    }

    /**
     * Launch message error during PlaceDice in schema
     */
    public void placeDiceSchemaError() {
        Platform.runLater(() -> {
            if (currentTool == 1 || currentTool == 6 || currentTool == 5) {
                textflow.setText(GameMessage.WRONG_INSERTION);
            }
            correctInsertion = false;
            synchronized (lock) {
                lock.notify();
            }
        });

    }


    /**
     * pick set null ImageView of the nickname's schema from cell(row, columns)
     * @param nickname player of dice picked
     * @param row row index of schema
     * @param column column index of schema
     */
    public void pickDiceSchema(String nickname, int row, int column) {
        for (int i = 1; i < schemaPlayers.size(); i = i + 2) {
            GridPane gridPaneLocal = (GridPane) schemaPlayers.get(i);
            if (gridPaneLocal.getId().equals(nickname)) {
                ImageView imageView = (ImageView) getNodeFromGridPane(gridPaneLocal, column, row);
                if (imageView != null) {
                    imageView.setImage(null);
                }
            }
        }

    }

    /**
     * error during the pick schema action. Notify message from server
     */
    public void pickDiceSchemaError() {
        pendingDice.setImage(null);
        textflow.setText(GameMessage.PICK_DICE_SCHEMA_ERROR);

    }

    /**
     * Notify message from server that palyer can proced to use it.
     * @param favor favor used for a tool card
     */
    public void useToolCardAccepted(final int favor) {

        Platform.runLater(() -> {
            iconTool.setVisible(false);

            if (currentTool == 7) {
                serverMessage.setText(GameMessage.EMPTY);
                textflow.setText(GameMessage.USE_TOOL_7);
                disableTool(false);

            } else {
                textflow.setText(GameMessage.USE_TOOL_GENERIC);
                serverMessage.setText(GameMessage.EMPTY);
            }
            nFavour.setText(" x" + favor);


        });

    }

    /**
     * notifies that the specified tool card has been used
     * @param toolCard the tool card that has been used
     */
    public void usedToolCard(int toolCard) {
        setToolUsed(toolCard, USED_TOOL);
    }

    /**
     * notifies that the specified tool card has not been used
     * @param toolCard the tool card that has not been used
     */
    public void notUsedToolCard(int toolCard) {
        setToolUsed(toolCard, NOT_USED_TOOL);
    }

    /**
     * set Image of tool in function of if it's used or not
     * @param toolCard number of toolcard
     * @param notUsedTool tool which is used or not used
     */
    private void setToolUsed(int toolCard, String notUsedTool) {
        Platform.runLater(() -> {
            final int[] tool = {0};
            List<ImageView> useTool = Arrays.asList(use1, use2, use3);
            IntStream.range(0, 3)
                    .forEach(i -> {
                        tool[0] = Integer.parseInt(useTool.get(i).getId());
                        if(tool[0] == toolCard)
                            useTool.get(i).setImage(new Image(notUsedTool));
                    });
        });
    }

    /**
     * Error to use tool card. Notify message from server.
     */
    public void useToolCardError() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.USE_TOOL_CARD_ERROR);
            iconTool.setVisible(true);
        });
    }


    /**
     * Server approves the change value using tool card 1.
     */
    public void changeValueAccepted() {
        Platform.runLater(() -> {
            if (decrement)
                numberMoved--;
            else numberMoved++;
            textflow.setText(GameMessage.PLACE_DICE_CHANGED);
            diceChanged = true;
            setDice(pendingDice, colorMoved, numberMoved);

        });

    }

    /**
     * Server does not approves the change value using tool card 1.
     */
    public void changeValueError() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.CHANGE_VALUE_ERROR);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            openUndecoreted(config.getParameter(NameConstants.CHANGE_VALUE));

        });
    }


    /**
     * action of PlaceDice is accepted by Server using toolCard 1,6,5,10,11.
     * ImageView in the Schema's gridPane was set with the dice placed
     */
    public void placeDiceAccepted() {
        Platform.runLater(() -> {
            if (currentTool == 1 || currentTool == 6 || currentTool == 5
                    || currentTool == 10 || currentTool == 11) {
                schemaCell.setImage(pendingDice.getImage());
                schemaCell.setId(GameMessage.FULL);
                pendingDice.setImage(null);
                textflow.setText(GameMessage.TOOL_USED);
                diceChanged = false;
                iconTool.setVisible(false);
                currentTool = 0;
            } else {
                correctInsertion = true;
                synchronized (lock) {
                    lock.notify();
                }
            }
        });

    }


    /**
     * Pending dice change value after his rolling
     * @param value value of dice rolled
     *
     */
    public void rollDiceAccepted(final int value) {
        Platform.runLater(() -> {

            diceChanged = true;

            textflow.setText(GameMessage.PLACE_DICE_ROOLED);
            setDice(pendingDice, colorMoved, value);
        });


    }

    /**
     * The dice in @nDice position of RoundTrack in the @nRound (number of round) was picked. Its ImageView is setted to null
     * @param nRound number of round
     * @param nDice index of dice in the round
     */
    public void pickDiceRoundTrack(int nRound, int nDice) {
        Platform.runLater(() -> {
            getRoundCell(nRound, nDice, roundTrack).setImage(null);
            diceRoundTrackSort(nRound);
        });
    }

    /**
     * Error during pickDice
     */
    public void pickDiceRoundTrackError() {
        textflow.setText(GameMessage.PICK_DICE_ROUND_ERROR);
    }

    /**
     * Dices(colors and respective numbers) are placed in the @nRound in latest free position
     * @param nRound number of round
     * @param colours color of new Dice
     * @param values number of new Dice
     */
    public void placeDiceRoundTrack(int nRound, final List<String> colours, List<Integer> values) {

        Platform.runLater(() -> IntStream.iterate(0, i -> i + 1)
                .limit(colours.size())
                .forEach(i -> {
                    String color = colours.get(i);
                    String number = values.get(i).toString();
                    ImageView imageView = getLastRoundCell(nRound, roundTrack);
                    setDice(imageView, color, number);

                        }

                ));


    }

    /**
     * Server notify the correct SwapDice action
     *
     */
    public void swapDiceAccepted() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.PLACE_DICE_SWAPPED);
            diceChanged = true;
            pendingDice.setImage(roundDice.getImage());
            disableTool(true);

        });

    }


    /**
     *  Server sent the new favor cancelling use of toolcard
     * @param favor current favor of player

     */
    public void cancelUseToolCardAccepted(final int favor) {
        Platform.runLater(() -> nFavour.setText(" x" + favor));

    }


    /**
     *  Server notify the correct FLipDice action.
     * @param value new value of Dic
     */
    public void flipDiceAccepted(final int value) {

        Platform.runLater(() -> {
            rollDiceAccepted(value);
            diceChanged = true;
        });

    }

    /**
     * Player can place pending dice on DiceSpace.
     */
    public void placeDiceSpaceAccepted() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.TOOL_USED);
            pendingDice.setImage(null);
            diceChanged = false;
            diceExtract.add(colorMoved);
            diceExtract.add(String.valueOf(numberMoved));
        });

    }

    /**
     * it set a newd Dice on DiceSpacd in the last free position.
     * @param colour color of dice placed
     * @param value value of dice placed
     */
    public void placeDiceSpace(final String colour, int value) {
        Platform.runLater(() -> {
            ImageView imageView = getLastCellDicespace(diceSpace);
            setDice(imageView, colour, value);
            diceExtract.add(colour);
            diceExtract.add(String.valueOf(value));
        });

    }

    /**
     * Server notify the correct RoolDice action
     */
    public void rollDiceSpaceAccepted() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.DICE_SPACE_ROLLED);
            iconTool.setVisible(false);
        });

    }

    /**
     * Server notify the correct extract of dice from Dicebag
     * @param colour color of dice swapped
     * @param value value of dice swapped
     */
    public void swapDiceBagAccepted(final String colour, int value) {
        Platform.runLater(() -> {
            colorMoved = colour;
            numberMoved = value;
            setDice(pendingDice, colour, value);
            openUndecoreted(config.getParameter(NameConstants.CHOOSE_VALUE));
        });

    }

    /**
     * Server notify the correct choose of dice value (using toolcard)
     */
    public void chooseValueAccepted() {
        Platform.runLater(() -> {
            setDice(pendingDice, colorMoved, numberMoved);
            textflow.setText(GameMessage.PLACE_DICE_CHOOSEN);

        });

    }

    /**
     *  Server notify the wrong choose of dice value (using toolcard)
     */
    public void chooseValueError() {
        Platform.runLater(() -> {
            textflow.setText(GameMessage.CHOOSE_VALUE_ERROR);
            openUndecoreted(config.getParameter(NameConstants.CHOOSE_VALUE));
        });

    }

    /**
     * Server notify the correct choose of schema loaded by file and it set its constrain.
     * @param name of schema loaded by player
     */
    public void schemaCustomAccepted(String name) {
        Platform.runLater(() -> {
            Schema sch;
            Gson g = new Gson();
            sch = g.fromJson(schemaChoosen, Schema.class);
            printConstrain(schemaConstrain, sch);
            Stage stage = (Stage) schemaA.getScene().getWindow();
            stage.close();
        });

    }

    /**
     * Server sent json schema of opponents player, and it set in the second Tab "Altri Giocatori"
     * @param opponentsSchemas list of schema (json) of opponents player
     */
    public void setOpponentsCustomSchemas(final List<String> opponentsSchemas) {
        Platform.runLater(() -> {

            Gson g = new Gson();
            int i;
            Schema s;

            for (int j = 0; j < opponentsSchemas.size(); j = j + 2) {
                i = 0;

                for (; i < schemaPlayers.size(); i = i + 2) {
                    if (((Text) schemaPlayers.get(i)).getText().equals(""))
                        break;
                }
                if (i == 6)
                    return;
                if (!opponentsSchemas.get(j).equals(nickname.getText())) {
                    ((Text) (schemaPlayers.get(i))).setText(opponentsSchemas.get(j));
                    s = g.fromJson(opponentsSchemas.get(j + 1), Schema.class);
                    printConstrain((GridPane) schemaPlayers.get(i + 1), s);
                    ((GridPane) schemaPlayers.get(i + 1)).setId(opponentsSchemas.get(j));
                }
            }
        });

    }

    /**
     * if @nick = myNickname    ?   (Open winner scene)  : (Open lose scene)
     * @param nick nickname of player who wins
     */
    @Override
    public void setWinner(String nick) {
        Platform.runLater(() -> {
            Stage stage = (Stage) schemaA.getScene().getWindow();
            stage.close();
            if (nick.equals(nickname.getText()))
                openUndecoreted(config.getParameter(NameConstants.WINNER_SCENE));
            else openUndecoreted(config.getParameter(NameConstants.LOSE_SCENE));
        });

    }

    /**
     * Server sent the players ranking with their game point
     * @param players player ordered by their game point
     * @param scores points of the respective player
     */
    @Override
    public void setRankings(List<String> players, List<Integer> scores) {
        Platform.runLater(() -> {
            final int[] count = {0};
            IntStream.iterate(0, i -> i + 1)
                    .limit(scores.size())
                    .forEach(i -> {
                        ((Text) score.getChildren().get(count[0])).setText(players.get(i));
                        ((Text) score.getChildren().get(count[0])).setFont(ea);
                        ((Text) score.getChildren().get(count[0] + 1)).setText(String.valueOf(scores.get(i)));
                        ((Text) score.getChildren().get(count[0] + 1)).setFont(ea);

                        count[0] += 2;
                    });

        });
    }

    /**
     * On reconnection it set every schema with its nickname. the own schema it set on the main tab on javafx
     * @param players list of players nickname
     * @param schemas (String)json of opponents schema
     */
    @Override
    public void setSchemasOnReconnect(List<String> players, List<String> schemas) {


        Platform.runLater(() -> {
            serverMessage.setText(GameMessage.RECONNECTED);
            disableAll();


            int index;
            Stage stage = (Stage) loginAction.getScene().getWindow();
            stage.close();
            schemaPlayers = new ArrayList<>();
            schemaPlayers = Arrays.asList(nickname2, constrain2,
                    nickname3, constrain3, nickname4, constrain4);

            if (players.contains(nickname.getText())) {
                index = players.indexOf(nickname.getText());
                if(!schemas.get(index).equals(" ")) {
                    resetMySchema(schemaConstrain, gridPane, schemas.get(index));
                    schemas.remove(index);
                    players.remove(index);
                }else{
                    serverMessage.setText(WAIT_CHOOSE_SCHEMA);
                    return;
                }
            }

            AtomicInteger count = new AtomicInteger();
            IntStream.iterate(0, i -> i + 1)
                    .limit(players.size())
                    .forEach(i -> {
                        ((Text) (schemaPlayers.get(count.get()))).setText(players.get(i));
                        resetSchema((GridPane) schemaPlayers.get(count.get() + 1), schemas.get(i));
                        ((GridPane) schemaPlayers.get(count.get() + 1)).setId(players.get(i));
                        count.set(count.get() + 2);
                    });
        });
    }

    /**
     * it sorts Dicespace in order not to have empty cells between two Dices
     */
    private void diceSpaceSort() {

        List<Image> dice = new ArrayList<>();
        diceSpace.getChildren().stream()
                .filter(imageView -> (((ImageView) imageView).getImage() != null))
                .forEach(imageView -> {
                    dice.add((((ImageView) imageView).getImage()));
                    ((ImageView) imageView).setImage(null);
                });

        IntStream.range(0, dice.size())
                .forEach(i -> ((ImageView) diceSpace.getChildren().get(i)).setImage(dice.get(i)));

    }

    /**
     *it sorts RoundTrack in order not to have empty cells between two Dices
     * @param round number of Round to sort
     */
    private void diceRoundTrackSort(int round) {
        int index = 3 * round;
        List<Image> dice = new ArrayList<>();
        ImageView imageView;
        AnchorPane anchorPane;
        int count = 0;
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = index; i < 3 + index ; i++) {
            anchorPane = (AnchorPane) roundTrack.getChildren().get(i);

            anchorPane.getChildren().stream()
                    .filter(imageview -> (((ImageView) imageview).getImage() != (null)))
                    .forEach(imageview ->
                    {
                        dice.add(((ImageView) imageview).getImage());
                        ((ImageView) imageview).setImage(null);

                    });
        }

        for (int i = index; i < index + 3; i++) {
            anchorPane = (AnchorPane) roundTrack.getChildren().get(index);
            for (int j = 0; j < 4; j++) {
                if (count < dice.size()) {
                    imageView = (ImageView) anchorPane.getChildren().get(j);
                    imageView.setImage(dice.get(count));
                    count++;
                } else break;

            }
        }

    }
    /**
     * print schema called @nameSchema on the @schemaConstrain (GridPane) constrain on @schemaConstrain
     * @param schemaConstrain gridPane to set schema constrain
     * @param nameSchema name of schema
     */
    private void printSchema(final GridPane schemaConstrain, final String nameSchema) {

        Platform.runLater(() -> {
            Schema schema = new Schema();
            schema = schema.InitSchema(config.getParameter(PATH_INIT_DEFAULT_SCHEMA) + nameSchema);
            printConstrain(schemaConstrain, schema);
        });
    }

    /**
     * load image constrain on imageview as a function of @constrain
     * @param imageView image to put constrain
     * @param constrain
     */
    private void putConstrain(final ImageView imageView, final String constrain) {
        Platform.runLater(() -> {
            String path = config.getParameter(CONSTRAIN_PATH);
            switch (constrain) {
                case ColorConstant.GREEN:
                    imageView.setImage(new Image(path + "green.png"));
                    break;
                case ColorConstant.RED:
                    imageView.setImage(new Image(path + "red.png"));
                    break;
                case ColorConstant.YELLOW:
                    imageView.setImage(new Image(path + "yellow.png"));
                    break;
                case ColorConstant.BLUE:
                    imageView.setImage(new Image(path + "blue.png"));
                    break;
                case ColorConstant.PURPLE:
                    imageView.setImage(new Image(path + "purple.png"));
                    break;
                default:
                    imageView.setImage(new Image(path + constrain + ".png"));
                    break;
            }
        });

    }
    /**
     * get a node (row, col) from gridpane
     * @param gridPane of schema
     * @param col columns index
     * @param row row index
     * @return node placed on cell(@row, @col)
     *
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Node node = gridPane.getChildren().get(count);
                if (i == row && j == col)
                    return node;
                else count++;
            }
        }
        return null;
    }

    /**
     * send to server use tool card request
     * @param event sends to server the number of toolcard that player wants to use
     */
    @FXML
    void useToolCard(final MouseEvent event) {
        Platform.runLater(() -> {
            ImageView tool = (ImageView) event.getSource();
            int numberTool = parseInt(tool.getId());

            if (currentTool == 7) {
                connection.rollDiceSpace();
            } else if (currentTool == 11)
                connection.swapDiceBag();
            else {
                currentTool = numberTool;
                connection.useToolCard(numberTool);
            }
        });
    }

    /**
     * It sents to server request of dice moving (only using toolcard 1,6,5,10,11,2,3,4)
     * and wait the server answer.
     * @param event with MouseEvent player, clicking on cell.
     */
    @FXML
    void moveDiceAction(final MouseEvent event) {

        Thread t = new Thread(() -> {

            Node source = ((Node) event.getTarget());


            if (source != schema1) {
                Node parent;
                while ((parent = source.getParent()) != gridPane) {
                    source = parent;
                }
            }

            Integer col = GridPane.getColumnIndex(source);

            Integer row = GridPane.getRowIndex(source);

            if (col == null)
                col = 0;

            if (row == null)
                row = 0;


            if (currentTool == 1 || currentTool == 6 || currentTool == 5 || currentTool == 10
                    || currentTool == 11) {
                schemaCell = (ImageView) event.getTarget();
                connection.sendPlaceDice(row, col);
            } else {


                if (x1 == null && y1 == null) {
                    imageMoved = (ImageView) event.getSource();
                    x1 = col;
                    y1 = row;
                    textflow.setText(GameMessage.DICE_ACCEPTED);

                } else {

                    schemaCell = (ImageView) event.getSource();

                    x2 = col;
                    y2 = row;

                    moveCorrect = false;
                    connection.moveDice(y1, x1, y2, x2);

                    try {
                        synchronized (lock) {
                            lock.wait();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (moveCorrect) {

                        schemaCell.setImage(imageMoved.getImage());
                        schemaCell.setId(GameMessage.FULL);
                        if (!schemaCell.equals(imageMoved)) {
                            imageMoved.setImage(null);
                            imageMoved.setId("");
                        }

                        x2 = null;
                        y2 = null;
                        if ((currentTool == 4 && isFirst) || (currentTool == 12 && isFirst)) {
                            x1 = null;
                            y1 = null;
                            textflow.setText(GameMessage.FIRST_DICE_TOOL_4_12_OK);
                            diceChanged = false;

                            isFirst = false;

                        } else {
                            x1 = null;
                            y1 = null;
                            textflow.setText(GameMessage.TOOL_USED);
                            iconTool.setVisible(false);
                            diceChanged = false;
                            disableTool(true);
                            currentTool = 0;
                            isFirst = true;

                        }

                    } else {
                        textflow.setText(GameMessage.PLACE_DICE_ERROR);

                        x1 = null;
                        y1 = null;
                    }

                }
            }
        });
        if (((ImageView) event.getTarget()).getId().equals(GameMessage.FULL) || ((y1 != null) && (x1 != null)) ||
                (currentTool == 5) || (currentTool == 10) || (currentTool == 1) || (currentTool == 6)
                || (currentTool == 9) || (currentTool == 11))
            t.start();
        else {
            x1 = null;
            x2 = null;
        }
    }

    /**
     * Player sent to server request to change the pending dice (using tool 6,10,11)
     * @param event clicking on pending dice.
     */
    @FXML
    void rollDice(final MouseEvent event) {
        Platform.runLater(() -> {
            ImageView diceRolling = (ImageView) event.getTarget();
            rotateImage(diceRolling);
            if (currentTool == 6) {
                connection.rollDice();
            } else if (currentTool == 10)
                connection.flipDice();
            else if (currentTool == 11)
                connection.swapDiceBag();
        });

    }

    /**
     *
     *player sents increment/decrement of dice value (toolcard 1)
     * @param event
     */
    @FXML
    void sendChangeValue(final MouseEvent event) {

        Platform.runLater(() -> {
            ImageView imageView = (ImageView) event.getTarget();
            decrement = imageView.getId().equals(GameMessage.DICE_DECREMENT);
            connection.changeValue(imageView.getId());
            Stage stage = (Stage) imageView.getScene().getWindow();
            stage.close();

        });

    }

    /**
     * @param event set pending dice with dice picked
     * on ImageClicked player picks from DiceSpace dice, which become the new DicePending
     */
    @FXML
    void pickDice(final MouseEvent event) {
        Platform.runLater(() -> {
            if (currentTool != 0) {
                rotateImage(pendingDice);
                if ((currentTool == 1 || currentTool == 6 || currentTool == 5 ||
                        currentTool == 10 || currentTool == 11) && !diceChanged) {
                    pendingDice.setImage(((ImageView) event.getTarget()).getImage());
                    indexDiceSpace = parseInt(((ImageView) event.getTarget()).getId());
                    colorMoved = diceExtract.get(2 * indexDiceSpace);
                    numberMoved = parseInt(diceExtract.get(2 * indexDiceSpace + 1));

                    connection.sendDraft(indexDiceSpace);
                } else if (diceChanged)
                    connection.placeDiceSpace();

            }
        });
    }

    /**
     * @param event on dice clicked on ROundTrack
     * on ImageClicked player picks from RoundTrack dice, which become the new DicePending
     */
    @FXML
    void pickRoundTrack(final MouseEvent event) {

        Platform.runLater(() -> {
            ImageView imageView = (ImageView) event.getTarget();
            roundDice = new ImageView();
            roundDice.setImage(imageView.getImage());
            Node source = ((Node) event.getSource());
            Node source2 = source.getParent();
            roundNumber = GridPane.getColumnIndex(source2);
            roundIndex = parseInt((source).getId());
            if (roundNumber == null)
                roundNumber = 0;
            connection.swapDice(roundNumber, roundIndex);

        });

    }


    /**
     * method returns ImageView which is the last round cell (but the first empty cell)
     * @param round number of round
     * @param roundTrack gridpane of RoundTrack
     * @return
     */
    private ImageView getLastRoundCell(int round, GridPane roundTrack) {

        int index = 3 * round;
        AnchorPane anchorPane;
        for (int i = index; i < index + 3; i++) {
            anchorPane = (AnchorPane) roundTrack.getChildren().get(i);
            for (int j = 0; j < 4; j++) {
                ImageView imageView = (ImageView) anchorPane.getChildren().get(j);
                if (imageView.getImage() == null)
                    return imageView;
            }
        }
        return null;

    }


    /**
     * get last cell free on Roundtrack's round
     * @param round number of round
     * @param roundIndex number of Dice on round of Roundtrack
     * @param roundTrack gridpane of RoundTrack
     * @return ImageView which is the image with index @roundIndex in the round number @round
     */
    private ImageView getRoundCell(int round, int roundIndex, GridPane roundTrack) {

        int index = 3 * round;
        AnchorPane anchorPane;
        int count = 0;
        for (int i = index; i < index + 3; i++) {
            anchorPane = (AnchorPane) roundTrack.getChildren().get(i);
            for (int j = 0; j < 4; j++) {
                ImageView imageView = (ImageView) anchorPane.getChildren().get(j);
                if (count == roundIndex)
                    return imageView;
                else count++;
            }
        }
        return null;

    }

    /**
     * (/forall toolcard setDisable(bool) UseToolCard action (on ImageView)
     * @param bool  value to set tools
     */
    private void disableTool(boolean bool) {
        use1.setDisable(bool);
        use2.setDisable(bool);
        use3.setDisable(bool);
    }

    /**
     * it disable all possible action on Javafx pane.
     */
    private void disableAll() {
        endTurn.setDisable(true);
        disableTool(true);
        gridPane.setDisable(true);
        diceSpace.setDisable(true);
        pendingDice.setDisable(true);
        roundTrack.setDisable(true);
    }

    /**
     * It cancels use of tool on Image Clicked
     * @param event Cancel image clicked
     */
    @FXML
    public void cancelTool(MouseEvent event) {
        textflow.setText(GameMessage.TOOL_NOT_USE);
        connection.cancelUseToolCard();
    }

    /**Get last cell free from DiceSpace
     * @param diceSpace gridPane of DiceSpace
     * @return returns ImageView which is the last DiceSpace cell (but the first empty cell)
     */
    @FXML
    public ImageView getLastCellDicespace(GridPane diceSpace) {


        Optional<Node> result = diceSpace.getChildren().stream()
                .filter(imageView -> (((ImageView) imageView).getImage() == (null)))
                .findFirst();

        return (ImageView) result.orElse(null);
    }

    /**
     * it set a new Dice with @color and @value, loading from file its image
     * @param imageView new Image of Dice
     * @param color of dice to set
     * @param number of dice to set
     */
    private void setDice(ImageView imageView, String color, Object number) {

        String path = config.getParameter(DICE_PATH);
        switch (color) {
            case GameMessage.BLUE:
                imageView.setImage(new Image(path + "/Blue/" + number + ".png"));
                break;
            case GameMessage.RED:
                imageView.setImage(new Image(path + "/Red/" + number + ".png"));
                break;
            case GameMessage.YELLOW:
                imageView.setImage(new Image(path + "/Yellow/" + number + ".png"));
                break;
            case GameMessage.GREEN:
                imageView.setImage(new Image(path + "/Green/" + number + ".png"));
                break;
            default:
                imageView.setImage(new Image(path + "/Purple/" + number + ".png"));
                break;
        }
    }

    /**
     * using toolcard 11 on MouseClicked player select the value of the new Dice
     * @param event
     */
    @FXML
    void chooseNumber(final MouseEvent event) {
        Platform.runLater(() -> {
            ImageView imageView = (ImageView) event.getTarget();
            Stage stage = (Stage) imageView.getScene().getWindow();
            stage.close();
            numberMoved = parseInt(imageView.getId());
            connection.chooseValue(numberMoved);
        });


    }


    /**
     * In function of sch, set every image of gridpane with its constrain if present
     * @param mySchema gridpane of schema
     * @param sch schema object
     */
    private void printConstrain(GridPane mySchema, Schema sch) {
        AtomicInteger count = new AtomicInteger();
        IntStream.range(0, 4)
                .forEach(i -> IntStream.range(0, 5)
                        .forEach(j -> {
                            ImageView imageView = (ImageView) mySchema.getChildren().get(count.get());
                            String constrain = sch.getGrid()[i][j].getConstraint();
                            if (!sch.getGrid()[i][j].getConstraint().equals(""))
                                putConstrain(imageView, constrain);
                            count.getAndIncrement();
                        }));


    }


    /**
     * Set disable(bool) Toolcard number @n
     * @param n number of tool
     * @param value boolean of setDisable action used in the method
     */
    private void disableToolNumber(String n, boolean value) {
        if (use1.getId().equals(n))
            use1.setDisable(value);
        else if (use2.getId().equals(n))
            use2.setDisable(value);
        else if (use3.getId().equals(n))
            use3.setDisable(value);
    }

    /**
     * Method make rotate animation to an imageview (@diceRolling)
     * @param diceRolling
     *
     */
    private void rotateImage(ImageView diceRolling) {
        RotateTransition rt;
        rt = new RotateTransition(Duration.millis(3000), diceRolling);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
    }

    /**
     * It closes game scene and reopone the main scene
     * @param event after game finshed, on Mouse Clicked.
     */
    @FXML
    void newGame(MouseEvent event) {
        connection.disconnect();
        mediaPlayer.stop();
        ImageView imageView = (ImageView) event.getTarget();
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
        stage = (Stage) gridPane.getScene().getWindow();
        stage.close();
        changeScene(config.getParameter(NameConstants.FIRST_SCENE));
    }


    /**
     * Print my schema on reconnection
     * @param constrains gridpane of constrain
     * @param gridPane gridPane which will placed dice
     * @param schemaString json of schema (including dices placed )
     */
    private void resetMySchema(GridPane constrains, GridPane gridPane, String schemaString){
        Gson gson = new Gson();
        Schema schema = gson.fromJson(schemaString,Schema.class);
        printConstrain(constrains, schema);
        IntStream.range(0, 4)
                .forEach(i-> IntStream.range(0, 5)
                        .forEach(j ->{
                            if(schema.getGrid()[i][j].getColour() !=null) {
                                ImageView imageView = (ImageView) getNodeFromGridPane(gridPane, j , i );
                                String color = schema.getGrid()[i][j].getColour().toString();
                                int number = schema.getGrid()[i][j].getNumber();
                                setDice(imageView, color, number);
                            }
                        }));
    }


    /**
     * Print schema on reconnection
     * @param constrains gridPane of constrain
     * @param schemaString json schema
     *
     */
    private void resetSchema(GridPane constrains, String schemaString){
        Gson gson = new Gson();
        Schema schema = gson.fromJson(schemaString,Schema.class);
        IntStream.range(0, 4)
                .forEach(i-> IntStream.range(0, 5)
                        .forEach(j ->{
                            ImageView imageView = (ImageView) getNodeFromGridPane(constrains, j , i );
                            if(schema.getGrid()[i][j].getColour() !=null || schema.getGrid()[i][j].getNumber() != 0) {
                                String color = schema.getGrid()[i][j].getColour().toString();
                                int number = schema.getGrid()[i][j].getNumber();
                                if (imageView != null) {
                                    imageView.setImage(null);
                                }
                                setDice(imageView, color, number);
                            }
                            else if(!schema.getGrid()[i][j].getConstraint().equals(""))
                                putConstrain(imageView, schema.getGrid()[i][j].getConstraint());
                        }));
    }

    /**
     * set shine Effect on image entered with mouse
     * @param event Mmouse Entered
     */
    @FXML
    void shineEffect(MouseEvent event) {
        ImageView imageView = (ImageView) event.getTarget();
        Glow glow = new Glow();
        glow.setLevel(0.4);
        imageView.setEffect(glow);
    }

    /**
     * cancel shine Effect
     * @param event Mouse Exited
     */
    @FXML
    void canceShine(MouseEvent event) {
        ImageView imageView = (ImageView) event.getTarget();
        Glow glow = new Glow();
        glow.setLevel(0.0);
        imageView.setEffect(glow);

    }

    /**
     * It opens disconnect request scene
     * @param event Mouse Exited
     */
    @FXML
    void exitAction(MouseEvent event) {
        setNotice(config.getParameter(CLOSE_MESSAGE));

    }

    /**
     * It zooms card on stage
     * @param event Mouse Entered
     */
    @FXML
    void exitImageZoom(MouseEvent event) {
        Platform.runLater(() -> {
            Stage stage = (Stage) imageZoomed.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Play and Pause music during game
     * @param event Mouse Clicked
     */
    @FXML
    void play_stop_Music(MouseEvent event) {
        Platform.runLater(() -> {
            ImageView imageView = (ImageView) event.getTarget();
            if (imageView.getId().equals("0")) {
                mediaPlayer.play();
                imageView.setId("1");
                mute.setVisible(false);
            } else {
                mediaPlayer.pause();
                imageView.setId("0");
                mute.setVisible(true);
            }
        });


    }


}
