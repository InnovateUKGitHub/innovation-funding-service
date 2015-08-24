package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.*;
import com.worth.ifs.helper.SectionHelper;
import com.worth.ifs.resource.ApplicationFinanceResource;
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
    public final String LABOUR = "Labour";
    public final String WORKING_DAYS_PER_YEAR = "Working days per year";

    @Autowired
    ApplicationService applicationService;
    @Autowired
    ResponseService responseService;
    @Autowired
    ApplicationFinanceService applicationFinanceService;
    @Autowired
    UserService userService;
    @Autowired
    SectionService sectionService;
    @Autowired
    CostService costService;
    
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

        List<Long> completedSections = sectionService.getCompletedSectionIds(applicationId);
        model.addAttribute("completedSections", completedSections);

        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);

        ApplicationFinanceResource applicationFinanceResource = getApplicationFinanceDetails(userId, applicationId);
        HashMap<Long, CostCategory> costCategoriesMap = new HashMap<>();
        model.addAttribute("applicationFinanceResource", applicationFinanceResource);

        Section currentSection = getSection(application, currentSectionId);
        model.addAttribute("currentSectionId", currentSectionId);
        model.addAttribute("currentSection", currentSection);
    }

    private ApplicationFinanceResource getApplicationFinanceDetails(Long applicationId, Long userId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(applicationId, userId);
        return applicationFinanceService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    @RequestMapping("/{applicationId}")
    public String applicationForm(Model model,@PathVariable("applicationId") final Long applicationId,
                                  HttpServletRequest request){
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        this.addApplicationDetails(applicationId, user.getId(), 0L, model);
        return "application-form";
    }


    @RequestMapping(value = "/addanother/{applicationId}/{sectionId}/{costCategoryId}")
    public String addAnother(Model model,
                             @PathVariable("applicationId") final Long applicationId,
                             @PathVariable("sectionId") final Long sectionId,
                             @PathVariable("costCategoryId") final Long costCategoryId, HttpServletRequest request) {
        log.debug("---------- ADD ANOTHER -------- : " + costCategoryId);
        costService.addAnother(costCategoryId);
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);
        return "redirect:/application-form/"+applicationId + "/section/" + sectionId;
    }

    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                     HttpServletRequest request){
        Application app = applicationService.getApplicationById(applicationId);
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();

        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);

        return "application-form";
    }

    private Section getSection(Application application, Long sectionId) {
        Competition comp = application.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Section section = sections.stream().
                filter(x -> x.getId().equals(sectionId)).
                findFirst().get();

        return section;
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button.
     */
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
                Boolean saved = responseService.saveQuestionResponse(user.getId(), applicationId, question.getId(), value);
                if(!saved){
                    log.error("save failed. " + question.getId());
                }
            }
        }

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        this.saveApplicationDetails(app, params);

        if(params.containsKey("mark_as_complete")){
            Long questionId = Long.valueOf(request.getParameter("mark_as_complete"));
            responseService.markQuestionAsComplete(applicationId, questionId,user.getId(), true);
        }else if(params.containsKey("mark_as_incomplete")){
            Long questionId = Long.valueOf(request.getParameter("mark_as_incomplete"));
            responseService.markQuestionAsComplete(applicationId, questionId, user.getId(), false);
        }

        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);

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

    /**
     * This method is for supporting ajax saving from the application form.
     */
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
