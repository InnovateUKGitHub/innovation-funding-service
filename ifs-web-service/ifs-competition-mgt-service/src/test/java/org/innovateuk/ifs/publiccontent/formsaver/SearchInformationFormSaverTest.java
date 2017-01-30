package org.innovateuk.ifs.publiccontent.formsaver;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.SearchInformationForm;
import org.innovateuk.ifs.publiccontent.saver.SearchInformationFormSaver;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SearchInformationFormSaverTest {

    private static final Long COMPETITION_ID = 1L;
    private static final String FUNDING_RANGE = "FUNDING_RANGE";
    private static final String ELIGIBILITY_SUMMARY = "SUMMARY";
    private static final String SHORT_DESCRIPTION = "SHORT_DESCRIPTION";
    private static final List<String> KEYWORDS = asList("keyword 1", "keyword 2");

    @InjectMocks
    private SearchInformationFormSaver target;

    @Mock
    private PublicContentService publicContentService;

    @Test
    public void testSave() {
        SearchInformationForm form = new SearchInformationForm();
        //check we normalise whitespace in the keywords
        form.setKeywords("  keyword   1    , keyword  2     ");
        form.setProjectFundingRange(FUNDING_RANGE);
        form.setEligibilitySummary(ELIGIBILITY_SUMMARY);
        form.setShortDescription(SHORT_DESCRIPTION);

        PublicContentResource resource = newPublicContentResource().build();

        ServiceResult<Void> result = target.save(form, resource);

        assertThat(resource.getEligibilitySummary(), equalTo(ELIGIBILITY_SUMMARY));
        assertThat(resource.getProjectFundingRange(), equalTo(FUNDING_RANGE));
        assertThat(resource.getShortDescription(), equalTo(SHORT_DESCRIPTION));
        KEYWORDS.forEach(keyword -> assertTrue(resource.getKeywords().contains(keyword)));

        verify(publicContentService).updateSection(resource, PublicContentSectionType.SEARCH);

    }
}
