package org.innovateuk.ifs.content;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
@PreAuthorize("permitAll")
public class StaticContentController {

    @GetMapping("/{contentName}")
    public String displayContentPage(@PathVariable("contentName") String contentName){
        return "content/"+contentName;
    }
}
