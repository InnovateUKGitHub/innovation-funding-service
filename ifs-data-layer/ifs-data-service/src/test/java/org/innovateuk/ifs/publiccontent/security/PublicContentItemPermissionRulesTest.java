package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.ASSESSMENT_ONLY;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PublicContentItemPermissionRulesTest extends BasePermissionRulesTest<PublicContentItemPermissionRules> {
    @Mock
    private PublicContentRepository contentRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Override
    protected PublicContentItemPermissionRules supplyPermissionRulesUnderTest() {
        return new PublicContentItemPermissionRules();
    }

    @Test
    public void canViewAllPublishedContent() {
        Competition competition = newCompetition().build();

        when(contentRepository.findByCompetitionId(competition.getId())).thenReturn(newPublicContent().withPublishDate(ZonedDateTime.now()).build());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        assertTrue(rules.allUsersCanViewPublishedContent(CompetitionCompositeId.id(competition.getId()), allGlobalRoleUsers.get(0)));
    }

    @Test
    public void canViewUnpublishedContentWhenCompetitionIsAssessmentOnly() {

        Competition competition = newCompetition()
                .withCompetitionType(newCompetitionType()
                        .withName(ASSESSMENT_ONLY.getText())
                        .build())
                .build();

        when(contentRepository.findByCompetitionId(competition.getId())).thenReturn(newPublicContent().build());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        assertTrue(rules.allUsersCanViewPublishedContent(CompetitionCompositeId.id(competition.getId()), allGlobalRoleUsers.get(0)));
    }

    @Test
    public void cannotViewUnpublishedContent() {
        Competition competition = newCompetition().build();

        when(contentRepository.findByCompetitionId(competition.getId())).thenReturn(newPublicContent().build());
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        assertFalse(rules.allUsersCanViewPublishedContent(CompetitionCompositeId.id(competition.getId()), allGlobalRoleUsers.get(0)));
    }
}
