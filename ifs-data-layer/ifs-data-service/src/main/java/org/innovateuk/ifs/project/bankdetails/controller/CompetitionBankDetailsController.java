package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.project.bankdetails.transactional.CompetitionBankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.innovateuk.ifs.commons.service.HttpHeadersUtils.getCSVHeaders;

@RestController
@RequestMapping(value = "/competition/{competitionId}/bank-details")
public class CompetitionBankDetailsController {

    @Autowired
    private CompetitionBankDetailsService competitionBankDetailsService;

    @GetMapping("/export")
    public @ResponseBody
    ResponseEntity<Object> export(@PathVariable long competitionId) throws IOException {
        return new ResponseEntity<>(competitionBankDetailsService.csvBankDetails(competitionId), getCSVHeaders(), HttpStatus.OK);
    }

}
