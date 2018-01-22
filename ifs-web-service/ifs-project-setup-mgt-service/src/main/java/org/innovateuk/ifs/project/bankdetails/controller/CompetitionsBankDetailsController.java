package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.bankdetails.viewmodel.CompetitionPendingBankDetailsApprovalsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for handling Bank Details across all competitions
 */
@Controller
@RequestMapping("/competitions")
public class CompetitionsBankDetailsController {

    @Autowired
    private BankDetailsRestService bankDetailsRestService;

    @SecuredBySpring(value = "PENDING_BANK_DETAILS_APPROVALS", description = "Project finance users can view and action pending bank details approvals for all competitions")
    @GetMapping("/status/pending-bank-details-approvals")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewPendingBankDetailsApprovals(Model model, UserResource loggedInUser) {

        List<BankDetailsReviewResource> pendingBankDetails = bankDetailsRestService.getPendingBankDetailsApprovals().getSuccessObjectOrThrowException();

        model.addAttribute("model", new CompetitionPendingBankDetailsApprovalsViewModel(pendingBankDetails));
        return "project/competition-pending-bank-details";
    }
}

