package com.worth.ifs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {
    @RequestMapping("/index")
    public String index(@RequestParam(value="name", required=false, defaultValue="World..") String name, Model model) {

        return "index";
    }
}
