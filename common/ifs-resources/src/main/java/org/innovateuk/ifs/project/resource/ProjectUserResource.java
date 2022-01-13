package org.innovateuk.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.user.resource.UserResource;

public class ProjectUserResource {
    private Long id;
    private Long user;
    private String userName;
    private Long project;
    private ProjectParticipantRole role;
    private String roleName;
    private Long organisation;
    private String email;
    private String phoneNumber;

    private Long invite;

    public ProjectUserResource(){
    	// no-arg constructor
    }

    public ProjectUserResource(Long id, UserResource user, ProjectResource project, ProjectParticipantRole role, OrganisationResource organisation, ProjectUserInviteResource invite) {
        this.id = id;
        this.user = user.getId();
        this.userName = user.getName();
        this.project = project.getId();
        this.role = role;
        this.roleName = role.getName();
        this.organisation = organisation.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.invite = invite.getId();
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public ProjectParticipantRole getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public Long getProject() {
        return this.project;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public void setRole(ProjectParticipantRole role) {
        this.role = role;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isFinanceContact() {
        return role == ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
    }

    @JsonIgnore
    public boolean isProjectManager() {
        return role == ProjectParticipantRole.PROJECT_MANAGER;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getInvite() {
        return invite;
    }

    public void setInvite(Long invite) {
        this.invite = invite;
    }

    @JsonIgnore
    public boolean isUser(Long anUserId) {
        return user.equals(anUserId);
    }

}
