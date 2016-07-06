CREATE DATABASE  IF NOT EXISTS `projectherkansing` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `projectherkansing`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: projectherkansing
-- ------------------------------------------------------
-- Server version	5.5.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `klant`
--

DROP TABLE IF EXISTS `klant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `klant` (
  `persoon_id` int(15) NOT NULL,
  `woonplaats` varchar(45) NOT NULL,
  `straatnaam` varchar(45) NOT NULL,
  `huisnummer` int(11) NOT NULL,
  PRIMARY KEY (`persoon_id`),
  CONSTRAINT `persoon_id` FOREIGN KEY (`persoon_id`) REFERENCES `persoon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `klant`
--

LOCK TABLES `klant` WRITE;
/*!40000 ALTER TABLE `klant` DISABLE KEYS */;
INSERT INTO `klant` VALUES (2,'Rotterdam','Clauster',59),(24,'Jan','',59);
/*!40000 ALTER TABLE `klant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kunstenaar`
--

DROP TABLE IF EXISTS `kunstenaar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kunstenaar` (
  `persoon_id` int(11) NOT NULL,
  `geboortedatum` date NOT NULL,
  `sterfdatum` date DEFAULT NULL,
  PRIMARY KEY (`persoon_id`),
  CONSTRAINT `persoon_idKU` FOREIGN KEY (`persoon_id`) REFERENCES `persoon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kunstenaar`
--

LOCK TABLES `kunstenaar` WRITE;
/*!40000 ALTER TABLE `kunstenaar` DISABLE KEYS */;
INSERT INTO `kunstenaar` VALUES (1,'1869-07-01','1899-04-01'),(14,'1993-01-10','2015-01-01'),(60,'1880-01-10',NULL);
/*!40000 ALTER TABLE `kunstenaar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persoon`
--

DROP TABLE IF EXISTS `persoon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persoon` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `voornaam` varchar(45) NOT NULL,
  `tussenvoegsel` varchar(45) DEFAULT 'null',
  `achternaam` varchar(45) NOT NULL,
  `type` char(2) NOT NULL DEFAULT 'kl',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persoon`
--

LOCK TABLES `persoon` WRITE;
/*!40000 ALTER TABLE `persoon` DISABLE KEYS */;
INSERT INTO `persoon` VALUES (1,'Vincent','Van','Gogh','ku'),(2,'Bob','','Turn','kl'),(14,'Harry','','Potter','ku'),(24,'Jan','','Peter','kl'),(60,'fa','f','f','ku'),(62,'t','','t','kl');
/*!40000 ALTER TABLE `persoon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schilderij`
--

DROP TABLE IF EXISTS `schilderij`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schilderij` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `naam` varchar(45) NOT NULL,
  `jaar` date NOT NULL,
  `kunstenaar` int(15) NOT NULL,
  `Uitleningen` int(11) NOT NULL DEFAULT '0',
  `Waardering` double NOT NULL DEFAULT '0',
  `waarde` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `kunstenaar_id_idx` (`kunstenaar`),
  CONSTRAINT `kunstenaar_id` FOREIGN KEY (`kunstenaar`) REFERENCES `persoon` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schilderij`
--

LOCK TABLES `schilderij` WRITE;
/*!40000 ALTER TABLE `schilderij` DISABLE KEYS */;
INSERT INTO `schilderij` VALUES (21,'Starry','1879-01-01',1,0,5.333333333333333,40000),(22,'StarryTwee','1880-01-01',1,0,7,400),(26,'V','1879-01-01',1,0,1,40),(27,'t','1888-01-01',1,0,5,4),(32,'k','1888-10-10',1,1,9,1),(33,'StarryTweede','1888-10-10',1,0,0,54),(34,'Harry','1994-10-10',14,2,7,50),(35,'Potter','1996-10-10',14,0,0,65),(37,'tar','1879-10-10',1,0,0,5);
/*!40000 ALTER TABLE `schilderij` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uitlening`
--

DROP TABLE IF EXISTS `uitlening`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uitlening` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `klant_id` int(15) DEFAULT NULL,
  `schilderij_id` int(15) DEFAULT NULL,
  `startdatum` date NOT NULL,
  `einddatum` date NOT NULL,
  `waardering` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `schilderij_id_idx` (`schilderij_id`),
  KEY `klant_id_idx` (`klant_id`),
  CONSTRAINT `klant_id` FOREIGN KEY (`klant_id`) REFERENCES `persoon` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `schilderij_id` FOREIGN KEY (`schilderij_id`) REFERENCES `schilderij` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uitlening`
--

LOCK TABLES `uitlening` WRITE;
/*!40000 ALTER TABLE `uitlening` DISABLE KEYS */;
INSERT INTO `uitlening` VALUES (21,2,21,'1993-01-01','1993-01-02',6),(22,2,22,'1994-01-01','1995-01-01',5),(25,2,NULL,'2012-01-01','2013-01-01',9),(26,NULL,21,'1994-01-01','1994-01-05',1),(27,NULL,22,'2000-01-10','2000-01-11',9),(28,NULL,NULL,'2014-01-01','2014-01-02',9),(29,24,21,'1995-01-01','1996-01-01',9),(30,24,27,'1999-01-01','2000-01-01',5),(31,24,26,'2012-01-01','2013-01-01',1),(32,24,NULL,'2011-01-10','2012-01-11',1),(33,NULL,NULL,'1991-01-01','1992-01-01',5),(37,2,21,'2015-01-03','2015-01-04',7),(40,NULL,NULL,'2015-01-01','2015-01-19',4),(41,NULL,32,'2015-01-06','2015-01-13',9),(42,2,21,'2015-01-21','2016-01-23',6),(44,2,34,'2015-01-13','2017-01-01',9),(51,NULL,21,'2020-01-10','2021-01-10',7);
/*!40000 ALTER TABLE `uitlening` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-16 17:25:15
