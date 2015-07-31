package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Competition;
import com.worth.ifs.domain.Section;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/application-form")
public class ApplicationFormController extends ApplicationController {


    @RequestMapping("/{applicationId}")
    public String applicationDetails(Model model,@PathVariable("applicationId") final Long applicationId){
        System.out.println("Application with id " + applicationId);

        this.addApplicationDetails(applicationId, model);

        return "application-form";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetails(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){

        Application app = applicationService.getApplicationById(applicationId);
        Competition comp = app.getCompetition();
        List<Section> sections = comp.getSections();
        Section section = sections.stream().filter(x -> x.getId() == sectionId).findFirst().get();


        this.addApplicationDetails(applicationId, model);
        //this.addSectionsDetails(applicationId, sectionId, model);
        model.addAttribute("currentSectionId", sectionId);
        model.addAttribute("currentSection", section);

        return "application-form";
    }
}
