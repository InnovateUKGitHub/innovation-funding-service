package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.junit.Assert.assertEquals;

public class KtpTemplateTest {

    @Test
    public void assessorGuidanceOverriddenForKTP() {
        KtpTemplate template = new KtpTemplate();
        SectionBuilder sectionBuilder = aSection()
                .withAssessorGuidanceDescription("This should be overridden")
                .withType(SectionType.APPLICATION_QUESTIONS);

        List<SectionBuilder> sections = template.sections(newArrayList(sectionBuilder));

        assertEquals("", sections.get(0).getAssessorGuidanceDescription());
    }
}