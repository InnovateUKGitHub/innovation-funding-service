package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.resource.CompetitionCompositeId;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

public class PublicContentItemPermissionRulesTest extends BasePermissionRulesTest<PublicContentItemPermissionRules> {
    @Mock
    private PublicContentRepository contentRepository;

    @Override
    protected PublicContentItemPermissionRules supplyPermissionRulesUnderTest() {
        return new PublicContentItemPermissionRules();
    }

    @Test
    public void testCanViewAllPublishedContent() {
        when(contentRepository.findByCompetitionId(1L)).thenReturn(newPublicContent().withPublishDate(ZonedDateTime.now()).build());
        assertTrue(rules.allUsersCanViewPublishedContent(CompetitionCompositeId.id(1L), allGlobalRoleUsers.get(0)));
    }

    @Test
    public void testCannotViewUnpublishedContent() {
        when(contentRepository.findByCompetitionId(1L)).thenReturn(newPublicContent().build());
        assertFalse(rules.allUsersCanViewPublishedContent(CompetitionCompositeId.id(1L), allGlobalRoleUsers.get(0)));
    }
}
