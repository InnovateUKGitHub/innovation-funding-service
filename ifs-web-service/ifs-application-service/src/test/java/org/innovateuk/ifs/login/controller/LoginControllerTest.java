package org.innovateuk.ifs.login.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.InvalidURLException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.exception.ErrorControllerAdvice;
import org.innovateuk.ifs.login.LoginController;
import org.innovateuk.ifs.token.resource.TokenResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginControllerTest extends BaseControllerMockMVCTest<LoginController> {

    @Override
    protected LoginController supplyControllerUnderTest() {
        return new LoginController();
    }


    @Before
    public void setUp() {
        super.setUp();
        setLoggedInUser(null);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testRequestPasswordReset() throws Exception {
        mockMvc.perform(get("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD));
    }

    @Test
    public void testRequestPasswordResetPost() throws Exception {

        setLoggedInUser(null);
        String email = "test@test.nl";
        when(userRestServiceMock.sendPasswordResetNotification(eq(email))).thenReturn(completedFuture(restSuccess()));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD)
                        .param("email", email)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_NOTIFICATION_SEND));

    }

    @Test
    public void testRequestPasswordResetPostInvalid() throws Exception {

        setLoggedInUser(null);
        String email = "testtest.nl";
        when(userRestServiceMock.sendPasswordResetNotification(eq(email))).thenReturn(completedFuture(restSuccess()));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD)
                        .param("email", email)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD));

    }

    @Test
    public void testResetPassword() throws Exception {
        String hash = UUID.randomUUID().toString();
        mockMvc.perform(get("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash))
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));
    }

    @Test
    public void testResetPasswordInvalidHash() throws Exception {
        String hash = UUID.randomUUID().toString();
        Error error = CommonErrors.notFoundError(TokenResource.class, hash);
        when(userService.checkPasswordResetHash(hash)).thenThrow(new InvalidURLException(error.getErrorKey(), error.getArguments()));

        mockMvc.perform(get("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash))
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));
    }



    @Test
    public void testResetPasswordPost() throws Exception {
        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd12";
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.PASSWORD_CHANGED));

    }

    @Test
    public void testResetPasswordPostShibErrors() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd123";
        when(userRestServiceMock.checkPasswordResetHash(eq(hash))).thenReturn(restSuccess());
        List<Error> errors = new ArrayList<>();
        errors.add(new Error("INVALID_PASSWORD", HttpStatus.CONFLICT));
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(ServiceResult.serviceFailure(errors));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("resetPasswordForm", "password", "registration.INVALID_PASSWORD"))
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));

    }

    @Test
    public void testResetPasswordPostPasswordTooShort() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "letm3In";
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));

    }

    @Test
    public void testResetPasswordPostInvalidHash() throws Exception {
        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd";
        Error error = CommonErrors.notFoundError(TokenResource.class, hash);
        when(userService.checkPasswordResetHash(eq(hash))).thenThrow(new InvalidURLException(error.getErrorKey(), error.getArguments()));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
        )
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));

    }
}
