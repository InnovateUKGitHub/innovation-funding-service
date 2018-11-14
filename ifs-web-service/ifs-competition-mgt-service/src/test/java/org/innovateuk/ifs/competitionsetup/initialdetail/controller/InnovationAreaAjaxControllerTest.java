package org.innovateuk.ifs.competitionsetup.initialdetail.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competitionsetup.CompetitionSetupController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Class for testing public functions of {@link CompetitionSetupController}
 */
@RunWith(MockitoJUnitRunner.class)
public class InnovationAreaAjaxControllerTest extends BaseControllerMockMVCTest<InnovationAreaAjaxController> {

    private static final String URL_PREFIX = "/competition/setup";

    @Mock
    private CategoryRestService categoryRestService;

    @Override
    protected InnovationAreaAjaxController supplyControllerUnderTest() {
        return new InnovationAreaAjaxController();
    }

    @Test
    public void getInnovationAreas() throws Exception {
        Long innovationAreaId = 5L;
        String innovationAreaName = "Innovation Area 1";
        long innovationSectorId = 7L;
        List<InnovationAreaResource> innovationAreas = newInnovationAreaResource()
                .withId(innovationAreaId)
                .withName(innovationAreaName)
                .build(1);

        when(categoryRestService.getInnovationAreasBySector(innovationSectorId))
                .thenReturn(restSuccess(innovationAreas));

        mockMvc.perform(get(URL_PREFIX + "/get-innovation-areas/" + innovationSectorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(5)))
                .andExpect(jsonPath("[0]name", is(innovationAreaName)))
                .andExpect(jsonPath("[0]type", is(INNOVATION_AREA.toString())));

        Mockito.verify(categoryRestService).getInnovationAreasBySector(innovationSectorId);
        Mockito.verifyNoMoreInteractions(categoryRestService);
    }
}