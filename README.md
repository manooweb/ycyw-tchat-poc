# YCYW — PoC tchat

Preuve de concept (PoC) du **tchat entre un client et un agent du service client** de Your Car Your Way.

Une preuve de concept est un petit programme qui vérifie qu'un choix technique fonctionne, avant de construire l'application complète. Celle-ci vérifie l'architecture annoncée pour la future application :

- **back** (le serveur) : Spring Boot 4.1 en Java 25 ;
- **front** (l'interface dans le navigateur) : Angular 22 ;
- **base de données** : PostgreSQL 18 ;
- **échanges en direct** : WebSocket avec le protocole STOMP.

## Sommaire

1. [Ce que la PoC démontre, et ce qu'elle ne fait pas](#1-ce-que-la-poc-démontre-et-ce-quelle-ne-fait-pas)

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
