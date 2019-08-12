
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tcc_demo
-- ----------------------------
DROP TABLE IF EXISTS `tcc_demo`;
CREATE TABLE `tcc_demo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `group_id` varchar(64) DEFAULT NULL,
  `app_name` varchar(158) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
