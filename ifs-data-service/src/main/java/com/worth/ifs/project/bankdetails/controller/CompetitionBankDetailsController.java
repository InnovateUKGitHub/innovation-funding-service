package com.worth.ifs.project.bankdetails.controller;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/competition/{competitionId}/bank-details")
public class CompetitionBankDetailsController {
    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @RequestMapping("/export")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> export(@PathVariable("competitionId") final Long competitionId) throws IOException {
        List<BankDetails> bankDetails = bankDetailsRepository.findByProjectApplicationCompetitionId(competitionId);

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        List<String[]> allRows = new ArrayList<>();

        List<String> title = new ArrayList<>();
        title.add("Company name");
        title.add("Application Number");
        title.add("Address Line 1");
        title.add("Address Line 2");
        title.add("Address Line 3");
        title.add("Town/City");
        title.add("County");
        title.add("Postcode");
        title.add("Account name");
        title.add("Account number");
        title.add("Sort code");

        allRows.add(title.toArray(new String[title.size()]));

        bankDetails.forEach(bankDetail -> {
            Address address = bankDetail.getOrganisationAddress().getAddress();
            List<String> row = new ArrayList<>();
            row.add(bankDetail.getOrganisation().getName());
            row.add(bankDetail.getProject().getApplication().getFormattedId());
            row.add(address.getAddressLine1());
            row.add(address.getAddressLine2());
            row.add(address.getAddressLine3());
            row.add(address.getTown());
            row.add(address.getCounty());
            row.add(address.getPostcode());
            row.add(bankDetail.getOrganisation().getName());
            row.add(bankDetail.getAccountNumber());
            row.add(bankDetail.getSortCode());
            allRows.add(new String[row.size()]);
        });

        csvWriter.writeAll(allRows);
        csvWriter.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(stringWriter.toString().getBytes());
        HttpHeaders httpHeaders = new HttpHeaders();
        // Prevent caching
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        httpHeaders.add("Pragma", "no-cache");
        httpHeaders.add("Expires", "0");
        return new ResponseEntity<>(new ByteArrayResource(baos.toByteArray()), httpHeaders, HttpStatus.OK);
    }
}
