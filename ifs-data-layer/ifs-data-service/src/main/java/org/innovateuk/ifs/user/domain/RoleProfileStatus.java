package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

/**
 * The status of a user and the reason for making them
 * unavailable
 */
@Entity
public class RoleProfileStatus extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="userId", referencedColumnName="id", nullable = false)
    private User user;

    @Enumerated(STRING)
    private RoleProfileState roleProfileState;

    @Enumerated(STRING)
    private ProfileRole profileRole;

    private String description;

    public RoleProfileStatus() {
    }

    public RoleProfileStatus(User user, RoleProfileState roleProfileState, ProfileRole profileRole, String description) {
        this.user = user;
        this.roleProfileState = roleProfileState;
        this.profileRole = profileRole;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RoleProfileState getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(RoleProfileState roleProfileState) {
        this.roleProfileState = roleProfileState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProfileRole getProfileRole() {
        return profileRole;
    }

    public void setProfileRole(ProfileRole profileRole) {
        this.profileRole = profileRole;
    }
}
