package it.polimi.ingsw.client.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.client.setUp.TakeDataFile;
import it.polimi.ingsw.client.view.Colour;
import it.polimi.ingsw.client.view.Schema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static it.polimi.ingsw.client.constants.NameConstants.ICON_GAME;
import static it.polimi.ingsw.client.constants.NameConstants.NAME_IS_EMPTY;

public class ControllerEditor {

    @FXML
    private Button okButton;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField schemaName;
    @FXML
    private Text error;
    private int constrain = 0;
    private String id;
    @FXML
    private ImageView nextButton;
    private Schema s;
    private TakeDataFile config;

    public ControllerEditor(){
        this.s = new Schema();
         config = new TakeDataFile();
    }



    public void handleImageDropped(DragEvent dragEvent) {


        ImageView imageView = (ImageView) dragEvent.getTarget();
        GridPane.setColumnIndex(gridPane, 5);
        GridPane.setRowIndex(gridPane, 4);
        Node source = ((Node) dragEvent.getTarget());
        // traverse towards root until userSelectionGrid is the parent node
        if (source != gridPane) {
            Node parent;
            while ((parent = source.getParent()) != gridPane) {
                source = parent;
            }
        }
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        if (colIndex == null)
            colIndex = 0;


        if(rowIndex == null)
            rowIndex = 0;


        if(s.nearConstraint(rowIndex, colIndex, getConstrain(id))) {
            s.getGrid()[rowIndex][colIndex].setConstraint(getConstrain(id));
            imageView.setImage(dragEvent.getDragboard().getImage());
            dragEvent.getDragboard().setContent(null);


            constrain++;
            error.setText("");
        }

        else error.setText("Errore: non stai rispettando le restrizioni!!!");





    }

    public void handleDiceDrag(DragEvent dragEvent) {
        if(dragEvent.getDragboard().hasImage()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void handleDragDetection(MouseEvent mouseEvent) {

        ImageView imageView = (ImageView) mouseEvent.getTarget();
        Dragboard db = imageView.startDragAndDrop(TransferMode.ANY);

        id = imageView.getId();


        ClipboardContent cb = new ClipboardContent();
        cb.putImage(imageView.getImage());
        db.setContent(cb);
        mouseEvent.consume();
    }

    public void handleDragDone(DragEvent dragEvent) {

        ImageView imageView = (ImageView) dragEvent.getTarget();


    }

   public String getConstrain(String s){
        if(s.equals("red"))
            return Colour.ANSI_RED.escape();
       else if(s.equals("blue"))
           return Colour.ANSI_BLUE.escape();
       else if(s.equals("green"))
           return Colour.ANSI_GREEN.escape();
       else if(s.equals("purple"))
           return Colour.ANSI_PURPLE.escape();
       else if(s.equals("yellow"))
           return Colour.ANSI_YELLOW.escape();
       else if(s.equals("one"))
           return "1";
       else if(s.equals("two"))
           return "2";
       else if(s.equals("three"))
           return "3";
       else if(s.equals("four"))
           return "4";
       else if(s.equals("five"))
           return "5";
       else return "6";

   }

    public void cancelRestriction(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getTarget();
        imageView.setImage(null);

        Node source = ((Node) mouseEvent.getTarget());

        if (source != gridPane) {
            Node parent;
            while ((parent = source.getParent()) != gridPane) {
                source = parent;
            }
        }
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        if (colIndex == null)
            colIndex = 0;


        if(rowIndex == null)
            rowIndex = 0;

        s.getGrid()[rowIndex][colIndex].setConstraint("");
        constrain--;

    }


    public void schemaDone(MouseEvent mouseEvent) throws IOException {

        s.setName(schemaName.getText());
        s.setDifficult(constrain);
        saveSchema(s);


    }


    public void saveSchema(Schema s) throws IOException
    {
        String path = null;
        String name;
        String schema;
        Gson g = new Gson();
        s.setPaint(null);
        schema = g.toJson(s);
        String copyPath;
            name = schemaName.getText();
            if (name.equals("")) {
                setNotice(config.getParameter(NAME_IS_EMPTY));
                return;
            }
            else {
                Stage stage = new Stage();
                final Label labelSelectedDirectory = new Label();

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(GameMessage.CHOOSE_DESTINATION);
                File selectedDirectory = directoryChooser.showDialog(stage);
                if (selectedDirectory == null) {
                    labelSelectedDirectory.setText(GameMessage.NO_DIRECTORY);
                } else {
                    path = selectedDirectory.getAbsolutePath();

                    copyPath = path + "/" + name + ".json";
                    FileWriter fw;
                    BufferedWriter b = null;
                    File file = new File(copyPath);

                    if (file.exists())
                        error.setText("Il file " + copyPath + " esiste già");
                    else if (file.createNewFile()) {
                        error.setText("Il file " + copyPath + " è stato creato");
                        stage = (Stage) gridPane.getScene().getWindow();
                        stage.close();
                        fw = new FileWriter(file);
                        try {
                            b = new BufferedWriter(fw);
                            b.write(schema);
                            b.flush();
                        } finally {
                            b.close();
                            fw.close();
                        }


                    } else
                        error.setText("Il file " + path + " non può essere creato");


                }

            }

    }

    public void setNotice(String src) {

        Stage stage = new Stage();
        Pane p = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FXML/" + src + ".fxml"));
            p = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setTitle("SAGRADA GAME");

        Image image = new Image(config.getParameter(ICON_GAME));
        stage.getIcons().add(image);
        stage.setResizable(false);

        stage.show();

    }


    public void nameError(ActionEvent actionEvent) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
