package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.user.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("NOTE")
public class Note extends Thread {

    public Note(Long id, List<Post> posts, String title, LocalDateTime createdOn) {
        super(id, posts, title, createdOn);
    }
}