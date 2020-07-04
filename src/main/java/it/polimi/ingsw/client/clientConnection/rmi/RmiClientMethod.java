package it.polimi.ingsw.client.clientConnection.rmi;


import it.polimi.ingsw.client.view.Handler;
import it.polimi.ingsw.client.view.View;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import static it.polimi.ingsw.client.constants.MessageConstants.LOGIN_ERROR;
import static it.polimi.ingsw.client.constants.MessageConstants.LOGIN_SUCCESSFUL;


public class RmiClientMethod extends UnicastRemoteObject implements RmiClientMethodInterface {
    private Handler hand;
    private View v;

    RmiClientMethod(Handler hand) throws RemoteException {
        this.hand = hand;
        this.v = hand.getView();
    }

    /**
     * invoked to connect player
     * @param nickname is player's name
     * @param lobbySize is number of player in lobby
     */
    public void login(String nickname, int lobbySize) {
        if (nickname.equals(v.getName())) {
            v.login(LOGIN_SUCCESSFUL);
            v.setNumberPlayer(lobbySize);
        }else
            v.playerConnected(nickname);
    }

    /**
     * invoked when login failed
     * @param cause is cause of the error
     */
    public void loginError(String cause) {
        v.login(LOGIN_ERROR + "-" + cause);
    }


    /**
     * invoked by player when want to disconnect
     * @param nickname is player's name disconnected
     */
    public void playerDisconnected(String nickname) {
        v.playerDisconnected(nickname);
    }


    /**
     * lobby timer
     * @param timeLeft is the remaining lobby time
     */
    public void timerPing(int timeLeft) {
        v.timerPing(((Integer)timeLeft).toString());
    }


    /** turn timer
     * @param timeLeft is the remaining turn time
     */
    public void turnTimerPing(int timeLeft){ v.turnTimerPing(timeLeft); }


    /**
     * Invoked when start the game
     */
    public void createGame() {
        v.createGame();
    }


    /**
     * set player's schema
     * @param schemas are the list of players' schemes
     */
    public void setSchemas(List<String> schemas) { v.setSchemas(schemas); }


    /**
     * set private objective
     * @param privateCard is  player's private objective
     */
    public void setPrivateCard(String privateCard) {
        v.setPrivateCard(privateCard);
    }

    /**
     * set public objective
     * @param publicObjectives are public objective of game
     */
    public void setPublicObjectives(List<String> publicObjectives) { v.setPublicObjectives(publicObjectives); }


    /**
     * set tool card
     * @param toolCards are tool card of game
     */
    public void setToolCards(List<Integer> toolCards) { v.setToolCards(toolCards); }


    /**
     * set own scheme
     * @param schema is the own scheme
     */
    public void chooseSchema(String schema) { v.chooseSchema(schema); }

    /**
     * set opponents schemas
     * @param opponentsSchemas are the opponents schemas
     */
    public void setOpponentsSchemas(List<String> opponentsSchemas) {
        v.setOpponentsSchemas(opponentsSchemas);
    }

    /**
     *  invoked when start round
     */
    public void startRound() {
        v.startRound();
    }

    /**
     * invoked when the turn starts
     * @param nickname is the name of the player to whom the turn is assigned
     */
    public void startTurn(String nickname) {
        v.startTurn(nickname);
    }



    /**
     * used to set legal action
     * @param actions are the possible actions
     */
    public void setActions(List<String> actions) { v.setActions(actions); }

    /**
     * used to set round's diceSpace
     * @param colours are the dice colors of the DiceSpaceused to set round's diceSpace
     *@param values are the dice value of the DiceSpace
     */
    public void setDiceSpace(List<String> colours, List<Integer> values) { v.setDiceSpace(colours,values); }

    /**
     * invoked by server to accept InsertDice action
     */
    public void insertDiceAccepted() {
        v.insertDiceAccepted();
    }

    /**
     * confirm draft dice
     */
    public void draftDiceAccepted() {
        v.draftDiceAccepted();
    }

    /**
     * confirm move dice
     */
    public void moveDiceAccepted() {
        v.moveDiceAccepted();
    }

    /**
     * reject move dice
     */
    public void moveDiceError() {
        v.moveDiceError();
    }

    /**
     * used to remove Dice from DiceSpace
     * @param index of Dice in DiceSpace
     */
    public void pickDiceSpace(int index) {
        try {
            v.pickDiceSpace(index);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *  used to notify the user of an insertDiceSpace error
     */
    public void pickDiceSpaceError() {
        v.pickDiceSpaceError();
    }

    /**
     * used to place a Dice in Schema
     * @param nickname is the name of the player to insert the die in the scheme
     * @param row is index of row of scheme
     * @param column is inde of column of schema
     * @param colour is colour of die
     * @param value is value of die
     */
    public void placeDiceSchema(String nickname, int row, int column, String colour, int value) { v.placeDiceSchema(nickname,row,column,colour,value); }

    /**
     * used to notify the user of an placeDiceSchema error
     */
    public void placeDiceSchemaError() {
        v.placeDiceSchemaError();
    }

    /**
     * pick dice from schema
     * @param nickname is the owner of scheme
     * @param row is index of row
     * @param column is index of column
     */
    public void pickDiceSchema(String nickname, int row, int column) { v.pickDiceSchema(nickname,row,column); }
    /**
     * reject pick dice schema
     */
    public void pickDiceSchemaError() {
        v.pickDiceSchemaError();
    }

    /**
     * confirms that the use of the toolcard has been accepted
     * @param favors is favor remain
     */
    public void useToolCardAccepted(int favors) {
        v.useToolCardAccepted(favors);
    }


    /**
     * notifies that the specified tool card has been used
     * @param toolCard is the tool card that has been used
     */
    public void usedToolCard(int toolCard) { v.usedToolCard(toolCard); }


    /**
     * notifies that the specified tool card has not been used
     * @param toolCard is the tool card that has not been used
     */
    public void notUsedToolCard(int toolCard) { v.notUsedToolCard(toolCard); }

    /**
     * rejects the use of the toolcard
     */
    public void useToolCardError() {
        v.useToolCardError();
    }

    /**
     * confirms that change value has been accepted
     */
    public void changeValueAccepted() {
        v.changeValueAccepted();
    }

    /**
     * rejects change value
     */
    public void changeValueError() {
        v.changeValueError();
    }

    /**
     * confirms place dice
     */
    public void placeDiceAccepted() {
        v.placeDiceAccepted();
    }

    /**
     * confirm roll dice
     * @param value is the new value of dice
     */
    public void rollDiceAccepted(int value) {
        v.rollDiceAccepted(value);
    }

    /**
     * confirm exchange dice
     */
    public void swapDiceAccepted() {
        v.swapDiceAccepted();
    }

    /**
     * pick dice from  round track
     * @param nRound is the index of round track
     * @param nDice  is the index of dice
     */
    public void pickDiceRoundTrack(int nRound, int nDice) { v.pickDiceRoundTrack(nRound,nDice); }

    /**
     * reject pick dice from round track
     */
    public void pickDiceRoundTrackError() {
        v.pickDiceRoundTrackError();
    }

    /**
     * place dices to round track
     * @param nRound is index of round track
     * @param colours are colours of dices
     * @param values are value of dices
     */
    public void placeDiceRoundTrack(int nRound, List<String> colours, List<Integer> values) { v.placeDiceRoundTrack(nRound, colours, values); }

    /**
     * confirm flip dice
     * @param value is new value of the dice
     */
    public void flipDiceAccepted(int value) {
        v.flipDiceAccepted(value);
    }

    /**
     * confirm the invocation of the method cancelUseToolCard
     * @param favors is favors remain
     */
    public void cancelUseToolCardAccepted(int favors) {
        v.cancelUseToolCardAccepted(favors);
    }

    /**
     * place dice in dice space
     * @param colour is the colour of dice
     * @param value is the value of dice
     */
    public void placeDiceSpace(String colour, int value) { v.placeDiceSpace(colour,value); }

    /**
     * confirm place dice in dice space
     */
    public void placeDiceSpaceAccepted() {
        v.placeDiceSpaceAccepted();
    }

    /**
     * confirm roll dice space
     */
    public void rollDiceSpaceAccepted() {
        v.rollDiceSpaceAccepted();
    }

    /**
     * confirm swap dice bag
     * @param colour is the new colour of dice
     * @param value is the new value of dice
     */
    public void swapDiceBagAccepted(String colour, int value) { v.swapDiceBagAccepted(colour,value); }

    /**
     * confirm value accepted
     */
    public void chooseValueAccepted() {
        v.chooseValueAccepted();
    }

    /**
     * reject choose value
     */
    public void chooseValueError() {
        v.chooseValueError();
    }

    /**
     * set winner player
     * @param nickname is name of the winner
     */
    public void setWinner(String nickname) { v.setWinner(nickname); }

    /**
     * set ranking
     * @param players are the names of players
     * @param scores are the scores of players
     */
    public void setRankings(List<String> players, List<Integer> scores) {
        v.setRankings(players,scores);
    }

    /**
     * @param players are the name of players
     * @param schemas are the schemas of players
     */
    public void setSchemasOnReconnect(List<String> players, List<String> schemas) {
        v.setSchemasOnReconnect(players,schemas);
    }


    /**
     * check connection
     */
    public void ping() {}

    /**
     * confirm custom schema
     * @param schema is name of custom schema
     */
    public void schemaCustomAccepted(String schema){
        v.schemaCustomAccepted(schema);
    }

    /**
     * set opponents custom schemas
     * @param opponentsSchemas are custom schemas
     */
    public void setOpponentsCustomSchemas(List<String> opponentsSchemas) { v.setOpponentsCustomSchemas(opponentsSchemas); }
}


