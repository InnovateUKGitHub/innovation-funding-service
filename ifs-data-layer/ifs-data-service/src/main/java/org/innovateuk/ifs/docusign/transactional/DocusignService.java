package org.innovateuk.ifs.docusign.transactional;

import com.docusign.esign.client.ApiException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.innovateuk.ifs.docusign.resource.DocusignRequest;

import java.io.IOException;

public interface DocusignService {

    ServiceResult<DocusignDocument> send(DocusignRequest request);
    ServiceResult<DocusignDocument> resend(long docusignDocumentId, DocusignRequest request);

    void downloadFileIfSigned() throws ApiException, IOException;

    String getDocusignUrl(String envelopeId, long userId, String name, String email, String redirect);

    ServiceResult<Void> importDocument(String envelopeId);
}
