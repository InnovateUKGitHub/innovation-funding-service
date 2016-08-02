package com.worth.ifs.invite.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.user.domain.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/*
* The Invite is used for saving invites into the database. Data about the Invitee and related Application and organisation is saved through this entity.
* */
@Table(
        // Does this constraint still hold?
    uniqueConstraints= @UniqueConstraint(columnNames={"type", "target_id", "email"})
)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Entity
public abstract class Invite<T extends InvitationTarget> {
    private static final CharSequence HASH_SALT = "b80asdf00poiasd07hn";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private  String name;
    @NotBlank
    @Email
    private  String email; // invitee

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private User user;

    private String hash;

    @Enumerated(EnumType.STRING)
    private InviteStatusConstants status;

    Invite() {
    	// no-arg constructor
    }

    Invite(final String name, final String email, final String hash, final InviteStatusConstants status) {
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

    public InviteStatusConstants getStatus() {
        return status;
    }

    public void setStatus(InviteStatusConstants status) {
        this.status = status;
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

    public abstract T getTarget(); // the thing we're being invited to

    public abstract void setTarget(T target);
}
