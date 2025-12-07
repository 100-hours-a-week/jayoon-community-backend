CREATE TABLE `user`
(
    `id`                int unsigned NOT NULL AUTO_INCREMENT,
    `nickname`          varchar(10)  NOT NULL,
    `profile_image_url` varchar(2048)         DEFAULT NULL,
    `created_at`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`        datetime              DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `user_auth`
(
    `user_id`       int unsigned NOT NULL,
    `email`         varchar(320) NOT NULL,
    `password_hash` varchar(128) NOT NULL,
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`    datetime     NULL     DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `email` (`email`),
    CONSTRAINT `FK_user_TO_user_auth_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `post`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `user_id`       int unsigned NULL,
    `title`         varchar(26)  NOT NULL,
    `body`          text         NOT NULL,
    `view_count`    bigint       NOT NULL DEFAULT 0,
    `like_count`    int unsigned NOT NULL DEFAULT 0,
    `comment_count` int unsigned NOT NULL DEFAULT 0,
    `created_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`    datetime     NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_user_TO_post_1` (`user_id`),
    CONSTRAINT `FK_user_TO_post_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `post_image`
(
    `id`             bigint        NOT NULL AUTO_INCREMENT,
    `post_id`        bigint        NOT NULL,
    `post_image_url` varchar(2048) NOT NULL,
    `created_at`     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     datetime      NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_post_TO_post_image_1` (`post_id`),
    CONSTRAINT `FK_post_TO_post_image_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
);

CREATE TABLE `post_like`
(
    `user_id`    int unsigned NOT NULL,
    `post_id`    bigint       NOT NULL,
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `post_id`),
    KEY `FK_post_TO_post_like_1` (`post_id`),
    CONSTRAINT `FK_post_TO_post_like_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
    CONSTRAINT `FK_user_TO_post_like_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `post_comment`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `user_id`    int unsigned NOT NULL,
    `post_id`    bigint       NOT NULL,
    `body`       text         NOT NULL,
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at` datetime     NULL     DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_user_TO_post_comment_1` (`user_id`),
    KEY `FK_post_TO_post_comment_1` (`post_id`),
    CONSTRAINT `FK_user_TO_post_comment_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FK_post_TO_post_comment_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
);
