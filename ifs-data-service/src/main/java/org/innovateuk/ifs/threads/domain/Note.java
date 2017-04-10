package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("NOTE")
public class Note extends Thread {
    public Note() {super();}
    public Note(Long id, Long classPk, String className, List<Post> posts, String title, ZonedDateTime createdOn) {
        super(id, classPk, className, posts, title, createdOn);
    }

    public Note(Long id, Long classPk, List<Post> posts, String title, ZonedDateTime createdOn) {
        this(id, classPk, null, posts, title, createdOn);
    }

}