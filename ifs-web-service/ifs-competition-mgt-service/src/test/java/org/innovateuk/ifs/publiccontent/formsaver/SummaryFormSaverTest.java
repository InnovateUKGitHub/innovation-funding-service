package org.innovateuk.ifs.publiccontent.formsaver;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.section.SummaryForm;
import org.innovateuk.ifs.publiccontent.saver.section.SummaryFormSaver;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SummaryFormSaverTest {

    private static final String DESCRIPTION = "SUMMARY";
    private static final FundingType FUNDING_TYPE = FundingType.GRANT;
    private static final String PROJECT_SIZE = "PROJECT_SIZE";

    @InjectMocks
    private SummaryFormSaver target;

    @Mock
    private PublicContentService publicContentService;


    @Test
    public void testSave() {
        SummaryForm form = new SummaryForm();
        form.setDescription(DESCRIPTION);
        form.setFundingType(FUNDING_TYPE.getDisplayName());
        form.setProjectSize(PROJECT_SIZE);
        form.setContentGroups(new ArrayList<>());

        PublicContentResource resource = newPublicContentResource().
                withContentSections(
                        newPublicContentSectionResource().withType(PublicContentSectionType.SUMMARY).build(1)
                ).build();

        target.save(form, resource);

        assertThat(resource.getSummary(), equalTo(DESCRIPTION));
        assertThat(resource.getFundingType(), equalTo(FUNDING_TYPE));
        assertThat(resource.getProjectSize(), equalTo(PROJECT_SIZE));

        verify(publicContentService).updateSection(resource, PublicContentSectionType.SUMMARY);

    }

    @Test
    public void testSaveWithNoContentGroups() {
        SummaryForm form = new SummaryForm();
        form.setDescription(DESCRIPTION);
        form.setFundingType(FUNDING_TYPE.getDisplayName());
        form.setProjectSize(PROJECT_SIZE);

        PublicContentResource resource = newPublicContentResource().
                withContentSections(
                        newPublicContentSectionResource()
                                .withType(PublicContentSectionType.SUMMARY)
                                .build(1)
                ).build();

        target.save(form, resource);

        assertThat(resource.getSummary(), equalTo(DESCRIPTION));
        assertThat(resource.getFundingType(), equalTo(FUNDING_TYPE));
        assertThat(resource.getProjectSize(), equalTo(PROJECT_SIZE));

        verify(publicContentService).updateSection(resource, PublicContentSectionType.SUMMARY);

    }
}
