package com.worth.ifs.user.controller;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.resource.ResourceError;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
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
    public ResourceEnvelope<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @RequestBody UserResource userResource) {
        User newUser = assembleUserFromResource(userResource);
        addOrganisationToUser(newUser, organisationId);
        addRoleToUser(newUser, UserRoleType.APPLICANT.getName());

        ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new UserResource());

        if(repository.findByEmail(userResource.getEmail()).isEmpty()) {
            UserResource createdUserResource = createUserWithToken(newUser);
            addUserResource(resourceEnvelope, createdUserResource);
        }
        else {
            addDuplicateEmailError(resourceEnvelope);
        }

        return resourceEnvelope;
    }

    @RequestMapping("/updateDetails")
    public ResourceEnvelope<UserResource> createUser(@RequestBody UserResource userResource) {
        ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), new ArrayList<>(), new UserResource());
        List<User> existingUser = repository.findByEmail(userResource.getEmail());
        if(existingUser == null || existingUser.size() <=0) {
            log.error("User with email " + userResource.getEmail() + " doesn't exist!");
            addUserDoesNotExistError(resourceEnvelope);
            return resourceEnvelope;
        }
        User newUser = updateUser(existingUser.get(0), userResource);
        UserResource updatedUser = updateUser(newUser);
        addUserResource(resourceEnvelope, updatedUser);
        return resourceEnvelope;
    }

    private void addUserResource(ResourceEnvelope<UserResource> resourceEnvelope, UserResource userResource) {
        resourceEnvelope.setEntity(userResource);
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.OK.getName());
    }

    private void addDuplicateEmailError(ResourceEnvelope<UserResource> resourceEnvelope) {
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.ERROR.getName());
        resourceEnvelope.addError(new ResourceError("email", "This email address is already in use"));
    }

    private void addUserDoesNotExistError(ResourceEnvelope<UserResource> resourceEnvelope) {
        resourceEnvelope.setStatus(ResourceEnvelopeConstants.ERROR.getName());
        resourceEnvelope.addError(new ResourceError("email", "User with given email address does not exist!"));
    }

    private UserResource createUserWithToken(User user) {
        User createdUser = repository.save(user);
        User createdUserWithToken = addTokenBasedOnIdToUser(createdUser);
        User finalUser = repository.save(createdUserWithToken);
        return new UserResource(finalUser);
    }

    private User updateUser(User existingUser, UserResource updatedUserResource){
        existingUser.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUser.setTitle(updatedUserResource.getTitle());
        existingUser.setLastName(updatedUserResource.getLastName());
        existingUser.setFirstName(updatedUserResource.getFirstName());
        return existingUser;
    }

    private UserResource updateUser(User user) {
        User savedUser = repository.save(user);
        return new UserResource(savedUser);
    }

    private void addRoleToUser(User user, String roleName) {
        List<Role> userRoles = roleRepository.findByName(roleName);
        user.setRoles(userRoles);
    }

    private void addOrganisationToUser(User user, Long organisationId) {
        Organisation userOrganisation = organisationRepository.findOne(organisationId);
        List<Organisation> userOrganisationList = new ArrayList<>();
        userOrganisationList.add(userOrganisation);
        user.setOrganisations(userOrganisationList);
    }

    private User assembleUserFromResource(UserResource userResource) {
        User newUser = new User();
        newUser.setFirstName(userResource.getFirstName());
        newUser.setLastName(userResource.getLastName());
        newUser.setPassword(userResource.getPassword());
        newUser.setEmail(userResource.getEmail());
        newUser.setTitle(userResource.getTitle());
        newUser.setPhoneNumber(userResource.getPhoneNumber());

        String fullName = concatenateFullName(userResource.getFirstName(), userResource.getLastName());
        newUser.setName(fullName);

        return newUser;
    }

    private String concatenateFullName(String firstName, String lastName) {
        return firstName+" "+lastName;
    }

    private User addTokenBasedOnIdToUser(User user) {
        String userToken = user.getId() + "abc123";
        user.setToken(userToken);
        return user;
    }
}
