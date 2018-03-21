-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Mar 21, 2018 at 12:43 AM
-- Server version: 10.1.19-MariaDB
-- PHP Version: 5.6.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `emergency`
--

-- --------------------------------------------------------

--
-- Table structure for table `tblaccident`
--

CREATE TABLE `tblaccident` (
  `UID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `LAT` float NOT NULL,
  `LNG` float NOT NULL,
  `TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `tblaccident`
--

INSERT INTO `tblaccident` (`UID`, `USER_ID`, `LAT`, `LNG`, `TIME`) VALUES
(133, 101, 26.8834, 75.8124, '2018-03-20 11:08:47'),
(1111, 101, 26.8834, 71.812, '2018-03-20 22:22:53'),
(1115, 101, 26.8834, 71.812, '2018-03-20 22:29:06'),
(1258, 2589, 23.886, 72.526, '2018-03-20 22:40:21'),
(1712, 101, 26.8834, 75.8124, '2018-03-20 11:05:53'),
(2222, 2569, 26.8824, 75.8261, '2018-03-20 23:03:30'),
(2282, 2569, 26.8824, 75.8261, '2018-03-20 23:05:50'),
(2978, 101, 26.8834, 75.8124, '2018-03-20 22:11:40'),
(3002, 101, 26.8834, 75.8124, '2018-03-20 20:44:16'),
(3095, 101, 26.8834, 75.8124, '2018-03-20 19:09:14'),
(3873, 101, 26.8834, 75.8124, '2018-03-20 11:07:43'),
(4073, 101, 26.8834, 75.8124, '2018-03-20 07:55:20'),
(4344, 101, 26.8834, 75.8124, '2018-03-20 22:31:12'),
(4641, 101, 26.8834, 75.8124, '2018-03-20 22:57:01'),
(5038, 101, 26.8834, 75.8124, '2018-03-20 21:06:35'),
(5192, 101, 26.8834, 75.8124, '2018-03-20 22:17:23'),
(5290, 101, 26.8834, 75.8124, '2018-03-20 22:10:27'),
(5551, 101, 26.8834, 75.8124, '2018-03-20 20:42:22'),
(5783, 101, 26.8834, 75.8124, '2018-03-20 20:40:46'),
(6043, 101, 26.8834, 75.8124, '2018-03-20 22:58:26'),
(9312, 101, 26.8834, 75.8124, '2018-03-20 22:55:58'),
(12258, 101, 26.8824, 75.8261, '2018-03-20 23:39:35'),
(45785, 101, 26.8824, 75.8261, '2018-03-20 23:13:50'),
(78541, 101, 26.8824, 75.8261, '2018-03-20 23:08:04');

-- --------------------------------------------------------

--
-- Table structure for table `tblcare`
--

CREATE TABLE `tblcare` (
  `UID` bigint(20) NOT NULL,
  `USERNAME` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `NAME` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `LAT` float NOT NULL,
  `LNG` float NOT NULL,
  `PASSWORD` varchar(30) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `tblcare`
--

INSERT INTO `tblcare` (`UID`, `USERNAME`, `NAME`, `LAT`, `LNG`, `PASSWORD`) VALUES
(934062, 'sourav', 'hospital1', 26.9725, 75.7725, 'sourav');

-- --------------------------------------------------------

--
-- Table structure for table `tbluser`
--

CREATE TABLE `tbluser` (
  `UID` int(11) NOT NULL,
  `NAME` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `AGE` int(11) NOT NULL,
  `ADDRESS` varchar(30) COLLATE utf8_unicode_ci NOT NULL,
  `EMERGENCY` bigint(20) NOT NULL,
  `PERSONAL` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `tbluser`
--

INSERT INTO `tbluser` (`UID`, `NAME`, `AGE`, `ADDRESS`, `EMERGENCY`, `PERSONAL`) VALUES
(101, 'himanshu', 21, 'sangrur', 7404985403, 9876543210),
(102, 'sourav', 20, 'sirsa', 9315118009, 8708774316);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tblaccident`
--
ALTER TABLE `tblaccident`
  ADD PRIMARY KEY (`UID`);

--
-- Indexes for table `tblcare`
--
ALTER TABLE `tblcare`
  ADD PRIMARY KEY (`UID`),
  ADD UNIQUE KEY `USERNAME` (`USERNAME`);

--
-- Indexes for table `tbluser`
--
ALTER TABLE `tbluser`
  ADD PRIMARY KEY (`UID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
