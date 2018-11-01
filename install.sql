CREATE TABLE `site` (
    `id` int NOT NULL AUTO_INCREMENT,
    `url` VARCHAR(256) NOT NULL,
    CHECK (LENGTH(`url`) >= 3),
    CONSTRAINT `XGSQL__site_PK_site` PRIMARY KEY (`id`)
);
ALTER TABLE `site` AUTO_INCREMENT=10000001;

CREATE TABLE `checkpage` (
    `id` int NOT NULL AUTO_INCREMENT,
    `site` int NOT NULL,
    `page` VARCHAR(256) NOT NULL,
    CHECK (LENGTH(`page`) >= 3),
    `timestamp` bigint NOT NULL,
    `source` blob NOT NULL,
    CONSTRAINT `XGSQL__checkpage_PK_checkpage` PRIMARY KEY (`id`),
    CONSTRAINT `XGSQL__checkpage_FK_site` FOREIGN KEY (`site`) REFERENCES `site` (`id`) ON DELETE restrict
);
ALTER TABLE `checkpage` AUTO_INCREMENT=100000001;

CREATE TABLE `variable` (
    `id` int NOT NULL AUTO_INCREMENT,
    `checkpage` int NOT NULL,
    `name` VARCHAR(256) NOT NULL,
    CHECK (LENGTH(`name`) >= 3),
    `value` blob NOT NULL,
    CONSTRAINT `XGSQL__variable_PK_variable` PRIMARY KEY (`id`),
    CONSTRAINT `XGSQL__variable_FK_checkpage` FOREIGN KEY (`checkpage`) REFERENCES `checkpage` (`id`) ON DELETE restrict
);
ALTER TABLE `variable` AUTO_INCREMENT=1000000001;

CREATE TABLE `marker` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `checkpage` int NOT NULL,
    `severity` smallint NOT NULL,
    `position` bigint,
    `eleTagName` VARCHAR(16),
    CHECK (LENGTH(`eleTagName`) >= 1),
    `eleTagNumber` int,
    `attribute` VARCHAR(64),
    CHECK (LENGTH(`attribute`) >= 1),
    `check` VARCHAR(8) NOT NULL,
    `desc` VARCHAR(256),
    CHECK (LENGTH(`check`) >= 1),
    CONSTRAINT `XGSQL__marker_PK_marker` PRIMARY KEY (`id`),
    CONSTRAINT `XGSQL__marker_FK_checkpage` FOREIGN KEY (`checkpage`) REFERENCES `checkpage` (`id`) ON DELETE restrict
);
ALTER TABLE `marker` AUTO_INCREMENT=5500000001;
