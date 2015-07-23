package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by nbijl on 23/07/15.
 */
@Controller
public class LoginController {



    /**
     * Greeting request handler
     * @param name Name of the user
     * @param model response model
     * @return thymeleaf template reference
     */
    @RequestMapping("/login")
    public String login(@RequestParam(value="name", required=false, defaultValue="World..") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

}
