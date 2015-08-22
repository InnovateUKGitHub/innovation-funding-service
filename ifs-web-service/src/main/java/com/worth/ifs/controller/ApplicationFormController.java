package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.helper.SectionHelper;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    ApplicationFinanceService applicationFinanceService;
    @Autowired
    UserService userService;
    @Autowired
    TokenAuthenticationService tokenAuthenticationService;
    @Autowired
    CostCategoryService costCategoryService;

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */
    private void addApplicationDetails(Long applicationId, Long userId, Long currentSectionId, Model model){
        Application application = applicationService.getApplicationById(applicationId);
        SectionHelper sectionHelper = new SectionHelper();
        model.addAttribute("currentApplication", application);

        Competition competition = application.getCompetition();
        model.addAttribute("currentCompetition", competition);


        List<Section> sections = sectionHelper.getParentSections(competition.getSections());
        model.addAttribute("sections", sections);

        log.debug("USER ID: " + userId + " application ID: " + applicationId);
        List<CostCategory> costCategories = getApplicationFinanceDetails(userId, applicationId);
        model.addAttribute("costs", costCategories);

        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);
    }

    private List<CostCategory> getApplicationFinanceDetails(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(applicationId, userId);

        log.debug("find organisation id: " + userApplicationRole.getOrganisation());
        ApplicationFinance applicationFinance = applicationFinanceService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
        return costCategoryService.getCostCategoriesByApplicationFinance(applicationFinance.getId());
    }

    @RequestMapping("/{applicationId}")
    public String applicationForm(Model model,@PathVariable("applicationId") final Long applicationId,
                                  HttpServletRequest request){
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        this.addApplicationDetails(applicationId, user.getId(), 0L, model);
        return "application-form";
    }

    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                     HttpServletRequest request){
        Application app = applicationService.getApplicationById(applicationId);
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        Competition comp = app.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Section section = sections.stream().
                filter(x -> x.getId().equals(sectionId)).
                findFirst().get();

        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);
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


        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);
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
        if(applicationDetailParams.containsKey("question[application_details-startdate][year]")){
            int year = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][year]")[0]);
            int month = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][month]")[0]);
            int day = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][day]")[0]);
            LocalDate date = LocalDate.of(year, month, day);
            application.setStartDate(date);
        }
        if(applicationDetailParams.containsKey("question[application_details-duration]")){
            Long duration = Long.valueOf(applicationDetailParams.get("question[application_details-duration]")[0]);
            application.setDurationInMonths(duration);
        }

        applicationService.saveApplication(application);
    }


    @RequestMapping(value = "/saveFormElement", method = RequestMethod.POST)
    public @ResponseBody JsonNode saveFormElement(@RequestParam("questionId") String inputIdentifier,
                                                  @RequestParam("value") String value,
                                                  @RequestParam("applicationId") Long applicationId,
                                                  HttpServletRequest request) {

        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();


        if(inputIdentifier.equals("application_details-title")){
            Application application = applicationService.getApplicationById(applicationId);
            application.setName(value);
            applicationService.saveApplication(application);
        }else if(inputIdentifier.equals("application_details-duration")){
            Application application = applicationService.getApplicationById(applicationId);
            application.setDurationInMonths(Long.valueOf(value));
            applicationService.saveApplication(application);
        }else if(inputIdentifier.startsWith("application_details-startdate")){
            Application application = applicationService.getApplicationById(applicationId);
            LocalDate startDate = application.getStartDate();

            if(startDate == null){
                startDate = LocalDate.now();
            }
            if (inputIdentifier.endsWith("_day")) {
                startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
            }else if (inputIdentifier.endsWith("_month")) {
                startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
            }else if (inputIdentifier.endsWith("_year")) {
                startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
            }
            application.setStartDate(startDate);
            applicationService.saveApplication(application);
        }else{
            Long questionId = Long.valueOf(inputIdentifier);
            responseService.saveQuestionResponse(user.getId(), applicationId, questionId, value);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", "true");
        return node;

    }
}
