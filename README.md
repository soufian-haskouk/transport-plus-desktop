# 🚍 Transport+

![Status](https://img.shields.io/badge/Status-Completed-success)
![Version](https://img.shields.io/badge/Release-v1.0.0-blue)
![Author](https://img.shields.io/badge/Author-Soufian%20Haskouk-purple?style=for-the-badge&logo=github)
![Java](https://img.shields.io/badge/Java-Swing-orange?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red)

> **Application de bureau robuste développée en Java et connectée à MySQL pour la gestion complète d'une société de transport de voyageurs.**

---

## 📌 Présentation du Projet
**Transport+** est une solution logicielle bureautique conçue pour optimiser et automatiser les processus opérationnels d'une agence de transport. Elle offre des interfaces graphiques intuitives adaptées aux différents utilisateurs (clients et administrateurs) en s'appuyant sur l'architecture JDBC.

---

## 🚀 Fonctionnalités Principales

* **🔐 Authentification & Gestion des Rôles :** Espaces sécurisés et dédiés pour les **Clients** et les **Administrateurs**.
* **🗺️ Gestion des Voyages :** Planification, recherche et suivi en temps réel des trajets (villes de départ/arrivée, horaires, prix et places disponibles).
* **🎟️ Réservations de Billets :** Processus complet de réservation avec gestion interactive des sièges et des statuts.
* **🏷️ Offres Spéciales & Promotions :** Gestion dynamique des codes promo et des réductions (pourcentages ou montants fixes).
* **🚌 Gestion de la Flotte & Paiements :** Suivi des véhicules, des états de maintenance et centralisation des paiements.

---

## 🛠️ Stack Technologique & Architecture

| Composant | Technologie Utilisée |
| :--- | :--- |
| **Langage Principal** | Java |
| **Interface Graphique (UI)** | Java Swing / AWT |
| **Base de Données** | MySQL (Connexion via JDBC) |
| **Environnement de Développement** | Eclipse IDE |

---

## 📂 Structure du Projet

* `src/com/transport/config/` : Configuration globale de l'application et des thèmes graphiques.
* `src/com/transport/dao/` : Classes d'accès aux données (*Data Access Objects*).
* `src/com/transport/models/` : Modèles de données et entités métiers.
* `src/com/transport/services/` : Implémentation de la logique métier.
* `src/com/transport/ui/` : Fenêtres (*Frames*), panneaux (*Panels*) et composants graphiques.
* `database/` : Script SQL complet de la base de données (`transport_db.sql`).

---

## ⚙️ Installation et Exécution

### 1. Cloner le dépôt
```bash
git clone [https://github.com/soufian-haskouk/transport-plus-desktop.git](https://github.com/soufian-haskouk/transport-plus-desktop.git)
