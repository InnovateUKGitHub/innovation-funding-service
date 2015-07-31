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
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping("/application-form")
public class ApplicationFormController {
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
    public String applicationForm(Model model,@PathVariable("applicationId") final Long applicationId){
        System.out.println("Application with id " + applicationId);

        this.addApplicationDetails(applicationId, model);

        return "application-form";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationFormWithOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){

        Application app = applicationService.getApplicationById(applicationId);
        Competition comp = app.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Section section = sections.stream().filter(x -> x.getId() == sectionId).findFirst().get();


        this.addApplicationDetails(applicationId, model);
        model.addAttribute("currentSectionId", sectionId);
        model.addAttribute("currentSection", section);

        return "application-form";
    }
}
