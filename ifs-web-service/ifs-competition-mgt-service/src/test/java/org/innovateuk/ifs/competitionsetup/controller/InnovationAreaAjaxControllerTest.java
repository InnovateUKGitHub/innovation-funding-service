package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
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
        Long innovationSectorId = 1L;
        InnovationAreaResource category = newInnovationAreaResource()
                .withId(1L)
                .withName("Innovation Area 1")
                .build();

        when(categoryRestService.getInnovationAreasBySector(innovationSectorId)).thenReturn(restSuccess(singletonList(category)));

        mockMvc.perform(get(URL_PREFIX + "/getInnovationArea/" + innovationSectorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[0]name", is("Innovation Area 1")))
                .andExpect(jsonPath("[0]type", is(INNOVATION_AREA.toString())));

    }
}
