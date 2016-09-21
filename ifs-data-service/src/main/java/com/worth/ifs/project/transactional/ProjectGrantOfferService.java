package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.security.NotSecured;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Transactional and secure service for Project processing work - grant offer service.
 **/
public interface ProjectGrantOfferService {

    @NotSecured(value = "Lead Partner or project manager can download grant offer", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileAndContents> getSignedGrantOfferLetterFileAndContents(Long projectId);

    @NotSecured(value = "Lead Partner or project manager can download grant offer", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileAndContents> getGrantOfferLetterFileAndContents(Long projectId);

    @NotSecured(value = "Lead Partner or project manager can download grant offer", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileAndContents> getAdditionalContractFileAndContents(Long projectId);

    @NotSecured(value = "Partner can view grant offer Letter", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileEntryResource> getSignedGrantOfferLetterFileEntryDetails(Long projectId);

    @NotSecured(value = "Partner can view grant offer Letter", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileEntryResource> getGrantOfferLetterFileEntryDetails(Long projectId);

    @NotSecured(value = "Partner can view grant offer Letter", mustBeSecuredByOtherServices = false)
    public ServiceResult<FileEntryResource> getAdditionalContractFileEntryDetails(Long projectId);

    @NotSecured(value = "Project Manager and Lead Partner can can upload grant offer Letter", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> createSignedGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "Project Manager and Lead Partner can can upload grant offer Letter", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> createGrantOfferLetterFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "Project Manager and Lead Partner can can upload additional contract Letter", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> createAdditionalContractFileEntry(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "Project Manager and Lead Partner can update grant offer Letter", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateSignedGrantOfferLetterFile(Long projectId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

}
