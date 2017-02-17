package org.innovateuk.ifs.competition.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.ContentGroupResource;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.AbstractPublicContentGroupViewModel;
import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.SummaryViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.publiccontent.builder.ContentGroupResourceBuilder.newContentGroupResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;

/**
 * Testing {@link SummaryViewModel} and the parts of {@link AbstractPublicContentGroupViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class SummaryViewModelTest {

    @InjectMocks
    private SummaryViewModel viewModel;

    private Map<Long, FileEntryResource> fileEntries;
    public List<ContentGroupResource> contentGroups;

    @Before
    public void setup() {
        viewModel = new SummaryViewModel();
    }

    @Test
    public void getContentGroupsOrderedTest() {
        contentGroups = newContentGroupResource().with((integer, contentGroupResource) -> {
            contentGroupResource.setId(Long.valueOf(integer + 1));
            contentGroupResource.setPriority(10 - integer);
        }).build(3);
        viewModel.setContentGroups(contentGroups);

        List<ContentGroupResource> resultOne = viewModel.getContentGroupsOrdered();
        assertEquals(Long.valueOf(3), resultOne.get(0).getId());
        assertEquals(Long.valueOf(2), resultOne.get(1).getId());
        assertEquals(Long.valueOf(1), resultOne.get(2).getId());

        contentGroups = newContentGroupResource().with((integer, contentGroupResource) -> {
            contentGroupResource.setId(Long.valueOf(integer + 1));
            contentGroupResource.setPriority(integer);
        }).build(3);
        viewModel.setContentGroups(contentGroups);

        List<ContentGroupResource> resultTwo = viewModel.getContentGroupsOrdered();
        assertEquals(Long.valueOf(1), resultTwo.get(0).getId());
        assertEquals(Long.valueOf(2), resultTwo.get(1).getId());
        assertEquals(Long.valueOf(3), resultTwo.get(2).getId());
    }

    @Test
    public void hasAttachmentTest() {
        final Long contentGroupId = 5231L;

        assertEquals(FALSE, viewModel.hasAttachment(contentGroupId));

        viewModel.setFileEntries(asMap(1234L, newFileEntryResource().withId(123L).build()));

        assertEquals(FALSE, viewModel.hasAttachment(contentGroupId));

        viewModel.setFileEntries(asMap(contentGroupId, newFileEntryResource().withId(null).build()));

        assertEquals(FALSE, viewModel.hasAttachment(contentGroupId));

        viewModel.setFileEntries(asMap(contentGroupId, newFileEntryResource().withId(123L).build()));

        assertEquals(TRUE, viewModel.hasAttachment(contentGroupId));
    }

    @Test
    public void idTest() {
        final Long contentGroupId = 5231L;
        final Long fileEntryId = 924921L;

        assertEquals(null, viewModel.id(contentGroupId));

        viewModel.setFileEntries(asMap(contentGroupId, newFileEntryResource().withId(fileEntryId).build()));

        assertEquals(fileEntryId, viewModel.id(contentGroupId));
    }

    @Test
    public void fileNameTest() {
        final Long contentGroupId = 5231L;
        final String fileName = "someFilename.pdf";

        assertEquals(null, viewModel.id(contentGroupId));

        viewModel.setFileEntries(asMap(contentGroupId, newFileEntryResource().withName(fileName).build()));

        assertEquals(fileName, viewModel.fileName(contentGroupId));
    }
}
