package com.workingbit.xlspaceship.domain;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Aleksey Popryaduhin on 13:26 24/07/2017.
 */
public class WingerShipTest {

    private Game game;

    @Before
    public void setUp() throws Exception {
        this.game = new Game(
                new User("player-1", "player-1"),
                new User("player-2", "player-2"));
    }

    @Test
    public void init() throws Exception {
        game.placeShips();
    }
}
