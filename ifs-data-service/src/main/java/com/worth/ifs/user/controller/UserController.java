package com.worth.ifs.user.controller;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.resource.ResourceError;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.RegistrationService;
import com.worth.ifs.user.transactional.UserProfileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link User} related data.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository repository;
    @Autowired
    ProcessRoleRepository processRoleRepository;
    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserProfileService userProfileService;

    private final Log log = LogFactory.getLog(getClass());

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleException(Exception e) {
        log.error(e.getStackTrace());
        return "return error object instead";
    }

    @RequestMapping("/uid/{uid}")
     public User getUserByUid(@PathVariable("uid") final String uid) {
        return repository.findOneByUid(uid);
    }

    @RequestMapping("/id/{id}")
    public User getUserById(@PathVariable("id") final Long id) {
        return repository.findOne(id);
    }

    @RequestMapping("/name/{name}")
    public List<User> getUserByName(@PathVariable("name") final String name) {
        return repository.findByName(name);
    }

    @RequestMapping("/findAll/")
    public List<User> findAll() {
        return repository.findAll();
    }

    @RequestMapping("/findByEmail/{email}/")
    public List<UserResource> findByEmail(@PathVariable("email") final String email) {
        List<User> users = repository.findByEmail(email);

        return users.stream().map(UserResource::new).collect(Collectors.toList());
    }

    @RequestMapping("/findAssignableUsers/{applicationId}")
    public Set<User> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        return roles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    @RequestMapping("/findRelatedUsers/{applicationId}")
    public Set<User> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        return roles.stream()
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
    }

    @RequestMapping("/createLeadApplicantForOrganisation/{organisationId}")
    public ResourceEnvelope<UserResource> updateUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {

        ServiceResult<User> createUserResult = registrationService.createUserLeadApplicantForOrganisation(organisationId, userResource);

        return createUserResult.mapLeftOrRight(
            failure -> {

                // TODO DW - INFUND-1267 - correctly map service errors to correct controller errors

                ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new UserResource());
                addDuplicateEmailError(resourceEnvelope);
                return resourceEnvelope;
            },
            successfullyCreatedUser ->
                new ResourceEnvelope<>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), new UserResource(successfullyCreatedUser))
        );
    }

    private void addDuplicateEmailError(ResourceEnvelope<UserResource> resourceEnvelope) {
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.ERROR.getName());
        resourceEnvelope.addError(new ResourceError("email", "This email address is already in use"));
    }

    @RequestMapping("/updateDetails")
    public ResourceEnvelope<UserResource> updateUser(@RequestBody UserResource userResource) {

        ServiceResult<User> updateResult = userProfileService.updateProfile(userResource);

        return updateResult.mapLeftOrRight(
            failure -> {

                // TODO DW - INFUND-1267 - correctly map service errors to correct controller errors

                ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new UserResource());
                addUserDoesNotExistError(resourceEnvelope);
                return resourceEnvelope;
            },
            success -> {
                ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new UserResource());
                addUserResource(resourceEnvelope, new UserResource(success));
                return resourceEnvelope;
            }
        );
    }

    private void addUserResource(ResourceEnvelope<UserResource> resourceEnvelope, UserResource userResource) {
        resourceEnvelope.setEntity(userResource);
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.OK.getName());
    }

    private void addUserDoesNotExistError(ResourceEnvelope<UserResource> resourceEnvelope) {
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.ERROR.getName());
        resourceEnvelope.addError(new ResourceError("email", "User with given email address does not exist!"));
    }
}
