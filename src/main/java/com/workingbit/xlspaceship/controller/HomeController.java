package com.workingbit.xlspaceship.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aleksey Popryaduhin on 17:12 24/07/2017.
 */
@RestController
@RequestMapping("/")
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "Home, sweet home…";
    }
}
