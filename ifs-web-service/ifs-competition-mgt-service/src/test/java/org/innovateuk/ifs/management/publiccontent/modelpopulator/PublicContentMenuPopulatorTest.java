package org.innovateuk.ifs.management.publiccontent.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.publiccontent.modelpopulator.PublicContentMenuPopulator;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.management.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicContentMenuPopulatorTest {

    private static final Long COMPETITION_ID = 1L;

    private static final String WEB_BASE_URL = "https://environment";

    @InjectMocks
    private PublicContentMenuPopulator target;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testPopulate() {
        List<PublicContentSectionResource> sections = asList(PublicContentSectionType.values())
                        .stream()
                .map(type -> newPublicContentSectionResource().withType(type).build())
                .collect(Collectors.toList());
        ZonedDateTime date = ZonedDateTime.now();
        Boolean publishSetting = Boolean.FALSE;

        PublicContentResource publicContent = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date)
                .withInviteOnly(publishSetting)
                .build();

        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();

        when(publicContentService.getCompetitionById(COMPETITION_ID)).thenReturn(publicContent);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        PublicContentMenuViewModel viewModel = target.populate(competition, WEB_BASE_URL);

        assertThat(viewModel.getSections(), equalTo(sections));
        assertThat(viewModel.getCompetition(), equalTo(competition));
        assertThat(viewModel.getPublishDate(), equalTo(date));

        assertThat(viewModel.isInviteOnly(), equalTo(publishSetting));
        assertThat(viewModel.getCompetitionURL(), equalTo("https://environment/competition/" + COMPETITION_ID + "/overview"));
    }

}
