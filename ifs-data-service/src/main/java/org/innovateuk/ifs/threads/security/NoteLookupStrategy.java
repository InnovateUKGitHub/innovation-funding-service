package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.threads.resource.NoteResource;

public class NoteLookupStrategy extends ThreadLookupStrategy<Note, NoteResource, NoteMapper> {}