/**
 * Created by nunoalexandre on 10/09/15.
 */
package com.worth.ifs.repository;

        import com.worth.ifs.domain.Application;
        import com.worth.ifs.domain.Competition;
        import org.springframework.data.repository.PagingAndSortingRepository;
        import org.springframework.data.repository.query.Param;
        import org.springframework.data.rest.core.annotation.RepositoryRestResource;

        import java.util.List;
        import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "competitions", path = "competition")
public interface CompetitionsRepository extends PagingAndSortingRepository<Competition, Long> {

    List<Competition> findByName(@Param("name") String name);
    Competition findById(@Param("id") Long id);
    List<Competition> findAll();

}
