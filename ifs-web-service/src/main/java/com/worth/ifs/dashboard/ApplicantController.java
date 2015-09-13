package com.worth.ifs.dashboard;


import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This controller will handle requests related to the current applicant. So pages that are relative to that user,
 * are implemented here. For example the my-applications page.
 */
@Controller
@RequestMapping("/applicant")
public class ApplicantController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationService applicationService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);

        model.addAttribute("applicationProgress", applicationService.getProgress(user.getId()));
        model.addAttribute("applicationsInProcess", applicationService.getInProgress(user.getId()));
        model.addAttribute("applicationsFinished", applicationService.getFinished(user.getId()));

        return "applicant-dashboard";
    }

}
