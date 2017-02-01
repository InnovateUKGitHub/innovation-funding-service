package org.innovateuk.ifs.threads.generic.domain;


import org.innovateuk.ifs.threads.generic.post.domain.Post;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Entity
public final class Thread implements Threadable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long classPk;
    private String className;

    @Size(max=255)
    private String title;

    @OneToMany
    private List<Post> posts;

    @CreatedDate
    private LocalDateTime createdOn;


    public final Optional<Post> latestPost() {
        return postAtIndex(numberOfPosts()-1);
    }

    public final Optional<Post> initialPost() {
        return postAtIndex(0);
    }

    private final Optional<Post> postAtIndex(int i) {
        return hasPosts() ? of(posts.get(i)) : empty();
    }

    public final int numberOfPosts() {
        return posts.size();
    }

    public final boolean hasPosts() {
        return !posts.isEmpty();
    }

    public List<Post> posts() {
        return posts;
    }
}