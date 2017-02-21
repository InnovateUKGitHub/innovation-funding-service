package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_FORBIDDEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional service implementation for linking an {@link Application} to an {@link InnovationArea}.
 */
public class ApplicationInnovationAreaServiceImpl extends BaseTransactionalService implements ApplicationInnovationAreaService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Override
    public ServiceResult<Application> setInnovationArea(Long applicationId, Long innovationAreaId) {
        return findApplication(applicationId).andOnSuccess(application ->
                findInnovationAreaInAllowedList(application, innovationAreaId).andOnSuccess(innovationArea ->
                        saveApplicationWithInnovationArea(application, innovationArea)));
    }

    @Override
    public ServiceResult<Application> setNoInnovationAreaApplies(Long applicationId) {
        return findApplication(applicationId).andOnSuccess(application ->
                saveWithNoInnovationAreaApplies(application));
    }

    @Override
    public ServiceResult<List<InnovationArea>> getAvailableInnovationAreas(Long applicationdId) {
        return findApplication(applicationdId).andOnSuccess(application ->
                getAllowedInnovationAreas(application));
    }

    private ServiceResult<InnovationArea> findInnovationAreaInAllowedList(Application application, Long innovationAreaId) {
        return getAllowedInnovationAreas(application).andOnSuccess(areas ->
                findInnovationAreaInList(areas, innovationAreaId));
    }

    private ServiceResult<List<InnovationArea>> getAllowedInnovationAreas(Application application) {

        if(application.getCompetition() !=null
                && application.getCompetition().getInnovationSector() !=null
                && application.getCompetition().getInnovationSector().getChildren() !=null) {
            List<InnovationArea> innovationAreas = application.getCompetition().getInnovationSector().getChildren();
            return serviceSuccess(innovationAreas);
        }
        else {
            return serviceFailure(GENERAL_NOT_FOUND);
        }
    }

    private ServiceResult<InnovationArea> findInnovationAreaInList(List<InnovationArea> innovationAreasList, Long innovationAreaId) {
        Optional<InnovationArea> allowedInnovationArea = innovationAreasList.stream().filter(area ->
                area.getId().equals(innovationAreaId)).findAny();

        if(allowedInnovationArea.isPresent()) {
            return serviceSuccess(allowedInnovationArea.get());
        }
        else {
            return serviceFailure(GENERAL_FORBIDDEN);
        }
    }

    private ServiceResult<Application> saveWithNoInnovationAreaApplies(Application application) {
        application.setInnovationArea(null);
        application.setNoInnovationAreaApplicable(true);
        return serviceSuccess(applicationRepository.save(application));
    }

    private ServiceResult<Application> saveApplicationWithInnovationArea(Application application, InnovationArea innovationArea) {
        application.setNoInnovationAreaApplicable(false);
        application.setInnovationArea(innovationArea);
        return serviceSuccess(applicationRepository.save(application));
    }

    private ServiceResult<Application> findApplication(Long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }
}
