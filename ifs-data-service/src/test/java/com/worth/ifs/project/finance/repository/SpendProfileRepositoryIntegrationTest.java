package com.worth.ifs.project.finance.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Repository Integration tests for Costs.
 */
public class SpendProfileRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<SpendProfileRepository> {

    @Override
    @Autowired
    protected void setRepository(SpendProfileRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private CostCategoryTypeRepository costCategoryTypeRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    @Rollback
    public void test_createSpendProfile() {

        List<CostCategory> costCategories = asList(new CostCategory("Labour"), new CostCategory("Materials"));
        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType costCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);
        costCategoryTypeRepository.save(costCategoryType);

        Application application = applicationRepository.findOne(1L);

        Project project = new Project(null, application, null, null, null, "A name", null);
        projectRepository.save(project);

        Organisation organisation = organisationRepository.findOne(1L);

        CostGroup eligibleCosts = new CostGroup("My eligible costs", asList(new Cost("1.2"), new Cost("3.4")));
        CostGroup spendProfileFigures = new CostGroup("My spend profile costs", asList(new Cost("5.6"), new Cost("7.8")));

        SpendProfile saved = repository.save(new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileFigures));

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        SpendProfile retrieved = repository.findOne(saved.getId());
        assertNotSame(saved, retrieved);
        assertEquals(costCategoryType.getId(), retrieved.getCostCategoryType().getId());
    }
}
