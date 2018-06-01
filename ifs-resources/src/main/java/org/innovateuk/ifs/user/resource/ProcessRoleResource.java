package org.innovateuk.ifs.user.resource;

public class ProcessRoleResource {
    private long id;
    private long user;
    private String userName;
    private long applicationId;
    private long role;
    private String roleName;
    private long organisationId;

    public ProcessRoleResource(){
    	// no-arg constructor
    }

    public ProcessRoleResource(Long id, UserResource user, long applicationId, long roleId, String roleName, long organisationId) {
        this.id = id;
        this.user = user.getId();
        this.userName = user.getName();
        this.applicationId = applicationId;
        this.role = roleId;
        this.roleName = roleName;
        this.organisationId = organisationId;
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public Long getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getApplicationId() {
        return this.applicationId;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
