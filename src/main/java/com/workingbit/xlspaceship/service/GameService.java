package com.workingbit.xlspaceship.service;

import com.workingbit.xlspaceship.domain.Game;
import com.workingbit.xlspaceship.domain.User;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
}
