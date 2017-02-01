package org.innovateuk.ifs.threads.generic.domain;

import org.innovateuk.ifs.threads.generic.post.domain.Post;

import java.util.List;

/**
 * Created by nalexandre@worth.systems on 01/02/2017.
 */
public interface Threadable {

    List<Post> posts();

}
