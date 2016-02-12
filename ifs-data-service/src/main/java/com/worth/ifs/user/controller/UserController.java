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
        return userService.getUserByToken(token).toDefaultRestResultForGet();
    }

    @RequestMapping("/email/{email}/password/{password}")
    public RestResult<User> getUserByEmailandPassword(@PathVariable("email") final String email, @PathVariable("password") final String password) {
        return userService.getUserByEmailandPassword(email, password).toDefaultRestResultForGet();
    }

    @RequestMapping("/id/{id}")
    public RestResult<User> getUserById(@PathVariable("id") final Long id) {
        return userService.getUserById(id).toDefaultRestResultForGet();
    }

    @RequestMapping("/name/{name}")
    public RestResult<List<User>> getUserByName(@PathVariable("name") final String name) {
        return userService.getUserByName(name).toDefaultRestResultForGet();
    }

    @RequestMapping("/findAll/")
    public RestResult<List<User>> findAll() {
        return userService.findAll().toDefaultRestResultForGet();
    }

    @RequestMapping("/findByEmail/{email}/")
    public RestResult<List<UserResource>> findByEmail(@PathVariable("email") final String email) {
        return userService.findByEmail(email).toDefaultRestResultForGet();
    }

    @RequestMapping("/findAssignableUsers/{applicationId}")
    public RestResult<Set<User>> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findAssignableUsers(applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/findRelatedUsers/{applicationId}")
    public RestResult<Set<User>> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        return userService.findRelatedUsers(applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/createLeadApplicantForOrganisation/{organisationId}")
    public RestResult<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {
        return userService.createUser(organisationId, userResource).toDefaultRestResultForPostCreate();
    }

    @RequestMapping("/updateDetails")
    public RestResult<UserResource> createUser(@RequestBody UserResource userResource) {
        return userService.updateUser(userResource).toDefaultRestResultForPutWithBody();
    }
}
