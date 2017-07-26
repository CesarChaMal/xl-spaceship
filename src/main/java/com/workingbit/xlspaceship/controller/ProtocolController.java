package com.workingbit.xlspaceship.controller;

import com.workingbit.xlspaceship.common.AppConstants;
import com.workingbit.xlspaceship.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Aleksey Popryaduhin on 10:40 24/07/2017.
 */
@RestController
@RequestMapping(AppConstants.PROTOCOL_RESOURCE)
public class ProtocolController {

    @Autowired
    private GameService gameService;

    @PostMapping(AppConstants.PROTOCOL_GAME_NEW_RESOURCE)
    public Map<String, Object> newGame(@RequestBody Map<String, Object> request) {
        return gameService.createGame(request);
    }

    @PutMapping(AppConstants.PROTOCOL_CATCH_SALVO_RESOURCE)
    public Map<String, Object> catchSalvo(@PathVariable String gameId, @RequestBody Map<String, Object> request) {
        return gameService.fire(gameId, request, false);
    }
}
