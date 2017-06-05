package org.innovateuk.ifs.publiccontent.formpopulator.section;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.form.section.SearchInformationForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchInformationFormPopulatorTest {
    
    private static final String FUNDING_RANGE = "FUNDING_RANGE";
    private static final String ELIGIBILITY_SUMMARY = "SUMMARY";
    private static final String SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
    private static final List<String> KEYWORDS = asList("keyword1", "keyword2");

    @InjectMocks
    private SearchInformationFormPopulator target;

    @Test
    public void testPopulate() {
        PublicContentResource resource = newPublicContentResource()
                .withProjectFundingRange(FUNDING_RANGE)
                .withEligibilitySummary(ELIGIBILITY_SUMMARY)
                .withKeywords(KEYWORDS)
                .withShortDescription(SHORT_DESCRIPTION).
                withInviteOnly(Boolean.TRUE).build();

        SearchInformationForm form = target.populate(resource);

        assertThat(form.getEligibilitySummary(), equalTo(ELIGIBILITY_SUMMARY));
        assertThat(form.getProjectFundingRange(), equalTo(FUNDING_RANGE));
        assertThat(form.getShortDescription(), equalTo(SHORT_DESCRIPTION));
        KEYWORDS.forEach(keyword -> assertThat(form.getKeywords(), containsString(keyword)));
        assertThat(form.getPublishSetting(), equalTo("invite"));
    }
}
