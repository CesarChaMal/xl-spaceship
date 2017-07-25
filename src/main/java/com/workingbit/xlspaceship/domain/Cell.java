package com.workingbit.xlspaceship.domain;

import com.workingbit.xlspaceship.domain.ship.Ship;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 11:42 24/07/2017.
 */
@Data
public class Cell {
    private Ship ship;
    private Coords coords;
    private EnumCellType type = EnumCellType.UNKNOWN;

    public Cell(int x, int y, Ship ship) {
        this.ship = ship;
        this.coords = new Coords(x, y);
    }

    public Cell(int x, int y, EnumCellType type, Ship ship) {
        this(x, y, ship);
        this.type = type;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "coords=" + coords +
                ", type=" + type +
                '}';
    }
}
