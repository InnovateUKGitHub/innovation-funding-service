package org.innovateuk.ifs.eugrant;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class EUGrantController {

    @GetMapping("/overview")
    public String overview() {

        return "eugrant/overview";
    }

}
