package com.workingbit.xlspaceship.service;

import com.workingbit.xlspaceship.domain.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Aleksey Popryaduhin on 15:51 24/07/2017.
 */
@Service
public class GameService {

    private Map<String, Game> games = new HashMap<>();

    public Map<String, Object> createGame(Map<String, Object> request) {
        User opponent = new User("computer", "XL Computer");
        User player = new User(((String) request.get("user_id")), ((String) request.get("full_name")));
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", opponent.getUserId());
        response.put("full_name", opponent.getFullName());
        // assign game id
        response.put("game_id", "match-" + RandomUtils.nextInt());
        response.put("starting", player.getUserId());
        response.put("rules", request.get("rules"));
        // create game
        Game game = new Game(player, opponent);
        game.placeShips();
        games.put(((String) response.get("game_id")), game);
        return response;
    }

    public Map<String, Object> getGame(String gameId) {
        if (games.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> self = new HashMap<>();
        self.put("user_id", games.get(gameId).getPlayerBoard().getPlayer().getUserId());
        self.put("board", games.get(gameId).getPlayerBoardAsList());
        response.put("self", self);
        Map<String, Object> opponent = new HashMap<>();
        opponent.put("user_id", games.get(gameId).getOpponentBoard().getPlayer().getUserId());
        opponent.put("board", games.get(gameId).getOpponentBoardAsList());
        response.put("self", self);
        response.put("opponent", opponent);
        Map<String, Object> playerTurn = new HashMap<>();
        playerTurn.put("player_turn", games.get(gameId).getPlayerTurn().getUserId());
        response.put("game", playerTurn);
        return response;
    }

    /**
     * Marks game's board with shots
     * @param gameId id of game
     * @param request salvo
     * @param opponent who will be fired
     * @return bitten fields
     */
    public Map<String, Object> fire(String gameId, Map<String, Object> request, boolean opponent) {
        List<String> salvo = (List<String>) request.get("salvo");
        List<Map<String, Coords>> salvos = salvo.stream()
                .map((s) -> {
                    String[] coord = s.split("x");
                    Coords coords = new Coords(Integer.parseInt(coord[0], 16), Integer.parseInt(coord[1], 16));
                    Map<String, Coords> map = new HashMap<>();
                    map.put(s, coords);
                    return map;
                })
                .collect(Collectors.toList());
        Game game = games.get(gameId);
        Map<String, String> fired = new HashMap<>();
        Board playerBoard = opponent ? game.getOpponentBoard() : game.getPlayerBoard();
        for (Map<String, Coords> stringCoordsMap : salvos) {
            for (Cell[] cells : playerBoard.getBoard()) {
                for (Cell cell : cells) {
                    if (stringCoordsMap.values().toArray()[0].equals(cell.getCoords())) {
                        switch (cell.getType()) {
                            case SHIP: {
                                cell.setType(EnumCellType.HIT);
                                if (cell.getShip().isKilled()) {
                                    playerBoard.decShipCount();
                                    fired.put((String) stringCoordsMap.keySet().toArray()[0], "kill");
                                } else {
                                    fired.put((String) stringCoordsMap.keySet().toArray()[0], "hit");
                                }
                                break;
                            }
                            case UNKNOWN: {
                                fired.put((String) stringCoordsMap.keySet().toArray()[0], "miss");
                                cell.setType(EnumCellType.MISS);
                                break;
                            }
                            case HIT:
                            case MISS:
                                break;
                        }
                    }
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("salvo", fired);
        Map<String, String> playerTurn = new HashMap<>();
        game.setPlayerTurn(playerBoard.getPlayer());
        if (game.getPlayerBoard().getShipCount() == 0) {
            playerTurn.put("won", game.getOpponentBoard().getPlayer().getUserId());
        } else if (game.getOpponentBoard().getShipCount() == 0) {
            playerTurn.put("won", game.getPlayerBoard().getPlayer().getUserId());
        } else {
            playerTurn.put("player_turn", game.getPlayerTurn().getUserId());
        }
        response.put("game", playerTurn);
        return response;
    }

    public Map<String, Object> autopilot(String gameId) {
        Map<String, Object> request = new HashMap<>();
        List<String> salvo = new ArrayList<>();
        Game game = games.get(gameId);
        int shipCount = game.getPlayerBoard().getShipCount();
        for (int i = 0; i < shipCount; i++) {
            String x = Integer.toHexString(RandomUtils.nextInt(0, 16));
            String y = Integer.toHexString(RandomUtils.nextInt(0, 16));
            salvo.add(y + "x" + x);
        }
        request.put("salvo", salvo);
        return fire(gameId, request, true);
    }
}
