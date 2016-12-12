package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.transactional.SectionService;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static com.worth.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static com.worth.ifs.commons.rest.ValidationMessages.reject;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SectionControllerTest extends BaseControllerMockMVCTest<SectionController> {

    @Mock
    protected SectionService sectionService;
    @Mock
    private BindingResult bindingResult;

    @Override
    protected SectionController supplyControllerUnderTest() {
        return new SectionController();
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
        Competition competition = newCompetition().withSections(allSections).build();
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionService.getCompletedSections(application.getId(), organisationId))
                .thenReturn(serviceSuccess(completedSectionIds));

        mockMvc.perform(post("/section/getCompletedSections/" + application.getId() + "/" + organisationId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(completedSectionIds)))
                .andExpect(status().isOk());
    }

    @Test
    public void getIncompleteSectionsTest() throws Exception {
        // Initial data setup is not necessary, but gives an indication of how the data model should be.
        List<Section> completedSections = newSection().build(2);
        List<Section> incompleteSections = newSection().build(2);
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(completedSections);
        allSections.addAll(incompleteSections);
        List<Long> incompleteSectionIds = incompleteSections.stream().map(s -> s.getId()).collect(toList());
        Competition competition = newCompetition().withSections(allSections).build();
        Application application = newApplication().withCompetition(newCompetition().withSections(allSections).build()).build();
        when(sectionService.getIncompleteSections(application.getId()))
                .thenReturn(serviceSuccess(incompleteSectionIds));

        mockMvc.perform(post("/section/getIncompleteSections/" + application.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(incompleteSectionIds)))
                .andExpect(status().isOk());
    }

    @Test
    public void getNextSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        SectionResource nextSection = newSectionResource().build();
        when(sectionService.getNextSection(section.getId())).thenReturn(serviceSuccess(nextSection));

        mockMvc.perform(get("/section/getNextSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(nextSection)));
    }

    @Test
    public void getPreviousSectionTest() throws Exception {
        Section section = newSection().withCompetitionAndPriority(newCompetition().build(), 1).build();
        SectionResource previousSection = newSectionResource().build();
        when(sectionService.getPreviousSection(section.getId())).thenReturn(serviceSuccess(previousSection));

        mockMvc.perform(get("/section/getPreviousSection/" + section.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(previousSection)));
    }

    @Test
    public void markSectionAsComplete() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        when(sectionService.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(new ArrayList<>()));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/section/markAsComplete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
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

        when(sectionService.markSectionAsInComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/section/markAsInComplete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "section/mark-as-incomplete",
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
    public void markSectionAsCompleteInvalid() throws Exception {
        Section section = newSection().withId(7L).withCompetitionAndPriority(newCompetition().build(), 1).build();
        Long processRoleId = 1L;
        Long applicationId = 1L;

        ArrayList<ValidationMessages> validationMessages = new ArrayList<>();
        bindingResult = new BeanPropertyBindingResult(section, "costItem");
        reject(bindingResult, "validation.finance.min.row");
        ValidationMessages messages = new ValidationMessages(1L, bindingResult);
        validationMessages.add(messages);
        when(sectionService.markSectionAsComplete(section.getId(), applicationId, processRoleId)).thenReturn(serviceSuccess(validationMessages));


        mockMvc.perform(RestDocumentationRequestBuilders.post("/section/markAsComplete/{sectionId}/{applicationId}/{processRoleId}", section.getId(), applicationId, processRoleId))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"objectName\":\"costItem\",\"objectId\":1,\"errors\":[{\"errorKey\":\"validation.finance.min.row\",\"fieldName\":null,\"fieldRejectedValue\":null,\"arguments\":[]}]}]"))
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
                                    fieldWithPath("[0].objectName").description("name of the object type"),
                                    fieldWithPath("[0].objectId").description("identifier of the object, for example the FinanceRow.id"),
                                    fieldWithPath("[0].errors").description("list of Error objects, containing the validation messages"),
                                    fieldWithPath("[0].errors[0].fieldName").description("the name of the field that is invalid"),
                                    fieldWithPath("[0].errors[0].errorKey").description("the key to identity the type of validation message"),
                                    fieldWithPath("[0].errors[0].arguments").description("array of arguments used to validate this object")
                            )
                        )
                );
    }
}