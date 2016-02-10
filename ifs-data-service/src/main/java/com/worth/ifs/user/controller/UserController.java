package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/token/{token}")
    public RestResult<User> getUserByToken(@PathVariable("token") final String token) {
        return newRestHandler().perform(() -> userService.getUserByToken(token));
    }

    @RequestMapping("/email/{email}/password/{password}")
    public RestResult<User> getUserByEmailandPassword(@PathVariable("email") final String email, @PathVariable("password") final String password) {
        return newRestHandler().perform(() -> userService.getUserByEmailandPassword(email, password));
    }

    @RequestMapping("/id/{id}")
    public RestResult<User> getUserById(@PathVariable("id") final Long id) {
        return newRestHandler().perform(() -> userService.getUserById(id));
    }

    @RequestMapping("/name/{name}")
    public RestResult<List<User>> getUserByName(@PathVariable("name") final String name) {
        return newRestHandler().perform(() -> userService.getUserByName(name));
    }

    @RequestMapping("/findAll/")
    public RestResult<List<User>> findAll() {
        return newRestHandler().perform(() -> userService.findAll());
    }

    @RequestMapping("/findByEmail/{email}/")
    public RestResult<List<UserResource>> findByEmail(@PathVariable("email") final String email) {
        return newRestHandler().perform(() -> userService.findByEmail(email));
    }

    @RequestMapping("/findAssignableUsers/{applicationId}")
    public RestResult<Set<User>> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        return newRestHandler().perform(() -> userService.findAssignableUsers(applicationId));
    }

    @RequestMapping("/findRelatedUsers/{applicationId}")
    public RestResult<Set<User>> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        return newRestHandler().perform(() -> userService.findRelatedUsers(applicationId));
    }

    @RequestMapping("/createLeadApplicantForOrganisation/{organisationId}")
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {
        return newRestHandler().perform(() -> userService.createUser(organisationId, userResource));
    }

    @RequestMapping("/updateDetails")
    public RestResult<UserResource> createUser(@RequestBody UserResource userResource) {
        return newRestHandler().perform(() -> userService.updateUser(userResource));
    }
}
