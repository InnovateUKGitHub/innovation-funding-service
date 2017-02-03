package org.innovateuk.ifs.threads.domain;


import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "thread_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long classPk;
    private String className;

    @Size(max=255)
    private String title;

    @OneToMany
    @OrderBy("created_on ASC")
    private List<Post> posts;

    @CreatedDate
    private LocalDateTime createdOn;

    Thread() {}
    Thread(Long id, List<Post> posts, String title, LocalDateTime createdOn) {
        this.id = id;
        this.posts = new LinkedList<>(posts);
        this.title = title;
        this.createdOn = createdOn;

    }
    public final Optional<Post> latestPost() {
        return postAtIndex(0);
    }

    private final Optional<Post> postAtIndex(int index) {
        return posts.size() >= (index-1) ? of(posts.get(index)): empty();
    }

    public final int numberOfPosts() {
        return posts.size();
    }

    public final boolean hasPosts() {
        return !posts.isEmpty();
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

    public String title() {
        return title;
    }

    public LocalDateTime createdOn() {
        return createdOn;
    }
}