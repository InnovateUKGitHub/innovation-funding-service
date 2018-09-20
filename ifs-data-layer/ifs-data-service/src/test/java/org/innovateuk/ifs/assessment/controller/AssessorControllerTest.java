package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.text.RandomStringGenerator;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.transactional.AssessorService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorControllerTest extends BaseControllerMockMVCTest<AssessorController> {

    @Mock
    private AssessorService assessorServiceMock;

    @Mock
    private CompetitionService competitionServiceMock;

    private RandomStringGenerator randomStringGeneratorAtoZ = new RandomStringGenerator.Builder()
            .withinRange('a', 'z').build();

    @Override
    protected AssessorController supplyControllerUnderTest() {
        return new AssessorController();
    }

    @Test
    public void registerAssessorByHash() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
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
    public void registerAssessorByHash_invalidAddress() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withPassword("Passw0rd123")
                .withAddress(new AddressResource())
                .build();


        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceSuccess());

        MvcResult result = mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        Error addressLine1Error = fieldError("address.addressLine1", null, "validation.standard.addressline1.required", "");
        Error addressTownError = fieldError("address.town", null, "validation.standard.town.required", "");
        Error addressPostcodeError = fieldError("address.postcode", null, "validation.standard.postcode.required", "");

        verifyResponseErrors(result, addressLine1Error, addressTownError, addressPostcodeError);

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_emptyFields() throws Exception {
        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .build();

        MvcResult result = mockMvc.perform(post("/assessor/register/{hash}", "testhash")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        Error firstNameError = fieldError("firstName", null, "validation.standard.firstname.required", "");
        Error lastNameError = fieldError("lastName", null, "validation.standard.lastname.required", "");
        Error phoneNumberError = fieldError("phoneNumber", null, "validation.standard.phonenumber.required", "");
        Error passwordError = fieldError("password", null, "validation.standard.password.required", "");
        Error addressError = fieldError("address", null, "validation.standard.address.required", "");

        verifyResponseErrors(result, firstNameError, lastNameError, phoneNumberError, passwordError, addressError);

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_firstNameTooShort() throws Exception {
        String firstName = "A";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName(firstName)
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
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
        String firstName = randomStringGeneratorAtoZ.generate(71);

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName(firstName)
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
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
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName(lastName)
                .withPhoneNumber("01234 56789890")
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
        String lastName = randomStringGeneratorAtoZ.generate(71);

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName(lastName)
                .withPhoneNumber("01234 56789890")
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
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
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
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.format", "", "", "^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_phoneNumberTooShort() throws Exception {

        String phoneNumber = "1234567-+";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
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
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.format", "", "", "^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_phoneNumberTooLong() throws Exception {

        String phoneNumber = "123456789012345678901-+";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber(phoneNumber)
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
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("phoneNumber", phoneNumber, "validation.standard.phonenumber.format", "", "", "^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_passwordTooShort() throws Exception {
        String password = randomStringGeneratorAtoZ.generate(7);

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
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
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("password", password, "validation.standard.password.length.min", "", "8", "2147483647")))));

        verify(assessorServiceMock, never()).registerAssessorByHash(isA(String.class), isA(UserRegistrationResource.class));
    }

    @Test
    public void registerAssessorByHash_returnsErrorOnFailure() throws Exception {
        String hash = "testhash";

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 56789890")
                .withPassword("Passw0rd123")
                .withAddress(newAddressResource()
                        .withAddressLine1("Electric Works")
                        .withTown("Sheffield")
                        .withPostcode("S1 2BJ")
                        .build())
                .build();

        Error notFoundError = notFoundError(AssessmentInvite.class, hash);

        when(assessorServiceMock.registerAssessorByHash(hash, userRegistrationResource)).thenReturn(serviceFailure(notFoundError));

        mockMvc.perform(post("/assessor/register/{hash}", hash)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationResource)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(toJson(new RestErrorResponse(notFoundError))))
                .andReturn();

        verify(assessorServiceMock, only()).registerAssessorByHash(hash, userRegistrationResource);
    }

    @Test
    public void getAssessorProfile() throws Exception {
        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(newUserResource()
                        .withFirstName("Test")
                        .withLastName("Tester")
                        .build()
                )
                .build();

        when(assessorServiceMock.getAssessorProfile(1L)).thenReturn(serviceSuccess(assessorProfileResource));

        mockMvc.perform(get("/assessor/profile/{id}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessorProfileResource)))
                .andReturn();

        verify(assessorServiceMock, only()).getAssessorProfile(1L);
    }

    @Test
    public void getAssessorProfile_notFound() throws Exception {
        Error notFoundError = notFoundError(User.class, 1L);
        when(assessorServiceMock.getAssessorProfile(1L)).thenReturn(serviceFailure(notFoundError));

        mockMvc.perform(get("/assessor/profile/{id}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(toJson(new RestErrorResponse(notFoundError))))
                .andReturn();

        verify(assessorServiceMock, only()).getAssessorProfile(1L);
    }

    @Test
    public void notifyAssessors() throws Exception {
        long competitionId = 1L;

        when(competitionServiceMock.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessor/notify-assessors/competition/{id}", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).notifyAssessors(competitionId);
        verify(assessorServiceMock).notifyAssessorsByCompetition(competitionId);
    }

    private void verifyResponseErrors(MvcResult mvcResult, Error... expectedErrors) throws UnsupportedEncodingException {
        RestErrorResponse response = fromJson(mvcResult.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEquals(expectedErrors.length, response.getErrors().size());
        asList(expectedErrors).forEach(e -> {
            String fieldName = e.getFieldName();
            String errorKey = e.getErrorKey();
            List<Error> matchingErrors = simpleFilter(response.getErrors(), error ->
                    fieldName.equals(error.getFieldName()) && errorKey.equals(error.getErrorKey()) &&
                            e.getArguments().containsAll(error.getArguments()));
            assertEquals(format("response contains error with fieldName=%s, and errorKey=%s", fieldName, errorKey), 1, matchingErrors.size());
        });
    }
}