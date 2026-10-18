# YCYW — PoC tchat

Preuve de concept (PoC) du **tchat entre un client et un agent du service client** de Your Car Your Way.

Une preuve de concept est un petit programme qui vérifie qu'un choix technique fonctionne, avant de construire l'application complète. Celle-ci vérifie l'architecture annoncée pour la future application :

- **back** (le serveur) : Spring Boot 4.1 en Java 25 ;
- **front** (l'interface dans le navigateur) : Angular 22 ;
- **base de données** : PostgreSQL 18 ;
- **échanges en direct** : WebSocket avec le protocole STOMP.

## Sommaire

1. [Ce que la PoC démontre, et ce qu'elle ne fait pas](#1-ce-que-la-poc-démontre-et-ce-quelle-ne-fait-pas)
2. [Prérequis](#2-prérequis)
3. [Installation](#3-installation)
4. [Lancement](#4-lancement)
5. [Fonctionnement](#5-fonctionnement)
6. [Structure des données](#6-structure-des-données)
7. [Dépannage](#7-dépannage)

## 1. Ce que la PoC démontre, et ce qu'elle ne fait pas

### 1.1 Ce qu'elle démontre

Elle couvre l'épique E5 « Support client (tchat) » du cahier des charges :

- un client ouvre une conversation et échange en direct avec le service client ;
- un agent voit les conversations des clients et y répond en direct ;
- le client et l'agent retrouvent l'historique de leurs échanges, enregistré en base de données.

### 1.2 Ce qu'elle ne fait pas, volontairement

Une PoC doit rester limitée à ce qu'elle veut prouver. Ces sujets sont traités dans la proposition d'architecture, pas dans le code :

| Absent de la PoC | Ce que fait la PoC à la place | Où c'est traité |
| --- | --- | --- |
| Connexion avec mot de passe | On choisit « qui je suis » parmi deux utilisateurs de démonstration. N'importe qui peut donc se faire passer pour n'importe qui : c'est assumé pour une démo locale | Proposition d'architecture : authentification avec Spring Security |
| Droits d'accès (un client ne voit que ses conversations) | Le front n'affiche au client que ses conversations, mais le back ne le contrôle pas | Proposition d'architecture : sécurité |
| Plusieurs serveurs en parallèle | Un seul back, qui distribue lui-même les messages | Proposition d'architecture : broker de messages externe |
| Clôture d'une conversation, affectation à un agent | Toute conversation reste ouverte, tous les agents peuvent répondre | Évolutions du tchat |
| Tests automatisés | La vérification se fait par une démo à deux navigateurs, décrite dans ce README | Proposition d'architecture : stratégie de test |

## 2. Prérequis

Installe ces outils. La commande de la dernière colonne doit afficher la bonne version.

| Outil | Version | Vérifier avec |
| --- | --- | --- |
| Git | 2 ou plus | `git --version` |
| JDK (kit de développement Java, Temurin conseillé) | 25 | `java -version` |
| Docker et Docker Compose | Docker 24 ou plus | `docker compose version` |
| Un éditeur de code | IntelliJ IDEA ou VS Code | — |

Tu n'as pas besoin d'installer Maven ni PostgreSQL :

- Maven, l'outil qui compile le back, est fourni dans `back/` par le *Maven Wrapper* (commande `./mvnw`) ;
- PostgreSQL tourne dans un conteneur Docker, une sorte de mini-machine isolée.

## 3. Installation

### 3.1 Récupérer le code

```bash
git clone <url-du-dépôt> ycyw-chat-poc
cd ycyw-chat-poc
```

Toutes les commandes partent de ce dossier, sauf indication contraire.

### 3.2 Créer le fichier `.env`

Le fichier `.env` contient le mot de passe de la base locale. Il n'est jamais enregistré dans Git : seul son modèle `.env.example` l'est.

```bash
cp .env.example .env
```

Ouvre `.env` et remplace `change-me` par un mot de passe de ton choix.

## 4. Lancement

Docker doit être démarré.

### 4.1 Le back et la base de données (port 8080)

Dans un terminal :

```bash
cd back
./mvnw spring-boot:run
```

- Sous Windows, remplace `./mvnw` par `mvnw.cmd`.
- Le premier lancement télécharge les dépendances : compte quelques minutes.
- Le back démarre lui-même la base : le module **Spring Boot Docker Compose** lance le conteneur PostgreSQL décrit dans `compose.yaml`, attend qu'il soit prêt et s'y connecte. À l'arrêt du back (Ctrl+C), le conteneur est arrêté ; les données sont conservées.
- La base démarre vide la première fois. Ce sont les **migrations Flyway** qui la remplissent : Flyway est un outil qui exécute, au démarrage du back, les scripts SQL du dossier `back/src/main/resources/db/migration/`, dans l'ordre de leur numéro et une seule fois chacun.
- Le back est prêt quand la console affiche `Started ChatPocApplication`.

## 5. Fonctionnement

### 5.1 Communiquer avec le back en REST

- **REST** : le front envoie une requête HTTP et reçoit une réponse, comme une page web classique. On l'utilise pour ce qui se lit une fois : la liste des utilisateurs, des conversations, et l'historique.

### 5.2 Les adresses utilisées

| Type | Adresse | Rôle |
| --- | --- | --- |
| REST | `GET /api/users` | Liste des utilisateurs de démonstration |
| REST | `GET /api/conversations?userId=1` | Conversations d'un client, ou toutes pour un agent |
| REST | `POST /api/conversations` | Un client ouvre une conversation |
| REST | `GET /api/conversations/{id}/messages` | Historique d'une conversation |

## 6. Structure des données

Trois tables, créées par la migration `V1__create_tables.sql`. Les deux utilisateurs de démonstration sont ajoutés par `V2__insert_demo_users.sql`.

| Table | Colonne | Type | Rôle |
| --- | --- | --- | --- |
| `app_user` | `id` | nombre, clé primaire | Identifiant unique |
| `app_user` | `display_name` | texte (100) | Nom affiché |
| `app_user` | `role` | texte, `CLIENT` ou `AGENT` | Rôle de l'utilisateur |
| `conversation` | `id` | nombre, clé primaire | Identifiant unique |
| `conversation` | `client_id` | clé étrangère vers `app_user` | Client qui a ouvert la conversation |
| `conversation` | `created_at` | date et heure | Date d'ouverture |
| `message` | `id` | nombre, clé primaire | Identifiant unique, croissant : il donne l'ordre des messages |
| `message` | `conversation_id` | clé étrangère vers `conversation` | Conversation du message |
| `message` | `author_id` | clé étrangère vers `app_user` | Auteur, client ou agent |
| `message` | `content` | texte (2 000), jamais vide | Contenu du message |
| `message` | `sent_at` | date et heure | Date d'envoi |

Quelques notions :

- une **clé étrangère** relie une ligne à une ligne d'une autre table : la base refuse un message dont l'auteur n'existe pas ;
- le schéma respecte la **troisième forme normale (3NF)** : chaque information n'est stockée qu'à un seul endroit. Par exemple, le nom de l'auteur n'est pas recopié dans chaque message, seul son identifiant l'est ;
- la base ne contient **aucune donnée sensible** : ni e-mail, ni mot de passe.

Pour regarder le contenu de la base, pendant que le back tourne :

```bash
docker compose exec postgres psql -U ycyw -d ycyw_chat -c "SELECT * FROM message;"
```

Remplace `ycyw` et `ycyw_chat` si tu as changé `POSTGRES_USER` ou `POSTGRES_DB` dans `.env`.

## 7. Dépannage

| Symptôme | Cause probable | Solution |
| --- | --- | --- |
| Le back s'arrête : *Unable to start docker process* ou *Cannot connect to the Docker daemon* | Docker n'est pas démarré | Démarre Docker Desktop ou le service Docker, puis relance le back |
| Le back s'arrête : *required variable POSTGRES_DB is missing* | Pas de fichier `.env` | `cp .env.example .env` ([section 3.2](#32-créer-le-fichier-env)) |
| Le back s'arrête : *port is already allocated* | Un autre PostgreSQL utilise déjà le port 5432 | Arrête-le, ou mets `POSTGRES_PORT=5433` dans `.env` |
| Le back s'arrête : *password authentication failed* | Le mot de passe de `.env` a changé après la création de la base | Recrée la base, ce qui efface ses données : `docker compose down -v` à la racine du dépôt, puis relance le back |
| Le back s'arrête sur une erreur Flyway (*checksum mismatch*, *more than one migration*) | Base ou dossier `target/` créés par une ancienne version du code | Recrée la base comme ci-dessus, puis relance avec `./mvnw clean spring-boot:run` |
| `./mvnw` affiche *release version 25 not supported* | Maven utilise un JDK plus ancien | `java -version` doit afficher 25 |