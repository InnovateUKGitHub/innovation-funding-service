package org.innovateuk.ifs.threads.domain;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "thread_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long classPk;
    private String className;

    @Size(max = 255)
    private String title;

    @OneToMany
    @JoinColumn(name = "thread_id", referencedColumnName = "id", nullable = false)
    @OrderBy("created_on ASC")
    private List<Post> posts;

    @CreatedDate
    private LocalDateTime createdOn;

    Thread() {
    }

    Thread(Long id, Long classPk, String className, List<Post> posts, String title, LocalDateTime createdOn) {
        this.id = id;
        this.posts = new LinkedList<>(posts);
        this.title = title;
        this.createdOn = createdOn;
    }

    public final Optional<Post> latestPost() {
        return postAtIndex(0);
    }

    private final Optional<Post> postAtIndex(int index) {
        return posts.size() > index ? of(posts.get(index)) : empty();
    }

    public List<Post> posts() {
        return new ArrayList<>(posts);
    }

    public void addPost(Post post) {
        posts.add(0, post);
    }

    public Long id() {
        return id;
    }

    public Long contextClassPk() {
        return classPk;
    }

    public String contextClassName() {
        return className;
    }
    public String title() {
        return title;
    }

    public LocalDateTime createdOn() {
        return createdOn;
    }
}