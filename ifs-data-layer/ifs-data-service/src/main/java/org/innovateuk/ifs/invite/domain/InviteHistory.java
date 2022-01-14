package org.innovateuk.ifs.invite.domain;

import lombok.Data;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
public class InviteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="inviteId", referencedColumnName="id")
    private Invite invite;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @Column
    private ZonedDateTime updatedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="updatedBy", referencedColumnName="id")
    private User updatedBy;
}
