package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Mapper(
        config = GlobalMapperConfig.class
)
public class ApplicationCountSummaryPageMapper extends PageResourceMapper<ApplicationStatistics, ApplicationCountSummaryResource> {

    @Autowired
    private ApplicationCountSummaryMapper applicationCountSummaryMapper;

    @Autowired
    private OrganisationRepository organisationRepository;

    public ApplicationCountSummaryPageResource mapToResource(Page<ApplicationStatistics> source) {
        ApplicationCountSummaryPageResource result = new ApplicationCountSummaryPageResource();
        return mapFields(source, result);
    }

    @Override
    protected Function<ApplicationStatistics, ApplicationCountSummaryResource> contentElementConverter() {
        return as -> {
            ApplicationCountSummaryResource resource = applicationCountSummaryMapper.mapToResource(as);
            Optional<Organisation> organisation = ofNullable(organisationRepository.findOne(as.getLeadOrganisationId()));
            resource.setLeadOrganisation(organisation.map(Organisation::getName).orElse(""));
            return resource;
        };
    }
}
