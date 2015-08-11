package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Competition;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.Section;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping("/application-form")
public class ApplicationFormController {
    private final Log log = LogFactory.getLog(getClass());

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


        List<Response> responses = applicationService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);
    }

    @RequestMapping("/{applicationId}")
    public String applicationForm(Model model,@PathVariable("applicationId") final Long applicationId){
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

    @RequestMapping(value = "/saveFormElement", method = RequestMethod.POST)
    public ResponseEntity<String> saveFormElement(final HttpServletRequest request, @RequestParam("questionId") String questionId, @RequestParam("value") String value) {
        final String referer = request.getHeader("referer");

        System.out.println("Save Form element: referer "+ referer);
        System.out.println("Save Form element: questionId "+ questionId);
        System.out.println("Save Form element: value "+ value);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<String>(headers, HttpStatus.ACCEPTED);

    }
}
