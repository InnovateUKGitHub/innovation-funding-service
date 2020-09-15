package org.innovateuk.ifs.content;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
@SecuredBySpring(value = "Controller", description = "Controller for static content.", securedType = StaticContentController.class)
@PreAuthorize("permitAll")
public class StaticContentController {

    @GetMapping("contact")
    public String contact() {
        return "content/contact";
    }

    @GetMapping("cookies")
    public String cookies() {
        return "content/cookies";
    }

    @GetMapping("accessibility")
    public String accessibility() {
        return "content/accessibility";
    }
}
