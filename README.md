\# Transport+ 🚍



**Transport+** est une application de bureau développée en **Java** (Swing / JDBC) et connectée à une base de données **MySQL**. Elle permet de gérer de manière complète les activités d'une société de transport de voyageurs.



## 🚀 Fonctionnalités Principales
- **Authentification & Gestion des Rôles :** Espaces dédiés pour les **Clients** et les **Administrateurs**.
- **Gestion des Voyages :** Planification, recherche et suivi des trajets (villes de départ/arrivée, horaires, prix et places disponibles).
- **Réservations de Billets :** Processus complet de réservation avec gestion des sièges et des statuts[cite: 3].
- **Offres Spéciales & Promotions :** Gestion des codes promo et des réductions (pourcentages ou montants fixes)[cite: 3].
- **Gestion de la Flotte & Paiements :** Suivi des véhicules, des états de maintenance et des paiements[cite: 3].

\## 🛠️ Technologies Utilisées

\- \*\*Langage :\*\* Java

\- \*\*Interface :\*\* Java Swing / AWT

\- \*\*Base de données :\*\* MySQL (JDBC)

\- \*\*Outil de build :\*\* Eclipse IDE

## 📂 Structure du Projet
- `src/com/transport/config/` : Configuration de l'application et des thèmes[cite: 3].
- `src/com/transport/dao/` : Classes d'accès aux données (<bdi>Data Access Objects</bdi>)[cite: 3].
- `src/com/transport/models/` : Modèles de données (Entités)[cite: 3].
- `src/com/transport/services/` : Logique métier[cite: 3].
- `src/com/transport/ui/` : Fenêtres (<bdi>Frames</bdi>), panneaux (<bdi>Panels</bdi>) et composants graphiques[cite: 3].
- `database/` : Script SQL complet de la base de données (`transport_db.sql`)[cite: 3].

\## ⚙️ Installation et Exécution

1\. Clonez le dépôt :

&#x20;  ```bash

&#x20;  git clone \[https://github.com/soufian-haskouk/transport-plus-desktop.git](https://github.com/soufian-haskouk/transport-plus-desktop.git)

## 🗄️ Base de Données & Configuration MySQL

L'application communique avec une base de données MySQL via le **JDBC Connector**.
  1. Créez une base de données nommée `transport_plus` :
   ```sql
   CREATE DATABASE transport_plus;
Importez le projet dans Eclipse IDE en tant que projet Java existant

Importez le fichier SQL situé dans le dossier database/transport_db.sql dans votre serveur MySQL local (via phpMyAdmin)[cite: 3].

Configurez vos accès à la base de données dans la classe de connexion (DBConnection.java).

Exécutez la classe principale pour lancer l'application


