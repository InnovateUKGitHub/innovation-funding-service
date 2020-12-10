package org.innovateuk.ifs.heukar.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.heukar.controller.HeukarPartnerOrganisationController;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.heukar.transactional.HeukarPartnerOrganisationService;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.heukar.documentation.HeukarPartnerOrganisationDocs.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HeukarPartnerOrganisationControllerDocumentation extends BaseControllerMockMVCTest<HeukarPartnerOrganisationController> {

    @Mock
    private HeukarPartnerOrganisationService heukarPartnerOrganisationService;

    @Override
    protected HeukarPartnerOrganisationController supplyControllerUnderTest() {
        return new HeukarPartnerOrganisationController();
    }

    @Test
    public void findByApplicationId() throws Exception {
        Long applicationId = 1L;
        List<HeukarPartnerOrganisationResource> resources = Arrays.asList(heukarParterOrganisationResourceBuilder.build());
        when(heukarPartnerOrganisationService.findByApplicationId(1L)).thenReturn(serviceSuccess(resources));

        mockMvc.perform(get("/heukar-partner-organisation/find-by-application-id/{id}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("heukar-partner-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Application Id for the partner organisations that are being requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("list of partner orgs"))
                                .andWithPrefix("[]", heukarPartnerOrganisationFields)
                                .andWithPrefix("[].organisationTypeResource.", organisationTypeFields))
                );
    }

    @Test
    public void getExistingPartnerById() throws Exception {
        Long id = 1L;
        HeukarPartnerOrganisationResource resource = heukarParterOrganisationResourceBuilder.build();
        when(heukarPartnerOrganisationService.findOne(1L)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/heukar-partner-organisation/{id}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("heukar-partner-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id for the existing partner resource being requested")
                        ),
                        responseFields(heukarPartnerOrganisationFields)
                                .andWithPrefix("organisationTypeResource.", organisationTypeFields))
                );
    }

    @Test
    public void addNewHeukarPartnerOrganisation() throws Exception {
        Long applicationId = 1L;
        Long organisationTypeId = 1L;
        when(heukarPartnerOrganisationService.addNewPartnerOrgToApplication(1L, 1L))
                .thenReturn(serviceSuccess(new HeukarPartnerOrganisation()));

        mockMvc.perform(post("/heukar-partner-organisation/add-new-org-type/{applicationId}/{organisationTypeId}",
                applicationId, organisationTypeId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("heukar-partner-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Application Id for the new partner org to be added to"),
                                parameterWithName("organisationTypeId").description("Type of organisation for the new partner org")
                        )
                ));
    }

    @Test
    public void updateHeukarPartnerOrganisation() throws Exception {
        Long id = 1L;
        when(heukarPartnerOrganisationService.updatePartnerOrganisation(1L, 1L))
                .thenReturn(serviceSuccess(new HeukarPartnerOrganisation()));

        mockMvc.perform(put("/heukar-partner-organisation/{id}/{organisationTypeId}", id, id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("heukar-partner-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id for the existing partner resource being requested"),
                                parameterWithName("organisationTypeId").description("Type of organisation for the new partner org")
                        )
                ));
    }

    @Test
    public void deleteHeukarOrganisationType() throws Exception {
        Long id = 1L;

        when(heukarPartnerOrganisationService.deletePartnerOrganisation(id)).thenReturn(serviceSuccess());
        mockMvc.perform(delete("/heukar-partner-organisation/{id}", id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("heukar-partner-organisation/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id for the existing partner resource being requested")
                        )
                ));
    }
}
