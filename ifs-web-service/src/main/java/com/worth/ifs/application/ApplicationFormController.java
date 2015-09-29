package com.worth.ifs.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.exception.AutosaveElementException;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
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
    public
    @ResponseBody
    String deleteCostWithFragmentResponse(Model model, @PathVariable("applicationId") final Long applicationId,
                                          @PathVariable("sectionId") final Long sectionId,
                                          @PathVariable("costId") final Long costId,
                                          @PathVariable("renderQuestionId") final Long renderQuestionId,
                                          HttpServletRequest request) {

        doDeleteCost(costId);
        return "{\"status\": \"OK\"}";
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
        Section currentSection = getSection(application.getCompetition().getSections(), Optional.of(sectionId));
        Question question = currentSection.getQuestions().stream().filter(q -> q.getId().equals(renderQuestionId)).collect(Collectors.toList()).get(0);
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

    private void saveApplicationForm(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                     HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Application application = applicationService.getById(applicationId);
        Competition comp = application.getCompetition();
        List<Section> sections = comp.getSections();

        // get the section that we want, so we can use this on to store the correct questions.
        Section section = sections.stream().filter(x -> x.getId().equals(sectionId)).findFirst().get();
        saveQuestionResponses(request, section.getQuestions(), user.getId(), applicationId);

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> log.info("key " + key));

        setApplicationDetails(application, params);
        markQuestion(request, params, applicationId, user.getId());

        applicationService.save(application);
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
                                         HttpServletRequest request, RedirectAttributes redirectAttributes){
        Map<String, String[]> params = request.getParameterMap();
        redirectAttributes.addFlashAttribute("applicationSaved", true);

        if (params.containsKey("assign_question")) {
            assignQuestion(model, applicationId, sectionId, request);
            redirectAttributes.addFlashAttribute("assignedQuestion", true);

        }
        saveApplicationForm(model, applicationId, sectionId, request);
        return "redirect:/application-form/"+applicationId + "/section/" + sectionId;
    }

    private void markQuestion(HttpServletRequest request, Map<String, String[]> params, Long applicationId, Long userId) {
        ProcessRole processRole = processRoleService.findProcessRole(userId, applicationId);
        if (processRole == null) {
            return;
        }
        if (params.containsKey("mark_as_complete")) {
            Long questionId = Long.valueOf(request.getParameter("mark_as_complete"));
            questionService.markAsComplete(questionId, applicationId, processRole.getId());
        } else if (params.containsKey("mark_as_incomplete")) {
            Long questionId = Long.valueOf(request.getParameter("mark_as_incomplete"));
            questionService.markAsInComplete(questionId, applicationId, processRole.getId());
        }
    }

    private void saveQuestionResponses(HttpServletRequest request, List<Question> questions, Long userId, Long applicationId) {
        // saving questions from section
        for (Question question : questions) {
            if (request.getParameterMap().containsKey("question[" + question.getId() + "]")) {
                String value = request.getParameter("question[" + question.getId() + "]");
                Boolean saved = responseService.save(userId, applicationId, question.getId(), value);
                if (!saved) {
                    log.error("save failed. " + question.getId());
                }
            }
        }
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
            Long duration = Long.valueOf(applicationDetailParams.get("question[application_details-duration]")[0]);
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
                             HttpServletRequest request) {

        try {
            User user = userAuthenticationService.getAuthenticatedUser(request);

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
                Long questionId = Long.valueOf(inputIdentifier);
                responseService.save(user.getId(), applicationId, questionId, value);
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("success", "true");
            return node;

        } catch (Exception e) {
            throw new AutosaveElementException(inputIdentifier, value, applicationId, e);
        }
    }

    public void assignQuestion(Model model,
                               @PathVariable("applicationId") final Long applicationId,
                               @PathVariable("sectionId") final Long sectionId,
                               HttpServletRequest request) {
        assignQuestion(request, applicationId);
        model.addAttribute("assignedQuestion", true);
    }

    protected Application addApplicationAndFinanceDetails(Long applicationId, Long userId, Optional<Long> currentSectionId, Model model) {
        Application application = super.addApplicationDetails(applicationId, userId, currentSectionId, model);
        addFinanceDetails(model, application, userId);
        return application;
    }
}