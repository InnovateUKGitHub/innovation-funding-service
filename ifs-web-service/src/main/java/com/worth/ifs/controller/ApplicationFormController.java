package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.ApplicationService;
import com.worth.ifs.service.ResponseService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ResponseService responseService;

    @Autowired
    TokenAuthenticationService tokenAuthenticationService;

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


        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
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

    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){
        Application app = applicationService.getApplicationById(applicationId);
        Competition comp = app.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Section section = sections.stream().
                filter(x -> x.getId().equals(sectionId)).
                findFirst().get();

        this.addApplicationDetails(applicationId, model);
        model.addAttribute("currentSectionId", sectionId);
        model.addAttribute("currentSection", section);

        return "application-form";
    }
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String applicationFormSubmit(Model model,
                                                 @PathVariable("applicationId") final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 HttpServletRequest request){

        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();

        Application app = applicationService.getApplicationById(applicationId);
        Competition comp = app.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Section section = sections.stream().filter(x -> x.getId().equals(sectionId)).findFirst().get();

        // saving questions from section
        List<Question> questions = section.getQuestions();
        for (Question question : questions) {
            if(request.getParameterMap().containsKey("question[" + question.getId() + "]")){
                String value = request.getParameter("question[" + question.getId() + "]");
                responseService.saveQuestionResponse(user.getId(), applicationId, question.getId(), value);
            }
        }

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        this.saveApplicationDetails(app, params);


        this.addApplicationDetails(applicationId, model);
        model.addAttribute("currentSectionId", sectionId);
        model.addAttribute("currentSection", section);
        model.addAttribute("applicationSaved", true);
        return "application-form";
    }

    private void saveApplicationDetails(Application application, Map<String, String[]> applicationDetailParams) {
        if(applicationDetailParams.containsKey("question[application_details-title]")){
            String title = applicationDetailParams.get("question[application_details-title]")[0];
            application.setName(title);
        }
//        if(applicationDetailParams.containsKey("question[application_details-startdate][year]")){
//            int year = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][year]")[0]) ;
//            int month = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][month]")[0]) ;
//            int day = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][day]")[0]) ;
//
//            log.error("Start date: "+ year);
//            log.error("Start date: "+ month);
//            log.error("Start date: "+ day);
//            LocalDate date = LocalDate.of(year, month, day);
//            log.error("Date obj "+ date.toString());
//            application.setStartDate(date);
//        }
        if(applicationDetailParams.containsKey("question[application_details-duration]")){
            Long duration = Long.valueOf(applicationDetailParams.get("question[application_details-duration]")[0]);
            log.error("set duration: "+ duration);
            application.setDurationInMonths(duration);
        }

        applicationService.saveApplication(application);

    }


    @RequestMapping(value = "/saveFormElement", method = RequestMethod.POST)
    public @ResponseBody JsonNode saveFormElement(@RequestParam("questionId") Long questionId,
                                                  @RequestParam("value") String value,
                                                  @RequestParam("applicationId") Long applicationId,
                                                  HttpServletRequest request) {

        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();

        responseService.saveQuestionResponse(user.getId(), applicationId, questionId, value);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", "true");
        return node;

    }
}
