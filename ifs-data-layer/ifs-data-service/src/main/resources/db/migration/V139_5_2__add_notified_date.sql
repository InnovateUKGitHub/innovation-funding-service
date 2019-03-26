-- IFS-3998 Adding date to record notifying user of DOI expiry.
ALTER TABLE profile
  ADD COLUMN `doi_notified_on` datetime;