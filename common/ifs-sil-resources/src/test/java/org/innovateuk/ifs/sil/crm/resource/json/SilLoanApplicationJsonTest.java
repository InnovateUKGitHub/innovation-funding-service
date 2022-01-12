package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;
import static org.innovateuk.ifs.sil.common.json.Constants.UTC;
import static org.innovateuk.ifs.sil.common.json.Constants.LOANS_DATETIME_FORMAT;

public class SilLoanApplicationJsonTest {

    private static final String loanSubmittedToString = "SilLoanApplication(applicationID=10000234, applicationSubmissionDate=2021-10-12T09:38:12.850Z[UTC], applicationName=Sample skips for plastic storage, applicationLocation=RG1 4AF, competitionCode=2109-13, competitionName=Resilience Fund: Strand 2, projectDuration=10, projectTotalCost=10634.0, projectOtherFunding=1200.0, markedIneligible=null, eligibilityStatusChangeDate=null, eligibilityStatusChangeSource=null)";
    private static final String loanIneligibleToString = "SilLoanApplication(applicationID=10000234, applicationSubmissionDate=null, applicationName=null, applicationLocation=null, competitionCode=null, competitionName=null, projectDuration=null, projectTotalCost=null, projectOtherFunding=null, markedIneligible=true, eligibilityStatusChangeDate=2021-10-12T09:38:12.850Z[UTC], eligibilityStatusChangeSource=IFS)";
    private static final String loanReinstateToString = "SilLoanApplication(applicationID=10000234, applicationSubmissionDate=null, applicationName=null, applicationLocation=null, competitionCode=null, competitionName=null, projectDuration=null, projectTotalCost=null, projectOtherFunding=null, markedIneligible=false, eligibilityStatusChangeDate=2021-10-12T09:38:12.850Z[UTC], eligibilityStatusChangeSource=IFS)";

    String loanSubmittedApplicationJson;
    String loanIneligibleApplicationJson;
    String loanReinstateApplicationJson;

    DateTimeFormatter datetimeFormat = LOANS_DATETIME_FORMAT.withZone(UTC);

    @Before
    public void setup() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File base = new File(classLoader.getResource("payloads/CRMPayload_LoanSubmitApplication.json").getFile());
        loanSubmittedApplicationJson =  new String(Files.readAllBytes(base.toPath()));


        base = new File(classLoader.getResource("payloads/CRMPayload_LoanIneligibleApplication.json").getFile());
        loanIneligibleApplicationJson = new String(Files.readAllBytes(base.toPath()));


        base = new File(classLoader.getResource("payloads/CRMPayload_LoanReinstateApplication.json").getFile());
        loanReinstateApplicationJson = new String(Files.readAllBytes(base.toPath()));

    }


    @Test
    public void shouldSerializeSubmittedApplicationAttributes() throws IOException {
        SilLoanApplication loanApplication = new SilLoanApplication();
        loanApplication.setApplicationID(10000234);
        loanApplication.setApplicationSubmissionDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z", datetimeFormat));
        loanApplication.setApplicationName("Sample skips for plastic storage");
        loanApplication.setApplicationLocation("RG1 4AF");
        loanApplication.setCompetitionCode("2109-13");
        loanApplication.setCompetitionName("Resilience Fund: Strand 2");
        loanApplication.setProjectDuration(10);
        loanApplication.setProjectTotalCost(10634d);
        loanApplication.setProjectOtherFunding(1200d);
        loanApplication.setMarkedIneligible(null);
        loanApplication.setEligibilityStatusChangeDate(null);
        loanApplication.setEligibilityStatusChangeDate(null);

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(loanApplication);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(loanSubmittedApplicationJson));
    }

    @Test
    public void shouldDeserializeSubmittedApplicationAttributes() throws IOException {

        SilLoanApplication out = new ObjectMapper().readValue(loanSubmittedApplicationJson, SilLoanApplication.class);
        assertThat(out.toString(), equalTo(loanSubmittedToString));

    }

    @Test
    public void shouldSerializeIneligibleApplicationAttributes() throws IOException {
        SilLoanApplication loanApplication = new SilLoanApplication();
        loanApplication.setApplicationID(10000234);
        loanApplication.setApplicationSubmissionDate(null);
        loanApplication.setApplicationName(null);
        loanApplication.setApplicationLocation(null);
        loanApplication.setCompetitionCode(null);
        loanApplication.setCompetitionName(null);
        loanApplication.setProjectDuration(null);
        loanApplication.setProjectTotalCost(null);
        loanApplication.setProjectOtherFunding(null);
        loanApplication.setMarkedIneligible(true);
        loanApplication.setEligibilityStatusChangeDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z", datetimeFormat));
        loanApplication.setEligibilityStatusChangeSource("IFS");

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(loanApplication);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(loanIneligibleApplicationJson));


    }

    @Test
    public void shouldDeserializeIneligibleApplicationAttributes() throws IOException {
        SilLoanApplication out = new ObjectMapper().readValue(loanIneligibleApplicationJson, SilLoanApplication.class);
        assertThat(out.toString(), equalTo(loanIneligibleToString));
    }


    @Test
    public void shouldSerializeReinstateApplicationAttributes() throws IOException {
        SilLoanApplication loanApplication = new SilLoanApplication();
        loanApplication.setApplicationID(10000234);
        loanApplication.setApplicationSubmissionDate(null);
        loanApplication.setApplicationName(null);
        loanApplication.setApplicationLocation(null);
        loanApplication.setCompetitionCode(null);
        loanApplication.setCompetitionName(null);
        loanApplication.setProjectDuration(null);
        loanApplication.setProjectTotalCost(null);
        loanApplication.setProjectOtherFunding(null);
        loanApplication.setMarkedIneligible(false);
        loanApplication.setEligibilityStatusChangeDate(ZonedDateTime.parse("2021-10-12T09:38:12.850Z", datetimeFormat));
        loanApplication.setEligibilityStatusChangeSource("IFS");

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(loanApplication);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(loanReinstateApplicationJson));


    }

    @Test
    public void shouldDeserializeReinstateApplicationAttributes() throws IOException {
        SilLoanApplication out = new ObjectMapper().readValue(loanReinstateApplicationJson, SilLoanApplication.class);
        assertThat(out.toString(), equalTo(loanReinstateToString));

    }

}
