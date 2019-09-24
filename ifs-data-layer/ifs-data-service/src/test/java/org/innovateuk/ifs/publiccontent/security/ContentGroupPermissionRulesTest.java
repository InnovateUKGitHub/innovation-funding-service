package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupCompositeId;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.publiccontent.builder.ContentGroupBuilder.newContentGroup;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ContentGroupPermissionRulesTest extends BasePermissionRulesTest<ContentGroupPermissionRules> {

    @Mock
    private ContentGroupRepository contentGroupRepository;

    @Override
    protected ContentGroupPermissionRules supplyPermissionRulesUnderTest() {
        return new ContentGroupPermissionRules();
    }

    @Test
    public void testInternalUsersCanViewAllContentGroupFiles(){
        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewAllContentGroupFiles(ContentGroupCompositeId.id(1L), user));
            } else {
                assertFalse(rules.internalUsersCanViewAllContentGroupFiles(ContentGroupCompositeId.id(1L), user));
            }
        });
    }

    @Test
    public void testExternalUsersCanViewPublishedContentGroupFiles(){
        ContentGroupCompositeId unpublishedContentGroupId = ContentGroupCompositeId.id(1L);
        when(contentGroupRepository.findById(unpublishedContentGroupId.id())).thenReturn(
                Optional.of(newContentGroup().withContentSection(newContentSection()
                        .withPublicContent(newPublicContent().build()).build()).build()));


        ContentGroupCompositeId publishedContentGroupId = ContentGroupCompositeId.id(2L);
        when(contentGroupRepository.findById(publishedContentGroupId.id())).thenReturn(
                Optional.of(newContentGroup().withContentSection(newContentSection()
                        .withPublicContent(newPublicContent().withPublishDate(ZonedDateTime.now()).build()).build()).build()));

        assertFalse(rules.externalUsersCanViewPublishedContentGroupFiles(unpublishedContentGroupId, systemRegistrationUser()));
        assertTrue(rules.externalUsersCanViewPublishedContentGroupFiles(publishedContentGroupId, systemRegistrationUser()));



    }
}
