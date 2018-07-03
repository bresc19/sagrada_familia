package it.polimi.ingsw.server.virtualView;

import it.polimi.ingsw.server.internalMesages.Message;
import it.polimi.ingsw.server.serverConnection.Connected;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static it.polimi.ingsw.server.costants.MessageConstants.*;

public class VirtualView extends Observable implements Observer {
    private static VirtualView instance = null;
    private Connected connection;

    private VirtualView(){
        connection = Connected.getConnected();
    }

    public static synchronized VirtualView getVirtualView(){
        if(instance == null)
            instance = new VirtualView();
        return instance;
    }

    public void login(String name){
        Message message = new Message(LOGIN);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void disconnected(String name){
        Message message = new Message(DISCONNECTED);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void sendSchema(String schema, String name){
        Message message = new Message(CHOOSE_SCHEMA);
        message.addStringArguments(schema);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void insertDice(String name, int indexDiceSpace, int row, int column){
        Message message = new Message(INSERT_DICE);
        message.addIntegerArgument(indexDiceSpace);
        message.addIntegerArgument(row);
        message.addIntegerArgument(column);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void useToolCard(String name, int toolNumber){
        Message message;
        message = new Message(USE_TOOL_CARD);
        message.addIntegerArgument(toolNumber);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void moveDice(String name, int oldRow, int oldColumn, int newRow, int newColumn){
        Message message = new Message(MOVE_DICE);
        message.addIntegerArgument(oldRow);
        message.addIntegerArgument(oldColumn);
        message.addIntegerArgument(newRow);
        message.addIntegerArgument(newColumn);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void sendEndTurn(String name){
        Message message = new Message(END_TURN);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void draftDice(String name, int indexDiceSpace){
        Message message = new Message(DRAFT_DICE);
        message.addPlayer(name);
        message.addIntegerArgument(indexDiceSpace);
        setChanged();
        notifyObservers(message);
    }

    public void placeDice(String name, int row, int column){
        Message message = new Message(PLACE_DICE);
        message.addIntegerArgument(row);
        message.addIntegerArgument(column);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void changeValue(String name, String change){
        Message message = new Message(CHANGE_VALUE);
        message.addPlayer(name);
        message.addStringArguments(change);
        setChanged();
        notifyObservers(message);
    }

    public void rollDice(String name){
        Message message = new Message(ROLL_DICE);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void swapDice(String name, int numRound, int indexDice){
        Message message;
        message = new Message(SWAP_DICE);
        message.addIntegerArgument(numRound);
        message.addIntegerArgument(indexDice);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void cancelUseToolCard(String name){
        Message message = new Message(CANCEL_USE_TOOL_CARD);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void flipDice(String name){
        Message message = new Message(FLIP_DICE);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void placeDiceSpace(String name){
        Message message = new Message(PLACE_DICE_SPACE);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void rollDiceSpace(String name){
        Message message = new Message(ROLL_DICE_SPACE);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void swapDiceBag(String name) {
        Message message = new Message(SWAP_DICE_BAG);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void chooseValue(String name, int value) {
        Message message = new Message(CHOOSE_VALUE);
        message.addIntegerArgument(value);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }

    public void sendCustomSchema(String schema, String name) {
        Message message;
        message = new Message(CUSTOM_SCHEMA);
        message.addStringArguments(schema);
        message.addPlayer(name);
        setChanged();
        notifyObservers(message);
    }


    public void update(Observable o, Object arg) {
        Message message = (Message) arg;

        if(message.getHead() == null)
            return;

        switch (message.getHead()){
            case LOGIN_SUCCESSFUL:
                connection.login(message.getPlayers(), message.getStringArgument(0),message.getIntegerArgument(0));
                break;
            case RECONNECT_PLAYER:
                connection.reconnectPlayer(message.getPlayers());
                break;
            case LOGIN_ERROR:
                connection.loginError(message.getPlayers(),message.getStringArgument(0),message.getStringArgument(1));
                break;
            case LOGOUT:
                connection.playerDisconnected(message.getPlayers(),message.getStringArgument(0));
                break;
            case TIMER_PING:
                connection.timerPing(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case START_GAME:
                connection.createGame(message.getPlayers());
                break;
            case SET_SCHEMAS:
                connection.setSchemas(message.getPlayers(),message.getStringArguments());
                break;
            case SET_PRIVATE_CARD:
                connection.setPrivateCard(message.getPlayers(),message.getStringArgument(0));
                break;
            case SET_PUBLIC_OBJECTIVES:
                connection.setPublicObjectives(message.getPlayers(),message.getStringArguments());
                break;
            case SET_TOOL_CARDS:
                connection.setToolCards(message.getPlayers(),message.getIntegerArguments());
                break;
            case APPROVED_SCHEMA:
                connection.chooseSchema(message.getPlayers(),message.getStringArgument(0));
                break;
            case SET_OPPONENTS_SCHEMAS:
                connection.setOpponentsSchemas(message.getPlayers(),message.getStringArguments());
                break;
            case APPROVED_SCHEMA_CUSTOM:
                connection.schemaCustomAccepted(message.getPlayers(),message.getStringArgument(0));
                break;
            case SET_OPPONENTS_CUSTOM_SCHEMAS:
                connection.setOpponentsCustomSchemas(message.getPlayers(),message.getStringArguments());
                break;
            case START_ROUND:
                connection.startRound(message.getPlayers());
                break;
            case START_TURN:
                connection.startTurn(message.getPlayers(),message.getStringArgument(0));
                break;
            case SET_ACTIONS:
                connection.setActions(message.getPlayers(),message.getStringArguments());
                break;
            case SET_DICE_SPACE:
                connection.setDiceSpace(message.getPlayers(),message.getStringArguments(),message.getIntegerArguments());
                break;
            case DRAFT_DICE_ACCEPTED:
                connection.draftDiceAccepted(message.getPlayers());
                break;
            case INSERT_DICE_ACCEPTED:
                connection.insertDiceAccepted(message.getPlayers());
                break;
            case MOVE_DICE_ACCEPTED:
                connection.moveDiceAccepted(message.getPlayers());
                break;
            case MOVE_DICE_ERROR:
                connection.moveDiceError(message.getPlayers());
                break;
            case PICK_DICE_SPACE:
                connection.pickDiceSpace(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case PICK_DICE_SPACE_ERROR:
                connection.pickDiceSpaceError(message.getPlayers());
                break;
            case PLACE_DICE_SCHEMA:
                connection.placeDiceSchema(message.getPlayers(), message.getStringArgument(0), message.getIntegerArgument(0),
                        message.getIntegerArgument(1), message.getStringArgument(1),message.getIntegerArgument(2));
                break;
            case PLACE_DICE_SCHEMA_ERROR:
                connection.placeDiceSchemaError(message.getPlayers());
                break;
            case PICK_DICE_SCHEMA:
                connection.pickDiceSchema(message.getPlayers(),message.getStringArgument(0),
                        message.getIntegerArgument(0),message.getIntegerArgument(1));
                break;
            case PICK_DICE_SCHEMA_ERROR:
                connection.pickDiceSchemaError(message.getPlayers());
                break;
            case USE_TOOL_CARD_ACCEPTED:
                connection.useToolCardAccepted(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case USE_TOOL_CARD_ERROR:
                connection.useToolCardError(message.getPlayers());
                break;
            case CHANGE_VALUE_ACCEPTED:
                connection.changeValueAccepted(message.getPlayers());
                break;
            case CHANGE_VALUE_ERROR:
                connection.changeValueError(message.getPlayers());
                break;
            case PLACE_DICE_ACCEPTED:
                connection.placeDiceAccepted(message.getPlayers());
                break;
            case ROLL_DICE_ACCEPTED:
                connection.rollDiceAccepted(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case SWAP_DICE_ACCEPTED:
                connection.swapDiceAccepted(message.getPlayers());
                break;
            case PICK_DICE_ROUND_TRACK:
                connection.pickDiceRoundTrack(message.getPlayers(),message.getIntegerArgument(0),
                        message.getIntegerArgument(1));
                break;
            case PICK_DICE_ROUND_TRACK_ERROR:
                connection.pickDiceRoundTrackError(message.getPlayers());
                break;
            case PLACE_DICE_ROUND_TRACK:
                List<Integer> values = message.getIntegerArguments();
                Integer round = values.get(0);
                values.remove(0);
                connection.placeDiceRoundTrack(message.getPlayers(),round, message.getStringArguments(),values);
                break;
            case FLIP_DICE_ACCEPTED:
                connection.flipDiceAccepted(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case CANCEL_USE_TOOL_CARD_ACCEPTED:
                connection.cancelUseToolCardAccepted(message.getPlayers(),message.getIntegerArgument(0));
                break;
            case PLACE_DICE_DICESPACE:
                connection.placeDiceSpace(message.getPlayers(),message.getStringArgument(0),
                        message.getIntegerArgument(0));
                break;
            case PLACE_DICE_SPACE_ACCEPTED:
                connection.placeDiceSpaceAccepted(message.getPlayers());
                break;
            case ROLL_DICE_SPACE_ACCEPTED:
                connection.rollDiceSpaceAccepted(message.getPlayers());
                break;
            case SWAP_DICE_BAG_ACCEPTED:
                connection.swapDiceBagAccepted(message.getPlayers(),message.getStringArgument(0),
                        message.getIntegerArgument(0));
                break;
            case CHOOSE_VALUE_ACCEPTED:
                connection.chooseValueAccepted(message.getPlayers());
                break;
            case CHOOSE_VALUE_ERROR:
                connection.chooseValueError(message.getPlayers());
                break;
            case SET_WINNER:
                connection.setWinner(message.getPlayers(),message.getStringArgument(0));
                break;
            case SET_RANKINGS:
                connection.setRankings(message.getPlayers(),message.getStringArguments(),message.getIntegerArguments());
                break;
            case SET_SCHEMAS_ON_RECONNECT:
                List<String> players = new ArrayList<>();
                List<String> schemas= new ArrayList<>();
                for(int i=0; i<message.getStringArguments().size(); i+=2){
                    players.add(message.getStringArgument(i));
                    schemas.add(message.getStringArgument(i+1));
                }
                connection.setSchemasOnReconnect(message.getPlayers(),players,schemas);
                break;
            case SET_PUBLIC_OBJECTIVES_ON_RECONNECT:
                connection.setPublicObjectivesOnReconnect(message.getPlayers(),message.getStringArguments());
                break;
            case SET_TOOL_CARDS_ON_RECONNECT:
                connection.setToolCardsOnReconnect(message.getPlayers(),message.getIntegerArguments());
                break;
            case SET_DICE_SPACE_ON_RECONNECT:
                connection.setDiceSpaceOnReconnect(message.getPlayers(),message.getStringArguments(),message.getIntegerArguments());
                break;
            case PLACE_DICE_ROUND_TRACK_ON_RECONNECT:
                List<Integer> value = message.getIntegerArguments();
                Integer nRound = value.get(0);
                value.remove(0);
                connection.placeDiceRoundTrackOnReconnect(message.getPlayers(),nRound, message.getStringArguments(),value);
                break;
            default:
                break;
        }
    }

    public void sendError() {
        System.out.println("Message not valid");
    }

}
