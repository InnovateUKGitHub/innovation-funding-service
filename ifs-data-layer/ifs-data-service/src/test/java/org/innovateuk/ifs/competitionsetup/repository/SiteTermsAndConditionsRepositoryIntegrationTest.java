package org.innovateuk.ifs.competitionsetup.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competitionsetup.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competitionsetup.repository.SiteTermsAndConditionsRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competitionsetup.builder.SiteTermsAndConditionsBuilder.newSiteTermsAndConditions;

public class SiteTermsAndConditionsRepositoryIntegrationTest extends
        BaseRepositoryIntegrationTest<SiteTermsAndConditionsRepository> {

    @Autowired
    @Override
    protected void setRepository(SiteTermsAndConditionsRepository repository) {
        this.repository = repository;
    }

    @Test
    public void findTopByOrderByVersionDesc() {
        loginCompAdmin();

        List<SiteTermsAndConditions> siteTermsAndConditions = IntStream.range(1, 3).mapToObj(i ->
                newSiteTermsAndConditions()
                        .with(id(null))
                        .withName("Site terms")
                        .withTemplate("site-terms-v" + i)
                        .withVersion(i)
                        .build())
                .collect(toList());

        repository.save(siteTermsAndConditions);

        assertThat(repository.findTopByOrderByVersionDesc()).isEqualToComparingOnlyGivenFields(siteTermsAndConditions
                .get(siteTermsAndConditions.size() - 1), "name", "template", "version");
    }

}