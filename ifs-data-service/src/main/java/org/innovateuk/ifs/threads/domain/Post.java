package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @NotNull
    private String body;

    @OneToMany
    @JoinTable(name = "post_attachment",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id", referencedColumnName = "id"))
    private List<Attachment> attachments;

    @CreatedDate
    private ZonedDateTime createdOn;

    public Post(){}
    public Post(Long id, User author, String body, List<Attachment> attachments, ZonedDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.attachments = ofNullable(attachments).map(ArrayList::new).orElse(new ArrayList<>());
        this.createdOn = createdOn;
    }

    public Long id() {
        return id;
    }

    public User author() {
        return author;
    }

    public List<Attachment> attachments() {
        return new ArrayList<>(attachments);
    }

    public final String body() {
        return body;
    }

    public final ZonedDateTime createdOn() {
        return createdOn;
    }
}