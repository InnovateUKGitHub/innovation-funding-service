package org.innovateuk.ifs.competition;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * Populator for retrieving and filling the viewmodel for public content competition search page.
 */
@Component
public class CompetitionSearchPopulator {

    @Autowired
    private PublicContentItemRestServiceImpl publicContentItemRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    public CompetitionSearchViewModel createItemSearchViewModel(Optional<Long> innovationAreaId, Optional<String> keywords, Optional<Integer> pageNumber) {
        CompetitionSearchViewModel viewModel = new CompetitionSearchViewModel();

        categoryRestService.getInnovationAreas().andOnSuccess(innovationAreas -> { viewModel.setInnovationAreas(innovationAreas); return restSuccess();});
        Optional<PublicContentItemPageResource> pageResource = publicContentItemRestService.getByFilterValues(
                innovationAreaId,
                keywords,
                pageNumber,
                CompetitionSearchViewModel.PAGE_SIZE).getOptionalSuccessObject();

        innovationAreaId.ifPresent(id -> viewModel.setSelectedInnovationAreaId(id));
        keywords.ifPresent(words -> viewModel.setSearchKeywords(words));

        if(pageNumber.isPresent()) {
            viewModel.setPageNumber(pageNumber.get());
        }
        else {
            viewModel.setPageNumber(0);
        }

        if(pageResource.isPresent()) {
            viewModel.setPublicContentItems(pageResource.get().getContent());
            viewModel.setTotalResults(pageResource.get().getTotalElements());
        }
        else {
            viewModel.setTotalResults(0L);
        }


        viewModel.setNextPageLink(createPageLink(innovationAreaId, keywords, pageNumber, 1));
        viewModel.setPreviousPageLink(createPageLink(innovationAreaId, keywords, pageNumber, -1));

        return viewModel;
    }

    private String createPageLink(Optional<Long> innovationAreaId, Optional<String> keywords, Optional<Integer> pageNumber, Integer delta) {
        List<NameValuePair> searchparams = new ArrayList<>();

        Integer page = delta;
        if(pageNumber.isPresent()) {
            page = pageNumber.get() + delta;
        }

        innovationAreaId.ifPresent(id -> searchparams.add(new BasicNameValuePair("innovationAreaId", id.toString())));
        keywords.ifPresent(words -> searchparams.add(new BasicNameValuePair("keywords", words)));
        searchparams.add(new BasicNameValuePair("page", page.toString()));

        return URLEncodedUtils.format(searchparams, "UTF-8");
    }

}
