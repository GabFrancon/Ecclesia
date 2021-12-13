CREATE DATABASE e_cclesia CHARACTER SET 'utf8';

USE e_cclesia;

CREATE TABLE User (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
firstname VARCHAR(30) NOT NULL,
lastname VARCHAR(30),
gender CHAR(1),
birth DATE,
email VARCHAR(80) NOT NULL,
password VARCHAR(30),
location VARCHAR(40) NOT NULL,
profile_picture TEXT,
facebook_id VARCHAR(15),
PRIMARY KEY (id),
FULLTEXT ind_full_name (firstname, lastname),
UNIQUE ind_u_email (email),
INDEX ind_password (password)
)ENGINE=INNODB;

CREATE TABLE Project (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
title VARCHAR(120) NOT NULL,
summary TEXT,
picture TEXT,
website TEXT NOT NULL,
likes INT NOT NULL DEFAULT 0,
child_friendly CHAR(1),
PRIMARY KEY (id),
FULLTEXT ind_full_title (title),
FULLTEXT ind_full_summary (summary)
)ENGINE=INNODB;

CREATE TABLE Meeting (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
project_id INT UNSIGNED NOT NULL,
meeting_date DATE NOT NULL,
meeting_time TIME,
place VARCHAR(120),
format CHAR(1),
PRIMARY KEY (id),
FOREIGN KEY (project_id) REFERENCES Project(id),
FULLTEXT ind_full_place (place)
)ENGINE=INNODB;

CREATE TABLE Area (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
PRIMARY KEY (id),
UNIQUE  ind_u_area (name)
)ENGINE=INNODB;

INSERT INTO Area (name) VALUES
('Energie'),('Agriculture'),('Travail'),('Commerce'),('Technologie'),
('Ecologie'),('Sécurité'),('International'),('Santé'),('Education'),('Urbanisme'),
('Musique'),('Arts plastiques'),('Arts décoratifs'),('Audiovisuel'),
('Spectacle vivant'),('Littérature'),('Cuisine'),('Mode'),('Math/Informatique'),
('Physique'),('Nature'),('Sciences sociales'),('Histoire'),
('Spiritualité'),('Jeux'),('Sport');

CREATE TABLE Category (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
PRIMARY KEY (id),
UNIQUE  ind_u_category (name)
)ENGINE=INNODB;

INSERT INTO Category (name) VALUES
('Conférence'),('Aménagement'),('Festival/Célébration'),('Divertissement'),
('Rencontre'),('Salon/Exposition'),('Action sociale'),('Compétition'),
('Excursion'),('Atelier/Formation'),('Expérience');

CREATE TABLE Support (
id INT UNSIGNED NOT NULL AUTO_INCREMENT,
name VARCHAR(30) NOT NULL,
PRIMARY KEY (id),
UNIQUE ind_u_support (name)
)ENGINE=INNODB;

INSERT INTO Support (name) VALUES
('Participer'),('Faire un don'),('S\'inscrire'),('Partager sur les réseaux'),
('Signer la pétition'),('Acheter');

CREATE TABLE User_pref_area (
user INT UNSIGNED NOT NULL,
area INT UNSIGNED NOT NULL,
FOREIGN KEY (user) REFERENCES User(id),
FOREIGN KEY (area) REFERENCES Area(id),
PRIMARY KEY (user, area)
)ENGINE=INNODB;

CREATE TABLE User_pref_category (
user INT UNSIGNED NOT NULL,
category INT UNSIGNED NOT NULL,
FOREIGN KEY (user) REFERENCES User(id),
FOREIGN KEY (category) REFERENCES Category(id),
PRIMARY KEY (user, category)
)ENGINE=INNODB;

CREATE TABLE User_like (
user INT UNSIGNED NOT NULL,
project INT UNSIGNED NOT NULL,
like TINYINT,
FOREIGN KEY (user) REFERENCES User(id),
FOREIGN KEY (project) REFERENCES Project(id),
PRIMARY KEY (user, project)
)ENGINE=INNODB;

CREATE TABLE User_participation (
user INT UNSIGNED NOT NULL,
project INT UNSIGNED NOT NULL,
FOREIGN KEY (user) REFERENCES User(id),
FOREIGN KEY (project) REFERENCES Project(id),
PRIMARY KEY (user, project)
)ENGINE=INNODB;

CREATE TABLE Project_area (
project INT UNSIGNED NOT NULL,
area INT UNSIGNED NOT NULL,
FOREIGN KEY (project) REFERENCES Project(id),
FOREIGN KEY (area) REFERENCES Area(id),
PRIMARY KEY (project, area)
)ENGINE=INNODB;

CREATE TABLE Project_category (
project INT UNSIGNED NOT NULL,
category INT UNSIGNED NOT NULL,
FOREIGN KEY (project) REFERENCES Project(id),
FOREIGN KEY (category) REFERENCES Category(id),
PRIMARY KEY (project, category)
)ENGINE=INNODB;

CREATE TABLE Project_support (
project INT UNSIGNED NOT NULL,
support INT UNSIGNED NOT NULL,
FOREIGN KEY (project) REFERENCES Project(id),
FOREIGN KEY (support) REFERENCES Support(id),
PRIMARY KEY (project, support)
)ENGINE=INNODB;

CREATE TABLE Friendship (
applicant INT UNSIGNED NOT NULL,
acceptor INT UNSIGNED NOT NULL,
friend_request_accepted BOOLEAN,
FOREIGN KEY (applicant) REFERENCES User(id),
FOREIGN KEY (acceptor) REFERENCES User(id),
PRIMARY KEY (applicant, acceptor)
)ENGINE=INNODB;


CREATE TABLE Project_recommandation (
project INT UNSIGNED NOT NULL,
user INT UNSIGNED NOT NULL,
grade DOUBLE,
FOREIGN KEY (project) REFERENCES Project(id),
FOREIGN KEY (user) REFERENCES User(id),
PRIMARY KEY (project, user)
)ENGINE=INNODB;
