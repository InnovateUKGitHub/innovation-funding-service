package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface KeywordRepository extends CrudRepository<Keyword, Long> {

    void deleteByPublicContentId(Long publicContentId);
    List<Keyword> findByKeywordLike(String keyword);
}
