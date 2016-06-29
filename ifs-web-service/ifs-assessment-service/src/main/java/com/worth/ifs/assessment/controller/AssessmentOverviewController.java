package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.assessment.form.AssessmentOverviewForm;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
import com.worth.ifs.assessment.service.AssessmentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@Controller
public class AssessmentOverviewController extends AbstractApplicationController {

    private static final Log LOG = LogFactory.getLog(AssessmentOverviewController.class);
    private static final String QUESTION_FORM = "assessor-application-overview";

    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentService assessmentService;


    @RequestMapping(method = RequestMethod.GET, value = "/{processId}")
    public String getQuestion(Model model, AssessmentOverviewForm form, HttpServletResponse response, @PathVariable("processId") final Long processId,
                              HttpServletRequest request) throws InterruptedException, ExecutionException {

        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        assessmentOverviewModelPopulator.populateModel(processId, userId, form, model);

        return "assessor-application-overview";
    }
}
