-- IFS-4459 - Project Documents - None of the competitions should have configured any Project Documents so far. If configured, then delete those.
DELETE FROM document_config_file_type;
DELETE FROM document_config;
