package org.innovateuk.ifs.publiccontent.formpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.SummaryForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SummaryFormPopulatorTest {

    private static final String DESCRIPTION = "SUMMARY";
    private static final FundingType FUNDING_TYPE = FundingType.GRANT;
    private static final String PROJECT_SIZE = "PROJECT_SIZE";

    @InjectMocks
    private SummaryFormPopulator target;

    @Test
    public void testPopulate() {
        PublicContentResource resource = newPublicContentResource()
                .withSummary(DESCRIPTION)
                .withFundingType(FUNDING_TYPE)
                .withProjectSize(PROJECT_SIZE)
                .build();

        SummaryForm form = target.populate(resource);

        assertThat(form.getDescription(), equalTo(DESCRIPTION));
        assertThat(form.getFundingType(), equalTo(FUNDING_TYPE.getDisplayName()));
        assertThat(form.getProjectSize(), equalTo(PROJECT_SIZE));

    }

    @Test
    public void testPopulateWithNullResourceValues() {
        PublicContentResource resource = newPublicContentResource()
                .build();

        SummaryForm form = target.populate(resource);

        assertThat(form.getDescription(), equalTo(null));
        assertThat(form.getFundingType(), equalTo(null));
        assertThat(form.getProjectSize(), equalTo(null));

    }

}
