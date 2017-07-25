package com.workingbit.xlspaceship.service;

import com.workingbit.xlspaceship.domain.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        response.put("starting", "game-" + RandomUtils.nextInt());
        response.put("rules", request.get("rules"));
        // create game
        Game game = new Game(player, opponent);
        game.placeShips();
        games.put(((String) response.get("game_id")), game);
        return response;
    }

    public Map<String, Object> getGame(String gameId) {
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

    public Map<String, Object> fire(String gameId, Map<String, Object> request) {
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
        for (Map<String, Coords> stringCoordsMap : salvos) {
            for (Cell[] cells : game.getOpponentBoard().getBoard()) {
                for (Cell cell : cells) {
                    if (stringCoordsMap.values().toArray()[0].equals(cell.getCoords())) {
                        switch (cell.getType()) {
                            case SHIP: {
                                cell.setType(EnumCellType.HIT);
                                if (cell.getShip().isKilled()) {
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
        game.setPlayerTurn(game.getOpponentBoard().getPlayer());
        playerTurn.put("player_turn", game.getPlayerTurn().getUserId());
        response.put("game", playerTurn);
        return response;
    }
}
