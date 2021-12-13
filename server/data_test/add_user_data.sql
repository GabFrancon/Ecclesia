--add users
INSERT INTO User (firstname, lastname, gender, birth, email, location, password,profile_picture) VALUES
('Juda','Nana','H','1988-11-22','juda.nana@ecclesia.com','Paris','juda','https://www.webstickersmuraux.com/fr/img/mag236-png/folder/products-detalle-png/stickers-muraux-enfants-bebes-ananas.png'),
('John','Doeuf','H','1996-03-18','john.doeuf@ecclesia.com','Paris','john','https://ih1.redbubble.net/image.1142462282.2392/st,small,507x507-pad,600x600,f8f8f8.u1.jpg'),
('Gérard','Mensoif','H','1965-11-05','gerard.mensoif@ecclesia.com','Paris','gerard','https://ih1.redbubble.net/image.2166061289.2400/ur,water_bottle_metal_lid_on,square,600x600.2.jpg'),
('Maude','Zarella','F','2002-09-12','maude.zarella@ecclesia.com','Paris','maude','https://img3.stockfresh.com/files/l/lineartestpilot/m/48/3110292_stock-photo-cartoon-cheese-with-face.jpg'),
('Lara','Leuze','F','2005-06_28','lara.leuze@ecclesia.com','Paris','lara','https://pbs.twimg.com/profile_images/908190284781178880/e5ym_E9b_400x400.jpg'),
('Edith','Avuleur','F','1992-07-15','edith.avuleur@ecclesia.com','Paris','edith','https://i.pinimg.com/originals/b4/f2/d4/b4f2d441e66ecf6dd10f3f33873b50a0.jpg'),
('Debby','Scott','F','1999-10-22','debby.scott@ecclesia.com','Paris','debby','https://st3.depositphotos.com/9034578/35073/v/600/depositphotos_350735296-stock-illustration-cartoon-character-of-chocolate-chips.jpg'),
('Gordon','Zola','H','1975-03-01','gordon.zola@ecclesia.com','Paris','gordon','https://ih1.redbubble.net/image.1045246354.5079/pp,504x498-pad,600x600,f8f8f8.jpg'),
('Alonzo','Bistrot','H','1993-04-24','alonzo.bistrot@ecclesia.com','Paris','alonzo','https://www.bar-le-88.fr/userfiles/27415/V3.png'),
('Omer','Dalors','H','2000-05-27','omer.dalors@ecclesia.com','Paris','omer','https://ih1.redbubble.net/image.537335200.1126/mp,504x498,matte,f8f8f8,t-pad,600x600,f8f8f8.u1.jpg'),
('Jean','Filmongillet','H','1959-12-12','jean.filmongillet@ecclesia.com','Paris','jean','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQZ8JhlBv4Ok1p6duVcgIqHlLEg6UC9UIae8g&usqp=CAU'),
('Marion','Nous','F','1988-01-06','marion.nous@ecclesia.com','Paris','marion','https://www.fr.clipproject.info/images/joomgallery/details/mariage_133/danser_dessin_gratuit_-_mariage_image_20160412_1211207001.png'),
('Jack','Ouche','H','1997-05_30','jack.ouche@ecclesia.com','Paris','jack','https://ih1.redbubble.net/image.1211498742.7701/aps,504x498,small,transparent-pad,600x600,f8f8f8.jpg'),
('Jessica','Serolle','F','2007-12-13','jessica.serolle@ecclesia.com','Paris','jessica','https://st2.depositphotos.com/1742172/6116/v/450/depositphotos_61163641-stock-illustration-cartoon-saucepan.jpg'),
('Guy','Tare','H','1950-07-10','guy.tare@ecclesia.com','Paris','guy','https://st3.depositphotos.com/9034578/36501/v/600/depositphotos_365012152-stock-illustration-happy-face-of-rounded-bandage.jpg'),
('Tom','Ate','H','1990-01-31','tom.ate@ecclesia.com','Paris','tom','https://thumbs.dreamstime.com/b/tomate-de-bande-dessin%C3%A9e-58834165.jpg');


('Jean','Sérien','H','2000-02-02','jean.serien@ecclesia.com','Paris','jean'),
('Douglas','Alavanille','H','2000-09-11','douglas.alavanille@ecclesia.com','Paris','douglas'),
('Yves','Atrovite','H','1968-02-27','yves.atrovite@ecclesia.com','Paris','yves'),
('Medhi','Caman','H','2002-10-01','mehdi.caman@ecclesia.com','Paris','mehdi'),
('Robby','Naidauchaude','F','1998-04-15','robby.naidauchaude@ecclesia.com','Paris','robby'),
('Gille','Etparbal','H','1975-07-09','gille.etparballe@ecclesia.com','Paris','gille'),
('Sacha','Touille','F','1991-06-30','sacha.touille@ecclesia.com','Paris','sacha');

--add friend relationship between some users
INSERT INTO Friendship (applicant, acceptor, friend_request_accepted) VALUES
(1,3,1),(1,4,0),(1,6,1),(1,12,1),(2,8,0),(2,3,1),(2,10,0),(2,16,1),
(2,23,0),(3,19,1),(3,18,1),(3,7,0),(4,10,1),(4,12,1), (4,13,0),(4,21,1),
(5,6,1),(5,7,1),(5,14,1),(5,18,1),(6,11,1),(6,23,0),(7,23,1),(8,18,0),
(9,15,1),(10,11,0),(16,17,1), (17,16,1),(17,12,1),(17,8,0),(16,22,0),
(15,21,1),(13,14,0),(19,20,1);


--Set preferences of first ten users
--1
INSERT INTO User_pref_area (user, area)
SELECT 1, id
FROM Area WHERE name IN ('Ecologie','Spectacle vivant','Audiovisuel','Jeux','Urbanisme','Technologie');

INSERT INTO User_pref_category (user, category)
SELECT 1, id
FROM Category WHERE name IN ('Expérience','Divertissement','Atelier/Formation');

--2
INSERT INTO User_pref_area (user, area)
SELECT 2, id
FROM Area WHERE name IN ('Ecologie','Spiritualité','Social','Histoire','Arts décoratifs');

INSERT INTO User_pref_category (user, category)
SELECT 2, id
FROM Category WHERE name IN ('Expérience','Action sociale','Salon/Exposition');

--3
INSERT INTO User_pref_area (user, area)
SELECT 3, id
FROM Area WHERE name IN ('Arts plastiques','Travail','Social','Commerce','Cuisine');

INSERT INTO User_pref_category (user, category)
SELECT 3, id
FROM Category WHERE name IN ('Divertissement','Aménagement','Exposition');

--4
INSERT INTO User_pref_area (user, area)
SELECT 4, id
FROM Area WHERE name IN ('Mode','Agriculture','Littérature','Sciences sociales','Histoire');

INSERT INTO User_pref_category (user, category)
SELECT 4, id
FROM Category WHERE name IN ('Action sociale','Rencontre','Conférence');

--5
INSERT INTO User_pref_area (user, area)
SELECT 5, id
FROM Area WHERE name IN ('Jeux','Santé','Spectacle vivant','Education','Sport','Sécurité');

INSERT INTO User_pref_category (user, category)
SELECT 5, id
FROM Category WHERE name IN ('Compétiton','Salon/Exposition','Excursion');

--6
INSERT INTO User_pref_area (user, area)
SELECT 6, id
FROM Area WHERE name IN ('Spiritualité','Urbanisme','Travail','Nature','Jeux');

INSERT INTO User_pref_category (user, category)
SELECT 6, id
FROM Category WHERE name IN ('Aménagement','Excursion','Expérience','Rencontre');

--7
INSERT INTO User_pref_area (user, area)
SELECT 7, id
FROM Area WHERE name IN ('Musique','Audiovisuel','Travail','Ecologie');

INSERT INTO User_pref_category (user, category)
SELECT 7, id
FROM Category WHERE name IN ('Divertissement','Action sociale','Comptétition');

--8
INSERT INTO User_pref_area (user, area)
SELECT 8, id
FROM Area WHERE name IN ('Spiritualité','Mode','Math/Informatique','Physique','Agriculture');

INSERT INTO User_pref_category (user, category)
SELECT 8, id
FROM Category WHERE name IN ('Festival/Célébration','Conférence','Compétition');

--9
INSERT INTO User_pref_area (user, area)
SELECT 9, id
FROM Area WHERE name IN ('Commerce','Mode','Nature','Sport','International','Musique');

INSERT INTO User_pref_category (user, category)
SELECT 9, id
FROM Category WHERE name IN ('Atelier/Formation','Conférence','Aménagement');

--10
INSERT INTO User_pref_area (user, area)
SELECT 10, id
FROM Area WHERE name IN ('Spiritualité','Sport','Education','Sécurité','Littérature');

INSERT INTO User_pref_category (user, category)
SELECT 10, id
FROM Category WHERE name IN ('Action sociale','Divertissement','Festival/Célébration');



--add user likes on some projects
INSERT INTO User_like (user_id, project_id) VALUES
(1,1,1),(2,6,1),(2,3,1),(3,1,0),(3,2,1),(3,3,1),(4,3,0),(4,5,0),(4,6,1),(5,1,1),(6,5,1),(6,3,0),
(7,1,1),(7,2,1),(8,4,1),(8,6,0),(9,1,1),(9,2,1),(9,4,0),(10,6,0),(10,3,0);
