package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.form.domain.Section;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.ValidationMessages.noErrors;
import static org.innovateuk.ifs.commons.error.ValidationMessages.reject;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionStatusControllerTest extends BaseControllerMockMVCTest<SectionStatusController> {

    @Mock
    private SectionStatusService sectionStatusServiceMock;

    @Mock
    private BindingResult bindingResult;

    @Override
    protected SectionStatusController supplyControllerUnderTest() {
        return new SectionStatusController();
    }

    @Test
    public void getCompletedSectionsTest() throws Exception {
        // Initial data setup is not necessary, but gives an indication of how the data model should be.
        long organisationId = 1L;
        List<Section> completedSections = newSection().build(2);
        List<Section> incompleteSections = newSection().build(2);
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(completedSections);
        allSections.addAll(incompleteSections);
        Set<Long> completedSectionIds = completedSections.stream().map(s -> s.getId()).collect(toSet());
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionStatusServiceMock.getCompletedSections(application.getId(), organisationId))
                .thenReturn(serviceSuccess(completedSectionIds));

        mockMvc.perform(get("/section-status/get-completed-sections/" + application.getId() + "/" + organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(completedSectionIds)));
    }

    @Test
    public void markSectionAsComplete() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(noErrors()));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/section-status/mark-as-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(noErrors())))
                .andDo(
                        document(
                                "section/mark-as-complete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("sectionId").description("id of section to mark as complete"),
                                        parameterWithName("applicationId").description("id of the application to mark the section as complete on"),
                                        parameterWithName("processRoleId").description("id of ProcessRole of the current user, (for user specific sections, finance sections)")
                                )
                        )
                );
    }

    @Test
    public void markSectionAsInComplete() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsInComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/section-status/mark-as-in-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "section/mark-as-incomplete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("sectionId").description("id of section to mark as incomplete"),
                                        parameterWithName("applicationId").description("id of the application to mark the section as incomplete on"),
                                        parameterWithName("processRoleId").description("id of ProcessRole of the current user, (for user specific sections, finance sections)")
                                )
                        )
                );
    }

    @Test
    public void markSectionAsNotRequired() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionStatusServiceMock.markSectionAsNotRequired(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/section-status/mark-as-not-required/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "section/mark-as-not-required",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("sectionId").description("id of section to mark as not required"),
                                        parameterWithName("applicationId").description("id of the application to mark the section as not required"),
                                        parameterWithName("processRoleId").description("id of ProcessRole of the current user, (for user specific sections, finance sections)")
                                )
                        )
                );
    }

    @Test
    public void markSectionAsCompleteInvalid() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        bindingResult = new BeanPropertyBindingResult(section, "costItem");
        reject(bindingResult, "validation.finance.min.row");
        ValidationMessages messages = new ValidationMessages(1L, bindingResult);
        when(sectionStatusServiceMock.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(messages));


        mockMvc.perform(RestDocumentationRequestBuilders.post("/section-status/mark-as-complete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(messages)))
                .andDo(
                        document(
                                "section/mark-as-complete-invalid",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("sectionId").description("id of section to mark as complete"),
                                        parameterWithName("applicationId").description("id of the application to mark the section as complete on"),
                                        parameterWithName("processRoleId").description("id of ProcessRole of the current user, (for user specific sections, finance sections)")
                                ),
                                responseFields(
                                        fieldWithPath("objectName").description("name of the object type"),
                                        fieldWithPath("objectId").description("identifier of the object, for example the FinanceRow.id"),
                                        fieldWithPath("errors").description("list of Error objects, containing the validation messages"),
                                        fieldWithPath("errors[0].fieldName").description("the name of the field that is invalid"),
                                        fieldWithPath("errors[0].fieldRejectedValue").description("the value of the field that is invalid"),
                                        fieldWithPath("errors[0].errorKey").description("the key to identity the type of validation message"),
                                        fieldWithPath("errors[0].arguments").description("array of arguments used to validate this object")
                                )
                        )
                );
    }

}
