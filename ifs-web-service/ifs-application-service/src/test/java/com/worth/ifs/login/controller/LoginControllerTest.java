package com.worth.ifs.login.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.login.LoginController;
import com.worth.ifs.token.resource.TokenResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        when(userService.sendPasswordResetNotification(eq(email))).thenReturn(RestResult.restSuccess());

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
        when(userService.sendPasswordResetNotification(eq(email))).thenReturn(RestResult.restSuccess());

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
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restSuccess());

        mockMvc.perform(get("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash))
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));
    }

    @Test
    public void testResetPasswordInvalidHash() throws Exception {

        String hash = UUID.randomUUID().toString();
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restFailure(CommonErrors.notFoundError(TokenResource.class, hash)));

        mockMvc.perform(get("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash))
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));
    }

    @Test
    public void testResetPasswordPost() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd12";
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restSuccess());
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(RestResult.restSuccess());

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
                        .param("retypedPassword", password)
        )
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.PASSWORD_CHANGED));

    }

    @Test
    public void testResetPasswordPostInvalidPassword() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd";
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restSuccess());
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(RestResult.restSuccess());

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
                        .param("retypedPassword", "something else")
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("resetPasswordForm", "retypedPassword"))
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));

    }

    @Test
    public void testResetPasswordPostShibErrors() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd123";
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restSuccess());
        List<Error> errors = new ArrayList<>();
        errors.add(new Error("INVALID_PASSWORD", HttpStatus.CONFLICT));
        when(userService.resetPassword(eq(hash), eq(password))).thenReturn(RestResult.restFailure(errors));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
                        .param("retypedPassword", password)
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("resetPasswordForm", "password", "registration.INVALID_PASSWORD"))
                .andExpect(view().name(LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD_FORM));

    }

    @Test
    public void testResetPasswordPostInvalidHash() throws Exception {

        String hash = UUID.randomUUID().toString();
        String password = "Passw0rd";
        when(userService.checkPasswordResetHash(eq(hash))).thenReturn(RestResult.restFailure(CommonErrors.notFoundError(TokenResource.class, hash)));

        mockMvc.perform(
                post("/" + LoginController.LOGIN_BASE + "/" + LoginController.RESET_PASSWORD + "/hash/" + hash)
                        .param("password", password)
                        .param("retypedPassword", password)
        )
                .andExpect(status().isAlreadyReported())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE));

    }
}