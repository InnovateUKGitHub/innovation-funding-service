package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Represents operations surrounding the use of Knowledge Base organisations in the system
 */
public interface KnowledgeBaseService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_KNOWLEDGE_BASE", description = "The System Registration user can get all Knowledge base organisations.")
    ServiceResult<List<String>> getKnowledegeBases();

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_KNOWLEDGE_BASE", description = "The System Registration user can get a Knowledge base organisation searched by id")
    ServiceResult<String> getKnowledegeBase(long id);
}
