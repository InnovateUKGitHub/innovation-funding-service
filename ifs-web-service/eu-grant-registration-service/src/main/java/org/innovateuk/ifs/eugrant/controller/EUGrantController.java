package org.innovateuk.ifs.eugrant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller for the Horizon 2020 grant registration.
 */
@Controller
@RequestMapping("/")
public class EUGrantController {

    @GetMapping("/overview")
    public String overview() {

        return "eugrant/overview";
    }

    @GetMapping("/contact-details")
    public String contactDetails() {

        return "eugrant/contact-details";
    }

}
