package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Competition;
import com.worth.ifs.domain.Section;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/application")
public class ApplicationController {


    @Autowired
    ApplicationService applicationService;
    @Autowired
    UserService userService;

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */
    private void addApplicationDetails(Long applicationId, Model model){
        Application application = applicationService.getApplicationById(applicationId);
        model.addAttribute("currentApplication", application);

        Competition competition = application.getCompetition();
        model.addAttribute("currentCompetition", competition);

        List<Section> sections = competition.getSections();
        model.addAttribute("sections", sections);
        return ;
    }


    @RequestMapping("/{applicationId}")
    public String applicationDetails(Model model, @PathVariable("applicationId") final Long applicationId){
        System.out.println("Application with id " + applicationId);

        this.addApplicationDetails(applicationId, model);

        return "application-details";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){
        this.addApplicationDetails(applicationId, model);
        //this.addSectionsDetails(applicationId, sectionId, model);
        model.addAttribute("currentSectionId", sectionId);

        return "application-details";
    }
}
