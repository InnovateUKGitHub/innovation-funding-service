ALTER TABLE project ADD CONSTRAINT fk_project_address FOREIGN KEY (address) REFERENCES address(id);
