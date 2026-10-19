-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 19, 2026 at 02:25 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `transport_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `titre` varchar(200) NOT NULL,
  `message` text NOT NULL,
  `type` enum('INFO','ALERTE','PROMOTION','RAPPEL') DEFAULT 'INFO',
  `vue` tinyint(1) DEFAULT 0,
  `date_notification` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `offres_speciales`
--

CREATE TABLE `offres_speciales` (
  `id` int(11) NOT NULL,
  `titre` varchar(200) NOT NULL,
  `description` text DEFAULT NULL,
  `code_promo` varchar(50) DEFAULT NULL,
  `reduction_pourcentage` decimal(5,2) DEFAULT NULL,
  `reduction_fixe` decimal(10,2) DEFAULT NULL,
  `date_debut` date NOT NULL,
  `date_fin` date NOT NULL,
  `statut` enum('ACTIVE','EXPIREE','DESACTIVEE') DEFAULT 'ACTIVE',
  `conditions` text DEFAULT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `offres_speciales`
--

INSERT INTO `offres_speciales` (`id`, `titre`, `description`, `code_promo`, `reduction_pourcentage`, `reduction_fixe`, `date_debut`, `date_fin`, `statut`, `conditions`, `date_creation`) VALUES
(1, 'Première réservation', '15% de réduction sur votre première réservation', 'PREMIER15', 15.00, 0.00, '2024-01-01', '2026-12-02', 'ACTIVE', '', '2026-01-16 10:21:47'),
(2, 'Offre famille', '10% de réduction pour les familles de 4 personnes ou plus', 'FAMILLE10', 10.00, 0.00, '2026-03-01', '2026-06-30', 'ACTIVE', '', '2026-01-16 10:21:47'),
(3, 'Réduction étudiant', '20% de réduction pour les étudiants sur présentation de la carte', 'ETUDIANT20', 20.00, 0.00, '2024-01-01', '2027-12-31', 'ACTIVE', '', '2026-01-16 10:21:47'),
(4, 'Offre week-end', 'Réduction spéciale pour les voyages du week-end', 'WEEKEND10', 10.00, 0.00, '2024-01-01', '2026-03-31', 'ACTIVE', '', '2026-01-16 10:21:47');

-- --------------------------------------------------------

--
-- Table structure for table `paiements`
--

CREATE TABLE `paiements` (
  `id` int(11) NOT NULL,
  `reservation_id` int(11) NOT NULL,
  `montant` decimal(10,2) NOT NULL,
  `mode_paiement` enum('CARTE','ESPECES','VIREMENT','CHEQUE') NOT NULL,
  `date_paiement` timestamp NOT NULL DEFAULT current_timestamp(),
  `reference` varchar(100) DEFAULT NULL,
  `statut` enum('PAYE','EN_ATTENTE','ECHOUE') DEFAULT 'EN_ATTENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `voyage_id` int(11) NOT NULL,
  `date_reservation` timestamp NOT NULL DEFAULT current_timestamp(),
  `nombre_places` int(11) NOT NULL,
  `montant_total` decimal(10,2) NOT NULL,
  `statut` enum('CONFIRME','EN_ATTENTE','ANNULE','REMBOURSE') DEFAULT 'EN_ATTENTE',
  `numero_billet` varchar(50) DEFAULT NULL,
  `siege_numbers` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`id`, `client_id`, `voyage_id`, `date_reservation`, `nombre_places`, `montant_total`, `statut`, `numero_billet`, `siege_numbers`) VALUES
(7, 2, 1, '2026-01-16 15:41:50', 1, 120.00, 'CONFIRME', NULL, 'À attribuer'),
(8, 2, 3, '2026-01-16 15:41:57', 1, 80.00, 'CONFIRME', NULL, 'À attribuer'),
(9, 2, 4, '2026-01-16 21:24:23', 3, 300.00, 'CONFIRME', NULL, 'À attribuer'),
(10, 2, 1, '2026-01-16 21:26:08', 3, 360.00, 'CONFIRME', NULL, 'À attribuer'),
(11, 2, 7, '2026-01-16 21:26:23', 5, 227.50, 'CONFIRME', NULL, 'À attribuer'),
(12, 5, 7, '2026-01-22 10:40:54', 12, 546.00, 'CONFIRME', NULL, 'À attribuer'),
(13, 5, 2, '2026-01-23 10:56:01', 1, 150.00, 'ANNULE', NULL, 'À attribuer'),
(14, 5, 4, '2026-01-23 10:56:07', 1, 100.00, 'CONFIRME', NULL, 'À attribuer'),
(15, 5, 5, '2026-01-23 10:56:13', 1, 120.00, 'ANNULE', NULL, 'À attribuer'),
(16, 5, 10, '2026-01-23 10:56:20', 1, 90.00, 'CONFIRME', NULL, 'À attribuer'),
(17, 5, 1, '2026-01-23 11:23:17', 1, 120.00, 'CONFIRME', NULL, 'À attribuer'),
(18, 5, 7, '2026-01-23 11:23:23', 1, 45.50, 'CONFIRME', NULL, 'À attribuer'),
(19, 5, 2, '2026-01-23 11:23:29', 1, 150.00, 'CONFIRME', NULL, 'À attribuer'),
(20, 5, 8, '2026-01-23 11:23:35', 1, 45.50, 'CONFIRME', NULL, 'À attribuer'),
(21, 5, 3, '2026-01-23 11:23:42', 1, 80.00, 'CONFIRME', NULL, 'À attribuer'),
(22, 5, 9, '2026-01-23 11:23:54', 1, 120.00, 'CONFIRME', NULL, 'À attribuer'),
(23, 5, 5, '2026-01-23 11:24:01', 1, 120.00, 'ANNULE', NULL, 'À attribuer'),
(24, 5, 13, '2026-01-23 11:24:17', 1, 120.00, 'CONFIRME', NULL, 'À attribuer'),
(25, 5, 11, '2026-01-23 11:24:22', 1, 100.00, 'CONFIRME', NULL, 'À attribuer'),
(26, 5, 14, '2026-01-23 11:24:27', 1, 100.00, 'ANNULE', NULL, 'À attribuer'),
(27, 5, 17, '2026-01-23 11:24:31', 1, 300.00, 'ANNULE', NULL, 'À attribuer'),
(28, 5, 16, '2026-01-23 11:24:37', 1, 275.00, 'ANNULE', NULL, 'À attribuer'),
(29, 5, 15, '2026-01-23 11:24:45', 1, 75.00, 'ANNULE', NULL, 'À attribuer'),
(30, 2, 11, '2026-01-26 15:14:08', 2, 200.00, 'ANNULE', NULL, 'À attribuer'),
(31, 2, 14, '2026-01-26 15:14:31', 6, 600.00, 'ANNULE', NULL, 'À attribuer'),
(32, 2, 7, '2026-01-27 20:53:01', 27, 1228.50, 'ANNULE', NULL, 'À attribuer'),
(33, 2, 1, '2026-01-27 20:53:29', 39, 4680.00, 'ANNULE', NULL, 'À attribuer'),
(34, 2, 18, '2026-02-04 15:54:14', 3, 1050.00, 'ANNULE', NULL, 'À attribuer'),
(35, 2, 17, '2026-02-12 20:46:09', 4, 1200.00, 'ANNULE', NULL, 'À attribuer'),
(36, 2, 3, '2026-02-23 14:05:26', 10, 800.00, 'ANNULE', NULL, 'À attribuer'),
(37, 2, 17, '2026-02-23 15:07:22', 4, 1200.00, 'ANNULE', NULL, 'À attribuer'),
(38, 2, 16, '2026-02-23 15:45:50', 2, 550.00, 'ANNULE', NULL, 'À attribuer'),
(39, 7, 2, '2026-02-24 12:27:11', 1, 150.00, 'ANNULE', NULL, 'À attribuer'),
(40, 7, 15, '2026-02-24 12:28:42', 5, 375.00, 'ANNULE', NULL, 'À attribuer'),
(41, 7, 17, '2026-02-24 12:28:57', 16, 4800.00, 'ANNULE', NULL, 'À attribuer'),
(42, 7, 17, '2026-02-24 13:44:00', 1, 300.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(43, 2, 14, '2026-02-24 13:47:15', 1, 100.00, 'ANNULE', NULL, 'À attribuer'),
(44, 2, 15, '2026-02-24 13:56:13', 1, 75.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(45, 2, 16, '2026-02-24 14:46:33', 1, 275.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(46, 2, 16, '2026-02-24 14:46:57', 1, 275.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(47, 2, 16, '2026-02-24 14:47:35', 1, 275.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(48, 2, 16, '2026-02-24 14:47:56', 2, 550.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(49, 2, 16, '2026-02-24 14:48:14', 37, 10175.00, 'EN_ATTENTE', NULL, 'À attribuer'),
(50, 2, 15, '2026-02-25 14:02:25', 2, 150.00, 'ANNULE', NULL, 'À attribuer');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) DEFAULT NULL,
  `prenom` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `cin` varchar(20) DEFAULT NULL,
  `role` enum('CLIENT','ADMIN') NOT NULL DEFAULT 'CLIENT',
  `date_inscription` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `nom`, `prenom`, `email`, `password`, `telephone`, `cin`, `role`, `date_inscription`) VALUES
(1, 'Admin', 'System', 'admin@transport.com', 'admin123', NULL, NULL, 'ADMIN', '2026-01-16 10:21:46'),
(2, 'Duf', 'Jean', 'client@test.com', 'client123', NULL, NULL, 'CLIENT', '2026-01-16 10:21:46'),
(3, 'Martin', 'Sophie', 'sophie.martin@email.com', 'pass123', NULL, NULL, 'CLIENT', '2026-01-16 10:21:46'),
(4, 'Benali', 'Ahmed', 'ahmed.benali@email.com', 'pass123', NULL, NULL, 'CLIENT', '2026-01-16 10:21:46'),
(5, 'hassane', 'oulberj', 'hassan@hoat.com', 'hassan123', '0628927381', 'CIN_4157', 'CLIENT', '2026-01-22 10:40:04'),
(6, 'jzsnj', 'hzxi', 'nznj@JZH.com', '12345HOAT', '0684575839', 'CIN_71634', 'CLIENT', '2026-01-22 13:32:51'),
(7, 'soufiane', 'hqhh', 'h.haskouk@test.com', 'haskouk123', '0617628862', 'CIN_55266', 'CLIENT', '2026-02-24 12:25:55');

-- --------------------------------------------------------

--
-- Table structure for table `vehicules`
--

CREATE TABLE `vehicules` (
  `id` int(11) NOT NULL,
  `type_vehicule` enum('BUS','VAN','CAR','MINIBUS') NOT NULL,
  `marque` varchar(50) DEFAULT NULL,
  `modele` varchar(50) DEFAULT NULL,
  `capacite` int(11) NOT NULL,
  `immatriculation` varchar(20) DEFAULT NULL,
  `statut` enum('ACTIF','EN_MAINTENANCE','HORS_SERVICE') DEFAULT 'ACTIF',
  `date_mise_service` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicules`
--

INSERT INTO `vehicules` (`id`, `type_vehicule`, `marque`, `modele`, `capacite`, `immatriculation`, `statut`, `date_mise_service`) VALUES
(1, 'BUS', 'Mercedes', 'Tourismo', 50, '12345AB', 'ACTIF', NULL),
(2, 'VAN', 'Volkswagen', 'Transporter', 20, '67890CD', 'ACTIF', NULL),
(3, 'CAR', 'Iveco', 'Daily', 30, '11223EF', 'ACTIF', NULL),
(4, 'BUS', 'MAN', 'Lion', 45, '44556GH', 'ACTIF', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `voyages`
--

CREATE TABLE `voyages` (
  `id` int(11) NOT NULL,
  `ville_depart` varchar(100) NOT NULL,
  `ville_arrivee` varchar(100) NOT NULL,
  `date_depart` datetime NOT NULL,
  `date_arrivee` datetime DEFAULT NULL,
  `vehicule_id` int(11) NOT NULL,
  `prix` decimal(10,2) NOT NULL,
  `places_totales` int(11) NOT NULL,
  `places_disponibles` int(11) NOT NULL,
  `statut` enum('ACTIF','ANNULE','COMPLET','EN_ATTENTE') DEFAULT 'ACTIF',
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `voyages`
--

INSERT INTO `voyages` (`id`, `ville_depart`, `ville_arrivee`, `date_depart`, `date_arrivee`, `vehicule_id`, `prix`, `places_totales`, `places_disponibles`, `statut`, `date_creation`) VALUES
(1, 'Casablanca', 'Marrakech', '2024-01-20 08:00:00', NULL, 1, 120.00, 50, 0, 'ACTIF', '2026-01-16 10:21:47'),
(2, 'Rabat', 'Tanger', '2024-01-20 10:00:00', NULL, 2, 150.00, 20, 14, 'ACTIF', '2026-01-16 10:21:47'),
(3, 'Fès', 'Meknès', '2024-01-20 14:00:00', NULL, 3, 80.00, 30, 12, 'ACTIF', '2026-01-16 10:21:47'),
(4, 'Agadir', 'Essaouira', '2024-01-21 09:00:00', NULL, 4, 100.00, 45, 35, 'ACTIF', '2026-01-16 10:21:47'),
(5, 'Marrakech', 'Casablanca', '2024-01-21 16:00:00', NULL, 1, 120.00, 50, 48, 'ACTIF', '2026-01-16 10:21:47'),
(7, 'Casablanca', 'Rabat', '2024-01-20 08:00:00', NULL, 1, 45.50, 50, 0, 'ACTIF', '2026-01-16 14:16:25'),
(8, 'Rabat', 'Casablanca', '2024-01-20 10:00:00', NULL, 2, 45.50, 50, 29, 'ACTIF', '2026-01-16 14:16:25'),
(9, 'Marrakech', 'Fès', '2024-01-21 09:00:00', NULL, 3, 120.00, 30, 24, 'ACTIF', '2026-01-16 14:16:25'),
(10, 'Tanger', 'Casablanca', '2024-01-22 14:00:00', NULL, 1, 90.00, 50, 14, 'ACTIF', '2026-01-16 14:16:25'),
(11, 'Errachidia', 'Ouarzazate', '2026-02-12 23:00:00', NULL, 1, 100.00, 50, 47, 'ACTIF', '2026-01-23 11:17:27'),
(12, 'OUARZAZATE', 'MARAKECH', '2026-01-31 22:30:00', NULL, 2, 150.00, 20, 20, 'ACTIF', '2026-01-23 11:18:15'),
(13, 'OUARZAZATE', 'INZEGANE', '2026-02-02 12:15:00', NULL, 4, 120.00, 45, 44, 'ACTIF', '2026-01-23 11:19:31'),
(14, 'MARAKECH', 'OUARZAZATE', '2026-03-15 13:30:00', NULL, 1, 100.00, 50, 42, 'ACTIF', '2026-01-23 11:20:19'),
(15, 'ERRACHIDIA', 'MEZOUGA', '2026-10-12 20:30:00', NULL, 1, 75.00, 50, 41, 'ACTIF', '2026-01-23 11:20:59'),
(16, 'ERRACHIDIA', 'TETOUANE', '2026-04-15 19:30:00', NULL, 1, 275.00, 50, 5, 'ACTIF', '2026-01-23 11:21:59'),
(17, 'OUARZAZATE', 'TANGER', '2026-03-16 22:30:00', NULL, 1, 300.00, 50, 24, 'ACTIF', '2026-01-23 11:22:38'),
(18, 'ouajeda', 'Ouarzazate', '2026-01-31 20:30:00', NULL, 4, 350.00, 45, 42, 'ACTIF', '2026-01-23 21:57:11');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user` (`user_id`),
  ADD KEY `idx_vue` (`vue`);

--
-- Indexes for table `offres_speciales`
--
ALTER TABLE `offres_speciales`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code_promo` (`code_promo`),
  ADD KEY `idx_statut` (`statut`),
  ADD KEY `idx_dates` (`date_debut`,`date_fin`);

--
-- Indexes for table `paiements`
--
ALTER TABLE `paiements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_reservation` (`reservation_id`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_billet` (`numero_billet`),
  ADD KEY `idx_client` (`client_id`),
  ADD KEY `idx_voyage` (`voyage_id`),
  ADD KEY `idx_statut` (`statut`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_role` (`role`);

--
-- Indexes for table `vehicules`
--
ALTER TABLE `vehicules`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `immatriculation` (`immatriculation`),
  ADD KEY `idx_statut` (`statut`);

--
-- Indexes for table `voyages`
--
ALTER TABLE `voyages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `vehicule_id` (`vehicule_id`),
  ADD KEY `idx_depart_arrivee` (`ville_depart`,`ville_arrivee`),
  ADD KEY `idx_date_depart` (`date_depart`),
  ADD KEY `idx_statut` (`statut`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `offres_speciales`
--
ALTER TABLE `offres_speciales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `paiements`
--
ALTER TABLE `paiements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `vehicules`
--
ALTER TABLE `vehicules`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `voyages`
--
ALTER TABLE `voyages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `paiements`
--
ALTER TABLE `paiements`
  ADD CONSTRAINT `paiements_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`voyage_id`) REFERENCES `voyages` (`id`);

--
-- Constraints for table `voyages`
--
ALTER TABLE `voyages`
  ADD CONSTRAINT `voyages_ibfk_1` FOREIGN KEY (`vehicule_id`) REFERENCES `vehicules` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
