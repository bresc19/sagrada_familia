package it.polimi.ingsw.serverTest.modelTest.boardTest;

import it.polimi.ingsw.server.exception.ChangeDiceValueException;
import it.polimi.ingsw.server.model.board.Dice;
import it.polimi.ingsw.server.model.board.DiceBag;
import org.junit.jupiter.api.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


class DiceBagTest {
    private DiceBag db = new DiceBag();

    private boolean fullSetup(DiceBag db) {
        int r = 0, y = 0, p = 0, g = 0, b = 0;
        int notZero = 0;
        for (Dice d : db.getDices()) {
            switch (d.getColour()) {
                case ANSI_RED:
                    r++;
                    break;
                case ANSI_YELLOW:
                    y++;
                    break;
                case ANSI_PURPLE:
                    p++;
                    break;
                case ANSI_GREEN:
                    g++;
                    break;
                case ANSI_BLUE:
                    b++;
                    break;
                default:
            }
            if (d.getValue() != 0)
                notZero++;
        }
        return r == 18 && y == 18 && p == 18 && g == 18 && b == 18 && notZero == 0;
    }

    @Test
    void FinishDice() {
        for (int i = 0; i < 10; i++)
            db.extract(4).size();
        int n = db.extract(4).size();
        assertEquals(0, n);
    }

    @Test
    void NumberExtractDice() {
        int nPlayer = 4;
        db.extract(nPlayer);
        assertEquals(db.getDices().size(), 90 - (nPlayer * 2 + 1));
    }

    @Test
    void initialQuantity() {
        assertEquals(90, db.getDices().size());
    }

    @Test
    void finalQuantity() {
        int nPlayer = 4;
        for (int i = 0; i < 10; i++) {
            db.extract(nPlayer);

        }
        assertEquals(0, db.getDices().size());
    }

    @Test
    void insertDiceTest() {
        Dice dice = db.takeDice();
        assertFalse(fullSetup(db));

        try {
            dice.setValue(2);
        } catch (ChangeDiceValueException e) {
            e.getMessage();
        }
        db.insertDice(dice);
        assertTrue(fullSetup(db));
    }
}
