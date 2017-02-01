package org.innovateuk.ifs.publiccontent.repository;

import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface PublicContentRepository extends PagingAndSortingRepository<PublicContent, Long> {

    PublicContent findByCompetitionId(Long id);
    Page<PublicContent> findByCompetitionIdInAndIdIn(List<Long> competitionIds, Set<Long> publicContentIds, Pageable pageable);
    Page<PublicContent> findByCompetitionIdIn(List<Long> competitionIds, Pageable pageable);
    Page<PublicContent> findByIdIn(Set<Long> publicContentIds, Pageable pageable);
}
