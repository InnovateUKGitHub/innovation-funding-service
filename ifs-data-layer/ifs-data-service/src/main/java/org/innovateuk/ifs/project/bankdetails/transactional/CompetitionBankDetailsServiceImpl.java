package org.innovateuk.ifs.project.bankdetails.transactional;

import com.opencsv.CSVWriter;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompetitionBankDetailsServiceImpl extends BaseTransactionalService implements CompetitionBankDetailsService {
    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Override
    public ByteArrayResource csvBankDetails(long competitionId) throws IOException {
        List<BankDetails> bankDetails = bankDetailsRepository.findByProjectApplicationCompetitionId(competitionId);
        List<String[]> allRows = buildBankDetailRecords(bankDetails);
        return writeCSVDataToByteArrayResource(allRows);
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
            Address address = bankDetail.getAddress();
            List<String> row = new ArrayList<>();
            row.add(bankDetail.getOrganisation().getName());
            row.add(bankDetail.getProject().getApplication().getId().toString());
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
        title.add("Application Number");
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
