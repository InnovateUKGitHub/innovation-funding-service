package org.innovateuk.ifs.docusign.transactional;

import com.docusign.esign.client.ApiException;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.innovateuk.ifs.docusign.resource.DocusignRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;

public interface DocusignService {
    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<DocusignDocument> send(DocusignRequest request);

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<DocusignDocument> resend(long docusignDocumentId, DocusignRequest request);

    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "DOWNLOAD_FILE_IF_SIGNED", description = "System maintainer will import files on schedule" )
    void downloadFileIfSigned() throws ApiException, IOException;

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "GET_DOCUSIGN_URL", description = "Applicants can get the url to access docusign" )
    String getDocusignUrl(String envelopeId, long userId, String name, String email, String redirect);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "IMPORT_DOCUMENT", description = "Applicants can request their signed documents" )
    ServiceResult<Void> importDocument(String envelopeId);
}
