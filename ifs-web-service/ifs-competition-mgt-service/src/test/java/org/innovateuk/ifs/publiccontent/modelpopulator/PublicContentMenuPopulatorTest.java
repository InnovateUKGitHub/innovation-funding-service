package org.innovateuk.ifs.publiccontent.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublicContentMenuPopulatorTest {

    private static final Long COMPETITION_ID = 1L;

    @InjectMocks
    private PublicContentMenuPopulator target;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void testPopulate() {
        List<PublicContentSectionResource> sections = asList(PublicContentSectionType.values())
                        .stream()
                .map(type -> newPublicContentSectionResource().withType(type).build())
                .collect(Collectors.toList());
        ZonedDateTime date = ZonedDateTime.now();

        PublicContentResource publicContent = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date).build();

        CompetitionResource competition = newCompetitionResource().build();

        when(publicContentService.getCompetitionById(COMPETITION_ID)).thenReturn(publicContent);
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        PublicContentMenuViewModel viewModel = target.populate(COMPETITION_ID);

        assertThat(viewModel.getSections(), equalTo(sections));
        assertThat(viewModel.getCompetition(), equalTo(competition));
        assertThat(viewModel.getPublishDate(), equalTo(date));
    }
}
