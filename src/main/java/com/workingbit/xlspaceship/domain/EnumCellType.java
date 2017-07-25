package com.workingbit.xlspaceship.domain;

/**
 * Created by Aleksey Popryaduhin on 11:58 24/07/2017.
 */
public enum EnumCellType {

    SHIP("*"), MISS("-"), HIT("x"), UNKNOWN(".");

    private final String shape;

    EnumCellType(String shape) {
        this.shape = shape;
    }

    public String shape() {
        return shape;
    }
}
