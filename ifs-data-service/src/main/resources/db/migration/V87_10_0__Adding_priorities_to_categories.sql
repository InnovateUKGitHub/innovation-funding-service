ALTER TABLE category
ADD COLUMN `priority` INT(11) NOT NULL DEFAULT 0;

-- INNOVATION SECTORS
UPDATE category SET priority = 0 WHERE NAME='Health and life sciences';
UPDATE category SET priority = 1 WHERE NAME='Materials and manufacturing';
UPDATE category SET priority = 2 WHERE NAME='Emerging and enabling technologies';
UPDATE category SET priority = 3 WHERE NAME='Infrastructure systems';

-- RESEARCH CATEGORIES
UPDATE category SET priority = 0 WHERE NAME='Technical feasibility';
UPDATE category SET priority = 1 WHERE NAME='Industrial research';
UPDATE category SET priority = 2 WHERE NAME='Experimental development';

-- INNOVATION AREAS
UPDATE category SET priority = 0 WHERE NAME='Advanced Materials';
UPDATE category SET priority = 1 WHERE NAME='Advanced Therapies';
UPDATE category SET priority = 2 WHERE NAME='Agri Productivity';
UPDATE category SET priority = 3 WHERE NAME='Bioscience';
UPDATE category SET priority = 4 WHERE NAME='Creative economy';
UPDATE category SET priority = 5 WHERE NAME='Cyber Security';
UPDATE category SET priority = 6 WHERE NAME='Data';
UPDATE category SET priority = 7 WHERE NAME='Design';
UPDATE category SET priority = 8 WHERE NAME='Digital Manufacturing';
UPDATE category SET priority = 9 WHERE NAME='Early Stage Manufacturing';
UPDATE category SET priority = 10 WHERE NAME='Earth Observation';
UPDATE category SET priority = 11 WHERE NAME='Electronics, Sensors and photonics';
UPDATE category SET priority = 12 WHERE NAME='Emerging Tech and Industries';
UPDATE category SET priority = 13 WHERE NAME='Energy Systems';
UPDATE category SET priority = 14 WHERE NAME='Enhanced Food Quality';
UPDATE category SET priority = 15 WHERE NAME='Infrastructure';
UPDATE category SET priority = 16 WHERE NAME='Internet of Things';
UPDATE category SET priority = 17 WHERE NAME='Manufacturing Readiness';
UPDATE category SET priority = 18 WHERE NAME='Medicines Technology';
UPDATE category SET priority = 19 WHERE NAME='Nuclear';
UPDATE category SET priority = 20 WHERE NAME='Offshore Renewable Energy';
UPDATE category SET priority = 21 WHERE NAME='Open';
UPDATE category SET priority = 22 WHERE NAME='Precision Medicine';
UPDATE category SET priority = 23 WHERE NAME='Resource efficiency';
UPDATE category SET priority = 24 WHERE NAME='Robotics and AS';
UPDATE category SET priority = 25 WHERE NAME='Transport Systems';
UPDATE category SET priority = 26 WHERE NAME='Urban living';
UPDATE category SET priority = 27 WHERE NAME='User Experience';

