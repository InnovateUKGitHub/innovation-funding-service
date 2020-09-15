package org.innovateuk.ifs.docusign.repository;

import org.innovateuk.ifs.docusign.domain.DocusignDocument;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DocusignDocumentRepository extends CrudRepository<DocusignDocument, Long> {

    Optional<DocusignDocument> findByEnvelopeId(String envelopeId);
}
