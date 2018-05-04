package it.polimi.ingsw.Model.CardsTest.ObjCardTest;

import com.google.gson.Gson;
import it.polimi.ingsw.Model.Cards.ObjCards.SetObj;
import it.polimi.ingsw.Model.Colour;
import it.polimi.ingsw.Model.Dice;
import it.polimi.ingsw.Model.Schema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//test verify the right calcolus of ScoreCard funnction. test made adding dices in a schema WITHOUT restriction. to complete

public class SetCTest {

    Gson g = new Gson();
    String stringa = "{\"name\":\"Kaleidoscopic Dream\",\"difficult\":4,\"table\":[[{\"c\":\"ANSI_YELLOW\",\"number\":0,\"full\":false},{\"c\":\"ANSI_BLUE\",\"number\":0,\"full\":false},{\"number\":0,\"full\":false},{\"number\":0,\"full\":false},{\"number\":1,\"full\":false}],[{\"c\":\"ANSI_GREEN\",\"number\":0,\"full\":false},{\"number\":0,\"full\":false},{\"number\":5,\"full\":false},{\"number\":0,\"full\":false},{\"number\":4,\"full\":false}],[{\"number\":3,\"full\":false},{\"number\":0,\"full\":false},{\"c\":\"ANSI_RED\",\"number\":0,\"full\":false},{\"number\":0,\"full\":false},{\"c\":\"ANSI_GREEN\",\"number\":0,\"full\":false}],[{\"number\":2,\"full\":false},{\"number\":0,\"full\":false},{\"number\":0,\"full\":false},{\"c\":\"ANSI_BLUE\",\"number\":0,\"full\":false},{\"c\":\"ANSI_YELLOW\",\"number\":0,\"full\":false}]]}";
    Schema s = g.fromJson(stringa, Schema.class);

    public void insertDice(){
        Dice d1 = new Dice(Colour.ANSI_GREEN, 1);
        Dice d2 = new Dice(Colour.ANSI_RED, 2);
        Dice d3 = new Dice(Colour.ANSI_YELLOW, 3);
        Dice d4 = new Dice(Colour.ANSI_BLUE, 4);
        Dice d5 = new Dice(Colour.ANSI_PURPLE, 5);
        Dice d6 = new Dice(Colour.ANSI_RED, 6);
        Dice d7 = new Dice(Colour.ANSI_PURPLE, 1);
        Dice d8 = new Dice(Colour.ANSI_YELLOW, 2);
        Dice d9 = new Dice(Colour.ANSI_GREEN, 3);
        Dice d10 = new Dice(Colour.ANSI_BLUE, 4);
        Dice d11 = new Dice(Colour.ANSI_GREEN, 5);
        Dice d12 = new Dice(Colour.ANSI_BLUE, 6);



        s.insertDice(0, 0, d1);
        s.insertDice(0, 1, d2);
        s.insertDice(0, 2, d3);
        s.insertDice(0, 3, d4);
        s.insertDice(0, 4, d5);
        s.insertDice(2, 0, d6);
        s.insertDice(2, 1, d7);
        s.insertDice(2, 2, d8);
        s.insertDice(2, 3, d9);
        s.insertDice(2, 4, d10);
        s.insertDice(3, 4, d11);
        s.insertDice(3, 2, d12);



    }

    @Test
    public void score_correct_colour(){

       insertDice();
        SetObj card = new SetObj("name", "description", 4);

        assertEquals(8, card.ScoreCard(s), "correct computation");
    }

    @Test
    public void score_correct_number(){
    insertDice();
        SetObj card = new SetObj("name", "description", 5);

        assertEquals(10, card.ScoreCard(s), "correct computation");

    }

    @Test
    public void score_null(){


        SetObj card = new SetObj("name", "description", 5);

        assertEquals(0, card.ScoreCard(s), "result is 0");


    }
}