Guerre des Royaumes
Jeu de stratégie multijoueur en mode texte.
Dans ce jeu, les joueurs dirigent des royaumes, gèrent leurs ressources et leurs territoires, et combattent pour étendre leur empire.

⚙️ Installation et Configuration

1. Cloner le dépôt
Pour commencer à travailler sur le projet, tu dois d'abord cloner le dépôt GitHub dans ton répertoire local. Ouvre un terminal et exécute les commandes suivantes :

    git clone https://github.com/ton-utilisateur/guerre-des-royaumes.git
    cd guerre-des-royaumes

🌿 Workflow Git & Branches
Branches principales
main : La branche stable, utilisée pour la production. Ne jamais push directement dessus.

develop : La branche d’intégration, utilisée pour toutes les nouvelles fonctionnalités avant de les publier.

Comment contribuer ?
1. Mettre à jour develop Avant de commencer à travailler sur une nouvelle fonctionnalité, assure-toi que ta branche develop est à jour. Exécute les commandes suivantes :

  git checkout develop
  git pull origin develop

2. Créer une branche pour ta fonctionnalité Pour chaque nouvelle fonctionnalité ou tâche, crée une nouvelle branche depuis develop :

  git checkout -b feature/nom-de-ta-feature develop

Par exemple, si tu ajoutes une fonctionnalité de gestion des ressources, tu pourrais nommer ta branche feature/gestion-ressources.

3. Développer la fonctionnalité

Effectue des commits fréquents et clairs pour décrire les changements.

  Exemple : après avoir ajouté une fonctionnalité, tu peux faire un commit comme :
  git commit -m "Ajout de la gestion des ressources"

4. Pousser la branche sur GitHub Une fois ton travail terminé sur la branche, pousse-la sur GitHub :

  git push origin feature/nom-de-ta-feature


Attention : 
Ne jamais pousser directement sur main. Une version stable sera créée à partir de develop quand l'application sera prête.

📂 Arborescence du Projet
Voici à quoi ressemble la structure du projet, avec une explication de chaque dossier :

	src/
	└── main/
	    └── java/
		
	        └── be/
			
	            └── helha/
				
	                └── projects/
					
	                    └── guerredesroyaumes/
						
	                        ├── Config/          # Fichiers de configuration (ex: paramètres de la BDD)
							
	                        ├── Controller/      # Contrôleurs qui gèrent les requêtes et l'API
	
	                        ├── DAO/             # Interfaces pour l'accès aux données
							
	                        ├── DAOImpl/         # Utilisation et Implementation concrètes des DAO (ex: MySQL)
							
	                        ├── DTO/             # Objets pour transférer des données entre couches serveur et autres
							
	                        ├── Exceptions/      # Gestion des erreurs personnalisées
							
	                        ├── Model/           # Modèles représentant les entités du jeu (ex: Joueur, Royaume)
							
	                        ├── Outils/          # Utilitaires réutilisables
							
	                        ├── Service/         # Logique métier, règles du jeu
							
	                        └── TUI/             # Interface utilisateur textuelle avec les PGMTEST main
						

🧑‍🤝‍🧑 Collaboration
Respecte le workflow Git : Crée des branches feature/ à partir de develop pour chaque nouvelle fonctionnalité.

Commits clairs et fréquents : Lorsque tu ajoutes ou modifies du code, fais des commits clairs et décris précisément ce que tu as fait.

Testes localement avant de pousser : Avant de pousser ton code, teste-le sur ton environnement local pour éviter d'introduire des erreurs.

Documente tes PRs : Lorsque tu crées une Pull Request, explique ce que ta fonctionnalité ajoute ou modifie.

Pose des questions : Si tu n’es pas sûr de quelque chose, pose des questions dans les issues GitHub ou sur le canal de communication de l’équipe.


