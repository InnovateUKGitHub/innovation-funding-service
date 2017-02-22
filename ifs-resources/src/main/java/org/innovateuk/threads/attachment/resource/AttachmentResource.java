package org.innovateuk.threads.attachment.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AttachmentResource {

    private final Long id;
    private final String name;
    private final String mediaType;
    private final long sizeInBytes;

    @JsonCreator
    public AttachmentResource(@JsonProperty("id") Long id, @JsonProperty("name") String name,
                              @JsonProperty("mediaType") String mediaType, @JsonProperty("sizeInBytes") long sizeInBytes)
    {
        this.id = id;
        this.name = name;
        this.mediaType = mediaType;
        this.sizeInBytes = sizeInBytes;
    }
}
