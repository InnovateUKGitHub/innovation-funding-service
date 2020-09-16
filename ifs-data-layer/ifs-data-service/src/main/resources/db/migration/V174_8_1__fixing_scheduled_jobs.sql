update profile p
set p.doi_notified_on = now()
inner join user u on p.id = u.profile_id
and p.doi_notified_on < '2020-04-06 00:00:00' OR p.doi_notified_on is NULL

update schedule_status set active = 0
where job_name in ('ASSESSOR_DOI_EXPIRY', 'DOCUSIGN_IMPORT');


