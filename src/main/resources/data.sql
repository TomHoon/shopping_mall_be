-- PRODUCT
INSERT IGNORE INTO product (id, seller_id, name, description, base_price, status)
VALUES
(1, 1, '스키니 바지', '테스트용 스키니 바지', 10000, 'ACTIVE');


-- OPTION GROUP
INSERT IGNORE INTO option_group (id, product_id, name, sort_order)
VALUES
(1, 1, 'Color', 1),
(2, 1, 'Size', 2);


-- OPTION VALUE
INSERT IGNORE INTO option_value (id, option_group_id, value)
VALUES
(1, 1, 'Black'),
(2, 1, 'Red'),
(3, 2, 'M'),
(4, 2, 'L');


-- SKU
INSERT IGNORE INTO sku (id, product_id, sku_code, price, stock, status)
VALUES
(1, 1, 'SKU-BLACK-M', 10000, 10, 'ACTIVE'),
(2, 1, 'SKU-BLACK-L', 10000, 5, 'ACTIVE'),
(3, 1, 'SKU-RED-M', 10000, 7, 'ACTIVE'),
(4, 1, 'SKU-RED-L', 10000, 3, 'ACTIVE');


-- SKU OPTION
INSERT IGNORE INTO sku_option (sku_id, option_value_id)
VALUES
(1, 1),
(1, 3),

(2, 1),
(2, 4),

(3, 2),
(3, 3),

(4, 2),
(4, 4);