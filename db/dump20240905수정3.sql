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
  `product_id` bigint DEFAULT NULL,
  `cart_qty` int DEFAULT NULL,
  PRIMARY KEY (`cart_id`),
  KEY `product_id` (`product_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE,
  CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`) ON DELETE SET NULL
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
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_code` varchar(255) DEFAULT NULL,
  `invoice_company` varchar(255) DEFAULT NULL,
  `invoice_create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `invoice_edited_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `order_id` bigint NOT NULL,
  `ship_id` bigint DEFAULT NULL,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `UKr6eudgf84s3b7c3khk8fbt428` (`ship_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `FK7tdgyy47y7967g56iomvswcsp` FOREIGN KEY (`ship_id`) REFERENCES `shipment` (`ship_id`),
  CONSTRAINT `FKt398hhbi6q4kpbbdao49daca5` FOREIGN KEY (`invoice_id`) REFERENCES `shipment` (`ship_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
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
  `total_price` decimal(38,2) DEFAULT NULL,
  `order_comment` varchar(255) DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `receive_name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `tel` varchar(255) DEFAULT NULL,
  `order_status` varchar(50) DEFAULT 'PENDING',
  `product_id` bigint DEFAULT NULL,
  `shipping_address_id` bigint DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `user_id` (`user_id`),
  KEY `FK787ibr3guwp6xobrpbofnv7le` (`product_id`),
  KEY `fk_order_shipping_address` (`shipping_address_id`),
  CONSTRAINT `FK787ibr3guwp6xobrpbofnv7le` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
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
  `p_name` varchar(255) DEFAULT NULL,
  `p_info` varchar(255) DEFAULT NULL,
  `p_stock` bigint DEFAULT NULL,
  `p_create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `p_sell` bigint DEFAULT NULL,
  `p_like` bigint DEFAULT NULL,
  `p_view` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
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
  `invoice_id` bigint NOT NULL,
  `ship_name` varchar(255) DEFAULT NULL,
  `ship_receive_name` varchar(255) DEFAULT NULL,
  `ship_tel` varchar(255) DEFAULT NULL,
  `ship_zipcode` varchar(255) DEFAULT NULL,
  `ship_address` varchar(255) DEFAULT NULL,
  `ship_start_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `ship_end_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `ship_status` varchar(255) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `ship_recive_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ship_id`),
  KEY `order_id` (`order_id`),
  KEY `invoice_id` (`invoice_id`),
  CONSTRAINT `shipment_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `shipment_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`invoice_id`) ON DELETE CASCADE
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
  `phone` varchar(20) NOT NULL,
  `delivery_instructions` text,
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

-- Dump completed on 2024-09-05 17:18:41
