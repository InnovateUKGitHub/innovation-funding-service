-- Update the value of the academic grant percentage for both the template competitions
SET @sector_template_name = "Template for the Sector competition type";
SET @programme_template_name = "Template for the Programme competition type";

UPDATE competition
SET academic_grant_percentage=100
WHERE name=@sector_template_name OR name=@programme_template_name AND non_ifs=0;