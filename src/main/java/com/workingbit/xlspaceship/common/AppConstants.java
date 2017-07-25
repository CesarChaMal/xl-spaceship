package com.workingbit.xlspaceship.common;


/**
 * Created by Aleksey Popryaduhin on 10:46 24/07/2017.
 */
public class AppConstants {

    public static final int BOARD_SIZE = 16;

    public static final String USER_RESOURCE = "/xl-spaceship/user";
    public static final String PROTOCOL_RESOURCE = "/xl-spaceship/protocol";

    public static final String GAME_FIRE_RESOURCE = "/game/{gameId}/fire";
    public static final String GAME_AUTOPILOT_RESOURCE = "/game/{gameId}/auto";
    public static final String GAME_NEW_RESOURCE = "/game/new";
    public static final String PROTOCOL_CATCH_SALVO_RESOURCE = "/game/{gameId}";
    public static final String PROTOCOL_GAME_NEW_RESOURCE = "/game/new";
    public static final String GAME_ID_RESOURCE = "/game/{gameId}";
    public static final String LOCAL_CLIENT_URL = "http://localhost:4200";
}
