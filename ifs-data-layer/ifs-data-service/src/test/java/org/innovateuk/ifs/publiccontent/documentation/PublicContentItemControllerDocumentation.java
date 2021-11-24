package org.innovateuk.ifs.publiccontent.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.publiccontent.controller.PublicContentItemController;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentItemService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicContentItemControllerDocumentation extends BaseControllerMockMVCTest<PublicContentItemController> {
    @Mock
    private PublicContentItemService publicContentItemService;

    @Override
    protected PublicContentItemController supplyControllerUnderTest() {
        return new PublicContentItemController();
    }

    @Test
    public void findFilteredItems() throws Exception {
        final PublicContentItemPageResource expected = new PublicContentItemPageResource();
        expected.setSize(0);
        expected.setNumber(1);
        expected.setSize(10);
        expected.setTotalElements(0);
        expected.setTotalPages(0);
        expected.setContent(newPublicContentItemResource().build(1));

        when(publicContentItemService.findFilteredItems(any(Optional.class), any(Optional.class), any(Optional.class), any(Integer.class)))
                .thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/public-content/items/find-by-filter?innovationAreaId=1&searchString=keyword&pageNumber=2&pageSize=20")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    @Test
    public void byCompetitionId() throws Exception {
        final Long id = 1L;
        final PublicContentItemResource expected = new PublicContentItemResource();
        expected.setCompetitionOpenDate(ZonedDateTime.now());
        expected.setCompetitionCloseDate(ZonedDateTime.now());
        expected.setCompetitionTitle("Random title");
        expected.setPublicContentResource(newPublicContentResource().build());

        when(publicContentItemService.byCompetitionId(id)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/public-content/items/by-competition-id/{id}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
