-- IFS-5331 Update academic viability checks to use new process name

UPDATE `process` SET `event` = 'viability-not-applicable' WHERE `event` = 'organisation-is-academic';