package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.BuilderAmendFunctions;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Gender.NOT_STATED;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorControllerTest extends BaseControllerMockMVCTest<AssessorController> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Test
    public void registerAssessorByHash() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();


        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isOk());

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userRegistrationResource);
    }

    // TODO address validation tests

    @Test
    public void registerAssessorByHash_emptyFields() throws Exception {
        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .build();

        MvcResult result = mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        Error titleError = fieldError("title", null, "validation.standard.title.selectionrequired", "");
        Error firstNameError = fieldError("firstName", null, "validation.standard.firstname.required", "");
        Error lastNameError = fieldError("lastName", null, "validation.standard.lastname.required", "");
        Error phoneNumberError = fieldError("phoneNumber", null, "validation.standard.phonenumber.required", "");
        Error genderError = fieldError("gender", null, "validation.standard.gender.selectionrequired", "");
        Error disabilityError = fieldError("disability", null, "validation.standard.disability.selectionrequired", "");
        Error ethnicityError = fieldError("ethnicity", null, "validation.standard.ethnicity.selectionrequired", "");
        Error passwordError = fieldError("password", null, "validation.standard.password.required", "");
        Error addressError = fieldError("address", null, "validation.standard.address.required", "");

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEquals(9, response.getErrors().size());
        asList(titleError, firstNameError, lastNameError, phoneNumberError, genderError, disabilityError, ethnicityError, passwordError, addressError).forEach(e -> {
            String fieldName = e.getFieldName();
            String errorKey = e.getErrorKey();
            List<Error> matchingErrors = simpleFilter(response.getErrors(), error ->
                    fieldName.equals(error.getFieldName()) && errorKey.equals(error.getErrorKey()) &&
                            e.getArguments().containsAll(error.getArguments()));
            assertEquals(1, matchingErrors.size());
        });

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_invalidTitleFormat() throws Exception {
        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Bad")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("title", "Bad", "validation.standard.title.format", "", "", "^(Mr|Miss|Mrs|Ms|Dr)$")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_firstNameTooShort() throws Exception {
        String firstName = "A";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName(firstName)
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("firstName", firstName, "validation.standard.firstname.length.min", "", "2", "2147483647")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_firstNameTooLong() throws Exception {
        String firstName = RandomStringUtils.random(71, "abcdefghijklmnopqrstuvwxyz");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName(firstName)
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("firstName", firstName, "validation.standard.firstname.length.max", "", "70", "0")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_lastNameTooShort() throws Exception {
        String lastName = "A";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName(lastName)
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("lastName", lastName, "validation.standard.lastname.length.min", "", "2", "2147483647")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_lastNameTooLong() throws Exception {
        String lastName = RandomStringUtils.random(71, "abcdefghijklmnopqrstuvwxyz");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName(lastName)
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("lastName", lastName, "validation.standard.lastname.length.max", "", "70", "0")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_invalidPhoneNumberFormat() throws Exception {
        String phoneNumber = "01234 567890 ext.123";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.format", "", "", "([0-9\\ +-])+")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_phoneNumberTooShort() throws Exception {
        String phoneNumber = RandomStringUtils.random(7, "01234567890 +-");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.length.min", "", "8", "2147483647")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_phoneNumberTooLong() throws Exception {
        String phoneNumber = RandomStringUtils.random(21, "01234567890 +-");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.length.max", "", "20", "0")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_passwordTooShort() throws Exception {
        String password = RandomStringUtils.random(9, "abcdefghijklmnopqrstuvwxyz");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword(password)
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("password", password, "validation.standard.password.length.min", "", "10", "2147483647")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_passwordTooLong() throws Exception {
        String password = RandomStringUtils.random(31, "abcdefghijklmnopqrstuvwxyz");

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword(password)
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("password", password, "validation.standard.password.length.max", "", "30", "0")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_returnsErrorOnFailure() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withGender(NOT_STATED)
                .withDisability(NO)
                .withEthnicity(newEthnicityResource().with(BuilderAmendFunctions.id(1L)).build())
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        Error notFoundError = notFoundError(CompetitionInvite.class, hash);

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceFailure(notFoundError));

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(toJson(new RestErrorResponse(notFoundError))))
                .andReturn();

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userRegistrationResource);
    }
}