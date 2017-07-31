package com.workingbit.xlspaceship.domain.ship;

import com.workingbit.xlspaceship.common.AppConstants;
import com.workingbit.xlspaceship.domain.*;
import lombok.Data;

import java.util.Arrays;

/**
 * Created by Aleksey Popryaduhin on 12:06 24/07/2017.
 */
@Data
public abstract class Ship {

    private Object[] lines;
    private Board board;
    private Coords coords;
    private int width;
    private int length;
    private EnumShipType type;
    private Cell[][] ship;

    public Ship(Integer x, Integer y, Object[] lines, Board board) {
        String sizeLine = (String) lines[0];
        String[] size = sizeLine.split("x");
        setBoard(board);
        setWidth(Integer.valueOf(size[0]));
        setLength(Integer.valueOf(size[1]));
        setCoords(new Coords(x, y));
        setLines(Arrays.copyOfRange(lines, 1, lines.length));
    }

    public boolean tryPlace(Board board) {
        int xStart = getCoords().getX() - 1, yStart = getCoords().getY() - 1;
        if (xStart < 0) {
            xStart = 0;
        }
        if (yStart < 0) {
            yStart = 0;
        }
        int y = yStart, x;
        while (y < yStart + getLength() + 2 && y < AppConstants.BOARD_SIZE) {
            x = xStart;
            while (x < xStart + getWidth() + 2 && x < AppConstants.BOARD_SIZE) {
                Cell cell = getBoard().getBoard()[y][x];
                if (cell.getType().equals(EnumCellType.SHIP)) {
                    return false;
                }
                x++;
            }
            y++;
        }
        return createShip();
    }

    private boolean createShip() {
        Cell[][] cells = new Cell[getLength()][getWidth()];
        for (int i = 0; i < lines.length; i++) {
            String line = (String) lines[i];
            char[] chars = line.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                String ch = String.valueOf(chars[j]);
                int y = getCoords().getY() + i;
                int x = getCoords().getX() + j;
                if (ch.equals(EnumCellType.SHIP.shape())) {
                    if (x >= AppConstants.BOARD_SIZE || y >= AppConstants.BOARD_SIZE) {
                        return false;
                    }
                    cells[i][j] = new Cell(x, y, EnumCellType.SHIP, this);
                } else {
                    cells[i][j] = new Cell(x, y, EnumCellType.UNKNOWN, this);
                }
            }
        }
        setShip(cells);

        // place ships on board
        placeOnBoard(false);
        return true;
    }

    private void placeOnBoard(boolean reset) {
        for (Cell[] c : getShip()) {
            for (Cell cell : c) {
                if (cell == null) {
                    continue;
                }
                getBoard().getBoard()[cell.getCoords().getY()][cell.getCoords().getX()] = reset ? null : cell;
            }
        }
    }

    /**
     * Ship is not killed if at least one part is ship
     * @return
     */
    public boolean isKilled() {
        for (Cell[] aShip : ship) {
            for (Cell anAShip : aShip) {
                if (anAShip.getType() == EnumCellType.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }
}
