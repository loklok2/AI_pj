-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: fashion
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `cart_user_fk` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='장바구니 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`cart_item_id`),
  KEY `cart_id` (`cart_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`) ON DELETE CASCADE,
  CONSTRAINT `cart_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='장바구니 아이템 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `qboard_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `edited_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `qboard_id` (`qboard_id`),
  KEY `FK5xverdcul8imlu3dfd4liaxxc` (`user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`qboard_id`) REFERENCES `qboard` (`qboard_id`) ON DELETE CASCADE,
  CONSTRAINT `FK5xverdcul8imlu3dfd4liaxxc` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글 댓글 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `resident_registration_number` varchar(7) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `style` varchar(50) DEFAULT NULL,
  `join_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `edited_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
INSERT INTO `member` VALUES (7,'admin','$2a$10$.fXm/N58Eh9DweIOmbdFLeLAe7qoscKWH.eMx6ssU/qEAyYIafp.6','Admin',NULL,NULL,'관리자','010-1234-5678','admin@example.com','ADMIN',NULL,'2024-09-05 16:57:58','2024-09-05 16:57:58',0),(9,'gks9168','$2a$10$edUffOKloV6frb4oNjiNOenzan8ITYJiV9r/mmQ4WBu0xIRsH6w7i','한창록','남자','9201011','부산광역시 해운대구 선수촌로','01021219168','gks9168@gmail.com','MEMBER','CASUAL','2024-09-05 16:59:52','2024-09-05 16:59:52',0);
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 상품 상세 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `order_comment` varchar(255) DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `order_status` varchar(50) DEFAULT 'PENDING',
  `shipping_address_id` bigint DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  KEY `fk_order_shipping_address` (`shipping_address_id`),
  CONSTRAINT `fk_order_shipping_address` FOREIGN KEY (`shipping_address_id`) REFERENCES `shipping_addresses` (`address_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payments_id` bigint NOT NULL AUTO_INCREMENT,
  `pay_value` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`payments_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='결제 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `size` enum('XS','S','M','L','XL','FREE') DEFAULT NULL,
  `stock` bigint DEFAULT NULL,
  `price` bigint NOT NULL COMMENT '상품 가격 (원 단위)',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `sell` bigint DEFAULT NULL,
  `like` bigint DEFAULT NULL,
  `view` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=351 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'상품61','상품 설명49','XS',16,74299,'2024-09-06 17:19:23',716,325,1092),(2,'상품591','상품 설명630','M',0,96487,'2024-09-06 17:19:23',324,13,1610),(4,'상품725','상품 설명142','L',25,75173,'2024-09-06 17:19:23',503,280,2995),(6,'상품811','상품 설명160','M',34,73493,'2024-09-06 17:19:23',136,388,4719),(7,'상품31','상품 설명741','L',84,46309,'2024-09-06 17:19:23',294,191,277),(8,'상품992','상품 설명878','M',44,106217,'2024-09-06 17:19:23',485,271,2548),(9,'상품646','상품 설명469','M',63,103789,'2024-09-06 17:19:23',791,72,3470),(11,'상품302','상품 설명469','M',78,72269,'2024-09-06 17:19:23',749,440,1519),(12,'상품119','상품 설명140','M',30,57106,'2024-09-06 17:19:23',452,425,8926),(13,'상품913','상품 설명887','XL',82,11368,'2024-09-06 17:19:23',606,496,1431),(15,'상품737','상품 설명258','XS',63,100643,'2024-09-06 17:19:23',641,245,5257),(16,'상품157','상품 설명211','L',28,76412,'2024-09-06 17:19:23',472,184,4256),(17,'상품23','상품 설명838','XS',9,21187,'2024-09-06 17:19:23',272,13,3172),(18,'상품505','상품 설명575','M',8,45057,'2024-09-06 17:19:23',488,196,4943),(19,'상품295','상품 설명994','XS',44,107222,'2024-09-06 17:19:23',523,349,9265),(22,'상품535','상품 설명896','FREE',69,92357,'2024-09-06 17:19:23',47,382,6865),(23,'상품135','상품 설명619','XL',59,99566,'2024-09-06 17:19:23',698,401,9237),(24,'상품207','상품 설명268','XL',78,89202,'2024-09-06 17:19:23',590,288,1090),(25,'상품817','상품 설명758','M',42,21416,'2024-09-06 17:19:23',289,52,6579),(26,'상품973','상품 설명894','L',7,83508,'2024-09-06 17:19:23',439,496,6490),(27,'상품264','상품 설명373','XS',25,13154,'2024-09-06 17:19:23',405,466,4455),(29,'상품430','상품 설명817','XL',51,30887,'2024-09-06 17:19:23',489,411,6436),(30,'상품750','상품 설명820','FREE',80,54983,'2024-09-06 17:19:23',844,435,8257),(31,'상품513','상품 설명91','FREE',30,87505,'2024-09-06 17:19:23',961,242,5370),(32,'상품231','상품 설명547','XS',56,80070,'2024-09-06 17:19:23',808,470,2792),(33,'상품573','상품 설명28','M',3,101086,'2024-09-06 17:19:23',443,241,868),(34,'상품984','상품 설명661','M',78,96045,'2024-09-06 17:19:23',949,82,9795),(35,'상품401','상품 설명66','XS',44,95141,'2024-09-06 17:19:23',913,5,3177),(39,'상품554','상품 설명818','M',68,24161,'2024-09-06 17:19:23',652,419,2399),(40,'상품680','상품 설명682','M',80,99245,'2024-09-06 17:19:23',62,316,9805),(41,'상품2','상품 설명71','M',53,73167,'2024-09-06 17:19:23',548,424,5990),(42,'상품448','상품 설명443','FREE',3,64373,'2024-09-06 17:19:23',621,239,5278),(43,'상품202','상품 설명430','L',42,60061,'2024-09-06 17:19:23',224,310,4296),(44,'상품286','상품 설명143','FREE',86,85028,'2024-09-06 17:19:23',154,260,1392),(45,'상품134','상품 설명256','FREE',62,58802,'2024-09-06 17:19:23',565,180,1098),(46,'상품466','상품 설명1','L',4,50167,'2024-09-06 17:19:23',868,69,843),(47,'상품7','상품 설명785','FREE',16,22259,'2024-09-06 17:19:23',110,92,5955),(48,'상품421','상품 설명318','S',69,60053,'2024-09-06 17:19:23',406,265,4335),(50,'상품575','상품 설명579','S',10,109891,'2024-09-06 17:19:23',693,236,2791),(51,'상품979','상품 설명59','M',61,10490,'2024-09-06 17:19:23',175,431,7889),(52,'상품355','상품 설명409','FREE',68,55610,'2024-09-06 17:19:23',239,415,4316),(53,'상품667','상품 설명44','S',95,22045,'2024-09-06 17:19:23',738,165,4433),(54,'상품221','상품 설명776','S',76,24748,'2024-09-06 17:19:23',456,420,8371),(55,'상품661','상품 설명794','FREE',56,93839,'2024-09-06 17:19:23',508,14,6208),(56,'상품15','상품 설명215','XS',50,51379,'2024-09-06 17:19:23',568,299,2945),(57,'상품673','상품 설명483','M',52,55367,'2024-09-06 17:19:23',684,30,2496),(58,'상품66','상품 설명584','XL',85,22305,'2024-09-06 17:19:23',41,418,640),(59,'상품807','상품 설명844','XL',47,106816,'2024-09-06 17:19:23',416,89,6439),(60,'상품683','상품 설명484','M',41,106258,'2024-09-06 17:19:23',561,459,9152),(61,'상품816','상품 설명335','S',12,106787,'2024-09-06 17:19:23',450,173,3823),(62,'상품872','상품 설명213','M',61,83307,'2024-09-06 17:19:23',812,431,8792),(65,'상품805','상품 설명392','L',54,17244,'2024-09-06 17:19:23',740,242,2034),(66,'상품562','상품 설명201','S',99,13261,'2024-09-06 17:19:23',166,366,1663),(68,'상품633','상품 설명666','M',15,59083,'2024-09-06 17:19:23',983,223,2845),(70,'상품80','상품 설명550','L',89,104456,'2024-09-06 17:19:23',39,182,7079),(73,'상품443','상품 설명93','XS',39,67284,'2024-09-06 17:19:23',677,334,3159),(74,'상품570','상품 설명904','XL',34,37665,'2024-09-06 17:19:23',357,478,7108),(75,'상품684','상품 설명290','M',12,52174,'2024-09-06 17:19:23',737,210,8927),(76,'상품201','상품 설명327','XS',18,92184,'2024-09-06 17:19:23',556,157,9098),(77,'상품602','상품 설명281','L',15,107689,'2024-09-06 17:19:23',418,81,5572),(78,'상품298','상품 설명820','S',57,33689,'2024-09-06 17:19:23',470,321,8016),(79,'상품79','상품 설명994','XL',68,31804,'2024-09-06 17:19:23',39,270,5883),(80,'상품318','상품 설명827','S',41,65808,'2024-09-06 17:19:23',531,490,3148),(82,'상품629','상품 설명202','XS',0,77132,'2024-09-06 17:19:23',332,324,2459),(84,'상품283','상품 설명679','L',69,95136,'2024-09-06 17:19:23',160,124,7608),(85,'상품58','상품 설명10','FREE',35,23978,'2024-09-06 17:19:23',636,380,8964),(86,'상품199','상품 설명308','FREE',80,26715,'2024-09-06 17:19:23',433,333,320),(87,'상품160','상품 설명705','XS',12,57527,'2024-09-06 17:19:23',4,297,9624),(88,'상품27','상품 설명249','XS',7,100193,'2024-09-06 17:19:23',270,322,4157),(89,'상품142','상품 설명466','FREE',12,102687,'2024-09-06 17:19:23',247,228,5446),(90,'상품350','상품 설명120','L',37,34774,'2024-09-06 17:19:23',104,389,5795),(92,'상품562','상품 설명71','XL',15,83431,'2024-09-06 17:19:23',218,446,8031),(93,'상품339','상품 설명288','M',25,12360,'2024-09-06 17:19:23',338,310,890),(94,'상품582','상품 설명647','M',49,10091,'2024-09-06 17:19:23',528,318,6036),(95,'상품105','상품 설명717','S',19,25665,'2024-09-06 17:19:23',205,278,1698),(96,'상품177','상품 설명379','M',67,37767,'2024-09-06 17:19:23',370,9,9799),(97,'상품845','상품 설명287','FREE',63,59165,'2024-09-06 17:19:23',543,120,5764),(98,'상품158','상품 설명63','FREE',1,67059,'2024-09-06 17:19:23',795,131,9315),(99,'상품867','상품 설명544','XS',96,55350,'2024-09-06 17:19:23',380,272,5775),(100,'상품255','상품 설명545','FREE',17,107189,'2024-09-06 17:19:23',345,405,156),(102,'상품647','상품 설명191','XS',49,54795,'2024-09-06 17:19:23',744,189,6642),(104,'상품182','상품 설명918','XS',48,35937,'2024-09-06 17:19:23',855,250,9400),(105,'상품195','상품 설명156','S',51,10397,'2024-09-06 17:19:23',461,146,839),(108,'상품539','상품 설명444','L',69,75660,'2024-09-06 17:19:23',198,11,5203),(110,'상품531','상품 설명98','FREE',19,37700,'2024-09-06 17:19:23',802,89,4924),(111,'상품922','상품 설명137','FREE',17,21263,'2024-09-06 17:19:23',45,444,3095),(113,'상품880','상품 설명472','XL',19,89546,'2024-09-06 17:19:23',400,307,8784),(114,'상품544','상품 설명86','XL',73,39148,'2024-09-06 17:19:23',243,171,9821),(116,'상품883','상품 설명470','XL',9,45600,'2024-09-06 17:19:23',505,230,7894),(117,'상품562','상품 설명445','L',36,29852,'2024-09-06 17:19:23',901,455,8476),(118,'상품507','상품 설명994','M',26,107098,'2024-09-06 17:19:23',63,202,8316),(119,'상품944','상품 설명229','S',87,54634,'2024-09-06 17:19:23',600,331,5096),(121,'상품562','상품 설명281','XL',76,74645,'2024-09-06 17:19:23',946,395,1154),(122,'상품205','상품 설명680','XL',88,16237,'2024-09-06 17:19:23',661,61,6264),(123,'상품764','상품 설명944','M',31,36674,'2024-09-06 17:19:23',401,103,8340),(124,'상품546','상품 설명232','L',90,107894,'2024-09-06 17:19:23',169,455,422),(125,'상품480','상품 설명277','FREE',89,75905,'2024-09-06 17:19:23',595,0,2214),(127,'상품102','상품 설명846','FREE',8,73480,'2024-09-06 17:19:23',930,373,9414),(128,'상품468','상품 설명516','S',34,30334,'2024-09-06 17:19:23',970,122,3101),(129,'상품817','상품 설명154','S',14,87006,'2024-09-06 17:19:23',408,365,4293),(130,'상품954','상품 설명483','L',33,108328,'2024-09-06 17:19:23',924,337,5967),(131,'상품960','상품 설명14','S',90,104438,'2024-09-06 17:19:23',14,120,1628),(132,'상품89','상품 설명959','L',77,37716,'2024-09-06 17:19:23',64,244,2535),(133,'상품800','상품 설명240','XL',28,10934,'2024-09-06 17:19:23',200,486,2615),(134,'상품390','상품 설명168','XL',84,32774,'2024-09-06 17:19:23',594,145,6698),(136,'상품476','상품 설명373','M',7,13620,'2024-09-06 17:19:23',970,372,8087),(137,'상품811','상품 설명631','XL',72,53613,'2024-09-06 17:19:23',17,388,8381),(138,'상품856','상품 설명769','S',7,64778,'2024-09-06 17:19:23',512,458,484),(139,'상품491','상품 설명312','XS',50,34995,'2024-09-06 17:19:23',741,477,5561),(140,'상품913','상품 설명896','XL',3,104670,'2024-09-06 17:19:23',623,138,5187),(141,'상품759','상품 설명243','FREE',95,105734,'2024-09-06 17:19:23',926,379,192),(142,'상품817','상품 설명27','XL',35,81513,'2024-09-06 17:19:23',508,198,4595),(144,'상품106','상품 설명154','M',80,78061,'2024-09-06 17:19:23',976,419,2642),(145,'상품805','상품 설명235','XL',9,28409,'2024-09-06 17:19:23',642,330,3748),(146,'상품892','상품 설명339','XS',7,43742,'2024-09-06 17:19:23',449,117,8234),(147,'상품414','상품 설명601','XL',1,87048,'2024-09-06 17:19:23',816,385,4063),(149,'상품718','상품 설명371','XL',40,99923,'2024-09-06 17:19:23',290,377,9077),(150,'상품270','상품 설명629','M',79,107937,'2024-09-06 17:19:23',501,284,3433),(152,'상품7','상품 설명9','XS',8,47539,'2024-09-06 17:19:23',609,460,7773),(153,'상품123','상품 설명284','XS',40,98813,'2024-09-06 17:19:23',213,200,3696),(154,'상품642','상품 설명103','L',63,51933,'2024-09-06 17:19:23',183,330,7553),(155,'상품792','상품 설명696','XS',43,94800,'2024-09-06 17:19:23',943,87,399),(157,'상품677','상품 설명265','S',68,63392,'2024-09-06 17:19:23',616,239,5469),(158,'상품297','상품 설명846','M',15,86692,'2024-09-06 17:19:23',364,259,5042),(159,'상품963','상품 설명305','L',26,50765,'2024-09-06 17:19:23',249,11,3676),(160,'상품769','상품 설명743','M',81,93498,'2024-09-06 17:19:23',738,94,7321),(162,'상품91','상품 설명259','XS',33,72435,'2024-09-06 17:19:23',105,326,9457),(163,'상품772','상품 설명24','XL',94,43181,'2024-09-06 17:19:23',812,32,8905),(164,'상품256','상품 설명610','S',58,19533,'2024-09-06 17:19:23',707,125,1362),(166,'상품925','상품 설명220','S',97,97360,'2024-09-06 17:19:23',455,329,9244),(167,'상품647','상품 설명462','M',47,34024,'2024-09-06 17:19:23',787,108,7261),(168,'상품976','상품 설명706','L',89,74956,'2024-09-06 17:19:23',574,460,8854),(170,'상품662','상품 설명657','S',52,84415,'2024-09-06 17:19:23',133,217,7712),(172,'상품553','상품 설명454','L',69,72671,'2024-09-06 17:19:23',57,204,8688),(173,'상품118','상품 설명988','L',95,12138,'2024-09-06 17:19:23',243,76,321),(174,'상품703','상품 설명420','FREE',70,64253,'2024-09-06 17:19:23',599,184,429),(175,'상품110','상품 설명424','XL',67,12043,'2024-09-06 17:19:23',67,137,1737),(176,'상품43','상품 설명696','M',66,38895,'2024-09-06 17:19:23',438,162,3116),(178,'상품581','상품 설명974','XS',71,29297,'2024-09-06 17:19:23',818,257,1204),(179,'상품55','상품 설명918','M',35,62934,'2024-09-06 17:19:23',568,126,5600),(180,'상품41','상품 설명527','L',98,49786,'2024-09-06 17:19:23',24,465,5774),(181,'상품96','상품 설명749','M',3,90108,'2024-09-06 17:19:23',903,57,8632),(182,'상품971','상품 설명269','M',34,52284,'2024-09-06 17:19:23',86,82,5632),(183,'상품322','상품 설명921','L',44,41356,'2024-09-06 17:19:23',222,85,1913),(186,'상품441','상품 설명633','FREE',32,17745,'2024-09-06 17:19:23',420,433,796),(187,'상품794','상품 설명733','S',21,30590,'2024-09-06 17:19:23',398,187,6806),(189,'상품277','상품 설명345','FREE',43,58633,'2024-09-06 17:19:23',130,97,5849),(190,'상품338','상품 설명936','XL',53,75476,'2024-09-06 17:19:23',677,212,907),(191,'상품179','상품 설명623','L',2,48302,'2024-09-06 17:19:23',843,34,8166),(192,'상품874','상품 설명922','FREE',16,98481,'2024-09-06 17:19:23',916,463,8899),(193,'상품666','상품 설명660','S',54,88737,'2024-09-06 17:19:23',316,110,1567),(194,'상품119','상품 설명128','S',3,41670,'2024-09-06 17:19:23',482,230,8625),(195,'상품927','상품 설명47','M',13,42300,'2024-09-06 17:19:23',200,15,5590),(196,'상품699','상품 설명819','FREE',53,79836,'2024-09-06 17:19:23',873,136,7428),(197,'상품896','상품 설명253','L',12,97750,'2024-09-06 17:19:23',24,244,3693),(198,'상품381','상품 설명800','FREE',87,92431,'2024-09-06 17:19:23',486,480,3419),(199,'상품828','상품 설명117','XS',15,56836,'2024-09-06 17:19:23',878,493,2968),(200,'상품525','상품 설명735','XS',30,29920,'2024-09-06 17:19:23',94,436,807),(201,'상품786','상품 설명688','XS',35,60557,'2024-09-06 17:19:23',474,428,8612),(202,'상품734','상품 설명91','S',97,24615,'2024-09-06 17:19:23',792,260,2329),(203,'상품599','상품 설명298','XL',57,87954,'2024-09-06 17:19:23',181,285,3079),(204,'상품827','상품 설명213','L',29,79205,'2024-09-06 17:19:23',589,436,5923),(206,'상품345','상품 설명947','XL',67,37961,'2024-09-06 17:19:23',362,486,7758),(208,'상품961','상품 설명480','L',14,27104,'2024-09-06 17:19:23',421,297,7137),(209,'상품781','상품 설명765','M',11,23574,'2024-09-06 17:19:23',328,118,1969),(210,'상품274','상품 설명782','XS',9,31046,'2024-09-06 17:19:23',766,100,7095),(211,'상품942','상품 설명581','XS',66,18294,'2024-09-06 17:19:23',417,418,9299),(212,'상품141','상품 설명915','XS',2,76521,'2024-09-06 17:19:23',248,124,4979),(213,'상품742','상품 설명219','FREE',69,97016,'2024-09-06 17:19:23',260,345,6764),(214,'상품307','상품 설명509','L',59,18149,'2024-09-06 17:19:23',635,467,7653),(215,'상품23','상품 설명819','XS',67,41484,'2024-09-06 17:19:23',535,367,648),(216,'상품120','상품 설명409','XL',20,105351,'2024-09-06 17:19:23',159,469,2167),(218,'상품265','상품 설명678','L',94,103425,'2024-09-06 17:19:23',837,192,4155),(219,'상품920','상품 설명355','XS',1,14600,'2024-09-06 17:19:23',171,358,751),(220,'상품222','상품 설명887','XL',19,73846,'2024-09-06 17:19:23',622,97,1091),(221,'상품960','상품 설명475','M',4,85819,'2024-09-06 17:19:23',645,475,8217),(222,'상품254','상품 설명806','S',93,94093,'2024-09-06 17:19:23',414,273,4954),(223,'상품834','상품 설명687','FREE',61,34683,'2024-09-06 17:19:23',403,138,1739),(225,'상품38','상품 설명673','S',22,48155,'2024-09-06 17:19:23',230,3,3478),(227,'상품715','상품 설명533','L',1,60311,'2024-09-06 17:19:23',471,424,8326),(228,'상품614','상품 설명573','XS',40,106678,'2024-09-06 17:19:23',606,67,8498),(229,'상품846','상품 설명684','FREE',35,24011,'2024-09-06 17:19:23',628,361,7276),(230,'상품470','상품 설명168','M',65,108502,'2024-09-06 17:19:23',955,411,2462),(231,'상품763','상품 설명79','XS',28,23023,'2024-09-06 17:19:23',783,262,2791),(232,'상품818','상품 설명254','XL',32,25798,'2024-09-06 17:19:23',824,324,7689),(233,'상품899','상품 설명189','S',68,75690,'2024-09-06 17:19:23',240,115,4336),(234,'상품475','상품 설명76','FREE',55,102294,'2024-09-06 17:19:23',936,456,7570),(235,'상품45','상품 설명956','L',35,94455,'2024-09-06 17:19:23',155,121,7447),(236,'상품997','상품 설명754','XL',63,91984,'2024-09-06 17:19:23',205,283,2174),(237,'상품387','상품 설명284','S',44,53046,'2024-09-06 17:19:23',828,425,7662),(238,'상품280','상품 설명103','XL',6,38674,'2024-09-06 17:19:23',248,190,1572),(239,'상품645','상품 설명754','FREE',91,15555,'2024-09-06 17:19:23',541,271,852),(240,'상품800','상품 설명744','S',38,104591,'2024-09-06 17:19:23',579,30,5636),(241,'상품636','상품 설명493','L',29,92281,'2024-09-06 17:19:23',220,316,5081),(243,'상품639','상품 설명670','M',16,64050,'2024-09-06 17:19:23',192,170,1313),(244,'상품631','상품 설명762','FREE',29,84089,'2024-09-06 17:19:23',809,412,6933),(245,'상품994','상품 설명889','M',66,103974,'2024-09-06 17:19:23',693,323,1510),(246,'상품817','상품 설명632','XL',66,29232,'2024-09-06 17:19:23',962,118,2967),(247,'상품772','상품 설명973','L',82,56116,'2024-09-06 17:19:23',842,414,6150),(248,'상품590','상품 설명105','XL',46,15282,'2024-09-06 17:19:23',871,99,3838),(249,'상품320','상품 설명448','S',6,58304,'2024-09-06 17:19:23',217,318,5355),(250,'상품765','상품 설명218','XL',33,38340,'2024-09-06 17:19:23',409,99,7673),(251,'상품238','상품 설명890','XL',1,96712,'2024-09-06 17:19:23',287,418,3226),(252,'상품101','상품 설명540','M',37,77010,'2024-09-06 17:19:23',229,69,39),(254,'상품603','상품 설명7','S',10,94043,'2024-09-06 17:19:23',892,471,375),(255,'상품357','상품 설명676','S',52,78815,'2024-09-06 17:19:23',868,137,7746),(256,'상품46','상품 설명906','M',25,16946,'2024-09-06 17:19:23',596,387,833),(257,'상품92','상품 설명214','XL',32,33554,'2024-09-06 17:19:23',208,168,549),(258,'상품266','상품 설명166','XS',66,34441,'2024-09-06 17:19:23',214,170,582),(259,'상품269','상품 설명174','XS',78,84753,'2024-09-06 17:19:23',377,323,972),(260,'상품548','상품 설명448','L',64,53924,'2024-09-06 17:19:23',255,479,285),(261,'상품265','상품 설명243','M',37,72113,'2024-09-06 17:19:23',973,2,1057),(262,'상품512','상품 설명246','XL',72,63811,'2024-09-06 17:19:23',521,497,4059),(263,'상품47','상품 설명18','FREE',69,70968,'2024-09-06 17:19:23',974,20,2842),(266,'상품297','상품 설명634','S',50,76545,'2024-09-06 17:19:23',821,56,982),(267,'상품152','상품 설명469','FREE',3,61053,'2024-09-06 17:19:23',446,350,1678),(268,'상품734','상품 설명170','L',72,78740,'2024-09-06 17:19:23',259,117,3926),(269,'상품260','상품 설명126','FREE',86,87259,'2024-09-06 17:19:23',272,21,4014),(270,'상품876','상품 설명177','S',75,10505,'2024-09-06 17:19:23',758,389,6170),(271,'상품749','상품 설명895','S',45,68834,'2024-09-06 17:19:23',578,63,8980),(272,'상품110','상품 설명857','FREE',21,29736,'2024-09-06 17:19:23',345,66,6316),(273,'상품758','상품 설명895','S',33,14440,'2024-09-06 17:19:23',228,5,3715),(274,'상품822','상품 설명997','L',61,60498,'2024-09-06 17:19:23',683,450,4597),(275,'상품592','상품 설명585','XS',97,55191,'2024-09-06 17:19:23',324,132,3560),(276,'상품982','상품 설명845','S',86,56487,'2024-09-06 17:19:23',742,158,3613),(277,'상품853','상품 설명182','M',22,15157,'2024-09-06 17:19:23',589,395,1878),(278,'상품565','상품 설명265','L',36,101181,'2024-09-06 17:19:23',474,319,7664),(279,'상품918','상품 설명290','XL',62,13781,'2024-09-06 17:19:23',306,209,1722),(281,'상품606','상품 설명516','XL',26,11782,'2024-09-06 17:19:23',306,240,4833),(282,'상품974','상품 설명422','S',68,93788,'2024-09-06 17:19:23',144,105,6169),(283,'상품454','상품 설명419','XL',41,95632,'2024-09-06 17:19:23',46,332,1882),(284,'상품943','상품 설명154','FREE',23,44479,'2024-09-06 17:19:23',27,50,4246),(285,'상품819','상품 설명824','L',84,34096,'2024-09-06 17:19:23',664,300,112),(286,'상품252','상품 설명230','M',27,27479,'2024-09-06 17:19:23',63,396,7715),(287,'상품480','상품 설명90','XS',76,91736,'2024-09-06 17:19:23',782,230,9557),(289,'상품396','상품 설명115','M',59,90115,'2024-09-06 17:19:23',226,365,9740),(290,'상품677','상품 설명465','S',8,61804,'2024-09-06 17:19:23',347,91,8707),(291,'상품805','상품 설명417','XL',9,57814,'2024-09-06 17:19:23',97,27,9770),(292,'상품723','상품 설명684','S',20,36525,'2024-09-06 17:19:23',715,389,7513),(293,'상품418','상품 설명840','FREE',21,32524,'2024-09-06 17:19:23',486,377,3173),(294,'상품321','상품 설명653','S',55,97950,'2024-09-06 17:19:23',723,488,7196),(295,'상품664','상품 설명163','XL',62,74629,'2024-09-06 17:19:23',363,438,2990),(296,'상품861','상품 설명411','M',13,34229,'2024-09-06 17:19:24',813,170,2654),(297,'상품303','상품 설명720','XL',29,49976,'2024-09-06 17:19:24',115,188,5364),(298,'상품553','상품 설명156','XS',13,42685,'2024-09-06 17:19:24',219,59,9338),(299,'상품313','상품 설명763','FREE',11,101157,'2024-09-06 17:19:24',226,197,2995),(300,'상품311','상품 설명657','M',79,101618,'2024-09-06 17:19:24',194,112,5437),(302,'상품41','상품 설명578','XL',9,26715,'2024-09-06 17:19:24',557,143,7597),(303,'상품939','상품 설명417','S',9,78126,'2024-09-06 17:19:24',110,255,2225),(304,'상품579','상품 설명228','M',34,61919,'2024-09-06 17:19:24',551,100,3530),(305,'상품160','상품 설명741','S',91,98450,'2024-09-06 17:19:24',681,378,7362),(306,'상품412','상품 설명851','XS',55,80524,'2024-09-06 17:19:24',863,101,4256),(307,'상품517','상품 설명312','XS',11,63497,'2024-09-06 17:19:24',335,35,3511),(308,'상품541','상품 설명654','L',27,53210,'2024-09-06 17:19:24',335,190,8971),(309,'상품343','상품 설명26','XS',42,93847,'2024-09-06 17:19:24',906,8,3638),(310,'상품769','상품 설명754','M',6,101559,'2024-09-06 17:19:24',391,104,8745),(311,'상품743','상품 설명95','S',94,109710,'2024-09-06 17:19:24',142,361,1835),(312,'상품750','상품 설명203','XL',21,89236,'2024-09-06 17:19:24',307,81,8892),(313,'상품958','상품 설명124','XL',35,63662,'2024-09-06 17:19:24',619,243,5798),(315,'상품436','상품 설명441','FREE',16,22437,'2024-09-06 17:19:24',132,143,390),(316,'상품334','상품 설명553','XL',16,63893,'2024-09-06 17:19:24',193,174,1675),(318,'상품789','상품 설명444','FREE',94,24975,'2024-09-06 17:19:24',918,72,9703),(319,'상품415','상품 설명165','L',41,42438,'2024-09-06 17:19:24',379,461,4784),(320,'상품622','상품 설명676','L',55,30714,'2024-09-06 17:19:24',382,144,3006),(322,'상품635','상품 설명273','M',48,13833,'2024-09-06 17:19:24',739,290,6914),(323,'상품711','상품 설명483','S',95,104242,'2024-09-06 17:19:24',840,186,3477),(324,'상품617','상품 설명46','M',75,72980,'2024-09-06 17:19:24',889,279,1292),(325,'상품967','상품 설명448','M',35,83881,'2024-09-06 17:19:24',640,492,50),(326,'상품69','상품 설명333','M',28,15694,'2024-09-06 17:19:24',427,484,5585),(327,'상품887','상품 설명761','XS',44,89947,'2024-09-06 17:19:24',653,434,3887),(329,'상품333','상품 설명503','L',6,87315,'2024-09-06 17:19:24',675,29,2636),(330,'상품144','상품 설명930','S',30,98252,'2024-09-06 17:19:24',486,393,4752),(331,'상품14','상품 설명647','S',2,66393,'2024-09-06 17:19:24',731,481,6257),(332,'상품237','상품 설명309','FREE',24,81157,'2024-09-06 17:19:24',829,6,5777),(333,'상품848','상품 설명511','XS',51,64352,'2024-09-06 17:19:24',174,119,6768),(334,'상품664','상품 설명294','M',49,16551,'2024-09-06 17:19:24',830,478,2958),(335,'상품606','상품 설명143','FREE',6,74022,'2024-09-06 17:19:24',997,32,3378),(336,'상품491','상품 설명443','XL',38,78030,'2024-09-06 17:19:24',257,122,4559),(337,'상품543','상품 설명348','XS',51,32418,'2024-09-06 17:19:24',586,129,5337),(338,'상품893','상품 설명866','L',65,42466,'2024-09-06 17:19:24',656,155,5833),(340,'상품984','상품 설명171','FREE',0,40894,'2024-09-06 17:19:24',532,369,914),(342,'상품242','상품 설명939','FREE',3,35770,'2024-09-06 17:19:24',186,80,2412),(343,'상품725','상품 설명903','M',0,108149,'2024-09-06 17:19:24',901,282,1157),(344,'상품886','상품 설명83','XL',55,57364,'2024-09-06 17:19:24',715,77,6276),(346,'상품674','상품 설명490','M',66,13412,'2024-09-06 17:19:24',182,404,4974),(347,'상품60','상품 설명810','FREE',92,11853,'2024-09-06 17:19:24',312,253,5932),(349,'상품448','상품 설명460','FREE',41,27799,'2024-09-06 17:19:24',657,377,8058),(350,'상품761','상품 설명390','L',15,89572,'2024-09-06 17:19:24',502,63,1255);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_attribute_link`
--

DROP TABLE IF EXISTS `product_attribute_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_attribute_link` (
  `link_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `attribute_id` bigint NOT NULL,
  PRIMARY KEY (`link_id`),
  KEY `product_id` (`product_id`),
  KEY `product_attribute_link_ibfk_2` (`attribute_id`),
  CONSTRAINT `product_attribute_link_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `product_attribute_link_ibfk_2` FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`attribute_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_attribute_link`
--

LOCK TABLES `product_attribute_link` WRITE;
/*!40000 ALTER TABLE `product_attribute_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_attribute_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_attributes`
--

DROP TABLE IF EXISTS `product_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_attributes` (
  `attribute_id` bigint NOT NULL,
  `attribute_type` varchar(255) NOT NULL,
  `name_ko` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`attribute_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_attributes`
--

LOCK TABLES `product_attributes` WRITE;
/*!40000 ALTER TABLE `product_attributes` DISABLE KEYS */;
INSERT INTO `product_attributes` VALUES (1,'texture','패딩'),(2,'texture','무스탕'),(3,'texture','퍼프'),(4,'texture','네오프렌'),(5,'texture','코듀로이'),(6,'texture','트위드'),(7,'texture','자카드'),(8,'texture','니트'),(9,'texture','페플럼'),(10,'texture','레이스'),(11,'texture','스판덱스'),(12,'texture','메시'),(13,'texture','비닐/PVC'),(14,'texture','데님'),(15,'texture','울/캐시미어'),(16,'texture','저지'),(17,'texture','시퀸/글리터'),(18,'texture','퍼'),(19,'texture','헤어 니트'),(20,'texture','실크'),(21,'texture','린넨'),(22,'texture','플리스'),(23,'texture','시폰'),(24,'texture','스웨이드'),(25,'texture','가죽'),(26,'texture','우븐'),(27,'texture','벨벳'),(28,'print','페이즐리'),(29,'print','하트'),(30,'print','지그재그'),(31,'print','깅엄'),(32,'print','하운즈 투스'),(33,'print','도트'),(34,'print','레터링'),(35,'print','믹스'),(36,'print','뱀피'),(37,'print','해골'),(38,'print','체크'),(39,'print','무지'),(40,'print','카무플라쥬'),(41,'print','그라데이션'),(42,'print','스트라이프'),(43,'print','호피'),(44,'print','아가일'),(45,'print','그래픽'),(46,'print','지브라'),(47,'print','타이다이'),(48,'print','플로럴'),(49,'detail','스터드'),(50,'detail','드롭숄더'),(51,'detail','드롭웨이스트'),(52,'detail','레이스업'),(53,'detail','슬릿'),(54,'detail','프릴'),(55,'detail','단추'),(56,'detail','퀄팅'),(57,'detail','스팽글'),(58,'detail','롤업'),(59,'detail','니트꽈베기'),(60,'detail','체인'),(61,'detail','프린지'),(62,'detail','지퍼'),(63,'detail','태슬'),(64,'detail','띠'),(65,'detail','플레어'),(66,'detail','싱글브레스티드'),(67,'detail','더블브레스티드'),(68,'detail','스트링'),(69,'detail','자수'),(70,'detail','폼폼'),(71,'detail','디스트로이드'),(72,'detail','페플럼'),(73,'detail','X스트랩'),(74,'detail','스티치'),(75,'detail','레이스'),(76,'detail','퍼프'),(77,'detail','비즈'),(78,'detail','컷아웃'),(79,'detail','버클'),(80,'detail','포켓'),(81,'detail','러플'),(82,'detail','글리터'),(83,'detail','퍼트리밍'),(84,'detail','플리츠'),(85,'detail','비대칭'),(86,'detail','셔링'),(87,'detail','패치워크'),(88,'detail','리본'),(89,'category','재킷'),(90,'category','조거팬츠'),(91,'category','짚업'),(92,'category','스커트'),(93,'category','가디건'),(94,'category','점퍼'),(95,'category','티셔츠'),(96,'category','셔츠'),(97,'category','팬츠'),(98,'category','드레스'),(99,'category','패딩'),(100,'category','청바지'),(101,'category','점프수트'),(102,'category','니트웨어'),(103,'category','베스트'),(104,'category','코트'),(105,'category','브라탑'),(106,'category','블라우스'),(107,'category','탑'),(108,'category','후드티'),(109,'category','래깅스');
/*!40000 ALTER TABLE `product_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_img`
--

DROP TABLE IF EXISTS `product_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_img` (
  `pimg_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `pimg_name` varchar(255) DEFAULT NULL,
  `pimg_data` tinyblob,
  PRIMARY KEY (`pimg_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `product_img_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 이미지 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_img`
--

LOCK TABLES `product_img` WRITE;
/*!40000 ALTER TABLE `product_img` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_img` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qboard`
--

DROP TABLE IF EXISTS `qboard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qboard` (
  `qboard_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `board_type` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `edited_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`qboard_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `qboard_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Q&A 게시판 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qboard`
--

LOCK TABLES `qboard` WRITE;
/*!40000 ALTER TABLE `qboard` DISABLE KEYS */;
/*!40000 ALTER TABLE `qboard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qboard_img`
--

DROP TABLE IF EXISTS `qboard_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qboard_img` (
  `qimg_id` bigint NOT NULL AUTO_INCREMENT,
  `qboard_id` bigint DEFAULT NULL,
  `qimg_name` varchar(255) DEFAULT NULL,
  `qimg_data` mediumblob,
  PRIMARY KEY (`qimg_id`),
  KEY `qboard_id` (`qboard_id`),
  CONSTRAINT `qboard_img_ibfk_1` FOREIGN KEY (`qboard_id`) REFERENCES `qboard` (`qboard_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Q&A 게시판 이미지 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qboard_img`
--

LOCK TABLES `qboard_img` WRITE;
/*!40000 ALTER TABLE `qboard_img` DISABLE KEYS */;
/*!40000 ALTER TABLE `qboard_img` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment`
--

DROP TABLE IF EXISTS `shipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment` (
  `ship_id` bigint NOT NULL AUTO_INCREMENT,
  `shipping_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `shipping_status` varchar(50) DEFAULT 'PENDING',
  `order_id` bigint NOT NULL,
  `shipping_address_id` bigint NOT NULL,
  `invoice_code` varchar(255) DEFAULT NULL,
  `courier_company` varchar(255) DEFAULT NULL,
  `invoice_created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_updated` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ship_id`),
  KEY `order_id` (`order_id`),
  KEY `fk_shipment_shipping_address` (`shipping_address_id`),
  CONSTRAINT `fk_shipment_shipping_address` FOREIGN KEY (`shipping_address_id`) REFERENCES `shipping_addresses` (`address_id`),
  CONSTRAINT `shipment_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment`
--

LOCK TABLES `shipment` WRITE;
/*!40000 ALTER TABLE `shipment` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_addresses`
--

DROP TABLE IF EXISTS `shipping_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_addresses` (
  `address_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `delivery_instructions` varchar(255) DEFAULT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`address_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `shipping_addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사용자별 배송지 목록 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_addresses`
--

LOCK TABLES `shipping_addresses` WRITE;
/*!40000 ALTER TABLE `shipping_addresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipping_addresses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-09-06 17:37:52
