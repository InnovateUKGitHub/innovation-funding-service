package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/competitions")
public class CompetitionsBankDetailsController {

    @Autowired
    private BankDetailsService bankDetailsService;

    @GetMapping("/pending-bank-details-approvals")
    public RestResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals() {
        return bankDetailsService.getPendingBankDetailsApprovals().toGetResponse();
    }
}

