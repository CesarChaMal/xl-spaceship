package com.workingbit.xlspaceship.controller;

import com.workingbit.xlspaceship.common.AppConstants;
import com.workingbit.xlspaceship.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Aleksey Popryaduhin on 10:20 24/07/2017.
 */
@RestController
@RequestMapping(AppConstants.USER_RESOURCE)
public class UserController {

    @Autowired
    private GameService gameService;

    @GetMapping(AppConstants.GAME_ID_RESOURCE)
    public Map<String, Object> getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }

    @PutMapping(AppConstants.GAME_FIRE_RESOURCE)
    public Map<String, Object> fire(@PathVariable String gameId, @RequestBody Map<String, Object> request) {
        return gameService.fire(gameId, request, true);
    }

    @GetMapping(AppConstants.GAME_AUTOPILOT_RESOURCE)
    public String autopilot(@PathVariable("game_id") String gameId) {
        return gameId;
    }

    @GetMapping(AppConstants.GAME_NEW_RESOURCE)
    public String newGame() {
        return "";
    }
}
