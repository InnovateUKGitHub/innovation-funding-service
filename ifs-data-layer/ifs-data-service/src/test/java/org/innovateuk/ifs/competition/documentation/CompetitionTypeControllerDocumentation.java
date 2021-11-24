package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.controller.CompetitionTypeController;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.transactional.CompetitionTypeService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionTypeControllerDocumentation extends BaseControllerMockMVCTest<CompetitionTypeController> {
    @Mock
    CompetitionTypeService competitionTypeService;

    @Override
    protected CompetitionTypeController supplyControllerUnderTest() {
        return new CompetitionTypeController();
    }

    @Test
    public void findAll() throws Exception {
        when(competitionTypeService.findAllTypes()).thenReturn(ServiceResult.serviceSuccess(asList(new CompetitionTypeResource())));

        mockMvc.perform(get("/competition-type/find-all")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
