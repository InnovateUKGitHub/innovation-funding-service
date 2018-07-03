package org.innovateuk.ifs.publiccontent.formsaver;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;
import org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver;
import org.innovateuk.ifs.publiccontent.service.PublicContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for abstract form savers in public content.
 * {@link org.innovateuk.ifs.publiccontent.saver.AbstractPublicContentFormSaver} {@link org.innovateuk.ifs.publiccontent.saver.AbstractContentGroupFormSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractPublicContentFormSaverTest {

    private static final PublicContentSectionType TEST_TYPE = PublicContentSectionType.ELIGIBILITY;

    @Mock
    private PublicContentService publicContentService;

    @InjectMocks
    private AbstractContentGroupFormSaver target = new AbstractContentGroupFormSaver() {
        @Override
        protected PublicContentSectionType getType() {
            return TEST_TYPE;
        }
    };

    @Test
    public void testSave() {
        PublicContentSectionResource contentSection = newPublicContentSectionResource().withType(TEST_TYPE).build();
        PublicContentResource contentResource = newPublicContentResource()
                .withContentSections(asList(contentSection)).build();

        AbstractContentGroupForm form = newFormWithGroup();

        when(publicContentService.updateSection(contentResource, TEST_TYPE)).thenReturn(serviceSuccess());

        target.save(form, contentResource).getSuccess();

        assertThat(contentSection.getContentGroups().get(0).getContent(), equalTo(form.getContentGroups().get(0).getContent()));
        assertThat(contentSection.getContentGroups().get(0).getHeading(), equalTo(form.getContentGroups().get(0).getHeading()));
        assertThat(contentSection.getContentGroups().get(0).getId(), equalTo(form.getContentGroups().get(0).getId()));
        assertThat(contentSection.getContentGroups().get(0).getPriority(), equalTo(0));
        verify(publicContentService).updateSection(contentResource, TEST_TYPE);
    }


    @Test
    public void testMarkAsComplete() {
        PublicContentSectionResource contentSection = newPublicContentSectionResource().withType(TEST_TYPE).build();
        PublicContentResource contentResource = newPublicContentResource()
                .withContentSections(asList(contentSection)).build();

        AbstractContentGroupForm form = newFormWithGroup();

        when(publicContentService.markSectionAsComplete(contentResource, TEST_TYPE)).thenReturn(serviceSuccess());

        target.markAsComplete(form, contentResource).getSuccess();

        assertThat(contentSection.getContentGroups().get(0).getContent(), equalTo(form.getContentGroups().get(0).getContent()));
        assertThat(contentSection.getContentGroups().get(0).getHeading(), equalTo(form.getContentGroups().get(0).getHeading()));
        assertThat(contentSection.getContentGroups().get(0).getId(), equalTo(form.getContentGroups().get(0).getId()));
        assertThat(contentSection.getContentGroups().get(0).getPriority(), equalTo(0));
        verify(publicContentService).markSectionAsComplete(contentResource, TEST_TYPE);
    }

    @Test
    public void testEmptyContentGroupsValidation() {
        PublicContentSectionResource contentSection = newPublicContentSectionResource().withType(TEST_TYPE).build();
        PublicContentResource contentResource = newPublicContentResource()
                .withContentSections(asList(contentSection)).build();

        AbstractContentGroupForm form = newForm();

        ServiceResult<Void> result = target.save(form, contentResource);

        assertThat(result.isSuccess(), equalTo(false));
        assertThat(result.getErrors().size(), equalTo(1));
        assertThat(result.getErrors().get(0).getFieldName(), equalTo("contentGroups"));
        verifyZeroInteractions(publicContentService);

    }

    private AbstractContentGroupForm newForm() {
        AbstractContentGroupForm form = new AbstractContentGroupForm() {
        };
        form.setContentGroups(Collections.emptyList());
        return form;
    }

    private AbstractContentGroupForm newFormWithGroup() {
        AbstractContentGroupForm form = newForm();
        ContentGroupForm contentGroupForm = new ContentGroupForm();
        contentGroupForm.setContent("Content");
        contentGroupForm.setHeading("Heading");
        contentGroupForm.setId(1L);
        form.setContentGroups(asList(contentGroupForm));
        return form;
    }

}


