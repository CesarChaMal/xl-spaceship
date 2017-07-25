package com.workingbit.xlspaceship.domain.ship;

import com.workingbit.xlspaceship.domain.Board;
import com.workingbit.xlspaceship.domain.EnumShipType;

/**
 * Created by Aleksey Popryaduhin on 12:27 24/07/2017.
 */
public class AngleShip extends Ship {

    public AngleShip(Integer x, Integer y, Object[] lines, Board board) {
        super(x, y, lines, board);
        setType(EnumShipType.ANGLE);
    }
}
