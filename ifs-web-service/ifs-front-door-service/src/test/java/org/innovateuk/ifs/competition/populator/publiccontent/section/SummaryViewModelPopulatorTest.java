package org.innovateuk.ifs.competition.populator.publiccontent.section;


import org.innovateuk.ifs.competition.publiccontent.resource.*;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.SummaryViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentSectionResourceBuilder.newPublicContentSectionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing {@link SummaryViewModelPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class SummaryViewModelPopulatorTest {

    @InjectMocks
    private SummaryViewModelPopulator populator;

    private SummaryViewModel viewModel;
    private PublicContentResource publicContentResource;
    private PublicContentSectionResource publicContentSectionResource;
    private List<ContentGroupResource> contentGroups;

    @Before
    public void setup() {
        viewModel = new SummaryViewModel();

        contentGroups = newContentGroupResource()
                .withId(93922L)
                .withFileEntry(newFileEntryResource().withId(34L).build())
                .withContent("Content and stuff like that")
                .withHeading("This is an Awesome heading").build(1);

        publicContentSectionResource = newPublicContentSectionResource()
                .with(sectionResource -> {
                    sectionResource.setId(98125L);
                })
                .withPublicContent(1L)
                .withContentGroups(contentGroups)
                .build();
        publicContentResource = newPublicContentResource()
                .with(contentResource ->  {
                    contentResource.setId(89235L);
                })
                .withSummary("Summary")
                .withFundingType(FundingType.GRANT)
                .withProjectSize("5M")
                .withContentSections(asList(publicContentSectionResource))
                .build();
    }

    @Test
    public void populateSection() {
        populator.populateSection(viewModel, publicContentResource, publicContentSectionResource, Boolean.FALSE);

        assertEquals("Summary", viewModel.getDescription());
        assertEquals(FundingType.GRANT.getDisplayName(), viewModel.getFundingType());
        assertEquals("5M", viewModel.getProjectSize());

        assertEquals(1, viewModel.getContentGroups().size());
        assertEquals(contentGroups.get(0).getId(), viewModel.getContentGroups().get(0).getId());
        assertTrue(viewModel.getFileEntries().containsKey(contentGroups.get(0).getId()));
    }

    @Test
    public void getType() {
        assertEquals(PublicContentSectionType.SUMMARY, populator.getType());
    }
}
