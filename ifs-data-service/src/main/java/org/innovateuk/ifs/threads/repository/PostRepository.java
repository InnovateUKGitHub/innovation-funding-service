package org.innovateuk.ifs.threads.repository;

import org.innovateuk.ifs.threads.domain.Post;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
}
