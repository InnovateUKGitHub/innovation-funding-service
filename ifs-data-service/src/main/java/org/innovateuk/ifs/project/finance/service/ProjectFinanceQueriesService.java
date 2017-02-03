package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;

public interface ProjectFinanceQueriesService extends ThreadService<QueryResource, PostResource> {}
