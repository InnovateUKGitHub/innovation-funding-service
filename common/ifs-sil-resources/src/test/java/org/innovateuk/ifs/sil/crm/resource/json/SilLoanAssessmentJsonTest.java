package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessmentRow;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class SilLoanAssessmentJsonTest {

    private static final String loanAssessmentToString = "SilLoanAssessment(competitionID=12345, applications=[SilLoanAssessmentRow(applicationID=10000234, scoreAverage=76, scoreSpread=3, assessorNumber=5, assessorNotInScope=2, assessorRecommended=4, assessorNotRecommended=1), SilLoanAssessmentRow(applicationID=10000245, scoreAverage=57, scoreSpread=6, assessorNumber=5, assessorNotInScope=1, assessorRecommended=3, assessorNotRecommended=2)])";

    String loanAssessmentJson;

    @Before
    public void setup() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File base = new File(classLoader.getResource("payloads/CRMPayload_LoanCloseAssessment.json").getFile());
        loanAssessmentJson = new String(Files.readAllBytes(base.toPath()));
    }


    @Test
    public void shouldSerializeAssessmentAttributes() throws IOException {
        SilLoanAssessment loanAssessment = new SilLoanAssessment();
        loanAssessment.setCompetitionID(12345L);

        SilLoanAssessmentRow row1 = new SilLoanAssessmentRow();
        row1.setApplicationID(10000234);
        row1.setScoreAverage(BigDecimal.valueOf(76));
        row1.setScoreSpread(3);
        row1.setAssessorNumber(5);
        row1.setAssessorNotInScope(2);
        row1.setAssessorRecommended(4L);
        row1.setAssessorNotRecommended(1L);

        SilLoanAssessmentRow row2 = new SilLoanAssessmentRow();
        row2.setApplicationID(10000245);
        row2.setScoreAverage(BigDecimal.valueOf(57));
        row2.setScoreSpread(6);
        row2.setAssessorNumber(5);
        row2.setAssessorNotInScope(1);
        row2.setAssessorRecommended(3L);
        row2.setAssessorNotRecommended(2L);

        loanAssessment.setApplications(Stream.of(row1, row2).collect(Collectors.toList()));

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(loanAssessment);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(loanAssessmentJson));
    }

    @Test
    public void shouldDeserializeAssessmentAttributes() throws IOException {
        SilLoanAssessment out = new ObjectMapper().readValue(loanAssessmentJson, SilLoanAssessment.class);
        assertThat(out.toString(), equalTo(loanAssessmentToString));

    }

}
