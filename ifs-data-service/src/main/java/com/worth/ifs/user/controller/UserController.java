package com.worth.ifs.user.controller;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.resource.ResourceError;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
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

    @RequestMapping("/token/{token}")
     public User getUserByToken(@PathVariable("token") final String token) {
        List<User> users = repository.findByToken(token);
        if (users.size() > 0){
            log.debug("+++++++++++++++++++++++");
            log.debug(users.get(0).getName());
            log.debug(users.get(0).getId());
            log.debug("+++++++++++++++++++++++");
            return users.get(0);
        }else{
            return null;
        }
    }

    @RequestMapping("/email/{email}/password/{password}")
    public User getUserByEmailandPassword(@PathVariable("email") final String email, @PathVariable("password") final String password) {
        List<User> users = repository.findByEmail(email);

        if (users.size() > 0 ){
            User user = users.get(0);
            if(user.passwordEquals(password)){
                return user;
            }else{
                return null;
            }
        }else{
            log.warn("Return null");
            return null;
        }
    }

    @RequestMapping("/id/{id}")
    public User getUserById(@PathVariable("id") final Long id) {
        User user = repository.findOne(id);
        return user;
    }

    @RequestMapping("/name/{name}")
    public List<User> getUserByName(@PathVariable("name") final String name) {
        List<User> users = repository.findByName(name);
        return users;
    }
    @RequestMapping("/findAll/")
    public List<User> findAll() {
        List<User> users = repository.findAll();
        return users;
    }

    @RequestMapping("/findAssignableUsers/{applicationId}")
    public Set<User> findAssignableUsers(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<User> users = roles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
        return users;
    }

    @RequestMapping("/findRelatedUsers/{applicationId}")
    public Set<User> findRelatedUsers(@PathVariable("applicationId") final Long applicationId) {
        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<User> users = roles.stream()
                .map(ProcessRole::getUser)
                .collect(Collectors.toSet());
        return users;
    }

    @RequestMapping("/createUserForOrganisationWithRole/{organisationId}/{roleName}")
    public ResourceEnvelope<UserResource> createUser(@PathVariable("organisationId") final Long organisationId, @PathVariable("roleName") final String roleName, @RequestBody UserResource userResource) {

        User newUser = assembleUserFromResource(userResource);
        addOrganisationToUser(newUser, organisationId);
        addRoleToUser(newUser, roleName);

        ResourceEnvelope<UserResource> resourceEnvelope = new ResourceEnvelope<UserResource>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), userResource);

        if(!repository.findByEmail(userResource.getEmail()).isEmpty()) {
            resourceEnvelope.setStatus(ResourceEnvelopeConstants.ERROR.getName());
            resourceEnvelope.addError(new ResourceError("email", "This email address is already in use"));
        }
        else {
            User createdUser = repository.save(newUser);
            User createdUserWithToken = addTokenBasedOnIdToUser(createdUser);
            User user = repository.save(createdUserWithToken);
            resourceEnvelope.setEntity(new UserResource(user));
        }

        return resourceEnvelope;
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

    private User assembleUserFromResource(UserResource userDto) {
        User newUser = new User();
        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setPassword(userDto.getPassword());
        newUser.setEmail(userDto.getEmail());
        newUser.setTitle(userDto.getTitle());

        String fullName = concatenateFullName(userDto.getFirstName(), userDto.getLastName());
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
