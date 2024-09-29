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
-- Table structure for table `ai_analysis`
--

DROP TABLE IF EXISTS `ai_analysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_analysis` (
  `id` int NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `analysis_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `image_path` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ai_analysis_member` (`member_id`),
  KEY `idx_ai_analysis_member_id` (`member_id`),
  KEY `idx_ai_analysis_date` (`analysis_date`),
  CONSTRAINT `fk_ai_analysis_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ai_analysis_styles`
--

DROP TABLE IF EXISTS `ai_analysis_styles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_analysis_styles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ai_analysis_id` int NOT NULL,
  `attribute_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ai_analysis_styles_analysis` (`ai_analysis_id`),
  KEY `fk_ai_analysis_styles_attribute` (`attribute_id`),
  CONSTRAINT `fk_ai_analysis_styles_analysis` FOREIGN KEY (`ai_analysis_id`) REFERENCES `ai_analysis` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ai_analysis_styles_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ai_recommendations`
--

DROP TABLE IF EXISTS `ai_recommendations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_recommendations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ai_analysis_id` int NOT NULL,
  `product_id` bigint NOT NULL,
  `recommendation_order` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ai_recommendations_analysis` (`ai_analysis_id`),
  KEY `fk_ai_recommendations_product` (`product_id`),
  KEY `idx_ai_recommendation_product_id` (`product_id`),
  KEY `idx_ai_recommendation_order` (`recommendation_order`),
  CONSTRAINT `fk_ai_recommendations_analysis` FOREIGN KEY (`ai_analysis_id`) REFERENCES `ai_analysis` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ai_recommendations_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='장바구니 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `session_id` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cart_item_id`),
  KEY `cart_id` (`cart_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`) ON DELETE CASCADE,
  CONSTRAINT `cart_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=196675 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='장바구니 아이템 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `cart_summary_view`
--

DROP TABLE IF EXISTS `cart_summary_view`;
/*!50001 DROP VIEW IF EXISTS `cart_summary_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `cart_summary_view` AS SELECT 
 1 AS `user_id`,
 1 AS `session_id`,
 1 AS `cart_item_id`,
 1 AS `product_id`,
 1 AS `product_name`,
 1 AS `category`,
 1 AS `quantity`,
 1 AS `price`,
 1 AS `total_price`,
 1 AS `image_url`,
 1 AS `size`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `category_sales_percentage`
--

DROP TABLE IF EXISTS `category_sales_percentage`;
/*!50001 DROP VIEW IF EXISTS `category_sales_percentage`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `category_sales_percentage` AS SELECT 
 1 AS `category`,
 1 AS `order_count`,
 1 AS `total_sales`,
 1 AS `sales_percentage`*/;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=856 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글 댓글 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `daily_sales_report`
--

DROP TABLE IF EXISTS `daily_sales_report`;
/*!50001 DROP VIEW IF EXISTS `daily_sales_report`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `daily_sales_report` AS SELECT 
 1 AS `date`,
 1 AS `order_count`,
 1 AS `total_sales`*/;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='회원 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member_likes`
--

DROP TABLE IF EXISTS `member_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn7poq6mj1uhrhav0sip910vao` (`user_id`),
  KEY `FKsiukmi17lrd125n5y28dt24p2` (`product_id`),
  CONSTRAINT `FKn7poq6mj1uhrhav0sip910vao` FOREIGN KEY (`user_id`) REFERENCES `member` (`user_id`),
  CONSTRAINT `FKsiukmi17lrd125n5y28dt24p2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `monthly_sales_report`
--

DROP TABLE IF EXISTS `monthly_sales_report`;
/*!50001 DROP VIEW IF EXISTS `monthly_sales_report`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `monthly_sales_report` AS SELECT 
 1 AS `month`,
 1 AS `order_count`,
 1 AS `total_sales`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `order_details_view`
--

DROP TABLE IF EXISTS `order_details_view`;
/*!50001 DROP VIEW IF EXISTS `order_details_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `order_details_view` AS SELECT 
 1 AS `order_id`,
 1 AS `user_id`,
 1 AS `order_date`,
 1 AS `order_status`,
 1 AS `product_id`,
 1 AS `product_name`,
 1 AS `quantity`,
 1 AS `price`,
 1 AS `total_price`*/;
SET character_set_client = @saved_cs_client;

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
  `price` bigint DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2853310 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 상품 상세 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=2003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='주문 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `info` varchar(255) DEFAULT NULL,
  `price` bigint NOT NULL COMMENT '상품 가격 (원 단위)',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `sell` bigint DEFAULT NULL,
  `like_count` bigint DEFAULT NULL,
  `view_count` bigint DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `idx_product_name` (`name`),
  KEY `idx_product_price` (`price`),
  KEY `idx_product_create_date` (`create_date`)
) ENGINE=InnoDB AUTO_INCREMENT=5765 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
  KEY `idx_product_attribute_link_product_id` (`product_id`),
  KEY `idx_product_attribute_link_attribute_id` (`attribute_id`),
  CONSTRAINT `product_attribute_link_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `product_attribute_link_ibfk_2` FOREIGN KEY (`attribute_id`) REFERENCES `product_attributes` (`attribute_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `product_img`
--

DROP TABLE IF EXISTS `product_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_img` (
  `pimg_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `pimg_name` varchar(255) DEFAULT NULL,
  `pimg_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pimg_id`),
  KEY `idx_product_img_product_id` (`product_id`),
  CONSTRAINT `product_img_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10509 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 이미지 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_inventory`
--

DROP TABLE IF EXISTS `product_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_inventory` (
  `inventory_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `size` enum('XS','S','M','L','XL') NOT NULL,
  `stock` bigint DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`inventory_id`),
  KEY `product_id` (`product_id`),
  KEY `idx_product_inventory_size` (`size`),
  KEY `idx_product_inventory_stock` (`stock`),
  CONSTRAINT `product_inventory_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24774 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='상품 사이즈 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=237 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Q&A 게시판 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `qimg_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`qimg_id`),
  KEY `qboard_id` (`qboard_id`),
  CONSTRAINT `qboard_img_ibfk_1` FOREIGN KEY (`qboard_id`) REFERENCES `qboard` (`qboard_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Q&A 게시판 이미지 정보 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사용자별 배송지 목록 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stores`
--

DROP TABLE IF EXISTS `stores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores` (
  `store_id` bigint NOT NULL AUTO_INCREMENT,
  `store_type` varchar(255) DEFAULT NULL,
  `store_code` varchar(255) DEFAULT NULL,
  `store_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stores_sales`
--

DROP TABLE IF EXISTS `stores_sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores_sales` (
  `sale_id` bigint NOT NULL AUTO_INCREMENT,
  `sale_date` datetime(6) DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `size_id` enum('XS','S','M','L','XL') DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `transaction_type` varchar(255) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`sale_id`),
  KEY `size_id` (`size_id`),
  KEY `stores_sales_ibfk_1` (`store_id`),
  KEY `stores_sales_ibfk_2` (`product_id`),
  KEY `fk_category_id` (`category_id`),
  CONSTRAINT `fk_category_id` FOREIGN KEY (`category_id`) REFERENCES `product_attributes` (`attribute_id`),
  CONSTRAINT `fk_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `fk_size_id` FOREIGN KEY (`size_id`) REFERENCES `product_inventory` (`size`),
  CONSTRAINT `fk_store_id` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=58214 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `cart_summary_view`
--

/*!50001 DROP VIEW IF EXISTS `cart_summary_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `cart_summary_view` AS select `c`.`user_id` AS `user_id`,`ci`.`session_id` AS `session_id`,`ci`.`cart_item_id` AS `cart_item_id`,`p`.`product_id` AS `product_id`,`p`.`name` AS `product_name`,`p`.`category` AS `category`,`ci`.`quantity` AS `quantity`,`p`.`price` AS `price`,(`ci`.`quantity` * `p`.`price`) AS `total_price`,(select concat('/images/',`pi`.`pimg_path`,'/',`pi`.`pimg_name`) from `product_img` `pi` where (`pi`.`product_id` = `p`.`product_id`) limit 1) AS `image_url`,`ci`.`size` AS `size` from ((`cart` `c` join `cart_item` `ci` on((`c`.`cart_id` = `ci`.`cart_id`))) join `product` `p` on((`ci`.`product_id` = `p`.`product_id`))) where ((`c`.`user_id` is not null) or (`ci`.`session_id` is not null)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `category_sales_percentage`
--

/*!50001 DROP VIEW IF EXISTS `category_sales_percentage`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`choice`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `category_sales_percentage` AS select `p`.`category` AS `category`,count(distinct `o`.`order_id`) AS `order_count`,sum((`oi`.`price` * `oi`.`quantity`)) AS `total_sales`,((sum((`oi`.`price` * `oi`.`quantity`)) / (select sum((`order_items`.`price` * `order_items`.`quantity`)) from `order_items`)) * 100) AS `sales_percentage` from ((`orders` `o` join `order_items` `oi` on((`o`.`order_id` = `oi`.`order_id`))) join `product` `p` on((`oi`.`product_id` = `p`.`product_id`))) group by `p`.`category` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `daily_sales_report`
--

/*!50001 DROP VIEW IF EXISTS `daily_sales_report`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`choice`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `daily_sales_report` AS select cast(`o`.`order_date` as date) AS `date`,count(distinct `o`.`order_id`) AS `order_count`,sum((`oi`.`price` * `oi`.`quantity`)) AS `total_sales` from (`orders` `o` join `order_items` `oi` on((`o`.`order_id` = `oi`.`order_id`))) group by cast(`o`.`order_date` as date) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `monthly_sales_report`
--

/*!50001 DROP VIEW IF EXISTS `monthly_sales_report`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`choice`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `monthly_sales_report` AS select date_format(`o`.`order_date`,'%Y-%m-01') AS `month`,count(distinct `o`.`order_id`) AS `order_count`,sum((`oi`.`price` * `oi`.`quantity`)) AS `total_sales` from (`orders` `o` join `order_items` `oi` on((`o`.`order_id` = `oi`.`order_id`))) group by date_format(`o`.`order_date`,'%Y-%m-01') */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `order_details_view`
--

/*!50001 DROP VIEW IF EXISTS `order_details_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`choice`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `order_details_view` AS select `o`.`order_id` AS `order_id`,`o`.`user_id` AS `user_id`,`o`.`order_date` AS `order_date`,`o`.`order_status` AS `order_status`,`oi`.`product_id` AS `product_id`,`p`.`name` AS `product_name`,`oi`.`quantity` AS `quantity`,`oi`.`price` AS `price`,(`oi`.`quantity` * `oi`.`price`) AS `total_price` from ((`orders` `o` join `order_items` `oi` on((`o`.`order_id` = `oi`.`order_id`))) join `product` `p` on((`oi`.`product_id` = `p`.`product_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-09-29 12:53:32
