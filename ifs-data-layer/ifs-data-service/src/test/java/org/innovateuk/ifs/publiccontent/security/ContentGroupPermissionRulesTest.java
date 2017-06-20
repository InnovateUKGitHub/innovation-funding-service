package org.innovateuk.ifs.publiccontent.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.publiccontent.repository.ContentGroupRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.publiccontent.builder.ContentGroupBuilder.newContentGroup;
import static org.innovateuk.ifs.publiccontent.builder.ContentSectionBuilder.newContentSection;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentBuilder.newPublicContent;
import static org.junit.Assert.*;
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
                assertTrue(rules.internalUsersCanViewAllContentGroupFiles(1L, user));
            } else {
                assertFalse(rules.internalUsersCanViewAllContentGroupFiles(1L, user));
            }
        });
    }

    @Test
    public void testExternalUsersCanViewPublishedContentGroupFiles(){
        Long unpublishedContentGroupId = 1L;
        when(contentGroupRepository.findOne(unpublishedContentGroupId)).thenReturn(
                newContentGroup().withContentSection(newContentSection()
                        .withPublicContent(newPublicContent().build()).build()).build());


        Long publishedContentGroupId = 2L;
        when(contentGroupRepository.findOne(publishedContentGroupId)).thenReturn(
                newContentGroup().withContentSection(newContentSection()
                        .withPublicContent(newPublicContent().withPublishDate(ZonedDateTime.now()).build()).build()).build());


        assertFalse(rules.externalUsersCanViewPublishedContentGroupFiles(unpublishedContentGroupId, systemRegistrationUser()));
        assertTrue(rules.externalUsersCanViewPublishedContentGroupFiles(publishedContentGroupId, systemRegistrationUser()));



    }
}
