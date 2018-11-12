-- IFS-4737
-- add missing categories to dev environments (rows already present in prod/uat)

INSERT IGNORE INTO `category` (`id`, `name`, `type`, `parent_id`, `description`, `priority`)
VALUES
	(60, 'Non-metallics', 'INNOVATION_AREA', 2, 'Manufacture, manipulation, synthesis and characterisation of non-metallics, such as glass.', 48),
	(61, 'Resource efficiency', 'INNOVATION_AREA', 2, 'Development of technologies or methods to increase the efficiency of material use, extend the life of manufactured goods or materials, and recover value at the end of life.', 52),
	(62, 'Surface engineering', 'INNOVATION_AREA', 2, 'Chemical or physical modification of a surface to achieve a desired behaviour. Improved or new materials to meet technical or regulatory challenges.', 55),
	(63, 'Electronics manufacturing', 'INNOVATION_AREA', 2, 'Manufacture of materials, components and assemblies for use in electronic devices, including in sensors and lasers.', 58),
	(64, 'Sensor and instrument design or manufacture', 'INNOVATION_AREA', 2, 'Design or manufacture of sensors, instruments and other devices used in manufacturing.', 62),
	(65, 'Material recovery and treatment', 'INNOVATION_AREA', 2, 'Valorisation, remanufacture and reuse of waste materials through novel extraction and processing technologies.', 65);