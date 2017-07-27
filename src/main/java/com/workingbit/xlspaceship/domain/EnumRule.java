package com.workingbit.xlspaceship.domain;

import com.workingbit.xlspaceship.common.AppConstants;

import java.util.Objects;

/**
 * Created by Aleksey Popryaduhin on 16:14 27/07/2017.
 */
public enum EnumRule {
    STANDARD("standard"), X_SHOT("shot"), SUPER_CHARGE("super-charge"), DESPERATION("desperation");

    private int xShot;
    private final String str;

    EnumRule(String str) {
        this.str = str;
    }

    public int getxShot() {
        return xShot;
    }

    public static EnumRule fromString(String rule) {
        for (EnumRule enumRule : values()) {
            if (Objects.equals(enumRule.str, rule)) {
                return enumRule;
            } else if (enumRule.str.contains("shot") && rule.contains("shot")) {
                enumRule.xShot = Integer.valueOf(rule.split("-")[0]);
                return enumRule;
            }
        }
        return null;
    }

    public int getShotCount(Board player) {
        switch (this) {
            case STANDARD:
                return player.getShipCount();
            case X_SHOT:
                return xShot;
            case DESPERATION:
                return AppConstants.SHIP_COUNT - player.getOpponentShipCount() + 1;
        }
        throw new RuntimeException("Unknown rules");
    }
}
