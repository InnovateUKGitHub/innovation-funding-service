package org.innovateuk.ifs.competition.populator;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.mapper.PublicContentItemViewModelMapper;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.status.PublicContentStatusDeterminer;
import org.innovateuk.ifs.competition.status.PublicContentStatusText;
import org.innovateuk.ifs.competition.viewmodel.CompetitionSearchViewModel;
import org.innovateuk.ifs.competition.viewmodel.PublicContentItemViewModel;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Populator for retrieving and filling the viewmodel for public content competition search page.
 */
@Component
public class CompetitionSearchPopulator {

    @Autowired
    private PublicContentItemRestServiceImpl publicContentItemRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private PublicContentStatusDeterminer publicContentStatusDeterminer;

    @Autowired
    private PublicContentItemViewModelMapper publicContentItemViewModelMapper;

    @Value("${ifs.show.covid.questionnaire.links}")
    private boolean showCovidQuestionnaireLink;

    public CompetitionSearchViewModel createItemSearchViewModel(Optional<Long> innovationAreaId, Optional<String> keywords, Optional<Integer> pageNumber) {
        CompetitionSearchViewModel viewModel = new CompetitionSearchViewModel();
        viewModel.setInnovationAreas(categoryRestService.getInnovationAreas().getSuccess());

        PublicContentItemPageResource pageResource = publicContentItemRestService.getByFilterValues(
                innovationAreaId,
                keywords,
                pageNumber,
                CompetitionSearchViewModel.PAGE_SIZE).getSuccess();

        innovationAreaId.ifPresent(viewModel::setSelectedInnovationAreaId);
        keywords.ifPresent(viewModel::setSearchKeywords);

        if (pageNumber.isPresent()) {
            viewModel.setPageNumber(pageNumber.get());
        } else {
            viewModel.setPageNumber(0);
        }

        viewModel.setPublicContentItems(pageResource.getContent().stream()
                .map(this::createPublicContentItemViewModel).collect(Collectors.toList()));
        viewModel.setTotalResults(pageResource.getTotalElements());
        viewModel.setNextPageLink(createPageLink(innovationAreaId, keywords, pageNumber, 1));
        viewModel.setPreviousPageLink(createPageLink(innovationAreaId, keywords, pageNumber, -1));
        viewModel.setShowCovidQuestionnaireLink(showCovidQuestionnaireLink);

        return viewModel;
    }

    private String createPageLink(Optional<Long> innovationAreaId, Optional<String> keywords, Optional<Integer> pageNumber, Integer delta) {
        List<NameValuePair> searchparams = new ArrayList<>();

        Integer page = delta;
        if (pageNumber.isPresent()) {
            page = pageNumber.get() + delta;
        }

        innovationAreaId.ifPresent(id -> searchparams.add(new BasicNameValuePair("innovationAreaId", id.toString())));
        keywords.ifPresent(words -> searchparams.add(new BasicNameValuePair("keywords", words)));
        searchparams.add(new BasicNameValuePair("page", page.toString()));

        return URLEncodedUtils.format(searchparams, "UTF-8");
    }

    private PublicContentItemViewModel createPublicContentItemViewModel(PublicContentItemResource publicContentItemResource) {
        PublicContentItemViewModel publicContentItemViewModel = publicContentItemViewModelMapper.mapToViewModel(publicContentItemResource);

        PublicContentStatusText publicContentStatusText = publicContentStatusDeterminer.getApplicablePublicContentStatusText(publicContentItemResource);
        publicContentItemViewModel.setPublicContentStatusText(publicContentStatusText);

        return publicContentItemViewModel;
    }


}