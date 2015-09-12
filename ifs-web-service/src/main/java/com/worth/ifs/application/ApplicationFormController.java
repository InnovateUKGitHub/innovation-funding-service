package com.worth.ifs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.FinanceFormHandler;
import com.worth.ifs.application.finance.FinanceService;
import com.worth.ifs.application.helper.ApplicationHelper;
import com.worth.ifs.application.helper.SectionHelper;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ResponseService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.service.ApplicationFinanceService;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.service.OrganisationService;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
    OrganisationService organisationService;
    @Autowired
    UserService userService;
    @Autowired
    SectionService sectionService;
    @Autowired
    FinanceService financeService;
    @Autowired
    FinanceFormHandler financeFormHandler;
    
    @Autowired
    TokenAuthenticationService tokenAuthenticationService;


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

        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);

        return "application-form";
    }


    @RequestMapping(value = "/addcost/{applicationId}/{sectionId}/{questionId}")
    public String addAnother(Model model,
                             @PathVariable("applicationId") final Long applicationId,
                             @PathVariable("sectionId") final Long sectionId,
                             @PathVariable("questionId") final Long questionId,
                             HttpServletRequest request) {
        User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
        ApplicationFinance applicationFinance = getApplicationFinanceDetails(user.getId(), applicationId);
        financeService.addCost(applicationFinance.getId(), questionId);
        this.addApplicationDetails(applicationId, user.getId(), sectionId, model);
        return "redirect:/application-form/"+applicationId + "/section/" + sectionId;
    }


    private ApplicationFinance getApplicationFinanceDetails(Long userId, Long applicationId) {
        UserApplicationRole userApplicationRole = userService.findUserApplicationRole(userId, applicationId);
        return applicationFinanceService.getApplicationFinance(applicationId, userApplicationRole.getOrganisation().getId());
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */
    private void addApplicationDetails(Long applicationId, Long userId, Long currentSectionId, Model model){
        ApplicationHelper applicationHelper = new ApplicationHelper();
        Application application = applicationService.getApplicationById(applicationId);
        SectionHelper sectionHelper = new SectionHelper();
        model.addAttribute("currentApplication", application);

        Competition competition = application.getCompetition();
        model.addAttribute("currentCompetition", competition);

        model.addAttribute("applicationOrganisations", applicationHelper.getApplicationOrganisations(application));
        model.addAttribute("assignableUsers", userService.findAssignableUsers(application.getId()));
        // List<UserApplicationRole> relatedUsers = userService.findUserApplicationRole(application.getId());
        model.addAttribute("leadOrganisation", applicationHelper.getApplicationLeadOrganisation(application).orElseGet(() -> null));

        List<Section> sectionsList = sectionHelper.getParentSections(competition.getSections());
        // List to map convertion
        Map<Long, Section> sections =
                sectionsList.stream().collect(Collectors.toMap(Section::getId,
                        Function.identity()));
        model.addAttribute("sections", sections);

        List<Long> completedSections = sectionService.getCompletedSectionIds(applicationId);
        model.addAttribute("completedSections", completedSections);

        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);

        addFinanceDetails(model, applicationId, userId);

        Section currentSection = getSection(application, currentSectionId);
        model.addAttribute("currentSectionId", currentSectionId);
        model.addAttribute("currentSection", currentSection);

        int todayDay =  LocalDateTime.now().getDayOfYear();
        model.addAttribute("todayDay", todayDay);
        model.addAttribute("yesterdayDay", todayDay-1);
    }


    private Section getSection(Application application, Long sectionId) {
        Competition comp = application.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want to show, so we can use this on to show the correct questions.
        Optional<Section> section = sections.stream().
                filter(x -> x.getId().equals(sectionId))
                .findFirst();

        return section.isPresent() ? section.get() : null;
    }

    private void addFinanceDetails(Model model, Long applicationId, Long userId) {
        ApplicationFinance applicationFinance = financeService.getApplicationFinance(applicationId, userId);
        if(applicationFinance==null) {
            applicationFinance = financeService.addApplicationFinance(applicationId, userId);
        }
        log.debug("application id: " + applicationId + " userId " + userId + " finance id " + applicationFinance.getId());
        model.addAttribute("organisationFinance", financeService.getFinances(applicationFinance.getId()));
        model.addAttribute("financeTotal", financeService.getTotal(applicationFinance.getId()));
        model.addAttribute("financeSection", sectionService.getSection("Your finances"));
        model.addAttribute("organisationFinances", financeService.getFinances(applicationFinance.getId()));
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

        Application application = applicationService.getApplicationById(applicationId);
        Competition comp = application.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want, so we can use this on to store the correct questions.
        Section section = sections.stream().filter(x -> x.getId().equals(sectionId)).findFirst().get();
        saveQuestionResponses(request, section.getQuestions(), user.getId(), applicationId);

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> log.info("key "+ key));

        saveApplicationDetails(application, params);

        if(params.containsKey("mark_as_complete")){
            Long questionId = Long.valueOf(request.getParameter("mark_as_complete"));
            responseService.markQuestionAsComplete(applicationId, questionId,user.getId(), true);
        }else if(params.containsKey("mark_as_incomplete")){
            Long questionId = Long.valueOf(request.getParameter("mark_as_incomplete"));
            responseService.markQuestionAsComplete(applicationId, questionId, user.getId(), false);
        }
        if(params.containsKey("assign_question")){
            log.info("assign question now.");
            String assign = request.getParameter("assign_question");
            Long questionId = Long.valueOf(assign.split("_")[0]);
            Long assigneeId = Long.valueOf(assign.split("_")[1]);

            log.info("assign q: "+ questionId);
            log.info("assign a: "+ assigneeId);

            responseService.assignQuestion(applicationId, questionId, user.getId(), assigneeId);
        }

        financeFormHandler.handle(request);

        addApplicationDetails(applicationId, user.getId(), sectionId, model);

        model.addAttribute("applicationSaved", true);
        return "application-form";
    }

    private void saveQuestionResponses(HttpServletRequest request, List<Question> questions, Long userId, Long applicationId) {
        // saving questions from section
        for (Question question : questions) {
            if(request.getParameterMap().containsKey("question[" + question.getId() + "]")){
                String value = request.getParameter("question[" + question.getId() + "]");
                Boolean saved = responseService.saveQuestionResponse(userId, applicationId, question.getId(), value);
                if(!saved){
                    log.error("save failed. " + question.getId());
                }
            }
        }
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
        } else if(inputIdentifier.equals("application_details-duration")){
            Application application = applicationService.getApplicationById(applicationId);
            application.setDurationInMonths(Long.valueOf(value));
            applicationService.saveApplication(application);
        } else if(inputIdentifier.startsWith("application_details-startdate")){
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
        } else if(inputIdentifier.startsWith("cost-")) {
            String fieldName = request.getParameter("fieldName");
            if(fieldName != null && value != null) {
                financeFormHandler.handle(fieldName, value);
            }
        } else {
            Long questionId = Long.valueOf(inputIdentifier);
            responseService.saveQuestionResponse(user.getId(), applicationId, questionId, value);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", "true");
        return node;
    }

}
