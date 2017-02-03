package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User author;

    @NotNull
    private String body;

    @OneToMany
    @JoinTable(name = "post_attachment",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_entry_id", referencedColumnName = "id"))
    private List<FileEntry> attachments;

    @CreatedDate
    private LocalDateTime createdOn;

    public Post(){}
    public Post(Long id, User author, String body, List<FileEntry> attachments, LocalDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.attachments = new ArrayList<>(attachments);
        this.createdOn = createdOn;
    }

    public Long id() {
        return id;
    }

    public User author() {
        return author;
    }

    public List<FileEntry> attachments() {
        return attachments;
    }

    public final String body() {
        return body;
    }

    public final LocalDateTime createdOn() {
        return createdOn;
    }
}