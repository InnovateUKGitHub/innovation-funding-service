package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationFinanceResourceListType;

/**
 * ApplicationFinanceRestServiceImpl is a utility for CRUD operations on {@link ApplicationFinanceResource}.
 * This class connects to the k org.innovateuk.ifs.finance.controller.ApplicationFinanceController}
 * through a REST call
 */
@Service
public class ApplicationFinanceRestServiceImpl extends BaseRestService implements ApplicationFinanceRestService {

    private String applicationFinanceRestURL = "/applicationfinance";

    @Override
    public RestResult<ApplicationFinanceResource> getApplicationFinance(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null){
            return null;
        }
        return getWithRestResult(applicationFinanceRestURL + "/findByApplicationOrganisation/" + applicationId + "/" + organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getApplicationFinances(Long applicationId) {
        if(applicationId == null) {
            return null;
        }

        return getWithRestResult(applicationFinanceRestURL + "/findByApplication/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<ApplicationFinanceResource> addApplicationFinanceForOrganisation(Long applicationId, Long organisationId) {
        if(applicationId == null || organisationId == null) {
            return null;
        }
        return postWithRestResult(applicationFinanceRestURL + "/add/" + applicationId + "/" + organisationId, null, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<ApplicationFinanceResource> update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance){
        return postWithRestResult(applicationFinanceRestURL + "/update/"+ applicationFinanceId, applicationFinance, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<ApplicationFinanceResource> getById(Long applicationFinanceId){
        return getWithRestResult(applicationFinanceRestURL + "/getById/" + applicationFinanceId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<Double> getResearchParticipationPercentage(Long applicationId){
        return getWithRestResult(applicationFinanceRestURL + "/getResearchParticipationPercentage/" + applicationId, Double.class);
    }

    @Override
    public RestResult<ApplicationFinanceResource> getFinanceDetails(Long applicationId, Long organisationId) {
        return getWithRestResult(applicationFinanceRestURL + "/financeDetails/" + applicationId + "/"+organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getFinanceDetails(Long applicationId) {
        return getWithRestResult(applicationFinanceRestURL + "/financeDetails/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return getWithRestResult(applicationFinanceRestURL + "/financeTotals/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = applicationFinanceRestURL + "/financeDocument" +
                "?applicationFinanceId=" + applicationFinanceId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType,  contentLength);

        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFinanceDocument(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/financeDocument" +
                "?applicationFinanceId=" + applicationFinanceId;

        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/financeDocument" +
                "?applicationFinanceId=" + applicationFinanceId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetails(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/financeDocument/fileentry" +
                "?applicationFinanceId=" + applicationFinanceId;

        return getWithRestResult(url, FileEntryResource.class);
    }
}
