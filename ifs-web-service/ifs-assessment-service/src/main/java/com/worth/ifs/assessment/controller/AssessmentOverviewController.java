package com.worth.ifs.assessment.controller;

import com.worth.ifs.application.AbstractApplicationController;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.assessment.model.AssessmentOverviewModelPopulator;
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

@Controller
public class AssessmentOverviewController extends AbstractApplicationController {

    private static final Log LOG = LogFactory.getLog(AssessmentOverviewController.class);

    @Autowired
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Autowired
    private AssessmentService assessmentOverviewService;


    @RequestMapping(method = RequestMethod.GET, value = "/{processId}")
    public String getQuestion(Model model, ApplicationForm form, HttpServletResponse response, @PathVariable("processId") final Long processId,
                              HttpServletRequest request) {

        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();
        assessmentOverviewModelPopulator.populateModel(processId, userId, form, model);

        return "assessor-application-overview";
    }
}
