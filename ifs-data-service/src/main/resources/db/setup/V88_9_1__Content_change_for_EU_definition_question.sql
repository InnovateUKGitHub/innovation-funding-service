-- Content change for organisation size question. See INFUND-7554
UPDATE question SET
              `description` =  'To determine the level of funding you are eligible to receive please provide your business size using the <a href="http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm" target="_blank" rel="external">EU definition</a> for guidance.'
              WHERE `name`= "Organisation size";
