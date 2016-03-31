package com.worth.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {
    private static final Log LOG = LogFactory.getLog(DashboardController.class);


    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {


        return "redirect:/competition/1";
    }
}
