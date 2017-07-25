package com.workingbit.xlspaceship.domain;

import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 16:34 24/07/2017.
 */
@Data
public class User {

    private String userId;
    private String fullName;

    public User(String userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }
}
