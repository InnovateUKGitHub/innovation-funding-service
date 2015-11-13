package com.worth.ifs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.exception.AutosaveElementException;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This controller will handle all requests that are related to the application form.
 */
@Controller
@RequestMapping("/application-form")
public class ApplicationFormController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CostService costService;

    @RequestMapping("/{applicationId}")
    public String applicationForm(Model model, @PathVariable("applicationId") final Long applicationId,
                                  HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-form";
    }

    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.GET)
    public String applicationFormWithOpenSection(Model model,
                                                 @PathVariable("applicationId") final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 HttpServletRequest request) {
        Application app = applicationService.getById(applicationId);
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndFinanceDetails(applicationId, user.getId(), Optional.of(sectionId), model);

        return "application-form";
    }

    @RequestMapping(value = "/deletecost/{applicationId}/{sectionId}/{costId}")
    public String deleteCost(Model model, @PathVariable("applicationId") final Long applicationId,
                             @PathVariable("sectionId") final Long sectionId,
                             @PathVariable("costId") final Long costId, HttpServletRequest request) {

        doDeleteCost(costId);
        return "redirect:/application-form/" + applicationId + "/section/" + sectionId;
    }

    @RequestMapping(value = "/deletecost/{applicationId}/{sectionId}/{costId}/{renderQuestionId}", params = "singleFragment=true", produces = "application/json")
    public @ResponseBody JsonStatusResponse deleteCostWithFragmentResponse(Model model, @PathVariable("applicationId") final Long applicationId,
                                          @PathVariable("sectionId") final Long sectionId,
                                          @PathVariable("costId") final Long costId,
                                          @PathVariable("renderQuestionId") final Long renderQuestionId,
                                          HttpServletRequest request) {

        doDeleteCost(costId);
        return JsonStatusResponse.ok();
    }

    private void doDeleteCost(@PathVariable("costId") Long costId) {
        costService.delete(costId);
    }

    @RequestMapping(value = "/addcost/{applicationId}/{sectionId}/{questionId}/{renderQuestionId}", params = "singleFragment=true")
    public String addAnotherWithFragmentResponse(Model model,
                                                 @PathVariable("applicationId") final Long applicationId,
                                                 @PathVariable("sectionId") final Long sectionId,
                                                 @PathVariable("questionId") final Long questionId,
                                                 @PathVariable("renderQuestionId") final Long renderQuestionId,
                                                 HttpServletRequest request) {
        addCost(applicationId, questionId, request);
        return renderSingleQuestionHtml(model, applicationId, sectionId, renderQuestionId, request);
    }

    private String renderSingleQuestionHtml(Model model, Long applicationId, Long sectionId, Long renderQuestionId, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Application application = addApplicationAndFinanceDetails(applicationId, user.getId(), Optional.of(sectionId), model);
        Optional<Section> currentSection = getSection(application.getCompetition().getSections(), Optional.of(sectionId), false);
        Question question = currentSection.get().getQuestions().stream().filter(q -> q.getId().equals(renderQuestionId)).collect(Collectors.toList()).get(0);
        model.addAttribute("question", question);
        return "single-question";
    }

    @RequestMapping(value = "/addcost/{applicationId}/{sectionId}/{questionId}")
    public String addAnother(Model model,
                             @PathVariable("applicationId") final Long applicationId,
                             @PathVariable("sectionId") final Long sectionId,
                             @PathVariable("questionId") final Long questionId,
                             HttpServletRequest request) {
        addCost(applicationId, questionId, request);
        return "redirect:/application-form/" + applicationId + "/section/" + sectionId;
    }

    private void addCost(Long applicationId, Long questionId, HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationFinance applicationFinance = financeService.getApplicationFinance(user.getId(), applicationId);
        financeService.addCost(applicationFinance.getId(), questionId);
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     */

//    private Application addApplicationDetails(Long applicationId, Long userId, Long currentSectionId, Model model) {
//        Application application = applicationService.getById(applicationId);
//        model.addAttribute("currentApplication", application);
//        Competition competition = application.getCompetition();
//        model.addAttribute("currentCompetition", competition);
//
//        Organisation userOrganisation = organisationService.getUserOrganisation(application, userId).get();
//
//        addOrganisationDetails(model, application, Optional.of(userOrganisation));
//        addQuestionsDetails(model, application, Optional.of(userOrganisation), userId);
//        addOrganisationFinanceDetails(model, application, userId);
//        addFinanceDetails(model, application);
//        addMappedSectionsDetails(model, application, Optional.of(currentSectionId), Optional.of(userOrganisation), false);
//        addUserDetails(model, application, userId);
//
//        return application;
//    }

    private void saveApplicationForm(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                     HttpServletRequest request, HttpServletResponse response
                                    ) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Application application = applicationService.getById(applicationId);
        Competition comp = application.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want, so we can use this on to store the correct questions.
        Section section = sections.stream().filter(x -> x.getId().equals(sectionId)).findFirst().get();
        Map<String, String> errors = new HashMap<>();
        saveQuestionResponses(request, section.getQuestions(), user.getId(), applicationId, errors);

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> log.info("key " + key));

        setApplicationDetails(application, params);
        boolean marked = markQuestion(request, params, applicationId, user.getId());

        applicationService.save(application);
        // if a question is marked as complete, don't show the field saved message.
        if(!marked){
            cookieFlashMessageFilter.setFlashMessage(response, "applicationSaved");
        }

        FinanceFormHandler financeFormHandler = new FinanceFormHandler(costService);
        financeFormHandler.handle(request);

        addApplicationAndFinanceDetails(applicationId, user.getId(), Optional.of(sectionId), model);
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String applicationFormSubmit(Model model,
                                        @PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response
                                        ){
        Map<String, String[]> params = request.getParameterMap();


        saveApplicationForm(model, applicationId, sectionId, request, response);


        if (params.containsKey("assign_question")) {
            assignQuestion(model, applicationId, sectionId, request);
            cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
        }

        return "redirect:/application-form/"+applicationId + "/section/" + sectionId;
    }

    private boolean markQuestion(HttpServletRequest request, Map<String, String[]> params, Long applicationId, Long userId) {
        ProcessRole processRole = processRoleService.findProcessRole(userId, applicationId);
        if (processRole == null) {
            return false;
        }
        boolean success = false;
        if (params.containsKey("mark_as_complete")) {
            Long questionId = Long.valueOf(request.getParameter("mark_as_complete"));
            questionService.markAsComplete(questionId, applicationId, processRole.getId());
            success= true;
        }
        if (params.containsKey("mark_as_incomplete")) {
            Long questionId = Long.valueOf(request.getParameter("mark_as_incomplete"));
            questionService.markAsInComplete(questionId, applicationId, processRole.getId());
            success= true;
        }
        return success;
    }

    private Map<String, String> saveQuestionResponses(HttpServletRequest request, List<Question> questions, Long userId, Long applicationId, Map<String, String> errors) {
        // saving questions from section
        for(Question question : questions) {
            if(request.getParameterMap().containsKey("question[" + question.getId() + "]")) {
                String value = request.getParameter("question[" + question.getId() + "]");
                List<String> validatedResponse = responseService.save(userId, applicationId, question.getId(), value);
                if (validatedResponse.size() > 0) {
                    log.error("save failed. " + question.getId());
                    validatedResponse.stream().forEach(
                            v -> errors.put("question-" + question.getId(), v)
                    );
                }
            }
        }
        return errors;
    }

    private void setApplicationDetails(Application application, Map<String, String[]> applicationDetailParams) {
        if (applicationDetailParams.containsKey("question[application_details-title]")) {
            String title = applicationDetailParams.get("question[application_details-title]")[0];
            application.setName(title);
        }
        if (applicationDetailParams.containsKey("question[application_details-startdate][year]")) {
            int year = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][year]")[0]);
            int month = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][month]")[0]);
            int day = Integer.valueOf(applicationDetailParams.get("question[application_details-startdate][day]")[0]);
            LocalDate date = LocalDate.of(year, month, day);
            application.setStartDate(date);
        }
        if (applicationDetailParams.containsKey("question[application_details-duration]")) {
            String durationString = applicationDetailParams.get("question[application_details-duration]")[0];

            Long duration = null;
            if(StringUtils.hasText(durationString)){
                try {
                    duration = Long.parseLong(durationString);
                } catch(NumberFormatException e){
                    // just use the null value.
                }
            }
            application.setDurationInMonths(duration);

        }
    }

    /**
     * This method is for supporting ajax saving from the application form.
     */
    @RequestMapping(value = "/saveFormElement", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonNode saveFormElement(@RequestParam("questionId") String inputIdentifier,
                             @RequestParam("value") String value,
                             @RequestParam("applicationId") Long applicationId,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        try {
            User user = userAuthenticationService.getAuthenticatedUser(request);
            log.debug("INPUT ID: " + inputIdentifier);
            if (inputIdentifier.equals("application_details-title")) {
                Application application = applicationService.getById(applicationId);
                application.setName(value);
                applicationService.save(application);
            } else if (inputIdentifier.equals("application_details-duration")) {
                Application application = applicationService.getById(applicationId);
                application.setDurationInMonths(Long.valueOf(value));
                applicationService.save(application);
            } else if (inputIdentifier.startsWith("application_details-startdate")) {
                Application application = applicationService.getById(applicationId);
                LocalDate startDate = application.getStartDate();

                if (startDate == null) {
                    startDate = LocalDate.now();
                }
                if (inputIdentifier.endsWith("_day")) {
                    startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), Integer.parseInt(value));
                } else if (inputIdentifier.endsWith("_month")) {
                    startDate = LocalDate.of(startDate.getYear(), Integer.parseInt(value), startDate.getDayOfMonth());
                } else if (inputIdentifier.endsWith("_year")) {
                    startDate = LocalDate.of(Integer.parseInt(value), startDate.getMonth(), startDate.getDayOfMonth());
                }
                application.setStartDate(startDate);
                applicationService.save(application);
            } else if (inputIdentifier.startsWith("cost-")) {
                String fieldName = request.getParameter("fieldName");
                FinanceFormHandler financeFormHandler = new FinanceFormHandler(costService);
                if (fieldName != null && value != null) {
                    log.debug("FIELDNAME: " + fieldName + " VALUE: " + value);
                    financeFormHandler.storeField(fieldName, value);
                }
            } else {
                log.debug("ELSE :  " + inputIdentifier);
                Long questionId = Long.valueOf(inputIdentifier);


                List<String> validatedResponse = responseService.save(user.getId(), applicationId, questionId, value);
                if(validatedResponse.size() > 0){
                    log.debug("Response not saved: " + validatedResponse.size());
                    log.debug("Response errors: " + validatedResponse);

                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode node = mapper.createObjectNode();
                    node.put("success", "false");
                    ArrayNode validationErrors = node.putArray("validation_errors");
                    validatedResponse.stream().forEach(v -> validationErrors.add(v));

                    return node;

                }else{
                    log.debug("No errors found somehow...");
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("success", "true");
            return node;

        } catch (Exception e) {
            e.printStackTrace();
//            throw new AutosaveElementException(inputIdentifier, value, applicationId, e);
            AutosaveElementException ex = new AutosaveElementException(inputIdentifier, value, applicationId, e);
            response.setStatus(400);
            log.info("Autosave failed with error: "+ ex.getErrorMessage());
            return ex.createJsonResponse();
        }
    }

//    private void validate(Validator v, Long questionId, String value) {
//        log.info("Validator: "+ v.getId());
//        log.info("Validator classname: "+ v.getClassName());
//
//        try {
//            ResponseValidator responseValidator = new ResponseValidator();
//            responseValidator.validate(response, result);
//
////            Class<?> clazz = Class.forName(v.getClassName());
////            BaseValidator validator = (BaseValidator) clazz.getConstructor().newInstance();
////            validator.validate(value, errors);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }

    public void assignQuestion(Model model,
                               @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("sectionId") final Long sectionId,
                               HttpServletRequest request) {
        assignQuestion(request, applicationId);
    }

    protected Application addApplicationAndFinanceDetails(Long applicationId, Long userId, Optional<Long> currentSectionId, Model model) {
        Application application = super.addApplicationDetails(applicationId, userId, currentSectionId, model, true);
        addOrganisationFinanceDetails(model, application, userId);
        addFinanceDetails(model, application);
        return application;
    }
}