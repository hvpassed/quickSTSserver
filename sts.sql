/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 80035
Source Host           : localhost:3306
Source Database       : sts

Target Server Type    : MYSQL
Target Server Version : 80035
File Encoding         : 65001

Date: 2023-12-08 19:40:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for battle
-- ----------------------------
DROP TABLE IF EXISTS `battle`;
CREATE TABLE `battle` (
  `userid` int NOT NULL,
  `seed` int NOT NULL,
  `monsters` varchar(255) DEFAULT NULL,
  `curpos` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`),
  CONSTRAINT `battleMapPlayer` FOREIGN KEY (`userid`) REFERENCES `player` (`userid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for battle_players
-- ----------------------------
DROP TABLE IF EXISTS `battle_players`;
CREATE TABLE `battle_players` (
  `userid` int NOT NULL,
  `maxhp` int NOT NULL,
  `nowhp` int NOT NULL,
  `playing` int DEFAULT NULL,
  `playPos` varchar(255) DEFAULT NULL,
  `cardids` varchar(255) DEFAULT NULL,
  `block` int DEFAULT NULL,
  `seed` int DEFAULT NULL,
  `all_pile_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `draw_pile_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `hand_pile_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `discord_pile_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `cost` int NOT NULL,
  `draw_amount` int NOT NULL,
  `damage_reduction` int NOT NULL,
  `block_clear` int DEFAULT NULL,
  PRIMARY KEY (`userid`),
  CONSTRAINT `battle_playerMapBattle` FOREIGN KEY (`userid`) REFERENCES `battle` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for intents
-- ----------------------------
DROP TABLE IF EXISTS `intents`;
CREATE TABLE `intents` (
  `monsterid` int NOT NULL,
  `aim` varchar(255) NOT NULL,
  `block_gain` int DEFAULT NULL,
  `attack_amount` int DEFAULT NULL,
  `attack` int DEFAULT NULL,
  `type` int NOT NULL,
  PRIMARY KEY (`monsterid`),
  CONSTRAINT `monster` FOREIGN KEY (`monsterid`) REFERENCES `monsters` (`monsterid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for maps
-- ----------------------------
DROP TABLE IF EXISTS `maps`;
CREATE TABLE `maps` (
  `mapid` int NOT NULL,
  `userid` int NOT NULL,
  `currentposition` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `seed` int DEFAULT NULL,
  `endPos` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mapid`,`userid`),
  KEY `userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for monsters
-- ----------------------------
DROP TABLE IF EXISTS `monsters`;
CREATE TABLE `monsters` (
  `monsterid` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `maxhp` int DEFAULT NULL,
  `nowhp` int DEFAULT NULL,
  `block` int DEFAULT NULL,
  `seed` int DEFAULT NULL,
  `type` int DEFAULT NULL,
  `difficulty` int DEFAULT NULL,
  `damage_reduction` int NOT NULL,
  `block_clear` int DEFAULT NULL,
  PRIMARY KEY (`monsterid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `userid` int NOT NULL,
  `mapid` int NOT NULL,
  `playing` int NOT NULL,
  `maxhp` int NOT NULL,
  `nowhp` int NOT NULL,
  `playpos` varchar(255) DEFAULT '[-1,0]',
  `cardids` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`,`mapid`),
  KEY `userid` (`userid`),
  CONSTRAINT `playmapmaps` FOREIGN KEY (`userid`) REFERENCES `maps` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `password` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `userid` int NOT NULL,
  `hasmap` int DEFAULT NULL,
  `mapid` int DEFAULT NULL,
  `private_key` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `public_key` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`userid`,`username`),
  KEY `userid` (`userid`),
  KEY `mapid` (`mapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
