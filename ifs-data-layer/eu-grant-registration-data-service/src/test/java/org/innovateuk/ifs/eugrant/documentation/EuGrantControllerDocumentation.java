package org.innovateuk.ifs.eugrant.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.eugrant.controller.EuGrantController;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.innovateuk.ifs.eugrant.builder.EuContactResourceBuilder.newEuContactResource;
import static org.innovateuk.ifs.eugrant.builder.EuFundingResourceBuilder.newEuFundingResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.innovateuk.ifs.eugrant.builder.EuOrganisationResourceBuilder.newEuOrganisationResource;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EuGrantControllerDocumentation extends BaseControllerMockMVCTest<EuGrantController> {

    @Mock
    private EuGrantService euGrantService;

    @Override
    public EuGrantController supplyControllerUnderTest() {
        return new EuGrantController();
    }

    private EuOrganisationResource euOrganisationResource;
    private EuContactResource euContactResource;
    private EuFundingResource euFundingResource;
    private EuActionTypeResource euActionTypeResource;
    private EuGrantResource euGrantResource;
    private UUID uuid;

    @Before
    public void setUp() throws Exception {
        euOrganisationResource = newEuOrganisationResource()
                .withName("worth")
                .withOrganisationType(EuOrganisationType.BUSINESS)
                .withCompaniesHouseNumber("1234")
                .build();

        euContactResource = newEuContactResource()
                .withName("Worth")
                .withEmail("Worth@gmail.com")
                .withJobTitle("worth employee")
                .withTelephone("0123456789")
                .build();

        euActionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .withName("Action Type")
                .withDescription("Description")
                .withPriority(1)
                .build();

        euFundingResource = newEuFundingResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.valueOf(100000L))
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now().plusYears(1L))
                .withProjectName("Project Name")
                .withParticipantId("123456789")
                .build();

        euGrantResource = newEuGrantResource()
                .withContact(euContactResource)
                .withOrganisation(euOrganisationResource)
                .withFunding(euFundingResource)
                .withShortCode("1234")
                .build();

        uuid = UUID.randomUUID();

    }

    @Test
    public void create() throws Exception {
        when(euGrantService.create()).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                post("/eu-grant")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(euGrantResource))
                )
                .andExpect(status().isCreated())
                .andDo(document(
                        "eu-grant/{method-name}",
                        requestFields(fields()),
                        responseFields(fields())
                        )
                );
    }

    @Test
    public void findById() throws Exception {
        when(euGrantService.findById(uuid)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                get("/eu-grant/{uuid}", uuid.toString()))
                .andExpect(status().isOk())
                .andDo(document(
                        "eu-grant/{method-name}",
                        pathParameters(
                                parameterWithName("uuid").description("Id the grant registration")
                        ),
                        responseFields(fields())
                    )
                );
    }

    @Test
    public void update() throws Exception {
        when(euGrantService.update(uuid, euGrantResource)).thenReturn(serviceSuccess());

        mockMvc.perform(
                put("/eu-grant/{uuid}", uuid.toString())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(euGrantResource)))
                .andExpect(status().isOk())
                .andDo(document(
                        "eu-grant/{method-name}",
                        requestFields(fields()),
                        pathParameters(
                                parameterWithName("uuid").description("Id the grant registration to save")
                        ))
                );
    }

    @Test
    public void submit() throws Exception {
        when(euGrantService.submit(uuid)).thenReturn(serviceSuccess(euGrantResource));

        mockMvc.perform(
                post("/eu-grant/{uuid}/submit", uuid.toString()))
                .andExpect(status().isOk())
                .andDo(document(
                        "eu-grant/{method-name}",
                        pathParameters(
                                parameterWithName("uuid").description("Id the grant registration to save")
                        ),
                        responseFields(fields())
                ));
    }

    private FieldDescriptor[] fields() {
        return new FieldDescriptor[] {
                fieldWithPath("id").description("Unique id for the eu grant."),
                fieldWithPath("organisation").description("Organisation details for the eu grant."),
                fieldWithPath("organisation.name").description("Name of the organisation."),
                fieldWithPath("organisation.organisationType").description("The type of the the organisation e.g. BUSINESS."),
                fieldWithPath("organisation.companiesHouseNumber").description("Companies House number."),
                fieldWithPath("contact").description("Contact details for the eu grant."),
                fieldWithPath("contact.name").description("Full name of the contact."),
                fieldWithPath("contact.jobTitle").description("Job title of the contact."),
                fieldWithPath("contact.email").description("Email address of the contact."),
                fieldWithPath("contact.telephone").description("Telephone number of the contact."),
                fieldWithPath("funding").description("Contact details for the eu grant."),
                fieldWithPath("funding.grantAgreementNumber").description("This might also be referred to as your project ID."),
                fieldWithPath("funding.participantId").description("The 9-digit number unique to your organisation."),
                fieldWithPath("funding.projectName").description("The name of your project."),
                fieldWithPath("funding.projectStartDate").description("The date the project will start."),
                fieldWithPath("funding.projectEndDate").description("The date the project will end."),
                fieldWithPath("funding.fundingContribution").description("The total amount in euros granted to your organisation."),
                fieldWithPath("funding.projectCoordinator").description("Is your organisation the consortium lead on this project."),
                fieldWithPath("funding.actionType").description("The funding scheme you applied for."),
                fieldWithPath("funding.actionType.id").description("The id of the funding scheme you applied for."),
                fieldWithPath("funding.actionType.name").description("The name of the funding scheme you applied for."),
                fieldWithPath("funding.actionType.description").description("The description of the funding scheme you applied for."),
                fieldWithPath("funding.actionType.priority").description("The priority of the funding scheme you applied for."),
                fieldWithPath("organisationComplete").description("Status of whether the user has completed their organisation details."),
                fieldWithPath("contactComplete").description("Status of whether the user has completed their contact details."),
                fieldWithPath("fundingComplete").description("Status of whether the user has completed their funding."),
                fieldWithPath("shortCode").description("Short reference number for the grant registration.")
        };
    }
}
