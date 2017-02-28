-- Updating "Emerging and enabling technologies" Sector Innovation Areas list
UPDATE `category` SET `NAME`='Space technology', `description`='Technologies specifically designed for operating in space environments.' WHERE `id`='12';
UPDATE `category` SET `NAME`='Robotics and Autonomous Systems', `description`='Including autonomous transport.' WHERE `id`='10';
UPDATE `category` SET `NAME`='Emerging Technology', `description`='A technology that is progressing in, or has recently emerged from, the research base.' WHERE `id`='5';
UPDATE `category` SET `NAME`='Digital Technology', `description`='A cross-cutting enabling digital technology, such as “Internet of Things”.' WHERE `id`='6';
UPDATE `category` SET `NAME`='Digital Industries', `description`='Digital enabled vertical industries such as; FinTech, EdTech...' WHERE `id`='7';
UPDATE `category` SET `NAME`='Creative Industries', `description`='Technology in the creative sectors such as Film, Music...' WHERE `id`='8';
UPDATE `category` SET `NAME`='Electronics, Sensors & Photonics', `description`='Including compound semiconductors.' WHERE `id`='9';
UPDATE `category` SET `NAME`='Satellite Applications', `description`='Applications and technology that use space derived data for on the ground effect.' WHERE `id`='11';
DELETE FROM `category` WHERE `id`='13';
DELETE FROM `category` WHERE `id`='14';
DELETE FROM `category` WHERE `id`='15';