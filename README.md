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
5. [Dépannage](#5-dépannage)

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
- Le back est prêt quand la console affiche `Started ChatPocApplication`.

## 5. Dépannage

| Symptôme | Cause probable | Solution |
| --- | --- | --- |
| Le back s'arrête : *Unable to start docker process* ou *Cannot connect to the Docker daemon* | Docker n'est pas démarré | Démarre Docker Desktop ou le service Docker, puis relance le back |
| Le back s'arrête : *required variable POSTGRES_DB is missing* | Pas de fichier `.env` | `cp .env.example .env` ([section 3.2](#32-créer-le-fichier-env)) |
| Le back s'arrête : *port is already allocated* | Un autre PostgreSQL utilise déjà le port 5432 | Arrête-le, ou mets `POSTGRES_PORT=5433` dans `.env` |
| Le back s'arrête : *password authentication failed* | Le mot de passe de `.env` a changé après la création de la base | Recrée la base, ce qui efface ses données : `docker compose down -v` à la racine du dépôt, puis relance le back |
| `./mvnw` affiche *release version 25 not supported* | Maven utilise un JDK plus ancien | `java -version` doit afficher 25 |