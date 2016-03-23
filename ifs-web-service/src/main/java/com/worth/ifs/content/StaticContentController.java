package com.worth.ifs.content;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/info")
public class StaticContentController {

    @RequestMapping("/{contentName}")
    public String displayContentPage(@PathVariable("contentName") String contentName){
        return "/content/"+contentName;
    }
}
