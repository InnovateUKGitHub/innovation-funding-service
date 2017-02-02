package org.innovateuk.ifs.threads.domain;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

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
    private ArrayDeque<Post> posts;

    @CreatedDate
    private LocalDateTime createdOn;


    public final Optional<Post> latestPost() {
        return of(posts.peekFirst());
    }

    public final Optional<Post> initialPost() {
        return of(posts.peekLast());
    }

    public final int numberOfPosts() {
        return posts.size();
    }

    public final boolean hasPosts() {
        return !posts.isEmpty();
    }

    public ArrayDeque<Post> posts() {
        return posts;
    }

    public void addPost(Post post) {
        posts.addFirst(post);
    }
}