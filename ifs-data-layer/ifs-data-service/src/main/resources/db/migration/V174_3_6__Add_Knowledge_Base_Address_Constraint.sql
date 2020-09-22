ALTER TABLE knowledge_base
ADD CONSTRAINT `knowledge_base_to_address_fk` FOREIGN KEY (address_id) REFERENCES address (id);