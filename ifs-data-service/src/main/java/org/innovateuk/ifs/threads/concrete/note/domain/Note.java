package org.innovateuk.ifs.threads.concrete.note.domain;

import org.innovateuk.ifs.threads.concrete.query.domain.Query;
import org.innovateuk.ifs.threads.generic.domain.Threadable;
import org.innovateuk.ifs.threads.generic.post.domain.Post;

import javax.persistence.Entity;
import java.util.List;

@Entity
public final class Note implements Threadable {
    private Query query;

    @Override
    public final List<Post> posts() {
        return query.posts();
    }
}
