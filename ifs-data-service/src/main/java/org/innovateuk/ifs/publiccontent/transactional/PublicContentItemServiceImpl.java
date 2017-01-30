package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.CompetitionCategoryLinkRepository;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class PublicContentItemServiceImpl extends BaseTransactionalService implements PublicContentItemService {

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private CompetitionCategoryLinkRepository competitionCategoryLinkRepository;

    @Override
    public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Long> pageNumber, Optional<Long> pageSize) {
        List<Long> competitionIds;
        List<Long> publicContentIds;


        if(innovationAreaId.isPresent()) {
            InnovationArea innovationArea = innovationAreaRepository.findOne(innovationAreaId.get());
            competitionIds = competitionCategoryLinkRepository.findByCategoryId(innovationArea.getSector().getId()).stream()
                    .map(competitionCategoryLink -> competitionCategoryLink.getEntity().getId())
                    .collect(Collectors.toList());
        }

        if(searchString.isPresent()) {
            
        }

        return null;
    }

    @Override
    public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) {
        //TODO : Implement Method
        return null;
    }

    private List<String> separateSearchStringToList(String searchString) {
        return asList(searchString.replaceAll("[^A-Za-z0-9]", " ").split("\\s"));
    }


}
