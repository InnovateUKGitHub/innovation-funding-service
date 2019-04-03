-- IFS-3998 Insert scheduled job for DOI expiry.
insert into schedule_status (job_name, version) VALUES ('ASSESSOR_DOI_EXPIRY', now());