package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.user.resource.UserStatus;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

/**
 * The status of an assessor and the reason for making them
 * unavailable
 */
@Entity
public class UserRejectionStatus extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="userId", referencedColumnName="id", nullable = false)
    private User user;

    @Enumerated(STRING)
    private UserStatus status;

    private String rejectionReason;

    public UserRejectionStatus() {
    }

    public UserRejectionStatus(User user, UserStatus status, String rejectionReason) {
        this.user = user;
        this.status = status;
        this.rejectionReason = rejectionReason;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
