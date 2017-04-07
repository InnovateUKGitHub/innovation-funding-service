package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.publiccontent.resource.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.viewmodel.AbstractPublicContentGroupViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Test for all the abstract view model population in public content.
 * {@link AbstractPublicContentViewModelPopulator} and {@link AbstractPublicContentGroupViewModelPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractViewModelPopulatorTest {

    private static final PublicContentSectionType TEST_TYPE = PublicContentSectionType.ELIGIBILITY;

    @Mock
    private CompetitionService competitionService;

    @InjectMocks
    private AbstractPublicContentGroupViewModelPopulator target = new AbstractPublicContentGroupViewModelPopulator() {
        @Override
        protected AbstractPublicContentGroupViewModel createInitial() {
            return new AbstractPublicContentGroupViewModel() {
            };
        }

        @Override
        protected PublicContentSectionType getType() {
            return TEST_TYPE;
        }
    };

    @Test
    public void testPopulate() {
        long competitionId = 1L;
        FileEntryResource fileEntry = newFileEntryResource().build();
        ContentGroupResource contentGroup = newContentGroupResource()
                .withFileEntry(fileEntry).build();
        PublicContentSectionResource contentSection = newPublicContentSectionResource().withType(TEST_TYPE)
                .withStatus(PublicContentStatus.COMPLETE)
                .withContentGroups(asList(contentGroup)).build();
        PublicContentResource contentResource = newPublicContentResource()
                .withCompetitionId(competitionId)
                .withPublishDate(ZonedDateTime.now())
                .withContentSections(asList(contentSection)).build();
        boolean readOnly = true;
        CompetitionResource competitionResource = newCompetitionResource().build();
        when(competitionService.getById(competitionId)).thenReturn(competitionResource);


        AbstractPublicContentGroupViewModel model = (AbstractPublicContentGroupViewModel) target.populate(contentResource, readOnly);

        assertThat(model.getCompetition(), equalTo(competitionResource));
        assertThat(model.getSection(), equalTo(contentSection));
        assertThat(model.isReadOnly(), equalTo(readOnly));
        assertThat(model.isComplete(), equalTo(true));
        assertThat(model.isPublished(), equalTo(true));
        assertThat(model.getFileEntries().get(contentGroup.getId()), equalTo(fileEntry));
        assertThat(model.hasAttachment(contentGroup.getId()), equalTo(true));


    }
}
