package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * User Data Transfer Object
 */
public class UserResource {

    private static final CharSequence PASSWORD_SECRET = "a02214f47a45171c";

    private static final Log LOG = LogFactory.getLog(UserResource.class);
    private Long id;
    private String title;
    private String name;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    private String token;
    private String email;
    private String password;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> organisations = new ArrayList<>();
    private List<Long> roles = new ArrayList<>();

    public UserResource() {

    }

    public UserResource(String name, String email, String password, String token, String imageUrl,
                List<ProcessRole> processRoles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.imageUrl = imageUrl;
        this.processRoles = simpleMap(processRoles, ProcessRole::getId);
    }

    public UserResource(Long id, String name, String email, String password, String token, String imageUrl,
                List<ProcessRole> processRoles) {
        this(name, email, password, token, imageUrl, processRoles);
        this.id = id;
    }

    public UserResource(User user) {
        id = user.getId();
        title = user.getTitle();
        name = user.getName();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        inviteName = user.getInviteName();
        phoneNumber = user.getPhoneNumber();
        imageUrl = user.getImageUrl();
        token = user.getToken();
        email = user.getEmail();
        password = user.getEmail();
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getToken() {
        return token;
    }

    public List<Long> getProcessRoles() {
        return processRoles;
    }

    public List<Long> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = simpleMap(organisations, Organisation::getId);
    }

    public void addUserResourceApplicationRole(ProcessRole... r){
        if(this.processRoles == null){
            this.processRoles = new ArrayList<>();
        }
        this.processRoles.addAll(simpleMap(Arrays.asList(r), ProcessRole::getId));
    }

    public void addUserResourceOrganisation(Organisation... o){
        if(this.organisations == null){
            this.organisations  = new ArrayList<>();
        }
        this.organisations.addAll(simpleMap(Arrays.asList(o), Organisation::getId));
    }

    public Boolean passwordEquals(String passwordInput){
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        LOG.debug(encoder.matches(passwordInput, this.password));
        return encoder.matches(passwordInput, this.password);
    }

    public void setPassword(String setPassword) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        setPassword = encoder.encode(setPassword);
        this.password = setPassword;
    }

    public List<Long> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = simpleMap(roles, Role::getId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInviteName() {
        return inviteName;
    }

    public void setInviteName(String inviteName) {
        this.inviteName = inviteName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) { this.token = token; }

    public String getPassword() {
        return this.password;
    }

}
