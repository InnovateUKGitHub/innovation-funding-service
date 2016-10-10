package com.worth.ifs.project.finance.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.*;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static com.worth.ifs.project.finance.domain.TimeUnit.DAY;
import static com.worth.ifs.project.finance.domain.TimeUnit.MONTH;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

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

    @Autowired
    private UserRepository userRepository;

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

        List<Cost> eligibleCosts = asList(new Cost("1.20"), new Cost("3.40"));
        List<Cost> spendProfileFigures = asList(new Cost("5.60"), new Cost("7.80"));

        User generatedBy = getFinanceTeamUser();
        Calendar generatedDate = Calendar.getInstance();
        generatedDate.set(Calendar.MILLISECOND, 0);

        SpendProfile saved = repository.save(new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, false));

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        SpendProfile retrieved = repository.findOne(saved.getId());
        assertNotSame(saved, retrieved);
        assertEquals(costCategoryType.getId(), retrieved.getCostCategoryType().getId());
        assertEquals(project.getId(), retrieved.getProject().getId());
        assertEquals(organisation.getId(), retrieved.getOrganisation().getId());
        assertEquals(generatedBy, retrieved.getGeneratedBy());
        assertEquals(generatedDate, retrieved.getGeneratedDate());

        List<BigDecimal> expectedEligibleCostValues = simpleMap(eligibleCosts, Cost::getValue);
        List<BigDecimal> actualEligibleCostValues = simpleMap(retrieved.getEligibleCosts().getCosts(), Cost::getValue);
        assertEquals(expectedEligibleCostValues, actualEligibleCostValues);

        List<BigDecimal> expectedSpendProfileCostValues = simpleMap(spendProfileFigures, Cost::getValue);
        List<BigDecimal> actualSpendProfileCostValues = simpleMap(retrieved.getSpendProfileFigures().getCosts(), Cost::getValue);
        assertEquals(expectedSpendProfileCostValues, actualSpendProfileCostValues);
    }

    @Test
    @Rollback
    public void test_createSpendProfileWithTimePeriodsAndCategories() {

        CostCategory labourCostCategory = new CostCategory("Labour");
        CostCategory materialsCostCategory = new CostCategory("Materials");
        List<CostCategory> costCategories = asList(labourCostCategory, materialsCostCategory);

        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType costCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);
        costCategoryTypeRepository.save(costCategoryType);

        Application application = applicationRepository.findOne(1L);

        Project project = new Project(null, application, null, null, null, "A name", null);

        projectRepository.save(project);

        Organisation organisation = organisationRepository.findOne(1L);

        List<Cost> eligibleCosts = asList(new Cost("1.20").withCategory(labourCostCategory), new Cost("3.40").withCategory(materialsCostCategory));

        List<Cost> spendProfileFigures = asList(
                new Cost("5.60").withCategory(labourCostCategory).withTimePeriod(0, DAY, 1, MONTH),
                new Cost("7.80").withCategory(materialsCostCategory).withTimePeriod(0, DAY, 1, MONTH));

        User generatedBy = getFinanceTeamUser();
        Calendar generatedDate = Calendar.getInstance();
        generatedDate.set(Calendar.MILLISECOND, 0);

        SpendProfile saved = repository.save(new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, false));

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        SpendProfile retrieved = repository.findOne(saved.getId());
        CostGroup retrievedFigures = retrieved.getSpendProfileFigures();
        CostGroup retrievedEligibles = retrieved.getEligibleCosts();

        assertNotSame(saved, retrieved);
        assertEquals(costCategoryType.getId(), retrieved.getCostCategoryType().getId());
        assertEquals(project.getId(), retrieved.getProject().getId());
        assertEquals(organisation.getId(), retrieved.getOrganisation().getId());
        assertEquals(generatedBy, retrieved.getGeneratedBy());
        assertEquals(generatedDate, retrieved.getGeneratedDate());

        List<BigDecimal> expectedEligibleCostValues = simpleMap(eligibleCosts, Cost::getValue);
        List<BigDecimal> actualEligibleCostValues = simpleMap(retrievedEligibles.getCosts(), Cost::getValue);
        assertEquals(expectedEligibleCostValues, actualEligibleCostValues);
        assertEquals(labourCostCategory.getName(), retrievedEligibles.getCosts().get(0).getCostCategory().getName());
        assertEquals(materialsCostCategory.getName(), retrievedEligibles.getCosts().get(1).getCostCategory().getName());
        assertNull(retrievedEligibles.getCosts().get(0).getCostTimePeriod());
        assertNull(retrievedEligibles.getCosts().get(1).getCostTimePeriod());

        List<BigDecimal> expectedSpendProfileCostValues = simpleMap(spendProfileFigures, Cost::getValue);

        List<BigDecimal> actualSpendProfileCostValues = simpleMap(retrievedFigures.getCosts(), Cost::getValue);
        assertEquals(expectedSpendProfileCostValues, actualSpendProfileCostValues);
        assertEquals(labourCostCategory.getName(), retrievedFigures.getCosts().get(0).getCostCategory().getName());
        assertEquals(materialsCostCategory.getName(), retrievedFigures.getCosts().get(1).getCostCategory().getName());
        assertNotNull(retrievedFigures.getCosts().get(0).getCostTimePeriod());
        assertNotNull(retrievedFigures.getCosts().get(1).getCostTimePeriod());

        assertEquals(Integer.valueOf(0), retrievedFigures.getCosts().get(0).getCostTimePeriod().getOffsetAmount());
        assertEquals(DAY, retrievedFigures.getCosts().get(0).getCostTimePeriod().getOffsetUnit());
        assertEquals(Integer.valueOf(1), retrievedFigures.getCosts().get(0).getCostTimePeriod().getDurationAmount());
        assertEquals(MONTH, retrievedFigures.getCosts().get(0).getCostTimePeriod().getDurationUnit());

        assertEquals(Integer.valueOf(0), retrievedFigures.getCosts().get(1).getCostTimePeriod().getOffsetAmount());
        assertEquals(DAY, retrievedFigures.getCosts().get(1).getCostTimePeriod().getOffsetUnit());
        assertEquals(Integer.valueOf(1), retrievedFigures.getCosts().get(1).getCostTimePeriod().getDurationAmount());
        assertEquals(MONTH, retrievedFigures.getCosts().get(1).getCostTimePeriod().getDurationUnit());
    }

    @Test
    @Rollback
    public void test_updateSpendProfile() {

        CostCategory labourCostCategory = new CostCategory("Labour");
        CostCategory materialsCostCategory = new CostCategory("Materials");
        List<CostCategory> costCategories = asList(labourCostCategory, materialsCostCategory);

        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType costCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);
        costCategoryTypeRepository.save(costCategoryType);

        Application application = applicationRepository.findOne(1L);

        Project project = new Project(null, application, null, null, null, "A name", null);

        projectRepository.save(project);

        Organisation organisation = organisationRepository.findOne(1L);

        List<Cost> eligibleCosts = singletonList(new Cost("1.20").withCategory(labourCostCategory));
        List<Cost> spendProfileFigures = singletonList(new Cost("5.60").withCategory(labourCostCategory).withTimePeriod(0, DAY, 1, MONTH));

        User generatedBy = newUser().build();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile saved = repository.save(new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, false));

        // clear the Hibernate cache
        flushAndClearSession();

        saved.getEligibleCosts().getCosts().get(0).setValue("99.99");
        saved.getEligibleCosts().addCost(new Cost("3.40").withCategory(materialsCostCategory));
        saved.getSpendProfileFigures().removeCost(0);
        saved.getSpendProfileFigures().addCost(new Cost("7.80").withCategory(materialsCostCategory).withTimePeriod(0, DAY, 1, MONTH));

        repository.save(saved);

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        SpendProfile updated = repository.findOne(saved.getId());
        CostGroup retrievedFigures = updated.getSpendProfileFigures();
        CostGroup retrievedEligibles = updated.getEligibleCosts();

        assertNotSame(saved, updated);
        assertEquals(costCategoryType.getId(), updated.getCostCategoryType().getId());
        assertEquals(project.getId(), updated.getProject().getId());
        assertEquals(organisation.getId(), updated.getOrganisation().getId());

        assertEquals(2, updated.getEligibleCosts().getCosts().size());
        assertEquals(1, updated.getSpendProfileFigures().getCosts().size());

        List<BigDecimal> expectedEligibleCostValues = asList(new BigDecimal("99.99"), new BigDecimal("3.40"));
        List<BigDecimal> actualEligibleCostValues = simpleMap(retrievedEligibles.getCosts(), Cost::getValue);
        assertEquals(expectedEligibleCostValues, actualEligibleCostValues);
        assertEquals(labourCostCategory.getName(), retrievedEligibles.getCosts().get(0).getCostCategory().getName());
        assertEquals(materialsCostCategory.getName(), retrievedEligibles.getCosts().get(1).getCostCategory().getName());
        assertNull(retrievedEligibles.getCosts().get(0).getCostTimePeriod());
        assertNull(retrievedEligibles.getCosts().get(1).getCostTimePeriod());

        List<BigDecimal> expectedSpendProfileCostValues = singletonList(new BigDecimal("7.80"));
        List<BigDecimal> actualSpendProfileCostValues = simpleMap(retrievedFigures.getCosts(), Cost::getValue);
        assertEquals(expectedSpendProfileCostValues, actualSpendProfileCostValues);
        assertEquals(materialsCostCategory.getName(), retrievedFigures.getCosts().get(0).getCostCategory().getName());
        assertNotNull(retrievedFigures.getCosts().get(0).getCostTimePeriod());

        assertEquals(Integer.valueOf(0), retrievedFigures.getCosts().get(0).getCostTimePeriod().getOffsetAmount());
        assertEquals(DAY, retrievedFigures.getCosts().get(0).getCostTimePeriod().getOffsetUnit());
        assertEquals(Integer.valueOf(1), retrievedFigures.getCosts().get(0).getCostTimePeriod().getDurationAmount());
        assertEquals(MONTH, retrievedFigures.getCosts().get(0).getCostTimePeriod().getDurationUnit());
    }

    @Test
    @Rollback
    public void test_findOneByProjectIdAndOrganisationId() {

        List<CostCategory> costCategories = asList(new CostCategory("Labour"), new CostCategory("Materials"));
        CostCategoryGroup costCategoryGroup = new CostCategoryGroup("Industrial Cost Categories group", costCategories);
        CostCategoryType costCategoryType = new CostCategoryType("CR&D Industrial Cost Categories", costCategoryGroup);
        costCategoryTypeRepository.save(costCategoryType);

        Application application = applicationRepository.findOne(1L);

        Project project = new Project(null, application, null, null, null, "A name", null);

        projectRepository.save(project);

        Organisation organisation = organisationRepository.findOne(1L);

        List<Cost> eligibleCosts = asList(new Cost("1.20"), new Cost("3.40"));
        List<Cost> spendProfileFigures = asList(new Cost("5.60"), new Cost("7.80"));

        User generatedBy = getFinanceTeamUser();
        Calendar generatedDate = Calendar.getInstance();

        SpendProfile saved = repository.save(new SpendProfile(organisation, project, costCategoryType, eligibleCosts, spendProfileFigures, generatedBy, generatedDate, false));

        // clear the Hibernate cache
        flushAndClearSession();

        // and retrieve from the db again - ensure its value is retained
        SpendProfile retrieved = repository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId()).get();
        assertNotSame(saved, retrieved);
        assertEquals(costCategoryType.getId(), retrieved.getCostCategoryType().getId());
        assertEquals(project.getId(), retrieved.getProject().getId());
        assertEquals(organisation.getId(), retrieved.getOrganisation().getId());

        List<BigDecimal> expectedEligibleCostValues = simpleMap(eligibleCosts, Cost::getValue);
        List<BigDecimal> actualEligibleCostValues = simpleMap(retrieved.getEligibleCosts().getCosts(), Cost::getValue);
        assertEquals(expectedEligibleCostValues, actualEligibleCostValues);

        List<BigDecimal> expectedSpendProfileCostValues = simpleMap(spendProfileFigures, Cost::getValue);
        List<BigDecimal> actualSpendProfileCostValues = simpleMap(retrieved.getSpendProfileFigures().getCosts(), Cost::getValue);
        assertEquals(expectedSpendProfileCostValues, actualSpendProfileCostValues);
    }

    private User getFinanceTeamUser() {
        return userRepository.findByEmail("finance@innovateuk.gov.uk").get();
    }
}
