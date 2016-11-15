package com.worth.ifs.project.bankdetails.controller;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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

import static com.worth.ifs.commons.service.HttpHeadersUtils.getCSVHeaders;

@RestController
@RequestMapping(value = "/competition/{competitionId}/bank-details")
public class CompetitionBankDetailsController {
    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @RequestMapping("/export")
    public @ResponseBody
    ResponseEntity<Object> export(@PathVariable("competitionId") final Long competitionId) throws IOException {
        List<BankDetails> bankDetails = bankDetailsRepository.findByProjectApplicationCompetitionId(competitionId);
        List<String[]> allRows = buildBankDetailRecords(bankDetails);
        return new ResponseEntity<>(writeCSVDataToByteArrayResource(allRows), getCSVHeaders(), HttpStatus.OK);
    }

    private ByteArrayResource writeCSVDataToByteArrayResource(List<String[]> allRows) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);
        csvWriter.writeAll(allRows);
        csvWriter.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(stringWriter.toString().getBytes());
        return new ByteArrayResource(baos.toByteArray());
    }

    private List<String[]> buildBankDetailRecords(final List<BankDetails> bankDetails){
        List<String[]> allRows = new ArrayList<>();
        allRows.add(getBankDetailCSVHeadingRecord());
        bankDetails.forEach(bankDetail -> {
            Address address = bankDetail.getOrganisationAddress().getAddress();
            List<String> row = new ArrayList<>();
            row.add(bankDetail.getOrganisation().getName());
            row.add(bankDetail.getProject().getApplication().getFormattedId());
            row.add(address.getAddressLine1());
            row.add(address.getAddressLine2());
            row.add(address.getAddressLine3());
            row.add(""); // Note: we don't use address line 4 in this app but is necessary to add empty for target IFS system where it will be imported.
            row.add(address.getTown());
            row.add(address.getCounty());
            row.add(address.getPostcode());
            row.add(bankDetail.getOrganisation().getName());
            row.add(bankDetail.getAccountNumber());
            row.add(bankDetail.getSortCode());
            allRows.add(row.toArray(new String[row.size()]));
        });
        return allRows;
    }

    private String[] getBankDetailCSVHeadingRecord(){
        // Note: I don't like the unnecessarily long column names below but they are as described in INFUND-5852, and possibly required to match exactly for import into target IFS system
        List<String> title = new ArrayList<>();
        title.add("Company name");
        title.add("com.worth.ifs.Application Number");
        title.add("Company address");
        title.add("Company address 2");
        title.add("Company address 3");
        title.add("Company address 4"); // Note: we don't use address line 4 in this app but is necessary to add empty for IFS system where it will be imported.
        title.add("Company town/city");
        title.add("Company county");
        title.add("Company postcode");
        title.add("Bank account name");
        title.add("Bank account number");
        title.add("Bank sort code");
        return title.toArray(new String[title.size()]);
    }
}
