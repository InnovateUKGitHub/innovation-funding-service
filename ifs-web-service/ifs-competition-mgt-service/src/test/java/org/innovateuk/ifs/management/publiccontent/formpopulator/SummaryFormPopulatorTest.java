package org.innovateuk.ifs.management.publiccontent.formpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.management.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.management.publiccontent.formpopulator.section.SummaryFormPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.Silent.class)
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
                .withProjectSize(PROJECT_SIZE)
                .withContentSections(newPublicContentSectionResource().withType(PublicContentSectionType.SUMMARY).withContentGroups(Collections.emptyList()).build(1))
                .build();

        SummaryForm form = target.populate(resource);

        assertThat(form.getDescription(), equalTo(DESCRIPTION));
        assertThat(form.getProjectSize(), equalTo(PROJECT_SIZE));

    }

    @Test
    public void testPopulateWithNullResourceValues() {
        PublicContentResource resource = newPublicContentResource()
                .withContentSections(newPublicContentSectionResource().withType(PublicContentSectionType.SUMMARY).withContentGroups(Collections.emptyList()).build(1))
                .build();

        SummaryForm form = target.populate(resource);

        assertThat(form.getDescription(), equalTo(null));
        assertThat(form.getProjectSize(), equalTo(null));

    }

}
