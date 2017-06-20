package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.EligibilityViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertEquals;

/**
 * Testing {@link EligibilityViewModelPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class EligibilityViewModelPopulatorTest {

    @InjectMocks
    private EligibilityViewModelPopulator populator;

    private EligibilityViewModel viewModel;
    private PublicContentResource publicContentResource;
    private PublicContentSectionResource publicContentSectionResource;

    @Before
    public void setup() {
        viewModel = new EligibilityViewModel();

        publicContentSectionResource = newPublicContentSectionResource()
                .with(sectionResource -> {
                    sectionResource.setId(98125L);
                })
                .withPublicContent(1L)
                .build();
        publicContentResource = newPublicContentResource()
                .with(contentResource ->  {
                    contentResource.setId(89235L);
                })
                .withCompetitionId(5372L)
                .withSummary("Summary")
                .withFundingType(FundingType.GRANT)
                .withProjectSize("5M")
                .withContentSections(asList(publicContentSectionResource))
                .build();
    }

    @Test
    public void getType() {
        assertEquals(PublicContentSectionType.ELIGIBILITY, populator.getType());
    }
}
