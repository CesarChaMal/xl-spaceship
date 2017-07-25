package com.workingbit.xlspaceship.domain;

import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 12:13 24/07/2017.
 */
@Data
public class Coords {
    private int x;
    private int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
