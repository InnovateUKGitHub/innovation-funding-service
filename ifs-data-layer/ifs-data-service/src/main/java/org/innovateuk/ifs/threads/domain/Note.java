package org.innovateuk.ifs.threads.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("NOTE")
public class Note extends Thread {
    public Note() {super();}
    public Note(Long classPk, String className, List<Post> posts, String title, ZonedDateTime createdOn) {
        super(classPk, className, posts, title, createdOn);
    }

    public Note(Long classPk, List<Post> posts, String title, ZonedDateTime createdOn) {
        this(classPk, null, posts, title, createdOn);
    }

}