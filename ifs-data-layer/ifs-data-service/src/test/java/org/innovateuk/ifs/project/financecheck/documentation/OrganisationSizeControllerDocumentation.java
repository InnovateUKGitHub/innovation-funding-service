package org.innovateuk.ifs.project.financecheck.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.controller.OrganisationSizeController;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.finance.transactional.OrganisationSizeService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.OrganisationSizeResourceBuilder.newOrganisationSizeResource;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationSizeControllerDocumentation extends BaseControllerMockMVCTest<OrganisationSizeController> {

    private static final String BASE_URL = "/organisation-size";

    @Mock
    private OrganisationSizeService organisationSizeService;

    @Test
    public void getOrganisationSizes() throws Exception {
        OrganisationSizeResource size = newOrganisationSizeResource().build();
        size.setId(1L);
        size.setDescription("Desc");

        when(organisationSizeService.getOrganisationSizes()).thenReturn(serviceSuccess(Collections.singletonList(size)));

        mockMvc.perform(get(BASE_URL).
                contentType(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("organisation-size/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("An array of all the organisation sizes in the system"),
                                fieldWithPath("[].id").description("Database id of the organisation size"),
                                fieldWithPath("[].description").description("Description id of the organisation size"))
                ));
    }


    @Override
    protected OrganisationSizeController supplyControllerUnderTest() {
        return new OrganisationSizeController();
    }
}
