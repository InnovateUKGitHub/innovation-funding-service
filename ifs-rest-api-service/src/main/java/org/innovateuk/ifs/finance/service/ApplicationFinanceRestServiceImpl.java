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
        return getWithRestResult(applicationFinanceRestURL + "/find-by-application-organisation/" + applicationId + "/" + organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getApplicationFinances(Long applicationId) {
        if(applicationId == null) {
            return null;
        }

        return getWithRestResult(applicationFinanceRestURL + "/find-by-application/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<ApplicationFinanceResource> update(Long applicationFinanceId, ApplicationFinanceResource applicationFinance){
        return postWithRestResult(applicationFinanceRestURL + "/update/"+ applicationFinanceId, applicationFinance, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<ApplicationFinanceResource> getById(Long applicationFinanceId){
        return getWithRestResult(applicationFinanceRestURL + "/get-by-id/" + applicationFinanceId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<Double> getResearchParticipationPercentage(Long applicationId){
        return getWithRestResult(applicationFinanceRestURL + "/get-research-participation-percentage/" + applicationId, Double.class);
    }

    @Override
    public RestResult<ApplicationFinanceResource> getFinanceDetails(Long applicationId, Long organisationId) {
        return getWithRestResult(applicationFinanceRestURL + "/finance-details/" + applicationId + "/"+organisationId, ApplicationFinanceResource.class);
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getFinanceDetails(Long applicationId) {
        return getWithRestResult(applicationFinanceRestURL + "/finance-details/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<List<ApplicationFinanceResource>> getFinanceTotals(Long applicationId) {
        return getWithRestResult(applicationFinanceRestURL + "/finance-totals/" + applicationId, applicationFinanceResourceListType());
    }

    @Override
    public RestResult<FileEntryResource> addFinanceDocument(Long applicationFinanceId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = applicationFinanceRestURL + "/finance-document" +
                "?applicationFinanceId=" + applicationFinanceId +
                "&filename=" + originalFilename;

        final HttpHeaders headers = createFileUploadHeader(contentType,  contentLength);

        return postWithRestResult(url, file, headers, FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeFinanceDocument(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/finance-document" +
                "?applicationFinanceId=" + applicationFinanceId;

        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/finance-document" +
                "?applicationFinanceId=" + applicationFinanceId;

        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getFileDetails(Long applicationFinanceId) {
        String url = applicationFinanceRestURL + "/finance-document/fileentry" +
                "?applicationFinanceId=" + applicationFinanceId;

        return getWithRestResult(url, FileEntryResource.class);
    }
}
