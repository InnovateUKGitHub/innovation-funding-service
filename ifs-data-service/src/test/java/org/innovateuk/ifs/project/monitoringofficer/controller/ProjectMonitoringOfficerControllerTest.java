package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.transactional.SaveMonitoringOfficerResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

  public class ProjectMonitoringOfficerControllerTest extends BaseControllerMockMVCTest<ProjectMonitoringOfficerController> {

      private MonitoringOfficerResource monitoringOfficerResource;

      @Before
      public void setUp() {

          monitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                  .withId(null)
                  .withProject(1L)
                  .withFirstName("abc")
                  .withLastName("xyz")
                  .withEmail("abc.xyz@gmail.com")
                  .withPhoneNumber("078323455")
                  .build();
      }

      @Override
      protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
          return new ProjectMonitoringOfficerController();
      }

      @Test
      public void getMonitoringOfficer() throws Exception {

          MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();

          when(projectMonitoringOfficerServiceMock.getMonitoringOfficer(123L)).thenReturn(serviceSuccess(monitoringOfficer));

          mockMvc.perform(get("/project/{projectId}/monitoring-officer", 123L)).
                  andExpect(status().isOk()).
                  andExpect(content().json(toJson(monitoringOfficer)));
      }

      @Test
      public void saveMOWhenErrorWhistSaving() throws Exception {

          Long projectId = 1L;

          when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                  thenReturn(serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED)));


          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isBadRequest());

          verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

          // Ensure that notification is not sent when there is error whilst saving
          verify(projectMonitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMOWhenUnableToSendNotifications() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(projectMonitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isInternalServerError());

          verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(projectMonitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficer() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(projectMonitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceSuccess());

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isOk());

          verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(projectMonitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficerWithoutSendingNotifications() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          successResult.setMonitoringOfficerSaved(false);
          when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(projectMonitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceSuccess());

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isOk());

          verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(projectMonitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficerWithBindExceptions() throws Exception {

          Long projectId = 1L;

          MonitoringOfficerResource monitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                  .withId(null)
                  .withProject(projectId)
                  .withFirstName("")
                  .withLastName("")
                  .withEmail("abc")
                  .withPhoneNumber("hello")
                  .build();

          Error firstNameError = fieldError("firstName", "", "validation.standard.firstname.required", "");
          Error lastNameError = fieldError("lastName", "", "validation.standard.lastname.required", "");
          Error emailError = fieldError("email", "abc", "validation.standard.email.format", "", "", "^[^{}|]*$");
          Error phoneNumberError = fieldError("phoneNumber", "hello", "validation.standard.phonenumber.format", "", "", "([0-9\\ +-])+");
          Error phoneNumberLengthError = fieldError("phoneNumber", "hello", "validation.standard.phonenumber.length.min", "", "2147483647", "8");

          MvcResult result = mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isNotAcceptable())
                  .andReturn();

          RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
          assertEquals(5, response.getErrors().size());
          asList(firstNameError, lastNameError, emailError, phoneNumberError, phoneNumberLengthError).forEach(e -> {
              String fieldName = e.getFieldName();
              String errorKey = e.getErrorKey();
              List<Error> matchingErrors = simpleFilter(response.getErrors(), error ->
                      fieldName.equals(error.getFieldName()) && errorKey.equals(error.getErrorKey()) &&
                      e.getArguments().containsAll(error.getArguments()));
              assertEquals(1, matchingErrors.size());
          });

          verify(projectMonitoringOfficerServiceMock, never()).saveMonitoringOfficer(projectId, monitoringOfficerResource);
      }
  }
