package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

/**
 * Facilitates CRUD operations on overhead calculation document file entries for web services.
 */
@Service
public class OverheadFileRestServiceImpl extends BaseRestService implements OverheadFileRestService {
    private String restUrl = "/overheadcalculation";

    @Override
    public RestResult<FileEntryResource> addOverheadCalculationFile(Long overheadId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = restUrl + "/overheadCalculationDocument?overheadId=" + overheadId + "&filename=" + originalFilename;
        return postWithRestResult(url, file, createFileUploadHeader(contentType,  contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<FileEntryResource> updateOverheadCalculationFile(Long overheadId, String contentType, long contentLength, String originalFilename, byte[] file) {
        String url = restUrl + "/overheadCalculationDocument?overheadId=" + overheadId + "&filename=" + originalFilename;
        return postWithRestResult(url, file, createFileUploadHeader(contentType,  contentLength), FileEntryResource.class);
    }

    @Override
    public RestResult<Void> removeOverheadCalculationFile(Long overheadId) {
        String url = restUrl + "/overheadCalculationDocument?overheadId=" + overheadId;
        return deleteWithRestResult(url);
    }

    @Override
    public RestResult<ByteArrayResource> getOverheadFile(Long overheadId) {
        String url = restUrl + "/overheadCalculationDocument?overheadId=" + overheadId;
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<ByteArrayResource> getOverheadFileUsingProjectFinanceRowId(Long projectFinanceRowId) {
        String url = restUrl + "/projectOverheadCalculationDocument?overheadId=" + projectFinanceRowId;
        return getWithRestResult(url, ByteArrayResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getOverheadFileDetails(Long overheadId) {
        String url = restUrl + "/overheadCalculationDocumentDetails?overheadId=" + overheadId;
        return getWithRestResult(url, FileEntryResource.class);
    }

    @Override
    public RestResult<FileEntryResource> getOverheadFileDetailsUsingProjectFinanceRowId(Long overheadId) {
        String url = restUrl + "/projectOverheadCalculationDocumentDetails?overheadId=" + overheadId;
        return getWithRestResult(url, FileEntryResource.class);
    }

}
