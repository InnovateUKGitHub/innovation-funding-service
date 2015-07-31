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

@Controller
@RequestMapping("/application")
public class ApplicationController {


    @Autowired
    ApplicationService applicationService;
    @Autowired
    UserService userService;

    protected void addApplicationDetails(Long applicationId, Model model){
        Application application = applicationService.getApplicationById(applicationId);
        model.addAttribute("currentApplication", application);
        Competition competition = application.getCompetition();
        model.addAttribute("currentCompetition", competition);

        List<Section> sections = competition.getSections();


        model.addAttribute("sections", sections);
        return ;
    }
    protected void addSectionsDetails(Long applicationId, Long sectionId, Model model){

        return ;
    }


    @RequestMapping("/{applicationId}")
    public String applicationDetails(Model model,@PathVariable("applicationId") final Long applicationId){
        System.out.println("Application with id " + applicationId);

        this.addApplicationDetails(applicationId, model);

        return "application-details";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetails(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){
        this.addApplicationDetails(applicationId, model);
        //this.addSectionsDetails(applicationId, sectionId, model);
        model.addAttribute("currentSection", sectionId);

        return "application-details";
    }
}
