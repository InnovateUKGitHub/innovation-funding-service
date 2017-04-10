package org.innovateuk.ifs.publiccontent.formpopulator;

import org.innovateuk.ifs.competition.publiccontent.resource.*;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.*;

/**
 * Tests for abstract form populators in public content.
 * {@link AbstractPublicContentFormPopulator} {@link AbstractContentGroupFormPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractPublicContentFormPopulatorTest {

    private static final PublicContentSectionType TEST_TYPE = PublicContentSectionType.ELIGIBILITY;

    @InjectMocks
    private AbstractContentGroupFormPopulator target = new AbstractContentGroupFormPopulator() {
        @Override
        protected AbstractContentGroupForm createInitial() {
            return new AbstractContentGroupForm() {
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
        ContentGroupResource contentGroup = newContentGroupResource()
                .withHeading("Heading")
                .withContent("Content")
                .build();
        PublicContentSectionResource contentSection = newPublicContentSectionResource().withType(TEST_TYPE)
                .withStatus(PublicContentStatus.COMPLETE)
                .withContentGroups(asList(contentGroup)).build();
        PublicContentResource contentResource = newPublicContentResource()
                .withCompetitionId(competitionId)
                .withPublishDate(ZonedDateTime.now())
                .withContentSections(asList(contentSection)).build();


        AbstractContentGroupForm form = (AbstractContentGroupForm) target.populate(contentResource);

        assertThat(form.getContentGroups().size(), equalTo(1));
        assertThat(form.getContentGroups().get(0).getHeading(), equalTo(contentGroup.getHeading()));
        assertThat(form.getContentGroups().get(0).getContent(), equalTo(contentGroup.getContent()));
        assertThat(form.getContentGroups().get(0).getId(), equalTo(contentGroup.getId()));
    }
}
