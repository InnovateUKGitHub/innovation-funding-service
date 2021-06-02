INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Kydd Building', '40 Bell Street', '', 'Dundee', 'DD1 1HG', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Abertay University');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Visualisation Centre', 'Penglais', '', 'Aberystwyth', 'SY23 3BF', 'Dyfed', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Aberystwyth University (Prifysgol Aberystwyth)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('College Road', '', '', 'Bangor', 'LL57 2DG', 'Gwynedd', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Bangor University (Prifysgol Bangor)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('7 Queens Road', '', '', 'Belfast', 'BT3 9FQ', 'Gwynedd', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Belfast Metropolitan College');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Western Avenue', '', '', 'Cardiff', 'CF5 2YB', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Cardiff Metropolitan University (Prifysgol Metropolitan Caerdydd)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Friary House', 'Greyfriars Road', '', 'Cardiff', 'CF24 0DE', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Cardiff University (Prifysgol Caerdydd)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Grove Park Road', '', '', 'Wrexham', 'LL12 7AB', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Coleg Cambria');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Sighthill Campus', '9 Sighthill Court', '', 'Edinburgh', 'EH11 4BN', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Edinburgh Napier University');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('City Campus', '70 Cowcaddens Road', '', 'Glasgow', 'G4 0BA', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Glasgow Caledonian University');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('167 Renfrew Street', '', '', 'Glasgow', 'G3 6RQ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Glasgow School of Art');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Plas Coch', 'Mold Road', '', 'Wrexham', 'LL11 2AW', 'Clwyd', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Glyndŵr University (Prifysgol Glyndŵr)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Llandudno Road', 'Rhos On Sea', '', 'Colwyn Bay', 'LL28 4HZ', 'Clwyd', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Grwp Llandrillo Menai');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Riccarton', '', '', 'Currie', 'EH14 4AS', 'Midlothian', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Heriot-Watt University');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('78-80 Strand Road', '', '', 'Londonderry', 'BT48 7AL', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'North West Regional College');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Queen Margaret University Drive', '', '', 'Musselburgh', 'EH21 6UU', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Queen Margaret University, Edinburgh');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('University Road', '', '', 'Belfast', 'BT7 1NN', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Queen\'s University of Belfast');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Garthdee House', 'Garthdee Road', '', 'Aberdeen', 'AB10 7QB', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Robert Gordon University');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('West Common', '', '', 'Harpenden', 'AL5 2JQ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Rothamsted Research Limited');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('25 Castle Street', '', '', 'Lisburn', 'BT27 4SU', 'County Antrim', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'South Eastern Regional College');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Circular Road', '', '', 'Dungannon', 'BT71 6BQ', 'County Tyrone', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'South West College');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('22 Castlewellan Road', '', '', 'Banbridge', 'BT32 4AY', 'County Down', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Southern Regional College');

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('King\'s Buildings', 'West Mains Road', '', 'Edinburgh', 'EH9 3JG', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'SRUC (Scotland\'s Rural College)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Singleton Park', '', '', 'Swansea', 'SA2 8PP', 'West Glamorgan', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Swansea University (Prifysgol Abertawe)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Regent Walk', '', '', 'Aberdeen', 'AB24 3FX', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Aberdeen');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Perth Road', '', '', 'Dundee', 'DD1 4HN', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Dundee');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Old College', 'South Bridge', '', 'Edinburgh', 'EH8 9YL', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Edinburgh');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('University Avenue', '', '', 'Glasgow', 'G12 8QQ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Glasgow');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Treforest', '', '', 'Pontypridd', 'CF37 1DL', 'Mid Glamorgan', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of South Wales/Prifysgol De Cymru');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('College Gate', 'North Street', '', 'St. Andrews', 'KY16 9AJ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of St Andrews');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('', '', '', 'Stirling', 'FK9 4LA', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Stirling');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Mccance Building', '16 Richmond Street', '', 'Glasgow', 'G1 1XQ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Strathclyde');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('12b Ness Walk', '', '', 'Inverness', 'IV3 5SQ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of The Highlands and Islands');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('High Street', '', '', 'Paisley', 'PA1 2BE', 'Renfrewshire', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of the West of Scotland');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Cromore Road', '', '', 'Coleraine', 'BT52 1SA', 'County Londonderry', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Ulster');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('King Edward Vii Avenue', '', '', 'Cardiff', 'CF10 3NS', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Wales (Prifysgol Cymru)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('College Road', '', '', 'Carmarthen', 'SA31 3EP', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University of Wales Trinity Saint David (Prifysgol Cymru Y Drindod Dewi Sant)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Lambeth Palace', 'Lambeth Palace Road', '', 'London', 'SE1 7JU', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Archbishop of Canterbury, The');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Berkhamsted', '', '', 'Hertfordshire', 'HP4 1NS', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'ASHRIDGE STRATEGIC MANAGEMENT CENTRE');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('23 Kensington Square', '', '', 'London', 'W8 5HN', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Heythrop College');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Peninsular House', '36 Monument Street', '', 'London', 'EC3R 8LJ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'London Institute of Banking and Finance, The');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('20 Cavendish Square', '', '', 'London', 'W1G 0RN', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'ROYAL COLLEGE OF NURSING OF THE UNITED KINGDOM (THE)');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('100 Renfrew Street', '', '', 'Glasgow', 'G2 3DB', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Royal Conservatoire of Scotland');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Polaris House', 'North Star Avenue', '', 'Swindon', 'SN2 1SZ', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'Science And Technology Facilities Council');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);

INSERT INTO address (address_line1, address_line2, address_line3, town, postcode, county, country) VALUES ('Avon House', '275-287 Borough High Street', '', 'London', 'SE1 1JE', '', 'United Kingdom');
SET @knowledge_base_id = (SELECT id FROM knowledge_base WHERE name = 'University College of Osteopathy');
UPDATE knowledge_base SET address_id = LAST_INSERT_ID() WHERE (id = @knowledge_base_id);