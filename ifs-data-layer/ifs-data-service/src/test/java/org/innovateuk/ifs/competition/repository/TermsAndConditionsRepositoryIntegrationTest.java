package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.TermsAndConditions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsBuilder.newTermsAndConditions;

public class TermsAndConditionsRepositoryIntegrationTest extends
        BaseRepositoryIntegrationTest<TermsAndConditionsRepository> {

    @Autowired
    @Override
    protected void setRepository(TermsAndConditionsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findTopByNameOrderByVersionDesc() {

        List<TermsAndConditions> termsAndConditionsList = newTermsAndConditions()
                .with(id(null))
                .withName(
                        "name-1", "name-1", "name-1",
                        "name-2", "name-2", "name-2",
                        "name-3", "name-3", "name-3"
                )
                .withTemplate(
                        "template-1-v1", "template-1-v2", "template-1-v3",
                        "template-2-v1", "template-2-v2", "template-2-v3",
                        "template-3-v1", "template-3-v2", "template-3-v3"
                )
                .withVersion(
                        "1", "2", "3",
                        "1", "2", "3",
                        "1", "2", "3"
                )
                .build(9)
                .stream()
                .map(termsAndConditions -> repository.save(termsAndConditions))
                .collect(toList());

        // Get the latest versions and filter out any that were not created as part of this test
        List<TermsAndConditions> latestVersions = repository.findLatestVersions().stream().filter(
                termsAndConditions -> termsAndConditions.getName().startsWith("name-")).collect(toList());

        assertThat(latestVersions).containsExactlyInAnyOrder(termsAndConditionsList.get(2),
                termsAndConditionsList.get(5), termsAndConditionsList.get(8));

    }

}