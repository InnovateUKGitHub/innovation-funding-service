package org.innovateuk.ifs.docusign.transactional;

import com.docusign.esign.client.ApiException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.innovateuk.ifs.docusign.resource.DocusignRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;

public interface DocusignService {

    ServiceResult<DocusignDocument> send(DocusignRequest request);
    ServiceResult<DocusignDocument> resend(long docusignDocumentId, DocusignRequest request);

    @PreAuthorize("hasAuthority('system_maintainer')")
    void downloadFileIfSigned() throws ApiException, IOException;

    String getDocusignUrl(String envelopeId, long userId, String name, String email, String redirect);

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<Void> importDocument(String envelopeId);
}
