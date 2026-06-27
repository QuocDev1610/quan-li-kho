-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: inventory
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auth`
--

DROP TABLE IF EXISTS `auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `menu_id` int NOT NULL,
  `permission` int NOT NULL DEFAULT '1',
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `menu_id` (`menu_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `auth_ibfk_1` FOREIGN KEY (`menu_id`) REFERENCES `menu` (`id`),
  CONSTRAINT `auth_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth`
--

LOCK TABLES `auth` WRITE;
/*!40000 ALTER TABLE `auth` DISABLE KEYS */;
INSERT INTO `auth` VALUES (1,1,1,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(2,1,2,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(3,1,3,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(4,1,4,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(5,1,5,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(6,1,6,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(7,1,7,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(8,1,8,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(9,1,9,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(10,1,10,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(11,1,11,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(12,1,12,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(13,1,13,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(14,1,14,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(15,1,15,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(16,1,16,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(17,1,17,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(18,1,18,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(19,1,19,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(20,1,20,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(21,1,21,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(22,1,22,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(23,1,23,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(24,1,24,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(25,1,25,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(26,1,26,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(27,1,27,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(28,1,28,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(29,1,29,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(30,1,30,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(31,1,31,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(32,1,32,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(33,1,33,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(34,1,34,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(35,2,1,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(36,2,2,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(37,2,3,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(38,2,4,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(39,2,5,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(40,2,6,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(41,2,7,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(42,2,8,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(43,2,9,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(44,2,10,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(45,2,11,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(46,2,12,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(47,2,13,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(48,2,14,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(49,2,15,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(50,2,16,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(51,2,17,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(52,2,18,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(53,2,19,1,1,'2026-05-28 08:48:17','2026-05-28 08:48:17'),(54,1,35,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14'),(55,1,36,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14'),(56,1,37,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14'),(57,1,38,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14'),(58,1,39,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14'),(59,1,40,1,1,'2026-05-28 14:33:14','2026-05-28 14:33:14');
/*!40000 ALTER TABLE `auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` text,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Apple12','ap1','appleq12',1,'2026-05-24 15:39:14','2026-05-25 01:31:20'),(2,'Apple1','ap11','apple',1,'2026-05-24 16:17:44','2026-05-24 16:17:44'),(3,'Apple12','ap112','appleq121',1,'2026-05-25 02:34:01','2026-05-25 02:34:01'),(4,'lê ngọc quôc','ap1123','ư',1,'2026-05-25 02:38:06','2026-05-25 02:38:06'),(5,'lê ngọc quôc','ap11233','ư',1,'2026-05-25 11:58:26','2026-05-25 11:58:26'),(6,'lê ngọc quôc','ap112334','ư',1,'2026-05-25 12:13:29','2026-05-25 12:13:29'),(7,'lê ngọc quôc','ap1123344','ư',1,'2026-05-25 12:13:33','2026-05-25 12:13:33'),(8,'lê ngọc quôc','ap11233444','ư',1,'2026-05-25 12:13:36','2026-05-25 12:13:36'),(9,'lê ngọc quôc','ap112334444','ư',1,'2026-05-25 12:13:42','2026-05-25 12:13:42');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `action_name` varchar(100) NOT NULL,
  `type` int NOT NULL,
  `qty` int NOT NULL,
  `product_id` int NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES (1,'Add',1,11,1,12899.00,1,'2026-06-08 10:50:33','2026-06-08 10:50:33');
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `type` int NOT NULL,
  `qty` int NOT NULL,
  `product_id` int NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,'123',1,11,1,12899.00,1,'2026-06-08 10:50:33','2026-06-08 10:50:33');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NOT NULL,
  `url` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `order_index` int NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,0,'/product','Sản phẩm',1,1,'2026-05-28 08:43:33','2026-06-04 10:34:59'),(2,0,'/stock','Kho',2,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(3,0,'/management','Quản lý',3,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(4,1,'/category/list','Danh sách category',1,1,'2026-05-28 08:43:33','2026-06-04 10:38:48'),(5,1,'/category/add','Thêm mới',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(6,1,'/category/edit','Sửa',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(7,1,'/category/view','Xem',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(8,1,'/category/save','Lưu',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(9,1,'/category/delete','Xóa',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(10,1,'/product-info/list','Danh sách sản phẩm',2,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(11,1,'/product-info/add','Thêm mới',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(12,1,'/product-info/edit','Sửa',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(13,1,'/product-info/view','Xem',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(14,1,'/product-info/save','Lưu',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(15,1,'/product-info/delete','Xóa',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(16,2,'/goods-receipt/list','Danh sách nhập kho',1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(17,2,'/goods-receipt/add','Thêm mới',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(18,2,'/goods-receipt/view','Xem',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(19,2,'/goods-receipt/save','Lưu',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(20,2,'/goods-receipt/export','Xuất báo cáo',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(21,2,'/goods-issue/list','Danh sách xuất kho',2,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(22,2,'/product-in-stock/list','Sản phẩm trong kho',3,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(23,2,'/history/list','Lịch sử kho',4,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(24,3,'/user/list','Danh sách user',1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(25,3,'/user/add','Add',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(26,3,'/user/edit','Edit',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(27,3,'/user/view','View',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(28,3,'/user/save','Save',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(29,3,'/role/list','Danh sách quyền',3,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(30,3,'/role/add','Add',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(31,3,'/role/edit','Edit',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(32,3,'/role/view','View',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(33,3,'/role/save','Save',-1,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(34,3,'/menu/list','Danh sách menu',2,1,'2026-05-28 08:43:33','2026-05-28 08:43:33'),(35,2,'/goods-issue/add','Thêm mới phiếu xuất kho',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34'),(36,2,'/goods-issue/export','Xuất Excel danh sách xuất kho',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34'),(37,2,'/goods-issue/list','Xem danh sách xuất kho',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34'),(38,2,'/goods-issue/save','Lưu thông tin xuất kho',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34'),(39,2,'/goods-issue/view','Xem chi tiết phiếu xuất kho',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34'),(40,3,'/menu/change-status','Change',-1,1,'2026-05-28 14:25:34','2026-05-28 14:25:34');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_in_stock`
--

DROP TABLE IF EXISTS `product_in_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_in_stock` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `qty` int NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Price` decimal(15,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `product_in_stock_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_in_stock`
--

LOCK TABLES `product_in_stock` WRITE;
/*!40000 ALTER TABLE `product_in_stock` DISABLE KEYS */;
INSERT INTO `product_in_stock` VALUES (1,1,11,1,'2026-06-08 10:50:33','2026-06-08 10:50:33',12899.00);
/*!40000 ALTER TABLE `product_in_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_info`
--

DROP TABLE IF EXISTS `product_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cate_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `code` varchar(50) NOT NULL,
  `description` text,
  `img_url` varchar(200) NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cate_id` (`cate_id`),
  CONSTRAINT `product_info_ibfk_1` FOREIGN KEY (`cate_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_info`
--

LOCK TABLES `product_info` WRITE;
/*!40000 ALTER TABLE `product_info` DISABLE KEYS */;
INSERT INTO `product_info` VALUES (1,1,'lê ngọc quôc','ap11','appleJ12','/upload/1779788801939_888.1.jpg',0,'2026-05-26 09:46:42','2026-05-26 09:46:42'),(2,1,'lê ngọc quôc','ap1112','appleJ12','/upload/1779791124185_1779788801951_888.1.jpg',1,'2026-05-26 10:25:24','2026-05-26 10:25:24'),(3,4,'lê ngọc quôcqq','ap1112q','appleJ12','/upload/1779791170553_product-banner.jpg',1,'2026-05-26 10:26:11','2026-05-26 10:26:11');
/*!40000 ALTER TABLE `product_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL,
  `active_flag` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin','Admin of system',1,'2026-05-21 10:15:02','2026-05-21 10:15:02'),(2,'staff','Staff of system',1,'2026-05-21 10:15:02','2026-05-21 10:15:02'),(3,'employee','Empoyee  of system',1,'2026-05-21 10:23:07','2026-05-21 10:23:07');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `USER_NAME` varchar(50) NOT NULL,
  `PASSWORD` varchar(50) NOT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `NAME` varchar(100) NOT NULL,
  `ACTIVE_FLAG` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','admin@tongkho.com','Quản Trị Viên',1,'2026-05-21 05:18:21','2026-05-21 05:18:21'),(2,'thukho_an','thukho2026','an.nguyen@tongkho.com','Nguyễn Văn An',1,'2026-05-21 05:18:21','2026-05-21 05:18:21'),(3,'kiemke_binh','binh123','binh.tran@tongkho.com','Trần Thị Bình',1,'2026-05-21 05:18:21','2026-05-21 05:18:21'),(4,'nhanvien_cuong','cuong!@#','cuong.le@tongkho.com','Lê Mạnh Cường',0,'2026-05-21 05:18:21','2026-05-21 05:18:21');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  `ACTIVE_FLAG` int NOT NULL DEFAULT '1',
  `CREATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `user_id` (`user_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`ID`),
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1,1,'2026-05-21 10:25:25','2026-05-21 10:25:25'),(2,2,2,1,'2026-05-21 10:25:25','2026-05-21 10:25:25'),(3,3,2,1,'2026-05-21 10:25:25','2026-05-21 10:25:25'),(4,4,2,0,'2026-05-21 10:25:25','2026-05-21 10:25:25');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-10  9:35:50


-- ============================================================
-- IPHONE WAREHOUSE SAMPLE DATA
-- Compatible with the schema in Dump20260610.sql
-- type: 1 = goods receipt, 2 = goods issue
-- ============================================================

SET NAMES utf8mb4;
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM `history`;
DELETE FROM `invoice`;
DELETE FROM `product_in_stock`;
DELETE FROM `product_info`;
DELETE FROM `category`;

ALTER TABLE `history` AUTO_INCREMENT = 1;
ALTER TABLE `invoice` AUTO_INCREMENT = 1;
ALTER TABLE `product_in_stock` AUTO_INCREMENT = 1;
ALTER TABLE `product_info` AUTO_INCREMENT = 1;
ALTER TABLE `category` AUTO_INCREMENT = 1;

INSERT INTO `category`
(`id`, `name`, `code`, `description`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`)
VALUES
(1, 'iPhone 13 Series', 'IP13', 'Các mẫu iPhone thế hệ 13', 1, '2026-01-02 08:00:00', '2026-01-02 08:00:00'),
(2, 'iPhone 14 Series', 'IP14', 'Các mẫu iPhone thế hệ 14', 1, '2026-01-02 08:00:00', '2026-01-02 08:00:00'),
(3, 'iPhone 15 Series', 'IP15', 'Các mẫu iPhone thế hệ 15 dùng cổng USB-C', 1, '2026-01-02 08:00:00', '2026-01-02 08:00:00'),
(4, 'iPhone 16 Series', 'IP16', 'Các mẫu iPhone thế hệ 16 mới nhất', 1, '2026-01-02 08:00:00', '2026-01-02 08:00:00');

INSERT INTO `product_info`
(`id`, `cate_id`, `name`, `code`, `description`, `img_url`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`)
VALUES
(1, 1, 'iPhone 13 128GB', 'IP13-128', 'Màn hình 6.1 inch, bộ nhớ 128GB', '/upload/iphone-13-128gb.jpg', 1, '2026-01-03 08:00:00', '2026-01-03 08:00:00'),
(2, 1, 'iPhone 13 Pro 256GB', 'IP13P-256', 'Phiên bản Pro, bộ nhớ 256GB', '/upload/iphone-13-pro-256gb.jpg', 1, '2026-01-03 08:05:00', '2026-01-03 08:05:00'),
(3, 2, 'iPhone 14 128GB', 'IP14-128', 'iPhone 14 tiêu chuẩn, bộ nhớ 128GB', '/upload/iphone-14-128gb.jpg', 1, '2026-01-03 08:10:00', '2026-01-03 08:10:00'),
(4, 2, 'iPhone 14 Plus 128GB', 'IP14PL-128', 'Màn hình lớn 6.7 inch, bộ nhớ 128GB', '/upload/iphone-14-plus-128gb.jpg', 1, '2026-01-03 08:15:00', '2026-01-03 08:15:00'),
(5, 2, 'iPhone 14 Pro Max 256GB', 'IP14PM-256', 'Phiên bản Pro Max, bộ nhớ 256GB', '/upload/iphone-14-pro-max-256gb.jpg', 1, '2026-01-03 08:20:00', '2026-01-03 08:20:00'),
(6, 3, 'iPhone 15 128GB', 'IP15-128', 'Dynamic Island, cổng USB-C, bộ nhớ 128GB', '/upload/iphone-15-128gb.jpg', 1, '2026-01-03 08:25:00', '2026-01-03 08:25:00'),
(7, 3, 'iPhone 15 Plus 256GB', 'IP15PL-256', 'Màn hình 6.7 inch, bộ nhớ 256GB', '/upload/iphone-15-plus-256gb.jpg', 1, '2026-01-03 08:30:00', '2026-01-03 08:30:00'),
(8, 3, 'iPhone 15 Pro 256GB', 'IP15P-256', 'Khung titanium, chip A17 Pro, bộ nhớ 256GB', '/upload/iphone-15-pro-256gb.jpg', 1, '2026-01-03 08:35:00', '2026-01-03 08:35:00'),
(9, 3, 'iPhone 15 Pro Max 256GB', 'IP15PM-256', 'Camera telephoto 5x, bộ nhớ 256GB', '/upload/iphone-15-pro-max-256gb.jpg', 1, '2026-01-03 08:40:00', '2026-01-03 08:40:00'),
(10, 4, 'iPhone 16 128GB', 'IP16-128', 'Chip A18, Camera Control, bộ nhớ 128GB', '/upload/iphone-16-128gb.jpg', 1, '2026-01-03 08:45:00', '2026-01-03 08:45:00'),
(11, 4, 'iPhone 16 Pro 256GB', 'IP16P-256', 'Màn hình ProMotion, chip A18 Pro, bộ nhớ 256GB', '/upload/iphone-16-pro-256gb.jpg', 1, '2026-01-03 08:50:00', '2026-01-03 08:50:00'),
(12, 4, 'iPhone 16 Pro Max 256GB', 'IP16PM-256', 'Phiên bản cao cấp nhất, bộ nhớ 256GB', '/upload/iphone-16-pro-max-256gb.jpg', 1, '2026-01-03 08:55:00', '2026-01-03 08:55:00');

INSERT INTO `invoice`
(`id`, `code`, `type`, `qty`, `product_id`, `price`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`)
VALUES
(1, 'PN-2026-001', 1, 50, 1, 12500000.00, 1, '2026-01-05 09:00:00', '2026-01-05 09:00:00'),
(2, 'PX-2026-001', 2, 18, 1, 14990000.00, 1, '2026-02-10 14:00:00', '2026-02-10 14:00:00'),
(3, 'PN-2026-002', 1, 25, 2, 18000000.00, 1, '2026-01-12 09:15:00', '2026-01-12 09:15:00'),
(4, 'PX-2026-002', 2, 17, 2, 21990000.00, 1, '2026-03-02 10:00:00', '2026-03-02 10:00:00'),
(5, 'PN-2026-003', 1, 60, 3, 15800000.00, 1, '2026-02-03 08:30:00', '2026-02-03 08:30:00'),
(6, 'PX-2026-003', 2, 22, 3, 18490000.00, 1, '2026-02-20 15:20:00', '2026-02-20 15:20:00'),
(7, 'PN-2026-004', 1, 35, 4, 17500000.00, 1, '2026-02-08 09:40:00', '2026-02-08 09:40:00'),
(8, 'PX-2026-004', 2, 10, 4, 20490000.00, 1, '2026-03-12 11:10:00', '2026-03-12 11:10:00'),
(9, 'PN-2026-005', 1, 20, 5, 24000000.00, 1, '2026-02-15 13:00:00', '2026-02-15 13:00:00'),
(10, 'PX-2026-005', 2, 15, 5, 27990000.00, 1, '2026-04-01 09:25:00', '2026-04-01 09:25:00'),
(11, 'PN-2026-006', 1, 70, 6, 18500000.00, 1, '2026-03-04 08:45:00', '2026-03-04 08:45:00'),
(12, 'PX-2026-006', 2, 31, 6, 21490000.00, 1, '2026-03-21 16:00:00', '2026-03-21 16:00:00'),
(13, 'PN-2026-007', 1, 30, 7, 21000000.00, 1, '2026-03-10 10:30:00', '2026-03-10 10:30:00'),
(14, 'PX-2026-007', 2, 12, 7, 24490000.00, 1, '2026-04-08 14:10:00', '2026-04-08 14:10:00'),
(15, 'PN-2026-008', 1, 28, 8, 25000000.00, 1, '2026-04-02 08:20:00', '2026-04-02 08:20:00'),
(16, 'PX-2026-008', 2, 9, 8, 28990000.00, 1, '2026-04-18 15:45:00', '2026-04-18 15:45:00'),
(17, 'PN-2026-009', 1, 22, 9, 29000000.00, 1, '2026-04-07 09:10:00', '2026-04-07 09:10:00'),
(18, 'PX-2026-009', 2, 13, 9, 32990000.00, 1, '2026-05-03 10:50:00', '2026-05-03 10:50:00'),
(19, 'PN-2026-010', 1, 80, 10, 22000000.00, 1, '2026-05-05 08:00:00', '2026-05-05 08:00:00'),
(20, 'PX-2026-010', 2, 26, 10, 24990000.00, 1, '2026-05-19 14:30:00', '2026-05-19 14:30:00'),
(21, 'PN-2026-011', 1, 32, 11, 28000000.00, 1, '2026-05-11 09:00:00', '2026-05-11 09:00:00'),
(22, 'PX-2026-011', 2, 11, 11, 31990000.00, 1, '2026-06-04 11:20:00', '2026-06-04 11:20:00'),
(23, 'PN-2026-012', 1, 18, 12, 33000000.00, 1, '2026-06-01 08:10:00', '2026-06-01 08:10:00'),
(24, 'PX-2026-012', 2, 14, 12, 36990000.00, 1, '2026-06-15 15:00:00', '2026-06-15 15:00:00');

INSERT INTO `history`
(`id`, `action_name`, `type`, `qty`, `product_id`, `price`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`)
SELECT
    `id`, 'Add', `type`, `qty`, `product_id`, `price`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`
FROM `invoice`;

INSERT INTO `product_in_stock`
(`id`, `product_id`, `qty`, `active_flag`, `CREATE_DATE`, `UPDATE_DATE`, `Price`)
VALUES
(1, 1, 32, 1, '2026-01-05 09:00:00', '2026-02-10 14:00:00', 12500000.00),
(2, 2, 8, 1, '2026-01-12 09:15:00', '2026-03-02 10:00:00', 18000000.00),
(3, 3, 38, 1, '2026-02-03 08:30:00', '2026-02-20 15:20:00', 15800000.00),
(4, 4, 25, 1, '2026-02-08 09:40:00', '2026-03-12 11:10:00', 17500000.00),
(5, 5, 5, 1, '2026-02-15 13:00:00', '2026-04-01 09:25:00', 24000000.00),
(6, 6, 39, 1, '2026-03-04 08:45:00', '2026-03-21 16:00:00', 18500000.00),
(7, 7, 18, 1, '2026-03-10 10:30:00', '2026-04-08 14:10:00', 21000000.00),
(8, 8, 19, 1, '2026-04-02 08:20:00', '2026-04-18 15:45:00', 25000000.00),
(9, 9, 9, 1, '2026-04-07 09:10:00', '2026-05-03 10:50:00', 29000000.00),
(10, 10, 54, 1, '2026-05-05 08:00:00', '2026-05-19 14:30:00', 22000000.00),
(11, 11, 21, 1, '2026-05-11 09:00:00', '2026-06-04 11:20:00', 28000000.00),
(12, 12, 4, 1, '2026-06-01 08:10:00', '2026-06-15 15:00:00', 33000000.00);

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- Verification query: every result should have delta = 0.
SELECT
    p.code,
    COALESCE(SUM(CASE WHEN i.type = 1 AND i.active_flag = 1 THEN i.qty
                      WHEN i.type = 2 AND i.active_flag = 1 THEN -i.qty
                      ELSE 0 END), 0) AS calculated_qty,
    s.qty AS stored_qty,
    s.qty - COALESCE(SUM(CASE WHEN i.type = 1 AND i.active_flag = 1 THEN i.qty
                              WHEN i.type = 2 AND i.active_flag = 1 THEN -i.qty
                              ELSE 0 END), 0) AS delta
FROM product_info p
JOIN product_in_stock s ON s.product_id = p.id AND s.active_flag = 1
LEFT JOIN invoice i ON i.product_id = p.id
GROUP BY p.id, p.code, s.qty
ORDER BY p.id;
