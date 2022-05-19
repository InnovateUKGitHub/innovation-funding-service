-- IFS-11726: Add hash column to public content table to protect overview page for private competitions

ALTER TABLE public_content
    ADD COLUMN hash VARCHAR(255) DEFAULT NULL;