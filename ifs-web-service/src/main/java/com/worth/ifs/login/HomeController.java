package com.worth.ifs.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This Controller redirects the request from http://<domain>/ to http://<domain>/login
 * So we don't have a public homepage, the login page is the homepage.
 */
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
