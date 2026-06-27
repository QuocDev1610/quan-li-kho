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
