//it's the player class with every attributes to report his status during the game (about his turn,
//if it's connected ecc) , and the other object (privateCard, favour and his schema)
package it.polimi.ingsw.server.model.board;
import it.polimi.ingsw.server.internal.mesages.Message;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.virtual.view.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

import static it.polimi.ingsw.server.costants.MessageConstants.*;

public class Player extends Observable {
    private final String nickname;
    private Schema schema;
    private int favour;
    private boolean connected;
    private PrivateObjective prCard;
    private int score;
    private List<Schema> schemas = new ArrayList<>();


    public Player(String nickname) {
        this.nickname = nickname;
        this.connected = true;
        this.score = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public Schema getSchema() {
        return schema;
    }

    /**
     * Sets the schema which have the specified name as the schema of the player, adds the observer to it and sends a
     * confirm message to the player.
     * @param name is the name of the schema that the player has chosen.
     */
    public void setSchema(String name) {
        for (Schema s : schemas) {
            if (s.getName().equals(name)) {
                schema = s;
                favour = schema.getDifficult();
                schema.setPlayer(this);
                schema.addObserver(VirtualView.getVirtualView());
                notifyChanges(APPROVED_SCHEMA);
                return;
            }
        }
        notifyChanges(SET_SCHEMAS);
    }

    /**
     * Sets the specified schema as the schema of the player, adds the observer to it and sends a confirm message to the player.
     * @param schema is the schema that the player has chosen.
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
        favour = schema.getDifficult();
        schema.setPlayer(this);
        schema.addObserver(VirtualView.getVirtualView());
        notifyChanges(APPROVED_SCHEMA);
    }

    /**
     * Sets the specified schema as the schema of the player, adds the observer to it and sends a confirm message to the player.
     * @param schema is the schema that the player has chosen.
     */
    public void setCustomSchema(Schema schema) {
        this.schema = schema;
        favour = schema.getDifficult();
        schema.setPlayer(this);
        schema.addObserver(VirtualView.getVirtualView());
        notifyChanges(APPROVED_SCHEMA_CUSTOM);
    }

    public int getFavour() {
        return favour;
    }

    /**
     * Increment's the player's favors by the specified value.
     * @param value value to be decremented the player's favors.
     */
    public void decrementFavor(int value) {
        this.favour -= value;
    }

    /**
     * Increment's the player's favors by the specified value.
     * @param value value to be decremented the player's favors.
     * */
    public void incrementFavor(int value) {
        this.favour += value;
    }


    public void setFavour(int favour) {
        this.favour = favour;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public PrivateObjective getPrCard() {
        return prCard;
    }

    /**
     * Sets the private objective to the player and notifies it to him.
     * @param prCard is the private objective to be set to the player.
     */
    public void setPrCard(PrivateObjective prCard) {
        this.prCard = prCard;
        notifyChanges(SET_PRIVATE_CARD);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) { this.score = score; }

    /**
     * Sets the list of schema to the player and notifies it to him.
     * @param schemas is the list of schema extracted for the player.
     */
    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
        notifyChanges(SET_SCHEMAS);
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    /**
     * @return a list with the name of schemas that has been extracted for the player.
     */
    public List<String> getNameSchemas() {
        return schemas.stream()
                .map((Schema::getName))
                .collect(Collectors.toList());
    }

    /**
     * Sends the private objective's identifier to the player.
     */
    public void reconnectPlayer(){
        notifyChanges(SET_PRIVATE_CARD);
    }

    /**
     * Notifies different changes to the observer.
     * @param string head of the message that will be sent to the observer.
     */
    private void notifyChanges(String string) {
        Message message = new Message(string);

        switch (string) {
            case SET_SCHEMAS:
                schemas.forEach(sch -> message.addStringArguments(sch.getName()));
                break;
            case SET_PRIVATE_CARD:
                message.addStringArguments(prCard.getColour());
                break;
            case APPROVED_SCHEMA:
                message.addStringArguments(schema.getName());
                break;
            case APPROVED_SCHEMA_CUSTOM:
                message.addStringArguments(schema.getName());
                break;
            default:
                break;
        }
        message.addPlayer(nickname);
        setChanged();
        notifyObservers(message);
    }
}

