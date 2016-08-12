package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.assessment.form.AssessmentOverviewForm;
import com.worth.ifs.assessment.model.AssessmentFinancesSummaryModelPopulator;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.service.AssessmentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@Controller
public class AssessmentOverviewController extends AbstractApplicationController {

    private static final Log LOG = LogFactory.getLog(AssessmentOverviewController.class);
    private static final String OVERVIEW = "assessment/application-overview";
    private static final String FINANCES_SUMMARY = "assessment/application-finances-summary";
    private static final String DASHBOARD = "assessor-dashboard";


    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Autowired
    private AssessmentService assessmentService;


    @RequestMapping(method = RequestMethod.GET, value = "/{assessmentId}")
    public String getOverview(Model model, AssessmentOverviewForm form, HttpServletResponse response, @PathVariable("assessmentId") final Long assessmentId,
                              HttpServletRequest request) throws InterruptedException, ExecutionException {

        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        assessmentOverviewModelPopulator.populateModel(assessmentId, userId, form, model);

        return OVERVIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{assessmentId}/finances")
    public String getFinancesSummary(Model model, HttpServletResponse response, @PathVariable("assessmentId") final Long assessmentId,
                              HttpServletRequest request) throws InterruptedException, ExecutionException {

        assessmentFinancesSummaryModelPopulator.populateModel(assessmentId, model);

        return FINANCES_SUMMARY;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/{assessmentId}/reject")
    public String rejectInvitation(
            final Model model,
            final HttpServletResponse response,
            @ModelAttribute(MODEL_ATTRIBUTE_FORM) final AssessmentOverviewForm form,
            final BindingResult bindingResult,
            @PathVariable("assessmentId") final Long assessmentId) {

        assessmentService.rejectInvitation(assessmentId,form.getRejectReason(),form.getRejectComment());

        return DASHBOARD;
    }

}
