-- IFS-4959 - remove dashboard URLs from Roles themselves

ALTER TABLE role DROP COLUMN url;

