package org.innovateuk.ifs.eucontact.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eucontact.repository.EuContactRepository;
import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.mapper.EuContactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class EuContactServiceImpl implements EuContactService {

    @Autowired
    private EuContactRepository euContactRepository;

    @Autowired
    private EuContactMapper euContactMapper;

    @Override
    public ServiceResult<EuContactPageResource> getEuContactsByNotified(boolean notified,
                                                                        Pageable pageable) {
        Page<EuContact> euContactPage = notified ?
                euContactRepository.findByNotifiedTrue(pageable) :
                euContactRepository.findByNotifiedFalse(pageable);

        List<EuContactResource> resources = simpleMap(euContactPage.getContent(),
                                                      euContactMapper::mapToResource);
        return serviceSuccess(
                new EuContactPageResource(euContactPage.getTotalElements(),
                                          euContactPage.getTotalPages(),
                                          resources,
                                          euContactPage.getNumber(),
                                          euContactPage.getSize())
        );
    }
}
