package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.SaveMonitoringOfficerResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

  public class LegacyMonitoringOfficerControllerTest extends BaseControllerMockMVCTest<LegacyMonitoringOfficerController> {

      private LegacyMonitoringOfficerResource monitoringOfficerResource;

      @Mock
      private LegacyMonitoringOfficerService monitoringOfficerServiceMock;

      @Before
      public void setUp() {

          monitoringOfficerResource = LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource()
                  .withId(null)
                  .withProject(1L)
                  .withFirstName("abc")
                  .withLastName("xyz")
                  .withEmail("abc.xyz@gmail.com")
                  .withPhoneNumber("078323455")
                  .build();
      }

      @Override
      protected LegacyMonitoringOfficerController supplyControllerUnderTest() {
          return new LegacyMonitoringOfficerController();
      }

      @Test
      public void getMonitoringOfficer() throws Exception {

          LegacyMonitoringOfficerResource monitoringOfficer = newLegacyMonitoringOfficerResource().build();

          when(monitoringOfficerServiceMock.getMonitoringOfficer(123L)).thenReturn(serviceSuccess(monitoringOfficer));

          mockMvc.perform(get("/project/{projectId}/monitoring-officer", 123L)).
                  andExpect(status().isOk()).
                  andExpect(content().json(toJson(monitoringOfficer)));
      }

      @Test
      public void saveMOWhenErrorWhistSaving() throws Exception {

          Long projectId = 1L;

          when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                  thenReturn(serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED)));


          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isBadRequest());

          verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

          // Ensure that notification is not sent when there is error whilst saving
          verify(monitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMOWhenUnableToSendNotifications() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(monitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isInternalServerError());

          verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(monitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficer() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(monitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceSuccess());

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isOk());

          verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(monitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficerWithoutSendingNotifications() throws Exception {

          Long projectId = 1L;

          SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
          successResult.setMonitoringOfficerSaved(false);
          when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
          when(monitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                  thenReturn(serviceSuccess());

          mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isOk());

          verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
          verify(monitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

      }

      @Test
      public void saveMonitoringOfficerWithBindExceptions() throws Exception {

          Long projectId = 1L;

          LegacyMonitoringOfficerResource monitoringOfficerResource = LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource()
                  .withId(null)
                  .withProject(projectId)
                  .withFirstName("")
                  .withLastName("")
                  .withEmail("abc")
                  .withPhoneNumber("hello")
                  .build();

          Error firstNameError = fieldError("firstName", "", "validation.standard.firstname.required", "");
          Error lastNameError = fieldError("lastName", "", "validation.standard.lastname.required", "");
          Error emailError = fieldError("email", "abc", "validation.standard.email.format", "", "", "^$|^[a-zA-Z0-9._%+-^[^{}|]*$]+@[a-zA-Z0-9.-^[^{}|]*$]+\\.[a-zA-Z^[^0-9{}|]*$]{2,}$");
          Error phoneNumberError = fieldError("phoneNumber", "hello", "validation.standard.phonenumber.format", "", "", "^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$");

          MvcResult result = mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(monitoringOfficerResource)))
                  .andExpect(status().isNotAcceptable())
                  .andReturn();

          RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
          assertEquals(6, response.getErrors().size());
          asList(firstNameError, lastNameError, emailError, phoneNumberError).forEach(e -> {
              String fieldName = e.getFieldName();
              String errorKey = e.getErrorKey();
              List<Error> matchingErrors = simpleFilter(response.getErrors(), error ->
                      fieldName.equals(error.getFieldName()) && errorKey.equals(error.getErrorKey()) &&
                      e.getArguments().containsAll(error.getArguments()));
              assertEquals(1, matchingErrors.size());
          });

          verify(monitoringOfficerServiceMock, never()).saveMonitoringOfficer(projectId, monitoringOfficerResource);
      }
  }
