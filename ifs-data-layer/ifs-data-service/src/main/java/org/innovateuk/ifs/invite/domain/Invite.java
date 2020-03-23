package org.innovateuk.ifs.invite.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DiscriminatorOptions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * An invitation for a person (who may or may not be an existing {@link User}) to participate in some business activity,
 * the target {@link ProcessActivity}
 *
 * @param <T> the type of business activity to which we're inviting
 */
@Table(
        // Does this constraint still hold?
    uniqueConstraints= @UniqueConstraint(columnNames={"type", "target_id", "email"})
)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorOptions(force = true)
public abstract class Invite<T, I extends Invite<T,I>> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email; // invitee

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false) // case sensitive? remove anyway
    private User user;

    @Column(unique=true)
    private String hash;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    @Column
    private ZonedDateTime sentOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sentBy", referencedColumnName="id")
    private User sentBy;

    public static String generateInviteHash() {
        return UUID.randomUUID().toString();
    }

    protected Invite() {
    	// no-arg constructor
        this.status= InviteStatus.CREATED;
    }

    protected Invite(String name, String email, String hash, InviteStatus status) {
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.status = status;
    }

    protected Invite(Long id, String name, String email, String hash, InviteStatus status) {
        this(name, email, hash, status);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getSentOn() {
        if (InviteStatus.CREATED == getStatus()) {
            return null;
        }
        return requireNonNull(sentOn, "Unexpected null sentOn on a " + getStatus() + " Invite");
    }

    public User getSentBy() {
        if (InviteStatus.CREATED == getStatus()) {
            return null;
        }
        return requireNonNull(sentBy, "Unexpected null sentBy on a " + getStatus() + " Invite");
    }

    // TODO rename to getProcess() and delete the setter
    public abstract T getTarget(); // the thing we're being invited to

    public abstract void setTarget(T target);

    protected final I doSend(User sentBy, ZonedDateTime sentOn) {
        this.sentBy = requireNonNull(sentBy, "sentBy cannot be null");
        this.sentOn = requireNonNull(sentOn, "sendOn cannot be null");
        this.status = InviteStatus.SENT;
        return (I) this; // for object chaining
    }

    public I send(User sentBy, ZonedDateTime sentOn) {
        if (this.status != InviteStatus.CREATED) {
            throw new IllegalStateException("(" + this.status + ") -> (" + InviteStatus.CREATED + ") Cannot send an Invite that has already been sent.");
        }
        return doSend(sentBy, sentOn);
    }

    public I resend(User sentBy, ZonedDateTime sentOn) {
        return doSend(sentBy, sentOn);
    }

    public I open () {
        this.status = InviteStatus.OPENED;
        return (I) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Invite<?, ?> invite = (Invite<?, ?>) o;

        return new EqualsBuilder()
                .append(id, invite.id)
                .append(name, invite.name)
                .append(email, invite.email)
                .append(user, invite.user)
                .append(hash, invite.hash)
                .append(status, invite.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(email)
                .append(user)
                .append(hash)
                .append(status)
                .toHashCode();
    }
}
