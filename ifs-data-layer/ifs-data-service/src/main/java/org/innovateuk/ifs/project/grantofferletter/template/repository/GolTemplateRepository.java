package org.innovateuk.ifs.project.grantofferletter.template.repository;

import org.innovateuk.ifs.project.grantofferletter.template.domain.GolTemplate;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for GolTemplate
 */
public interface GolTemplateRepository extends CrudRepository<GolTemplate, Long> {

    GolTemplate findFirstByNameOrderByVersionDesc(String name);
}
