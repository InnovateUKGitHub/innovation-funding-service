-- Updating "Emerging and enabling technologies" Sector Innovation Areas list
UPDATE `category` SET `NAME`='Space technology', `description`='Technologies specifically designed for operating in space environments.'
WHERE `NAME`='Creative economy';

UPDATE `category` SET `NAME`='Robotics and Autonomous Systems', `description`='Including autonomous transport.'
WHERE `NAME`='Cyber Security';

UPDATE `category` SET `NAME`='Emerging Technology', `description`='A technology that is progressing in, or has recently emerged from, the research base.'
WHERE `NAME`='Data';

UPDATE `category` SET `NAME`='Digital Technology', `description`='A cross-cutting enabling digital technology, such as “Internet of Things”.'
WHERE `NAME`='Design';

UPDATE `category` SET `NAME`='Digital Industries', `description`='Digital enabled vertical industries such as; FinTech, EdTech...'
WHERE `NAME`='Earth Observation';

UPDATE `category` SET `NAME`='Creative Industries', `description`='Technology in the creative sectors such as Film, Music...'
WHERE `NAME`='Electronics, Sensors and photonics';

UPDATE `category` SET `NAME`='Electronics, Sensors & Photonics', `description`='Including compound semiconductors.'
WHERE `NAME`='Emerging Tech and Industries';

UPDATE `category` SET `NAME`='Satellite Applications', `description`='Applications and technology that use space derived data for on the ground effect.'
WHERE `NAME`='Internet of Things';

DELETE FROM `category` WHERE `NAME`='Open';
DELETE FROM `category` WHERE `NAME`='Robotics and AS';
DELETE FROM `category` WHERE `NAME`='User Experience';