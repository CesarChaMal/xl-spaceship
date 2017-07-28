package com.workingbit.xlspaceship.domain;

import com.workingbit.xlspaceship.common.AppConstants;
import com.workingbit.xlspaceship.domain.ship.*;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 16:37 24/07/2017.
 */
@Data
public class Board {

    public Integer shipCount = 0;
    private User player;
    private Cell[][] board;
    public Integer opponentShipCount;

    public Board(User player) {
        this.player = player;
        this.board = new Cell[AppConstants.BOARD_SIZE][AppConstants.BOARD_SIZE];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = new Cell(j, i, EnumCellType.UNKNOWN, null);
            }
        }
    }

    public Cell getCell(int x, int y) {
        return getCell(new Coords(x, y));
    }

    public Cell getCell(Coords neighbour) {
        for (Cell[] aBoard : board) {
            for (Cell cell : aBoard) {
                if (cell != null && cell.getCoords().equals(neighbour)) {
                    return cell;
                }
            }
        }
        return null;
    }

    public List<String> getBoardAsList(boolean opponent) {
        opponent = false;
        List<String> list = new ArrayList<>();
        for (Cell[] cells1 : board) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Cell cell : cells1) {
                if (!opponent || !cell.getType().equals(EnumCellType.SHIP)) {
                    stringBuilder.append(cell.getType().shape());
                } else {
                    stringBuilder.append(EnumCellType.UNKNOWN.shape());
                }
            }
            list.add(stringBuilder.toString());
        }
        return list;
    }

    public void randomPlaceShips() {
        randomPlaceShip(String.format("/ships/one/a-class-%s.ship", RandomUtils.nextInt(0, 4)), BClassShip.class);
//        randomPlaceShip(String.format("/ships/bclass/b-class-%s.ship", RandomUtils.nextInt(0, 4)), BClassShip.class);
        randomPlaceShip(String.format("/ships/sclass/s-class-%s.ship", RandomUtils.nextInt(0, 4)), SClassShip.class);
        randomPlaceShip(String.format("/ships/winger/winger-%s.ship", RandomUtils.nextInt(0, 4)), WingerShip.class);
        randomPlaceShip(String.format("/ships/aclass/a-class-%s.ship", RandomUtils.nextInt(0, 4)), AClassShip.class);
        randomPlaceShip(String.format("/ships/angle/angle-%s.ship", RandomUtils.nextInt(0, 4)), AngleShip.class);
    }

    private void randomPlaceShip(String name, Class<?> clazz) {
        InputStream resourceAsStream = Board.class.getResourceAsStream(name);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        Object[] lines = bufferedReader.lines().toArray();
        while (true) {
            int x = RandomUtils.nextInt(0, 16);
            int y = RandomUtils.nextInt(0, 16);
            try {
                Ship ship = (Ship) clazz.getConstructor(Integer.class, Integer.class, Object[].class, getClass())
                        .newInstance(x, y, lines, this);
                if (ship.tryPlace(this)) {
                    break;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        shipCount++;
    }

    public void decShipCount() {
        if (shipCount <= 0) {
            return;
        }
        shipCount--;
    }
}
