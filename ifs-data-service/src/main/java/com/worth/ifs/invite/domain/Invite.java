package com.worth.ifs.invite.domain;

import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;

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
public abstract class Invite<T extends ProcessActivity, I extends Invite<T,I>> {
    private static final CharSequence HASH_SALT = "b80asdf00poiasd07hn";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private  String name;
    @NotBlank
    @Email
    private  String email; // invitee

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false) // case sensitive? remove anyway
    private User user;

    @Column(unique=true)
    private String hash;

    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    Invite() {
    	// no-arg constructor
        this.status= InviteStatus.CREATED;
    }

    protected Invite(String name, String email, String hash, InviteStatus status) {
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.status = status;
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

    protected void setStatus(final InviteStatus newStatus) {
        if (newStatus == null) throw new NullPointerException("status cannot be null");
        switch (newStatus) {
            case CREATED:
                if (this.status != null) throw new IllegalStateException("(" + this.status + ") -> (" + newStatus + ") Cannot create an Invite that has already been created.");
                break;
            case SENT:
                if (this.status != InviteStatus.CREATED)
                    throw new IllegalStateException("(" + this.status + ") -> (" + newStatus + ") Cannot send an Invite that has already been sent.");
                break;
            case OPENED:
                // TODO check legal invite transitions
//                if (this.status != InviteStatus.SENT || this.status != InviteStatus.OPENED)
//                    throw new IllegalStateException("(" + this.status + ") -> (" + newStatus + ") Cannot accept an Invite that hasn't been sent");
                break;
        }
        this.status = newStatus;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String generateHash() {
        if(StringUtils.isEmpty(hash)){
            StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);
            int random = (int) Math.ceil(Math.random() * 100); // random number from 1 to 100
            hash = String.format("%s==%s==%s", id, email, random);
            hash = encoder.encode(hash);
        }
        return hash;
    }

    // TODO rename to getProcess() and delete the setter
    public abstract T getTarget(); // the thing we're being invited to

    public abstract void setTarget(T target);

    public I send() {
        setStatus(InviteStatus.SENT);
        return (I) this; // for object chaining
    }

    public I open () {
        setStatus(InviteStatus.OPENED);
        return (I) this; // for object chaining
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
