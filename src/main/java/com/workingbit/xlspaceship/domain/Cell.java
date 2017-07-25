package com.workingbit.xlspaceship.domain;

import com.workingbit.xlspaceship.domain.ship.Ship;
import lombok.Data;

import java.util.Arrays;

/**
 * Created by Aleksey Popryaduhin on 11:42 24/07/2017.
 */
@Data
public class Cell {
    private Ship ship;
    private Coords coords;
    private EnumCellType type = EnumCellType.UNKNOWN;
    private Cell[] neighbours;

    public Cell(int x, int y, Ship ship) {
        this.ship = ship;
        this.coords = new Coords(x, y);
        neighbours = new Cell[4];
    }

//    public void update() {
//        neighbours[0] = board.getCell(coords.getX() - 1, coords.getY());
//        neighbours[1] = board.getCell(coords.getX(), coords.getY() - 1);
//        neighbours[2] = board.getCell(coords.getX() + 1, coords.getY());
//        neighbours[3] = board.getCell(coords.getX(), coords.getY() + 1);
//        for (Cell neighbour : neighbours) {
//            System.out.print("N: " + neighbour + ", ");
//        }
//        System.out.println();
//    }

    public Cell(int x, int y, EnumCellType type, Ship ship) {
        this(x, y, ship);
        this.type = type;
    }

    public Cell getLeft() {
        return neighbours[0];
    }

    public Cell getUp() {
        return neighbours[1];
    }

    public Cell getRight() {
        return neighbours[2];
    }

    public Cell getDown() {
        return neighbours[3];
    }

    @Override
    public String toString() {
        return "Cell{" +
                "coords=" + coords +
                ", type=" + type +
                '}';
    }

    public boolean isOverlap(Cell boardCell) {
        return getCoords().equals(boardCell.getCoords());
    }

    public boolean isTouch(Cell boardCell) {
        return Arrays.stream(neighbours).anyMatch((neighbour) -> {
            boolean b = neighbour != null
                    && neighbour.getCoords().equals(boardCell.getCoords());
            System.out.println(String.format("VAL: %s, N: %s, C: %s", b, neighbour != null ? neighbour.getCoords() : "", this.getCoords()));
            return b;
        });
    }
}
