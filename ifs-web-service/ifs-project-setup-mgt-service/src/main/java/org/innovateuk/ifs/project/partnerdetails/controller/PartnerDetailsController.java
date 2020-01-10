package org.innovateuk.ifs.project.partnerdetails.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.project.partnerdetails.form.SelectPartnerForm;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectDetailsController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to partner details for a project.
 */
@Controller
@RequestMapping("/competition/{competitionId}/project/{projectId}/partner")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class PartnerDetailsController {

    private static final Log LOG = LogFactory.getLog(ProjectDetailsController.class);


    @GetMapping("/select")
    public String selectPartner(@ModelAttribute(value = "form", binding = false)
                                    SelectPartnerForm form,
                                BindingResult bindingResult,
                                @PathVariable long projectId,
                                @PathVariable long competitionId,
                                Model model) {
        return "project/partner-select";
    }



    @GetMapping("/{partnerId}/details")
    public String viewPartnerDetails() {
        return "project/partner-details";
    }
}

