CREATE TABLE `ak_interface` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `mark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                `menu_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '目录ID',
                                `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求路径',
                                `class_anntations` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '类注解',
                                `tables` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '表:缓存',
                                `table_detail` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '表和字段总内容',
                                `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                `created_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建人',
                                `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
                                `updated_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                `test_params` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '模拟测试请求参数',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态接口配置表';

CREATE TABLE `ak_tree_menu` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `name` varchar(255) NOT NULL COMMENT '名称',
                                `parent_id` bigint(20) DEFAULT NULL COMMENT '父组织',
                                `sort` int(11) DEFAULT '1' COMMENT '排序',
                                `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                `created_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建人',
                                `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
                                `updated_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='AKSQL目录';

CREATE TABLE `ak_associative_table` (
                                     `id` int(11) NOT NULL AUTO_INCREMENT,
                                     `table_first` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `associative_field_first` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `table_second` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `associative_field_second` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                                     `deleted` bit(1) DEFAULT b'0' COMMENT '是否删除',
                                     `created_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建人',
                                     `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updated_by` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新人',
                                     `updated_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表关联关系';