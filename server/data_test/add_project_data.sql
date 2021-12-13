--1/Marche Loi CLimat

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Marche pour une vraie loi Climat','Le projet de Loi Climat déposé par le gouvernement est loin d\'être à la hauteur de l\'urgence écologique. Pourtant, c\'est le dernier texte législatif du quinquennat consacré à l\'environnement. Le 28 mars, à la veille de l\'entrée du texte de loi à l\'Assemblée Nationale, nous nous mobilisons pour exiger une loi ambitieuse et défendre les mesures proposées par les 150 membres de la Convention citoyenne pour le Climat.','https://alternatiba.eu/wp-content/uploads/2021/03/Instagram-Post-carr%C3%A9_Marche-28-mars.jpg','https://vraieloiclimat.fr/28mars/','Y');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(1,'2021-03-28',NULL,'A travers toute la France','P');

INSERT INTO Project_area (project, area)
SELECT 1, id
FROM Area WHERE name IN ('Ecologie');

INSERT INTO Project_category (project, category)
SELECT 1, id
FROM Category WHERE name IN ('Action sociale');





--2/Salon workspace

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Le salon de l\'aménagement au travail : Workspace Paris','Nous savons qu\'aujourd\'hui les conditions dans lesquelles nous travaillons prennent de plus en plus d\'importance, et que l\'environnement est un facteur important pour la productivité d\'un salarié. En effet, plus un salarié se sent bien dans son entreprise, plus il va proposer de solutions et va s\'impliquer davantage. C\'est pourquoi il est important pour une entreprise d\'avoir des openspaces, des locaux, et des salles de détente bien aménagés. Le choix de la décoration et du mobilier rentre donc en jeu pour mettre à l\'aise les équipes.','https://boqa.fr/modules//smartblog/views/img/23-single-default.jpg','https://boqa.fr/fr/smartblog/23_le-salon-de-l-amenagement-au-travail-workspac.html','N');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(2,'2018-04-08',NULL,'Paris - Porte de Versailles - Pavillon 1','P'),
(2,'2018-04-09',NULL,'Paris - Porte de Versailles - Pavillon 1','P'),
(2,'2018-04-10',NULL,'Paris - Porte de Versailles - Pavillon 1','P');

INSERT INTO Project_area (project, area)
SELECT 2, id
FROM Area WHERE name IN ('Travail');

INSERT INTO Project_category (project, category)
SELECT 2, id
FROM Category WHERE name IN ('Salon/Exposition');





--3/Lac des Cygnes

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Le Lac des Cygnes par l\'opéra national de Russie','Ballet le plus joué au monde, Le Lac des Cygnes est de retour en 2021 pour une grande tournée en France et en Europe. A l\'image des années précédentes, une nouvelle compagnie russe présentera cet intemporel chef-d\'œuvre classique. Ce ballet nous plonge dans la folle histoire d\'amour du Prince Siegfried et de la Princesse Odette. Cette dernière est malheureusement prisonnière du célèbre sort du magicien Rothbart : elle se transforme en cygne le jour et redevient femme la nuit. Seule la promesse d\'un amour éternel pourra la libérer de cet ensorcellement. Créé en 1875 par le compositeur russe Piotr Tchaïkovsky, c\'est en 1895 avec la reprise du chorégraphe Marius Petipa que Le Lac des Cygnes deviendra le plus grand succès classique de tous les temps.','https://www.franceconcert.fr/wp-content/uploads/2019/02/Lac_des_cygnes.jpg','https://www.franceconcert.fr/fr/spectacle/le-lac-des-cygnes/','Y');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(3,'2021-05-22','15:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-05-22','20:30:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-05-23','16:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-05-25','20:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-05-26','20:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-05-27','20:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-10-23','16:00:00','PARIS - Le Palais des Congrès - Porte Maillot','P'),
(3,'2021-10-23','20:30:00','PARIS - Le Palais des Congrès - Porte Maillot','P');

INSERT INTO Project_area (project, area)
SELECT 3, id
FROM Area WHERE name IN ('Spectacle vivant');

INSERT INTO Project_category (project, category)
SELECT 3, id
FROM Category WHERE name IN ('Divertissement');





--4/Paris Games Week

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Paris Games Week','La Paris Games Week revient pour une nouvelle édition au Parc des expositions de la Porte de Versailles. Entièrement dédié à l\'univers du jeu vidéo, ce salon qui reçoit chaque année davantage de visiteurs, réunit les joueurs, les passionnés mais aussi les professionnels du milieu. Rendez-vous incontournable des gamers, la PGW est l\'occasion de découvrir des nouveautés, d\'assister à des conférences et des sessions de gameplay, de participer à des tournois et d\'avoir accès à des informations exclusives !','https://pro.parisinfo.com/var/otcp/sites/images/node_43/node_51/node_534/paris-games-week-|-630x405-|-%C2%A9-dr/15136206-1-fre-FR/Paris-Games-Week-|-630x405-|-%C2%A9-DR_block_media_big.png','https://pro.parisinfo.com/sortie-paris/142231/paris-games-week','Y');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(4,'2021:10:31',NULL,'Paris expo Porte de Versailles - VIPARIS','P');

INSERT INTO Project_area (project, area)
SELECT 4, id
FROM Area WHERE name IN ('Jeux');

INSERT INTO Project_category (project, category)
SELECT 4, id
FROM Category WHERE name IN ('Festival/Célébration');





--5/Atelier maroquinerie

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Créez votre sac en cuir','Apprenez pas à pas les secrets de la maroquinerie. Diane vous proposera trois modèles de sacs : le sac seau et pochette enveloppe et un modèle qui change toute les saisons. Ensuite, le cuir ! Vous choisirez, parmi les pièces de cuirs prédécoupées et travaillées au préalable, les textures et couleurs qui vous ressemblent. C\'est une expérience très sensitive ! Enfin l\'assemblage, toutes les coutures sont faites à la main : la couture sellier, celle-là même employée chez Hermès. Personnalisez votre sac avec des rivets ou pompons et signez-le à l\'intérieur pour repartir avec un sac unique et de superbes souvenirs.','https://d2armxes0yqb0y.cloudfront.net/eyJidWNrZXQiOiJ3ZWNhbmRvby1pbWctcHVibGljIiwia2V5IjoiWHBTZ0ZDNkMxZ25UaDRkQmlqcmFEN1pwUWRmVVhLSmxFem12eVFTYi5qcGVnIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjoxNjAsImhlaWdodCI6MTYwLCJmaXQiOiJjb3ZlciJ9LCJqcGVnIjp7InByb2dyZXNzaXZlIjp0cnVlLCJxdWFsaXR5Ijo5MH19fQ==','https://wecandoo.fr/atelier/initation-maroquinerie-diane-sac','Y');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(5,'2022-00-00',NULL,'Rue Daguerre - Paris','P');

INSERT INTO Project_area (project, area)
SELECT 5, id
FROM Area WHERE name IN ('Mode');

INSERT INTO Project_category (project, category)
SELECT 5, id
FROM Category WHERE name IN ('Atelier/Formation');





--6/Taverne Médiévale

INSERT INTO Project (title, summary, picture, website, child_friendly) VALUES
('Remontez le temps pour vous amuser comme au Moyen-Age','A la Taverne Médiévale vous avez la possibilité de voyager dans le temps pendant toute une soirée et de revivre les fêtes atypiques du Moyen-Age. La Taverne Médiévale est depuis 15 ans un lieu emblématique où vous y trouverez musiciens, artisans, jongleurs et cracheurs de feu. Tous les 2ème et 4ème jeudis du mois, la Taverne Médiévale reconstitue des moments de l\'histoire dans un univers fantastique. Les soirées à la Taverne sont rythmées de danses, spectacles et jeux tout en vous permettant de vous restaurer avec des plats médiévaux agrémentés de vins traditionnels. La Taverne Médiévale fait preuve d\'une communication originale, sortant de l\'ordinaire, en proposant des soirées à thèmes sous forme d\'histoire du Moyen-Age et animées de jeux de piste et d\'épreuves.','https://sortirdelordinaire.files.wordpress.com/2014/11/blog-3.png?w=1250&h=&crop=1','https://sortirdelordinaire.wordpress.com/2014/11/30/la-taverne-medieval/','N');

INSERT INTO Meeting (project_id, meeting_date, meeting_time, place, format) VALUES
(6,'2022-00-00',NULL,'50 Rue Saint-Sabin – 75011 Paris','P');

INSERT INTO Project_area (project, area)
SELECT 6, id
FROM Area WHERE name IN ('Histoire');

INSERT INTO Project_category (project, category)
SELECT 6, id
FROM Category WHERE name IN ('Expérience');





('Exposition sur le Big Band à la Cité des Sciences'),
('Cyranno de Bergerac : la nouvelle production de la Comédie-Française'),
('Réduire la circulation routière à Paris'),
('Semaine culinaire à Paris : manger à la table de chefs étoilés à moitié prix'),
('Edward Hopper de retour au musée de l\'Orangerie'),
('Conférence sur la transformation digitale par Gabriel Dabi-Schwebel'),
('Balade autour des étangs de la forêt de Rambouillet'),
('Créez votre sac en cuir'),
('"Ce soir j\'ouvre la boîte", nouveau spectacle de magie signé Duvivier'),
('Marché aux timbres et aux cartes postales'),
('Méditation contre le stress et l\'anxiété - Séance Gratuite'),
('Le quatuor Tetzlaff célèbre Mozart et Sibelius'),
('Louis de Funès : l\'exposition'),
('"Adieu les cons" en avant-première au Pathé Levallois avec Albert Dupontel'),
('Projet "Mille Arbres" Porte Maillot');
