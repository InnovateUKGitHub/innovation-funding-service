package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;

public interface ProjectFinanceNotesService extends ThreadService<NoteResource, PostResource> {}
