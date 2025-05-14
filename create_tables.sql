-- Création de la base de données
CREATE DATABASE guerre_des_royaumes;
GO

USE guerre_des_royaumes;
GO

-- Création de la table joueur
CREATE TABLE joueur (
                        id_joueur INT PRIMARY KEY IDENTITY(1,1),
                        nom_joueur NVARCHAR(255) NOT NULL,
                        prenom_joueur NVARCHAR(255) NOT NULL,
                        pseudo_joueur NVARCHAR(255) NOT NULL UNIQUE,
                        motDePasse_joueur NVARCHAR(255) NOT NULL,
                        argent_joueur INT NOT NULL DEFAULT 100,
                        victoires_joueur INT NOT NULL DEFAULT 0,
                        defaites_joueur INT NOT NULL DEFAULT 0,
                        id_royaume INT,
                        id_coffre INT,
                        id_personnage INT
);

-- Création de la table royaume
CREATE TABLE royaume (
                         id_royaume INT PRIMARY KEY IDENTITY(1,1),
                         id_joueur INT NOT NULL,
                         FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE
);

-- Création de la table personnage_joueur
CREATE TABLE personnage_joueur (
                                   id_personnage INT PRIMARY KEY IDENTITY(1,1),
                                   id_joueur INT NOT NULL,
                                   FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE
);

-- Création de la table combat
CREATE TABLE combat (
                        id_combat INT PRIMARY KEY IDENTITY(1,1),
                        id_joueur1 INT NOT NULL,
                        id_joueur2 INT NOT NULL,
                        id_vainqueur INT,
                        nombre_tours INT NOT NULL DEFAULT 0,
                        date_combat DATETIME NOT NULL DEFAULT GETDATE(),
                        FOREIGN KEY (id_joueur1) REFERENCES joueur(id_joueur),
                        FOREIGN KEY (id_joueur2) REFERENCES joueur(id_joueur),
                        FOREIGN KEY (id_vainqueur) REFERENCES joueur(id_joueur)
);

-- Création de la table inventaire
CREATE TABLE inventaire (
                            id_inventaire INT PRIMARY KEY IDENTITY(1,1),
                            id_joueur INT NOT NULL,
                            FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE
);

-- Création de la table coffre
CREATE TABLE coffre (
                        id_coffre INT PRIMARY KEY IDENTITY(1,1),
                        id_joueur INT NOT NULL,
                        FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE
);

-- Création de la table items (version simplifiée)
CREATE TABLE items (
    id_item INT PRIMARY KEY IDENTITY(1,1),
    nom NVARCHAR(255) NOT NULL,
    type NVARCHAR(50) NOT NULL,
    description NVARCHAR(MAX),
    prix INT NOT NULL,
    rarete NVARCHAR(50),
    niveau_requis INT
);

-- Création de la table competences
CREATE TABLE competences (
                             id_competence INT PRIMARY KEY IDENTITY(1,1),
                             nom_competence NVARCHAR(255) NOT NULL,
                             description_competence NVARCHAR(MAX),
                             prix_competence INT NOT NULL,
                             type_competence NVARCHAR(50) NOT NULL,
                             effet_competence NVARCHAR(MAX)
);

-- Création de la table joueur_competences_achetees
CREATE TABLE joueur_competences_achetees (
                                    id_joueur INT NOT NULL,
                                    id_competence INT NOT NULL,
                                    PRIMARY KEY (id_joueur, id_competence),
                                    FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE,
                                    FOREIGN KEY (id_competence) REFERENCES competences(id_competence) ON DELETE CASCADE
);

-- Ajout des contraintes de clés étrangères pour la table joueur
ALTER TABLE joueur
ADD CONSTRAINT FK_joueur_royaume FOREIGN KEY (id_royaume) REFERENCES royaume(id_royaume),
    CONSTRAINT FK_joueur_coffre FOREIGN KEY (id_coffre) REFERENCES coffre(id_coffre),
    CONSTRAINT FK_joueur_personnage FOREIGN KEY (id_personnage) REFERENCES personnage_joueur(id_personnage);
