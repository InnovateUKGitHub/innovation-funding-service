package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.SiteTermsAndConditions;
import org.innovateuk.ifs.competitionsetup.mapper.GrantTermsAndConditionsMapper;
import org.innovateuk.ifs.competitionsetup.mapper.SiteTermsAndConditionsMapper;
import org.innovateuk.ifs.competitionsetup.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competitionsetup.repository.SiteTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of TermsAndConditions
 */
@Service
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

    private GrantTermsAndConditionsMapper grantTermsAndConditionsMapper;
    private SiteTermsAndConditionsMapper siteTermsAndConditionsMapper;
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;
    private SiteTermsAndConditionsRepository siteTermsAndConditionsRepository;

    @Autowired
    public TermsAndConditionsServiceImpl(
            GrantTermsAndConditionsRepository grantTermsAndConditionsRepository,
            SiteTermsAndConditionsRepository siteTermsAndConditionsRepository,
            GrantTermsAndConditionsMapper grantTermsAndConditionsMapper,
            SiteTermsAndConditionsMapper siteTermsAndConditionsMapper) {
        this.grantTermsAndConditionsRepository = grantTermsAndConditionsRepository;
        this.siteTermsAndConditionsRepository = siteTermsAndConditionsRepository;
        this.grantTermsAndConditionsMapper = grantTermsAndConditionsMapper;
        this.siteTermsAndConditionsMapper = siteTermsAndConditionsMapper;
    }

    @Override
    public ServiceResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions() {
        return serviceSuccess((List<GrantTermsAndConditionsResource>)
                grantTermsAndConditionsMapper.mapToResource(grantTermsAndConditionsRepository.findLatestVersions())
        );
    }

    @Override
    public ServiceResult<GrantTermsAndConditionsResource> getById(Long id) {
        return find(grantTermsAndConditionsRepository.findOne(id), notFoundError(GrantTermsAndConditionsResource.class, id))
                .andOnSuccessReturn(grantTermsAndConditionsMapper::mapToResource);
    }

    @Override
    public ServiceResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return find(siteTermsAndConditionsRepository.findTopByOrderByVersionDesc(),
                notFoundError(SiteTermsAndConditions.class)).andOnSuccessReturn
                (siteTermsAndConditionsMapper::mapToResource);
    }
}
