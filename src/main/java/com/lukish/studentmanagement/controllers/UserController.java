package com.lukish.studentmanagement.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @PostMapping("/hello")
    public String hello(){
    return "Well done Lukeman";
}


}
