package org.innovateuk.ifs.management.publiccontent.modelpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.publiccontent.service.PublicContentService;
import org.innovateuk.ifs.management.publiccontent.viewmodel.PublicContentMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicContentMenuPopulatorTest {

    private static final Long COMPETITION_ID1 = 1L;
    private static final Long COMPETITION_ID2 = 2L;

    private static final String WEB_BASE_URL = "https://environment";

    @InjectMocks
    private PublicContentMenuPopulator target;

    @Mock
    private PublicContentService publicContentService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populatePublicAndPrivateCompetitionsWithNoHash() {
        List<PublicContentSectionResource> sections =
                simpleMap(PublicContentSectionType.values(),
                        type -> newPublicContentSectionResource().withType(type).build());

        ZonedDateTime date = ZonedDateTime.now();

        PublicContentResource publicContentWithNoHash = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date)
                .withInviteOnly(false)
                .build();

        PublicContentResource privateContentWithNoHash = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date)
                .withInviteOnly(true)
                .build();

        CompetitionResource publicCompetition = newCompetitionResource().withId(COMPETITION_ID1).build();
        CompetitionResource privateCompetition = newCompetitionResource().withId(COMPETITION_ID2).build();

        when(publicContentService.getCompetitionById(COMPETITION_ID1)).thenReturn(publicContentWithNoHash);
        when(publicContentService.getCompetitionById(COMPETITION_ID2)).thenReturn(privateContentWithNoHash);

        when(competitionRestService.getCompetitionById(COMPETITION_ID1)).thenReturn(restSuccess(publicCompetition));
        when(competitionRestService.getCompetitionById(COMPETITION_ID2)).thenReturn(restSuccess(privateCompetition));

        PublicContentMenuViewModel viewModel1 = target.populate(publicCompetition, WEB_BASE_URL);
        PublicContentMenuViewModel viewModel2 = target.populate(privateCompetition, WEB_BASE_URL);

        assertThat(viewModel1.getSections(), equalTo(sections));
        assertThat(viewModel1.getCompetition(), equalTo(publicCompetition));
        assertThat(viewModel1.getPublishDate(), equalTo(date));

        assertThat(viewModel1.isInviteOnly(), equalTo(false));
        assertThat(viewModel1.getCompetitionURL(),
                equalTo("https://environment/competition/" + COMPETITION_ID1 + "/overview"));

        assertThat(viewModel2.getSections(), equalTo(sections));
        assertThat(viewModel2.getCompetition(), equalTo(privateCompetition));
        assertThat(viewModel2.getPublishDate(), equalTo(date));

        assertThat(viewModel2.isInviteOnly(), equalTo(true));
        assertThat(viewModel2.getCompetitionURL(),
                equalTo("https://environment/competition/" + COMPETITION_ID2 + "/overview"));
    }

    @Test
    public void populatePublicAndPrivateCompetitionsWithHash() {
        List<PublicContentSectionResource> sections =
                simpleMap(PublicContentSectionType.values(),
                        type -> newPublicContentSectionResource().withType(type).build());

        ZonedDateTime date = ZonedDateTime.now();

        PublicContentResource publicContentWithHash = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date)
                .withInviteOnly(false)
                .withHash("hash")
                .build();

        PublicContentResource privateContentWithHash = newPublicContentResource()
                .withContentSections(sections)
                .withPublishDate(date)
                .withInviteOnly(true)
                .withHash("hash")
                .build();

        CompetitionResource competition1 = newCompetitionResource().withId(COMPETITION_ID1).build();
        CompetitionResource competition2 = newCompetitionResource().withId(COMPETITION_ID2).build();

        when(publicContentService.getCompetitionById(COMPETITION_ID1)).thenReturn(publicContentWithHash);
        when(publicContentService.getCompetitionById(COMPETITION_ID2)).thenReturn(privateContentWithHash);

        when(competitionRestService.getCompetitionById(COMPETITION_ID1)).thenReturn(restSuccess(competition1));
        when(competitionRestService.getCompetitionById(COMPETITION_ID2)).thenReturn(restSuccess(competition2));

        PublicContentMenuViewModel viewModel1 = target.populate(competition1, WEB_BASE_URL);
        PublicContentMenuViewModel viewModel2 = target.populate(competition2, WEB_BASE_URL);

        assertThat(viewModel1.getSections(), equalTo(sections));
        assertThat(viewModel1.getCompetition(), equalTo(competition1));
        assertThat(viewModel1.getPublishDate(), equalTo(date));

        assertThat(viewModel1.isInviteOnly(), equalTo(false));
        assertThat(viewModel1.getHash(), not(emptyOrNullString()));
        assertThat(viewModel1.getCompetitionURL(),
                equalTo("https://environment/competition/" + COMPETITION_ID1 + "/overview/" + publicContentWithHash.getHash()));

        assertThat(viewModel2.getSections(), equalTo(sections));
        assertThat(viewModel2.getCompetition(), equalTo(competition2));
        assertThat(viewModel2.getPublishDate(), equalTo(date));

        assertThat(viewModel2.isInviteOnly(), equalTo(true));
        assertThat(viewModel2.getHash(), not(emptyOrNullString()));
        assertThat(viewModel2.getCompetitionURL(),
                equalTo("https://environment/competition/" + COMPETITION_ID2 + "/overview/" + privateContentWithHash.getHash()));
    }
}