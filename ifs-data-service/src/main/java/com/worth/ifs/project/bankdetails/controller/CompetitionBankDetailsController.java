package com.worth.ifs.project.bankdetails.controller;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    ResponseEntity<Object> export(@PathVariable("competitionId") final Long competitionId) throws IOException {
        List<BankDetails> bankDetails = bankDetailsRepository.findByProjectApplicationCompetitionId(competitionId);
        List<String[]> allRows = buildBankDetailRecords(bankDetails);
        return new ResponseEntity<>(writeCSVDataToByteArrayResource(allRows), buildHttpHeaderForCSVExport(), HttpStatus.OK);
    }

    private HttpHeaders buildHttpHeaderForCSVExport(){
        HttpHeaders httpHeaders = new HttpHeaders();
        // Prevent caching
        httpHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        httpHeaders.setPragma("no-cache");
        httpHeaders.setExpires(0);
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        httpHeaders.add("Content-Transfer-Encoding", "binary");
        return httpHeaders;
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
        return title.toArray(new String[title.size()]);
    }
}
