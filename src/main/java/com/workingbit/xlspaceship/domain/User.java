package com.workingbit.xlspaceship.domain;

import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Aleksey Popryaduhin on 16:34 24/07/2017.
 */
@Data
public class User {

    private String userId;
    private String fullName;

    public User(String userId, String fullName) {
        this.userId = StringUtils.isBlank(userId) ? "user-" + RandomUtils.nextInt(0, 1000) : userId;
        this.fullName = fullName;
    }
}
