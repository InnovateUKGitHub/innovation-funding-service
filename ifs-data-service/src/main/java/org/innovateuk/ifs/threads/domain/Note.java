package org.innovateuk.ifs.threads.domain;

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
