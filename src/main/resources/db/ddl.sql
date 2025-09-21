CREATE TABLE `category` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(255),
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `item` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(255),
                        `price` DECIMAL(38,6),
                        `category_id` BIGINT,
                        PRIMARY KEY (`id`),
                        CONSTRAINT `FK2n9w8d0dp4bsfra9dcg0046l4`
                            FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `shopping_list` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `name` VARCHAR(255),
                                 `updated_at` DATETIME(6),
                                 `created_at` DATETIME(6),
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `shopping_list_item` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `item_id` BIGINT,
                                      `shopping_list_id` BIGINT,
                                      `quantity` INT,
                                      `created_at` DATETIME(6),
                                      `updated_at` DATETIME(6),
                                      PRIMARY KEY (`id`),
                                      CONSTRAINT `FK6y915gohkmyeynilt5rp2nmdv`
                                          FOREIGN KEY (`item_id`) REFERENCES `item`(`id`),
                                      CONSTRAINT `FK1et5nxqn9udm0cbbhco58cq12`
                                          FOREIGN KEY (`shopping_list_id`) REFERENCES `shopping_list`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;