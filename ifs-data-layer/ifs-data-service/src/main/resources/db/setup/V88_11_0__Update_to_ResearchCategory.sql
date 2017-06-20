-- Update to rename Technical feasibility research category to Feasibility studies.
UPDATE category
   SET `NAME` = "Feasibility studies"
 WHERE `NAME` = "Technical feasibility"
 AND type = "RESEARCH_CATEGORY";