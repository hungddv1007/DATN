-- ============================================================
-- ============================================================
-- SEED DATA
-- ============================================================
-- 1. Insert Roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('PT');
INSERT INTO roles (name) VALUES ('MEMBER');
INSERT INTO roles (name) VALUES ('SALE');

-- 2. Insert Users (100 users: 1 ADMIN, 15 PT, 84 MEMBER)
-- Mat khau cho tat ca la '123456' (da ma hoa BCrypt)
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (1, 'admin@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Quản Trị', '0910433218', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt1@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Đức Việt', '0900133890', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt2@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Minh Kiệt', '0963794026', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt3@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Ngọc Khánh', '0935116155', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt4@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đặng Ngọc Cường', '0978161849', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt5@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Thanh Long', '0910341316', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt6@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Xuân Tuấn', '0925534192', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt7@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Kim Nga', '0927648350', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt8@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Văn Thảo', '0964139537', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt9@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phan Gia Hạnh', '0924238849', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt10@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phan Đức Trung', '0953287101', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt11@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Gia Lan', '0969166978', 1, 'GOOGLE');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt12@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Thu Anh', '0918451462', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt13@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Vũ Văn Phong', '0982814893', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt14@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Thanh Lan', '0988095701', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (2, 'pt15@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Thanh Sơn', '0930391171', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member1@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Thu Hương', '0927824896', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member2@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Thu Long', '0946578713', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member3@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Thị Thắng', '0909839301', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member4@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đỗ Gia Dũng', '0931051834', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member5@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Bùi Xuân Mai', '0982997376', 1, 'GOOGLE');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member6@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Thị Hải', '0965667010', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member7@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phan Kim Thắng', '0913338726', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member8@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Ngọc Hạnh', '0931781080', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member9@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Minh Lan', '0967736026', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member10@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Quang Phong', '0974687234', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member11@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Văn Kiệt', '0905009788', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member12@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Văn My', '0912191361', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member13@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Đức Nga', '0999091699', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member14@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Thanh Phong', '0935346247', 1, 'GOOGLE');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member15@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Thị Anh', '0979911838', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member16@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Hữu Thúy', '0913542784', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member17@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đặng Gia Vy', '0908412411', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member18@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đỗ Thu Khánh', '0944935348', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member19@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Vũ Ngọc Dũng', '0916400524', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member20@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Kim Yến', '0986801128', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member21@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Thanh Như', '0926204505', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member22@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Gia Nga', '0915869232', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member23@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Hữu Tú', '0902563421', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member24@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phan Văn Đạt', '0933754330', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member25@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Bùi Minh Trung', '0954145868', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member26@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Văn Hùng', '0942940196', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member27@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Kim Thảo', '0969816934', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member28@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Kim Việt', '0908835615', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member29@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đặng Thanh Hùng', '0948465648', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member30@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Minh Tú', '0962994680', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member31@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Ngọc Mai', '0969957773', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member32@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Xuân Lan', '0914895134', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member33@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Minh Khánh', '0900379176', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member34@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Gia Long', '0967632016', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member35@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Hữu Vy', '0970831727', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member36@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Bùi Thu Như', '0995798687', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member37@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Hữu Đạt', '0974348734', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member38@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Vũ Thị Quân', '0934558122', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member39@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Quang Khánh', '0931665876', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member40@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Minh Tú', '0969096705', 1, 'GOOGLE');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member41@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Quang Tú', '0988937346', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member42@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Vũ Văn Trang', '0956272980', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member43@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Quang Bình', '0916272046', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member44@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Minh Hạnh', '0955646417', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member45@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Kim Kiệt', '0905310033', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member46@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Ngô Văn Khánh', '0932719374', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member47@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Thanh Lan', '0999124190', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member48@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Ngọc Trang', '0963193149', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member49@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Thị Cường', '0958651850', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member50@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Ngô Quang Khoa', '0916572628', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member51@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Bùi Ngọc Kiệt', '0977694531', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member52@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Xuân Nga', '0979965075', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member53@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Lê Xuân Mai', '0954549480', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member54@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Minh Giang', '0936783777', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member55@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Văn Giang', '0943634957', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member56@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Thu Thúy', '0968557444', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member57@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Phạm Thị Long', '0951823374', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member58@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đỗ Đức Vy', '0994134352', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member59@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Văn Kiệt', '0924008427', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member60@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Văn Quân', '0977752047', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member61@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Thị Trung', '0971902294', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member62@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Minh Hùng', '0986999386', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member63@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Vũ Xuân Sơn', '0996499091', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member64@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Minh Mai', '0941232812', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member65@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Quang Yến', '0997403447', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member66@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Gia Nam', '0949361832', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member67@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Ngọc Khánh', '0910249947', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member68@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Trần Xuân Sơn', '0964887719', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member69@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Nguyễn Quang Thảo', '0994013990', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member70@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hồ Gia Phương', '0990278742', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member71@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đặng Quang Khoa', '0917565512', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member72@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Quang Khoa', '0946807154', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member73@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Thị Trung', '0980876038', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member74@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Đức Khoa', '0970348247', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member75@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Kim Khoa', '0910932480', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member76@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Quang Giang', '0931712748', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member77@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Đỗ Ngọc Tú', '0977378263', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member78@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Dương Đức My', '0921465840', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member79@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Hoàng Kim Sơn', '0999727875', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member80@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Huỳnh Thu Kiệt', '0967533963', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member81@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Ngô Quang Cường', '0957662702', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member82@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Võ Đức Thắng', '0917187026', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member83@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Ngô Gia Khánh', '0917459615', 1, 'LOCAL');
INSERT INTO users (role_id, email, password, full_name, phone, status, provider) VALUES (3, 'member84@gympro.com', '$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi', N'Ngô Gia Kiệt', '0965780913', 1, 'LOCAL');

-- Avatar demo: chỉ cập nhật cột avatar, không thay đổi bất kỳ thông tin tài khoản nào.
-- DiceBear tạo ảnh ổn định theo seed; không dùng email, họ tên hay dữ liệu cá nhân trong URL.
UPDATE users
SET avatar = CASE
    WHEN role_id = 1 THEN 'https://api.dicebear.com/10.x/initials/svg?seed=GymPro-Admin&backgroundColor=f97316&fontFamily=Arial'
    WHEN role_id = 2 THEN CONCAT('https://api.dicebear.com/10.x/lorelei/svg?seed=gympro-pt-', RIGHT('00' + CAST(id - 1 AS VARCHAR(2)), 2), '&backgroundColor=e2e8f0')
    ELSE CONCAT('https://api.dicebear.com/10.x/adventurer-neutral/svg?seed=gympro-member-', RIGHT('000' + CAST(id - 16 AS VARCHAR(3)), 3), '&backgroundColor=e2e8f0')
END;

-- 3. Insert PT Profiles
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (2, N'Tăng cơ (Bulking)', N'Huấn luyện viên với 6 năm kinh nghiệm, chuyên sâu về tăng cơ (bulking).', N'Chứng chỉ PT Quốc tế NASM', 4.7, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (3, N'Thể hình & Giảm cân', N'Huấn luyện viên với 5 năm kinh nghiệm, chuyên sâu về thể hình & giảm cân.', N'Chứng chỉ CrossFit Level 1', 3.9, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (4, N'Boxing & Kickfit', N'Huấn luyện viên với 5 năm kinh nghiệm, chuyên sâu về boxing & kickfit.', N'Chứng chỉ CrossFit Level 1', 4.3, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (5, N'Boxing & Kickfit', N'Huấn luyện viên với 2 năm kinh nghiệm, chuyên sâu về boxing & kickfit.', N'Bằng HLV Boxing cấp Quốc gia', 4.1, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (6, N'Thể hình & Giảm cân', N'Huấn luyện viên với 10 năm kinh nghiệm, chuyên sâu về thể hình & giảm cân.', N'Chứng chỉ CrossFit Level 1', 4.2, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (7, N'Gym cho người mới bắt đầu', N'Huấn luyện viên với 4 năm kinh nghiệm, chuyên sâu về gym cho người mới bắt đầu.', N'Bằng HLV Boxing cấp Quốc gia', 3.9, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (8, N'Calisthenics', N'Huấn luyện viên với 5 năm kinh nghiệm, chuyên sâu về calisthenics.', N'Bằng HLV Boxing cấp Quốc gia', 4.3, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (9, N'Cardio & Sức bền', N'Huấn luyện viên với 1 năm kinh nghiệm, chuyên sâu về cardio & sức bền.', N'Chứng chỉ PT Quốc tế NASM', 4.2, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (10, N'Calisthenics', N'Huấn luyện viên với 2 năm kinh nghiệm, chuyên sâu về calisthenics.', N'Chứng chỉ PT Quốc tế NASM', 4.1, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (11, N'Tăng cơ (Bulking)', N'Huấn luyện viên với 3 năm kinh nghiệm, chuyên sâu về tăng cơ (bulking).', N'Chứng chỉ Dinh dưỡng thể thao ISSA', 4.3, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (12, N'Tăng cơ (Bulking)', N'Huấn luyện viên với 7 năm kinh nghiệm, chuyên sâu về tăng cơ (bulking).', N'Chứng chỉ CrossFit Level 1', 4.7, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (13, N'Cardio & Sức bền', N'Huấn luyện viên với 7 năm kinh nghiệm, chuyên sâu về cardio & sức bền.', N'Bằng HLV Boxing cấp Quốc gia', 3.9, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (14, N'Calisthenics', N'Huấn luyện viên với 10 năm kinh nghiệm, chuyên sâu về calisthenics.', N'Chứng chỉ Dinh dưỡng thể thao ISSA', 4.9, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (15, N'Dinh dưỡng thể thao', N'Huấn luyện viên với 1 năm kinh nghiệm, chuyên sâu về dinh dưỡng thể thao.', N'Bằng HLV Boxing cấp Quốc gia', 4.2, 5);
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score, max_members) VALUES (16, N'Calisthenics', N'Huấn luyện viên với 8 năm kinh nghiệm, chuyên sâu về calisthenics.', N'Bằng Yoga Alliance 200h', 4.8, 5);

-- 4. Insert Member Profiles
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (17, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (18, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (19, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (20, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (21, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (22, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (23, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (24, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (25, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (26, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (27, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (28, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (29, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (30, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (31, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (32, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (33, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (34, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (35, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (36, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (37, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (38, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (39, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (40, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (41, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (42, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (43, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (44, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (45, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (46, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (47, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (48, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (49, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (50, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (51, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (52, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (53, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (54, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (55, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (56, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (57, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (58, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (59, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (60, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (61, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (62, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (63, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (64, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (65, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (66, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (67, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (68, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (69, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (70, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (71, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (72, N'Thể lực tốt, mục tiêu tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (73, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (74, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (75, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (76, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (77, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (78, N'Thể trạng bình thường, không có bệnh nền.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (79, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (80, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (81, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (82, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (83, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (84, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (85, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (86, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (87, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (88, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (89, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (90, N'Thừa cân nhẹ, mục tiêu giảm mỡ toàn thân.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (91, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (92, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (93, N'Có vấn đề về khớp gối, hạn chế squat sâu.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (94, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (95, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (96, N'Mới bắt đầu tập, cần làm quen kỹ thuật cơ bản.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (97, N'Thể trạng gầy, mục tiêu tăng cân và tăng cơ.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (98, NULL);
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (99, N'Huyết áp hơi cao, cần theo dõi cường độ cardio.');
INSERT INTO member_profiles (user_id, medical_conditions) VALUES (100, N'Có tiền sử đau lưng dưới, cần tránh deadlift nặng.');

-- Bo sung du lieu ho so the chat de co the kiem thu AI chat va AI tao thuc don.
-- Cac gia tri duoc tao co dinh theo user_id, nam trong pham vi hop le va de tai lap.
UPDATE member_profiles
SET height_cm = 155 + (user_id % 31),
    weight_kg = 50 + (user_id % 36),
    date_of_birth = DATEFROMPARTS(1985 + (user_id % 18), 1 + (user_id % 12), 1 + (user_id % 27)),
    biological_sex = CASE WHEN user_id % 2 = 0 THEN 'MALE' ELSE 'FEMALE' END,
    chest_cm = 78 + (user_id % 24),
    waist_cm = 65 + (user_id % 27),
    hip_cm = 82 + (user_id % 23),
    activity_level = CASE user_id % 5
        WHEN 0 THEN 'SEDENTARY'
        WHEN 1 THEN 'LIGHT'
        WHEN 2 THEN 'MODERATE'
        WHEN 3 THEN 'HIGH'
        ELSE 'VERY_HIGH'
    END,
    fitness_goal = CASE user_id % 4
        WHEN 0 THEN 'WEIGHT_LOSS'
        WHEN 1 THEN 'MUSCLE_GAIN'
        WHEN 2 THEN 'MAINTENANCE'
        ELSE 'HEALTH_IMPROVEMENT'
    END,
    target_weight_kg = CASE user_id % 4
        WHEN 0 THEN 45 + (user_id % 36)
        WHEN 1 THEN 55 + (user_id % 36)
        ELSE 50 + (user_id % 36)
    END,
    training_experience = CASE user_id % 4
        WHEN 0 THEN N'Chưa từng tập luyện có hệ thống.'
        WHEN 1 THEN N'Đã tập dưới 6 tháng, đang làm quen kỹ thuật cơ bản.'
        WHEN 2 THEN N'Đã tập đều từ 6 đến 18 tháng.'
        ELSE N'Đã tập trên 2 năm và có thể tự thực hiện các bài tập cơ bản.'
    END,
    injury_history = CASE user_id % 7
        WHEN 0 THEN N'Từng đau khớp gối, cần hạn chế động tác tác động mạnh.'
        WHEN 1 THEN N'Từng đau lưng dưới, cần chú ý kỹ thuật nâng tạ.'
        ELSE NULL
    END;

-- Cong thuc Deurenberg dung cung cach voi backend/frontend cua du an.
UPDATE member_profiles
SET body_fat_percentage = CAST(ROUND(
        1.20 * (weight_kg / POWER(height_cm / 100.0, 2))
        + 0.23 * DATEDIFF(YEAR, date_of_birth, CAST(GETDATE() AS DATE))
        - 10.8 * CASE WHEN biological_sex = 'MALE' THEN 1 ELSE 0 END
        - 5.4,
        2) AS DECIMAL(5,2)),
    body_fat_source = 'ESTIMATED';

-- 5. Insert Packages
INSERT INTO packages (name, daily_price, description, has_pt, can_choose_pt, has_meal_plan, min_days, max_hold_times, hold_return_percent, is_active) VALUES 
('BASIC', 16000, N'Gói tập cơ bản sử dụng thiết bị phòng gym tự do', 0, 0, 0, 30, 0, 0, 1),
('PREMIUM', 50000, N'Gói tập nâng cao có PT hướng dẫn', 1, 1, 0, 30, 1, 50, 1),
('VIP', 83000, N'Gói tập cao cấp nhất được chọn PT + Meal Plan dinh dưỡng', 1, 1, 1, 30, 3, 90, 1);

-- 5.5 Insert Package Discounts (ap dung chung cho tat ca goi)
INSERT INTO package_discounts (package_id, min_days, discount_percent) VALUES 
(NULL, 90, 5),
(NULL, 180, 10),
(NULL, 365, 15),
(NULL, 730, 20);

-- 6. Insert Promotions
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('WELCOME10', 10, NULL, '2026-01-01', '2026-12-31', 500, 29, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('TET2026', 15, NULL, '2026-01-15', '2026-02-20', 200, 27, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('SUMMER20', 20, 2, '2026-05-01', '2026-08-31', 150, 27, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('VIPONLY15', 15, 3, '2026-01-01', '2026-12-31', 100, 28, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('BASICSALE5', 5, 1, '2026-01-01', '2026-12-31', 300, 29, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('STUDENT10', 10, NULL, '2026-03-01', '2026-09-30', 250, 52, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('FLASHSALE25', 25, NULL, '2026-07-01', '2026-07-31', 50, 10, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('NEWMEMBER8', 8, NULL, '2026-01-01', '2026-12-31', 400, 116, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('REFER5', 5, NULL, '2026-01-01', '2026-12-31', 1000, 204, 1);
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES ('ANNIVERSARY30', 30, NULL, '2026-11-01', '2026-11-30', 100, 26, 1);

-- 7. Insert Memberships
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (17, 2, 4, '2025-12-31', '2026-03-31', 'EXPIRED', NULL, 90, 50000, 0, NULL, 0, '2025-12-31 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (18, 2, 8, '2026-07-07', '2026-08-06', 'PAUSED', N'Lý do sức khỏe', 30, 50000, 1, '2026-07-09', 15, '2026-07-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (19, 2, 3, '2025-01-14', '2026-01-14', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-01-14 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (20, 3, 6, '2026-06-13', '2026-12-10', 'CANCELLED', NULL, 180, 83000, 0, NULL, 0, '2026-06-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (21, 2, 2, '2026-06-30', '2027-06-30', 'ACTIVE', NULL, 365, 50000, 0, NULL, 0, '2026-06-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (22, 3, 3, '2026-06-16', '2026-09-14', 'PAUSED', N'Đi du lịch', 90, 83000, 2, '2026-07-05', 6, '2026-06-16 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (23, 3, 10, '2025-01-21', '2026-01-21', 'EXPIRED', NULL, 365, 83000, 0, NULL, 0, '2025-01-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (24, 3, 13, '2026-01-26', '2026-04-26', 'EXPIRED', NULL, 90, 83000, 0, NULL, 0, '2026-01-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (24, 3, 15, '2026-06-28', '2026-09-26', 'ACTIVE', NULL, 90, 83000, 0, NULL, 0, '2026-06-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (25, 2, 2, '2025-12-24', '2026-01-23', 'EXPIRED', NULL, 30, 50000, 0, NULL, 0, '2025-12-24 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (25, 1, NULL, '2026-06-13', '2027-06-13', 'ACTIVE', NULL, 365, 16000, 0, NULL, 0, '2026-06-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (26, 1, NULL, '2025-11-11', '2026-02-09', 'EXPIRED', NULL, 90, 16000, 0, NULL, 0, '2025-11-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (27, 3, 2, '2026-05-31', '2026-07-30', 'ACTIVE', NULL, 60, 83000, 0, NULL, 0, '2026-05-31 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (28, 1, NULL, '2026-06-11', '2026-12-08', 'ACTIVE', NULL, 180, 16000, 0, NULL, 0, '2026-06-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (29, 1, NULL, '2026-06-12', '2027-06-12', 'ACTIVE', NULL, 365, 16000, 0, NULL, 0, '2026-06-12 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (30, 2, 6, '2025-01-15', '2026-01-15', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-01-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (30, 1, NULL, '2026-06-11', '2026-12-08', 'CANCELLED', NULL, 180, 16000, 0, NULL, 0, '2026-06-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (31, 2, 6, '2025-10-22', '2025-12-21', 'EXPIRED', NULL, 60, 50000, 0, NULL, 0, '2025-10-22 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (31, 1, NULL, '2026-06-16', '2026-07-16', 'ACTIVE', NULL, 30, 16000, 0, NULL, 0, '2026-06-16 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (32, 1, NULL, '2025-02-26', '2026-02-26', 'EXPIRED', NULL, 365, 16000, 0, NULL, 0, '2025-02-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (33, 2, 13, '2026-05-15', '2026-08-13', 'ACTIVE', NULL, 90, 50000, 0, NULL, 0, '2026-05-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (34, 1, NULL, '2025-09-18', '2025-10-18', 'CANCELLED', NULL, 30, 16000, 0, NULL, 0, '2025-09-18 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (34, 2, 6, '2025-09-21', '2026-03-20', 'EXPIRED', NULL, 180, 50000, 0, NULL, 0, '2025-09-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (35, 1, NULL, '2025-10-29', '2026-01-27', 'EXPIRED', NULL, 90, 16000, 0, NULL, 0, '2025-10-29 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (35, 2, 3, '2026-05-16', '2026-08-14', 'ACTIVE', NULL, 90, 50000, 0, NULL, 0, '2026-05-16 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (36, 2, 13, '2025-04-16', '2026-04-16', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-04-16 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (36, 2, 10, '2026-07-05', '2026-10-03', 'PAUSED', N'Bận việc cá nhân', 90, 50000, 1, '2026-07-07', 15, '2026-07-05 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (37, 3, 6, '2026-06-30', '2026-08-29', 'ACTIVE', NULL, 60, 83000, 0, NULL, 0, '2026-06-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (38, 2, 9, '2025-12-26', '2026-01-25', 'EXPIRED', NULL, 30, 50000, 0, NULL, 0, '2025-12-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (38, 2, 12, '2026-06-11', '2027-06-11', 'PAUSED', N'Bận việc cá nhân', 365, 50000, 2, '2026-07-09', 13, '2026-06-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (39, 1, NULL, '2025-10-27', '2026-04-25', 'EXPIRED', NULL, 180, 16000, 0, NULL, 0, '2025-10-27 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (39, 2, 9, '2026-06-20', '2026-09-18', 'ACTIVE', NULL, 90, 50000, 0, NULL, 0, '2026-06-20 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (40, 1, NULL, '2026-05-15', '2026-08-13', 'ACTIVE', NULL, 90, 16000, 0, NULL, 0, '2026-05-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (41, 1, NULL, '2026-06-10', '2027-06-10', 'ACTIVE', NULL, 365, 16000, 0, NULL, 0, '2026-06-10 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (42, 3, 11, '2026-06-14', '2026-07-14', 'ACTIVE', NULL, 30, 83000, 0, NULL, 0, '2026-06-14 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (43, 2, 9, '2026-07-06', '2027-01-02', 'ACTIVE', NULL, 180, 50000, 0, NULL, 0, '2026-07-06 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (44, 3, 10, '2026-05-27', '2026-08-25', 'CANCELLED', NULL, 90, 83000, 0, NULL, 0, '2026-05-27 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (44, 2, 9, '2026-06-25', '2026-07-25', 'ACTIVE', NULL, 30, 50000, 0, NULL, 0, '2026-06-25 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (45, 2, 2, '2026-06-21', '2026-09-19', 'ACTIVE', NULL, 90, 50000, 0, NULL, 0, '2026-06-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (46, 2, 12, '2026-05-30', '2027-05-30', 'ACTIVE', NULL, 365, 50000, 0, NULL, 0, '2026-05-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (47, 2, 6, '2025-04-23', '2026-04-23', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-04-23 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (47, 2, 3, '2026-01-20', '2027-01-20', 'CANCELLED', NULL, 365, 50000, 0, NULL, 0, '2026-01-20 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (48, 1, NULL, '2026-05-29', '2026-08-27', 'ACTIVE', NULL, 90, 16000, 0, NULL, 0, '2026-05-29 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (49, 2, 6, '2025-06-13', '2026-06-13', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-06-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (50, 2, 13, '2026-05-28', '2026-08-26', 'PAUSED', N'Đi du lịch', 90, 50000, 1, '2026-07-09', 13, '2026-05-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (51, 1, NULL, '2026-06-26', '2026-09-24', 'ACTIVE', NULL, 90, 16000, 0, NULL, 0, '2026-06-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (52, 3, 4, '2026-03-31', '2026-05-30', 'EXPIRED', NULL, 60, 83000, 0, NULL, 0, '2026-03-31 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (52, 1, NULL, '2026-06-22', '2026-07-22', 'ACTIVE', NULL, 30, 16000, 0, NULL, 0, '2026-06-22 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (53, 3, 4, '2025-12-28', '2026-01-27', 'EXPIRED', NULL, 30, 83000, 0, NULL, 0, '2025-12-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (54, 1, NULL, '2026-05-21', '2026-07-20', 'PAUSED', N'Đi công tác dài ngày', 60, 16000, 1, '2026-07-07', 10, '2026-05-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (55, 2, 14, '2026-02-14', '2026-04-15', 'CANCELLED', NULL, 60, 50000, 0, NULL, 0, '2026-02-14 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (56, 2, 14, '2026-05-19', '2027-05-19', 'ACTIVE', NULL, 365, 50000, 0, NULL, 0, '2026-05-19 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (57, 3, 9, '2025-11-10', '2026-02-08', 'EXPIRED', NULL, 90, 83000, 0, NULL, 0, '2025-11-10 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (58, 1, NULL, '2026-06-13', '2026-12-10', 'ACTIVE', NULL, 180, 16000, 0, NULL, 0, '2026-06-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (59, 3, 2, '2026-06-23', '2027-06-23', 'ACTIVE', NULL, 365, 83000, 0, NULL, 0, '2026-06-23 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (60, 3, 15, '2025-10-18', '2026-10-18', 'CANCELLED', NULL, 365, 83000, 0, NULL, 0, '2025-10-18 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (61, 1, NULL, '2025-12-25', '2026-02-23', 'EXPIRED', NULL, 60, 16000, 0, NULL, 0, '2025-12-25 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (61, 1, NULL, '2026-05-12', '2026-11-08', 'ACTIVE', NULL, 180, 16000, 0, NULL, 0, '2026-05-12 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (62, 2, 2, '2025-12-09', '2026-03-09', 'CANCELLED', NULL, 90, 50000, 0, NULL, 0, '2025-12-09 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (62, 2, 7, '2026-07-04', '2026-10-02', 'ACTIVE', NULL, 90, 50000, 0, NULL, 0, '2026-07-04 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (63, 2, 14, '2026-01-19', '2026-02-18', 'CANCELLED', NULL, 30, 50000, 0, NULL, 0, '2026-01-19 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (63, 1, NULL, '2026-06-13', '2026-07-13', 'ACTIVE', NULL, 30, 16000, 0, NULL, 0, '2026-06-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (64, 3, 16, '2026-07-09', '2027-01-05', 'ACTIVE', NULL, 180, 83000, 0, NULL, 0, '2026-07-09 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (65, 1, NULL, '2025-10-31', '2025-12-30', 'EXPIRED', NULL, 60, 16000, 0, NULL, 0, '2025-10-31 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (65, 2, 5, '2026-06-21', '2026-12-18', 'ACTIVE', NULL, 180, 50000, 0, NULL, 0, '2026-06-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (66, 1, NULL, '2025-07-30', '2026-01-26', 'EXPIRED', NULL, 180, 16000, 0, NULL, 0, '2025-07-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (66, 2, 11, '2026-06-24', '2026-07-24', 'PAUSED', N'Lý do sức khỏe', 30, 50000, 1, '2026-07-05', 11, '2026-06-24 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (67, 1, NULL, '2026-04-04', '2026-05-04', 'EXPIRED', NULL, 30, 16000, 0, NULL, 0, '2026-04-04 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (68, 1, NULL, '2026-06-28', '2026-07-28', 'ACTIVE', NULL, 30, 16000, 0, NULL, 0, '2026-06-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (69, 2, 10, '2025-02-25', '2026-02-25', 'EXPIRED', NULL, 365, 50000, 0, NULL, 0, '2025-02-25 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (69, 1, NULL, '2026-06-15', '2026-12-12', 'ACTIVE', NULL, 180, 16000, 0, NULL, 0, '2026-06-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (70, 1, NULL, '2026-07-07', '2026-08-06', 'ACTIVE', NULL, 30, 16000, 0, NULL, 0, '2026-07-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (71, 2, 14, '2025-11-28', '2025-12-28', 'EXPIRED', NULL, 30, 50000, 0, NULL, 0, '2025-11-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (71, 2, 15, '2026-06-29', '2026-07-29', 'ACTIVE', NULL, 30, 50000, 0, NULL, 0, '2026-06-29 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (72, 3, 12, '2025-11-06', '2026-05-05', 'CANCELLED', NULL, 180, 83000, 0, NULL, 0, '2025-11-06 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (73, 1, NULL, '2025-11-15', '2026-05-14', 'EXPIRED', NULL, 180, 16000, 0, NULL, 0, '2025-11-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (73, 1, NULL, '2026-01-29', '2026-03-30', 'CANCELLED', NULL, 60, 16000, 0, NULL, 0, '2026-01-29 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (74, 2, 14, '2026-07-07', '2027-07-07', 'ACTIVE', NULL, 365, 50000, 0, NULL, 0, '2026-07-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (75, 1, NULL, '2026-01-21', '2026-02-20', 'CANCELLED', NULL, 30, 16000, 0, NULL, 0, '2026-01-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (76, 3, 16, '2025-11-26', '2026-05-25', 'CANCELLED', NULL, 180, 83000, 0, NULL, 0, '2025-11-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (76, 3, 13, '2025-10-28', '2026-01-26', 'CANCELLED', NULL, 90, 83000, 0, NULL, 0, '2025-10-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (77, 3, 13, '2026-07-04', '2027-07-04', 'ACTIVE', NULL, 365, 83000, 0, NULL, 0, '2026-07-04 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (78, 2, 3, '2026-06-19', '2026-08-18', 'ACTIVE', NULL, 60, 50000, 0, NULL, 0, '2026-06-19 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (79, 2, 8, '2025-10-04', '2026-01-02', 'EXPIRED', NULL, 90, 50000, 0, NULL, 0, '2025-10-04 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (79, 1, NULL, '2026-07-07', '2027-01-03', 'ACTIVE', NULL, 180, 16000, 0, NULL, 0, '2026-07-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (80, 3, 12, '2026-02-19', '2027-02-19', 'CANCELLED', NULL, 365, 83000, 0, NULL, 0, '2026-02-19 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (81, 3, 7, '2026-02-21', '2026-03-23', 'EXPIRED', NULL, 30, 83000, 0, NULL, 0, '2026-02-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (82, 2, 15, '2026-04-27', '2026-06-26', 'CANCELLED', NULL, 60, 50000, 0, NULL, 0, '2026-04-27 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (83, 3, 14, '2026-07-02', '2026-08-01', 'ACTIVE', NULL, 30, 83000, 0, NULL, 0, '2026-07-02 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (84, 2, 2, '2026-06-30', '2027-06-30', 'ACTIVE', NULL, 365, 50000, 0, NULL, 0, '2026-06-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (85, 2, 4, '2026-06-03', '2026-11-30', 'PAUSED', N'Bận việc cá nhân', 180, 50000, 2, '2026-06-29', 8, '2026-06-03 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (86, 1, NULL, '2026-01-26', '2026-03-27', 'EXPIRED', NULL, 60, 16000, 0, NULL, 0, '2026-01-26 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (86, 3, 13, '2026-07-01', '2026-07-31', 'ACTIVE', NULL, 30, 83000, 0, NULL, 0, '2026-07-01 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (87, 3, 5, '2025-12-01', '2026-03-01', 'EXPIRED', NULL, 90, 83000, 0, NULL, 0, '2025-12-01 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (88, 3, 9, '2025-07-07', '2026-01-03', 'EXPIRED', NULL, 180, 83000, 0, NULL, 0, '2025-07-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (88, 3, 3, '2025-10-06', '2026-01-04', 'EXPIRED', NULL, 90, 83000, 0, NULL, 0, '2025-10-06 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (89, 1, NULL, '2026-04-11', '2026-07-10', 'CANCELLED', NULL, 90, 16000, 0, NULL, 0, '2026-04-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (90, 2, 5, '2026-04-29', '2026-05-29', 'CANCELLED', NULL, 30, 50000, 0, NULL, 0, '2026-04-29 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (91, 1, NULL, '2025-03-07', '2026-03-07', 'EXPIRED', NULL, 365, 16000, 0, NULL, 0, '2025-03-07 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (92, 2, 6, '2026-06-06', '2027-06-06', 'CANCELLED', NULL, 365, 50000, 0, NULL, 0, '2026-06-06 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (93, 2, 8, '2026-05-21', '2026-11-17', 'ACTIVE', NULL, 180, 50000, 0, NULL, 0, '2026-05-21 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (94, 1, NULL, '2025-12-11', '2026-03-11', 'EXPIRED', NULL, 90, 16000, 0, NULL, 0, '2025-12-11 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (95, 2, 12, '2025-09-30', '2025-11-29', 'CANCELLED', NULL, 60, 50000, 0, NULL, 0, '2025-09-30 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (96, 3, 15, '2026-05-12', '2026-07-11', 'ACTIVE', NULL, 60, 83000, 0, NULL, 0, '2026-05-12 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (97, 1, NULL, '2026-03-25', '2026-05-24', 'EXPIRED', NULL, 60, 16000, 0, NULL, 0, '2026-03-25 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (97, 2, 16, '2026-06-06', '2026-08-05', 'ACTIVE', NULL, 60, 50000, 0, NULL, 0, '2026-06-06 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (98, 1, NULL, '2025-12-15', '2026-02-13', 'EXPIRED', NULL, 60, 16000, 0, NULL, 0, '2025-12-15 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (98, 1, NULL, '2026-03-13', '2026-06-11', 'EXPIRED', NULL, 90, 16000, 0, NULL, 0, '2026-03-13 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (99, 1, NULL, '2026-05-28', '2027-05-28', 'ACTIVE', NULL, 365, 16000, 0, NULL, 0, '2026-05-28 00:00:00');
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price, hold_count, paused_at, total_hold_days, created_at) VALUES (100, 3, 2, '2026-06-03', '2026-09-01', 'PAUSED', N'Bận việc cá nhân', 90, 83000, 1, '2026-06-09', 3, '2026-06-03 00:00:00');

-- 8. Insert Transactions (khop voi memberships)
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (1, NULL, 4500000, 4500000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (2, NULL, 1500000, 1500000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (3, NULL, 18250000, 18250000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (4, NULL, 14940000, 14940000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (5, NULL, 18250000, 18250000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (6, NULL, 7470000, 7470000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (7, 6, 27265500, 30295000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (8, 7, 5602500, 7470000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (9, 8, 6872400, 7470000, 'CASH', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (10, 2, 1275000, 1500000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (11, NULL, 5840000, 5840000, 'BANK', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (12, NULL, 1440000, 1440000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (13, NULL, 4980000, 4980000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (14, NULL, 2880000, 2880000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (15, NULL, 5840000, 5840000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (16, 7, 13687500, 18250000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (17, 6, 2592000, 2880000, 'CASH', 'CANCELLED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (18, NULL, 3000000, 3000000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (19, NULL, 480000, 480000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (20, 6, 5256000, 5840000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (21, NULL, 4500000, 4500000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (22, 5, 456000, 480000, 'BANK', 'CANCELLED', 'NEW', NULL);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (23, NULL, 9000000, 9000000, 'ONLINE', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (24, 2, 1224000, 1440000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (25, NULL, 4500000, 4500000, 'BANK', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (26, 9, 17337500, 18250000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (27, 6, 4050000, 4500000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (28, 9, 4731000, 4980000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (29, NULL, 1500000, 1500000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (30, NULL, 18250000, 18250000, 'ONLINE', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (31, NULL, 2880000, 2880000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (32, NULL, 4500000, 4500000, 'BANK', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (33, 7, 1080000, 1440000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (34, NULL, 5840000, 5840000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (35, NULL, 2490000, 2490000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (36, NULL, 9000000, 9000000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (37, NULL, 7470000, 7470000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (38, 4, 1275000, 1500000, 'CASH', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (39, NULL, 4500000, 4500000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (39, NULL, 4191161, 4500000, 'CASH', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (40, NULL, 18250000, 18250000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (41, NULL, 18250000, 18250000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (42, NULL, 18250000, 18250000, 'ONLINE', 'CANCELLED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (43, NULL, 1440000, 1440000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (44, NULL, 18250000, 18250000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (45, 5, 4275000, 4500000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (46, NULL, 1440000, 1440000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (47, 5, 4731000, 4980000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (48, 6, 432000, 480000, 'ONLINE', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (49, NULL, 2490000, 2490000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (50, NULL, 960000, 960000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (51, NULL, 3000000, 3000000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (52, 2, 15512500, 18250000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (53, NULL, 7470000, 7470000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (54, 9, 2736000, 2880000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (55, NULL, 30295000, 30295000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (56, NULL, 30295000, 30295000, 'CASH', 'CANCELLED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (57, 4, 816000, 960000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (58, NULL, 2880000, 2880000, 'CASH', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (59, 9, 4275000, 4500000, 'BANK', 'CANCELLED', 'NEW', NULL);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (60, NULL, 4500000, 4500000, 'CASH', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (61, 5, 1425000, 1500000, 'CASH', 'CANCELLED', 'NEW', NULL);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (62, NULL, 480000, 480000, 'CASH', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (63, NULL, 14940000, 14940000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (64, 8, 883200, 960000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (65, NULL, 9000000, 9000000, 'CASH', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (66, NULL, 2880000, 2880000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (67, NULL, 1500000, 1500000, 'CASH', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (68, NULL, 480000, 480000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (69, 8, 441600, 480000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (70, 6, 16425000, 18250000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (71, 7, 2160000, 2880000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (72, 2, 408000, 480000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (73, NULL, 1500000, 1500000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (73, NULL, 1407536, 1500000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (74, NULL, 1500000, 1500000, 'BANK', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (75, NULL, 14940000, 14940000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (76, 2, 2448000, 2880000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (77, NULL, 960000, 960000, 'ONLINE', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (78, NULL, 18250000, 18250000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (79, 3, 384000, 480000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (80, NULL, 14940000, 14940000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (81, NULL, 7470000, 7470000, 'CASH', 'CANCELLED', 'UPGRADE', NULL);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (82, NULL, 30295000, 30295000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (83, NULL, 3000000, 3000000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (84, 9, 4275000, 4500000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (85, 9, 2736000, 2880000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (86, 10, 21206500, 30295000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (87, NULL, 2490000, 2490000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (88, NULL, 3000000, 3000000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (88, NULL, 2838713, 3000000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (89, NULL, 2490000, 2490000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (90, 4, 15512500, 18250000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (90, NULL, 17198247, 18250000, 'ONLINE', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (91, 1, 8100000, 9000000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (92, NULL, 960000, 960000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (93, NULL, 2490000, 2490000, 'ONLINE', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (94, NULL, 7470000, 7470000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (95, NULL, 14940000, 14940000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (96, NULL, 7470000, 7470000, 'BANK', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (97, NULL, 1440000, 1440000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (98, NULL, 1500000, 1500000, 'CASH', 'CANCELLED', 'NEW', NULL);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (99, NULL, 5840000, 5840000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (100, NULL, 18250000, 18250000, 'BANK', 'CANCELLED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (101, NULL, 9000000, 9000000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (102, 7, 1080000, 1440000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (103, NULL, 3000000, 3000000, 'CASH', 'CANCELLED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (104, 10, 3486000, 4980000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (105, 1, 864000, 960000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (106, NULL, 3000000, 3000000, 'CASH', 'CONFIRMED', 'RENEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (107, NULL, 960000, 960000, 'CASH', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (108, NULL, 1440000, 1440000, 'ONLINE', 'CONFIRMED', 'UPGRADE', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (109, 3, 4672000, 5840000, 'ONLINE', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (110, NULL, 7470000, 7470000, 'BANK', 'CONFIRMED', 'NEW', 1);
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, type, confirmed_by) VALUES (110, NULL, 7431544, 7470000, 'ONLINE', 'CONFIRMED', 'RENEW', 1);

-- Chuyen cac giao dich mau cu sang cau truc snapshot hien tai.
-- Du lieu snapshot giup lich su khong bi thay doi khi gia goi/PT thay doi ve sau.
WITH transaction_order AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY membership_id ORDER BY id) AS transaction_number
    FROM transactions
)
UPDATE transaction_record
SET requested_duration_days = CAST(ROUND(
        transaction_record.original_amount / NULLIF(membership.daily_price, 0), 0) AS INT),
    requested_package_id = membership.package_id,
    requested_pt_id = membership.pt_id,
    operation_applied = CASE WHEN transaction_record.status = 'CONFIRMED' THEN 1 ELSE 0 END,
    confirmed_by = CASE WHEN transaction_record.status = 'CONFIRMED'
                        THEN COALESCE(transaction_record.confirmed_by, 1)
                        ELSE NULL END,
    created_at = DATEADD(DAY, (transaction_order.transaction_number - 1) * 30,
                         membership.created_at)
FROM transactions AS transaction_record
INNER JOIN memberships AS membership ON membership.id = transaction_record.membership_id
INNER JOIN transaction_order ON transaction_order.id = transaction_record.id;

-- Loai ma khuyen mai khong dung pham vi goi hoac khong hieu luc tai thoi diem giao dich.
UPDATE transaction_record
SET promotion_id = NULL
FROM transactions AS transaction_record
INNER JOIN promotions AS promotion ON promotion.id = transaction_record.promotion_id
WHERE CAST(transaction_record.created_at AS DATE) NOT BETWEEN promotion.start_date AND promotion.end_date
   OR (promotion.package_id IS NOT NULL
       AND promotion.package_id <> transaction_record.requested_package_id);

-- Tinh lai so tien theo dung thu tu: gia goc -> chiet khau thoi han -> ma khuyen mai.
UPDATE transaction_record
SET amount = CAST(ROUND(
        transaction_record.original_amount
        * (100 - COALESCE(long_term_discount.discount_percent, 0)) / 100.0
        * (100 - COALESCE(promotion.discount_percent, 0)) / 100.0,
        0) AS DECIMAL(12,0))
FROM transactions AS transaction_record
LEFT JOIN promotions AS promotion ON promotion.id = transaction_record.promotion_id
OUTER APPLY (
    SELECT TOP (1) package_discount.discount_percent
    FROM package_discounts AS package_discount
    WHERE (package_discount.package_id IS NULL
           OR package_discount.package_id = transaction_record.requested_package_id)
      AND package_discount.min_days <= transaction_record.requested_duration_days
    ORDER BY package_discount.min_days DESC,
             CASE WHEN package_discount.package_id IS NULL THEN 1 ELSE 0 END
) AS long_term_discount;

-- current_usage phai phan anh dung so giao dich da xac nhan su dung ma.
UPDATE promotion
SET current_usage = (
    SELECT COUNT(*)
    FROM transactions AS transaction_record
    WHERE transaction_record.promotion_id = promotion.id
      AND transaction_record.status = 'CONFIRMED'
)
FROM promotions AS promotion;

-- Khong de goi ACTIVE/PAUSED qua ngay het han khi seed duoc chay lai.
UPDATE memberships
SET status = 'EXPIRED',
    pause_reason = NULL,
    paused_at = NULL
WHERE status IN ('ACTIVE', 'PAUSED')
  AND end_date < CAST(GETDATE() AS DATE);

-- 9. Insert Exercises
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Bench Press', N'Ngực', N'Nằm chắc trên ghế, giữ bả vai thu về sau, bàn chân bám sàn và hạ thanh đòn có kiểm soát về giữa ngực. Không bật thanh đòn khỏi ngực; chọn mức tạ cho phép duy trì cổ tay và khuỷu tay ổn định.', 'https://www.youtube.com/watch?v=SCVCLChPQFY', 16, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Incline Dumbbell Press', N'Ngực', N'Đặt ghế dốc vừa phải, giữ ngực mở và ép hai bả vai vào ghế. Hạ tạ đến khi khuỷu tay thấp hơn vai một chút rồi đẩy lên theo đường vòng cung tự nhiên, tránh va hai quả tạ.', 'https://www.youtube.com/watch?v=8iPEnn-ltC8', 4, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Push Up', N'Ngực', N'Giữ cơ thể thành một đường thẳng từ đầu đến gót chân, siết bụng và mông. Hạ ngực gần sàn với khuỷu tay chếch khoảng 30–45 độ, sau đó đẩy người lên mà không võng lưng.', 'https://www.youtube.com/watch?v=IODxDxX7oi4', 12, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Pull Up', N'Lưng', N'Bắt đầu ở tư thế treo chủ động, kéo bả vai xuống trước khi kéo ngực hướng lên xà. Giữ thân người ổn định, không vung chân; hạ chậm đến khi tay gần duỗi hết.', 'https://www.youtube.com/watch?v=eGo4IYlbE5g', 12, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Lat Pulldown', N'Lưng', N'Ngồi vững, hơi ngả thân và kéo thanh về phần trên ngực bằng chuyển động của khuỷu tay. Không kéo sau gáy hoặc giật người; trả thanh lên chậm để cơ xô được kéo giãn.', 'https://www.youtube.com/watch?v=CAwf7n6Luuc', 8, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Deadlift', N'Lưng', N'Đặt thanh đòn trên giữa bàn chân, giữ lưng trung lập và tạo lực căng trước khi nhấc. Đẩy sàn bằng chân, đưa hông về trước khi đứng thẳng; không ngửa lưng ở vị trí khóa.', 'https://www.youtube.com/watch?v=ZaTM37cfiDs', 10, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Barbell Row', N'Lưng', N'Gập hông, giữ cột sống trung lập và thân người ổn định. Kéo thanh về vùng bụng dưới bằng khuỷu tay, siết lưng ở cuối biên độ rồi hạ tạ có kiểm soát, không dùng đà từ lưng dưới.', 'https://www.youtube.com/watch?v=Nqh7q3zDCoQ', 9, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Squat', N'Chân', N'Đặt chân ở độ rộng thoải mái, giữ bàn chân bám sàn và đầu gối đi cùng hướng mũi chân. Hạ hông trong biên độ kiểm soát, giữ thân chắc rồi đẩy sàn để đứng lên.', 'https://www.youtube.com/watch?v=gcNh17Ckjgg', 2, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Leg Press', N'Chân', N'Đặt lưng và hông sát đệm, bàn chân ổn định trên bàn đạp. Hạ bàn đạp đến khi vẫn giữ được hông trên ghế, sau đó đẩy lên nhưng không khóa cứng đầu gối.', 'https://www.youtube.com/watch?v=IZxyjW7MPJQ', 7, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Lunges', N'Chân', N'Bước chân đủ dài để hai đầu gối gập tự nhiên, giữ thân người thẳng và trọng tâm ổn định. Hạ gối sau gần sàn, sau đó đẩy qua toàn bộ bàn chân trước để trở về vị trí ban đầu.', 'https://www.youtube.com/watch?v=QOVaHwm-Q6U', 12, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Leg Curl', N'Chân', N'Điều chỉnh trục máy ngang khớp gối và đệm nằm ngay trên gót chân. Gập gối có kiểm soát, siết cơ đùi sau ở cuối biên độ rồi trả tạ chậm, không nhấc hông khỏi đệm.', 'https://www.youtube.com/watch?v=1Tq3QdYUuHs', 12, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Overhead Press', N'Vai', N'Giữ thân người chắc, cổ tay thẳng trên khuỷu tay và thanh đòn gần cơ thể. Đẩy tạ qua đầu theo đường thẳng, không ưỡn lưng quá mức; hạ tạ có kiểm soát về vai.', 'https://www.youtube.com/watch?v=2yjwXTZQDDI', 8, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Lateral Raise', N'Vai', N'Giữ khuỷu tay hơi cong, nâng tạ sang hai bên đến gần ngang vai bằng lực cơ vai. Dùng mức tạ vừa phải, không nhún người và hạ tạ chậm để duy trì kiểm soát.', 'https://www.youtube.com/watch?v=3VcKaXpzqRo', 14, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Face Pull', N'Vai', N'Kéo dây về phía trán, tách hai đầu dây và xoay cánh tay ra ngoài. Giữ vai thấp, ngực mở và không ngả người quá nhiều; ưu tiên cảm nhận vai sau và lưng trên.', 'https://www.youtube.com/watch?v=rep-qVOkqgk', 10, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Bicep Curl', N'Tay', N'Giữ khuỷu tay gần thân và cổ tay trung lập. Gập khuỷu để nâng tạ mà không đẩy vai ra trước, siết cơ tay trước ở đỉnh rồi hạ chậm đến gần duỗi hết tay.', 'https://www.youtube.com/watch?v=ykJmrZ5v0Oo', 7, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Tricep Pushdown', N'Tay', N'Cố định khuỷu tay sát thân, kéo tay cầm xuống đến khi cánh tay gần duỗi thẳng. Chỉ di chuyển cẳng tay, không dùng trọng lượng cơ thể; trả cáp lên chậm và giữ vai thư giãn.', 'https://www.youtube.com/watch?v=2-LAMcpzODU', 13, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Hammer Curl', N'Tay', N'Cầm tạ với lòng bàn tay hướng vào nhau, giữ khuỷu tay cố định cạnh thân. Nâng tạ không dùng đà, dừng ngắn ở đỉnh rồi hạ có kiểm soát để tác động cơ cánh tay và cẳng tay.', 'https://www.youtube.com/watch?v=zC3nLlEvin4', 8, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Plank', N'Bụng', N'Đặt khuỷu tay dưới vai, siết bụng và mông để giữ đầu, lưng và chân trên một đường thẳng. Thở đều, không võng lưng hoặc nâng hông quá cao; dừng khi không còn giữ được tư thế.', 'https://www.youtube.com/watch?v=pSHjTRCQxIw', 8, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Crunch', N'Bụng', N'Nằm co gối, áp lưng dưới xuống sàn và nâng phần vai bằng lực cơ bụng. Không kéo cổ bằng tay; cuộn thân trong biên độ ngắn rồi hạ chậm, giữ căng vùng bụng.', 'https://www.youtube.com/watch?v=Xyd_fa5zoEU', 4, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Hanging Leg Raise', N'Bụng', N'Treo người chắc trên xà, giữ vai chủ động và hạn chế đung đưa. Cuộn xương chậu để nâng gối hoặc chân bằng cơ bụng, sau đó hạ chậm; bắt đầu bằng phiên bản co gối nếu cần.', 'https://www.youtube.com/watch?v=Pr1ieGZ5atk', 6, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Running (Treadmill)', N'Cardio', N'Khởi động bằng đi bộ nhanh, sau đó tăng tốc dần theo khả năng. Giữ thân thẳng, bước chân tự nhiên và không bám tay vịn; giảm tốc từ từ ở cuối buổi thay vì dừng đột ngột.', 'https://www.youtube.com/watch?v=_kGESn8ArrU', 8, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Cycling', N'Cardio', N'Điều chỉnh yên để đầu gối vẫn hơi cong khi bàn đạp ở vị trí thấp nhất. Giữ lưng trung lập, đạp tròn và tăng kháng lực từ từ; tránh để hông lắc sang hai bên.', 'https://www.youtube.com/watch?v=4Hl1WAGKjMc', 4, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Jump Rope', N'Cardio', N'Giữ khuỷu tay gần thân, xoay dây chủ yếu bằng cổ tay và bật nhảy thấp trên mũi bàn chân. Bắt đầu bằng nhịp chậm, nghỉ khi bắp chân mất kiểm soát và ưu tiên bề mặt có độ đàn hồi.', 'https://www.youtube.com/watch?v=1BZM2Vre5oc', 14, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Burpee', N'Toàn thân', N'Từ tư thế đứng, chống tay xuống sàn, đưa chân về sau vào tư thế plank rồi trở lại và bật lên. Giữ bụng chắc khi về plank; có thể bỏ phần chống đẩy hoặc bật nhảy để giảm độ khó.', 'https://www.youtube.com/watch?v=auBLPXO8Fww', 10, 1);
INSERT INTO exercises (name, muscle_group, description, video_url, created_by, is_active) VALUES (N'Kettlebell Swing', N'Toàn thân', N'Đây là chuyển động gập hông, không phải squat. Đưa tạ về sau giữa hai chân rồi duỗi hông mạnh để tạ bay đến ngang ngực; giữ tay thư giãn, lưng trung lập và không nâng tạ bằng vai.', 'https://www.youtube.com/watch?v=YSxHifyI6s8', 9, 1);

-- 10. Insert PT Notes
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (4, 53, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-01-20 19:44:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (9, 57, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2025-11-13 12:03:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (12, 46, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-06-13 16:14:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (8, 45, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2026-06-29 16:26:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (3, 19, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2025-12-29 06:30:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (11, 42, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2026-07-03 18:00:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (4, 52, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-05-08 18:19:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (14, 56, N'Hội viên đạt mục tiêu giảm 1kg trong tháng này, tiếp tục duy trì chế độ hiện tại.', '2026-06-15 20:24:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (11, 66, N'Hội viên báo mệt mỏi, giảm cường độ buổi tập và tăng thời gian nghỉ giữa hiệp.', '2026-07-08 10:13:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (15, 60, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-05-09 15:06:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (5, 90, N'Kỹ thuật squat đã cải thiện rõ rệt so với tuần trước.', '2026-05-20 19:53:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (2, 100, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-06-21 10:43:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (13, 86, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-07-02 11:17:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (14, 63, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-02-15 10:16:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (15, 60, N'Hội viên đạt mục tiêu giảm 1kg trong tháng này, tiếp tục duy trì chế độ hiện tại.', '2026-01-22 08:33:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (6, 49, N'Hội viên đạt mục tiêu giảm 1kg trong tháng này, tiếp tục duy trì chế độ hiện tại.', '2025-07-12 18:54:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (13, 76, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2026-01-20 11:43:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (6, 20, N'Kỹ thuật squat đã cải thiện rõ rệt so với tuần trước.', '2026-07-03 13:18:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (3, 78, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-07-01 12:47:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (10, 44, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2026-06-17 17:45:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (12, 95, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2025-11-09 18:30:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (10, 69, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2025-11-11 10:54:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (2, 100, N'Hội viên báo mệt mỏi, giảm cường độ buổi tập và tăng thời gian nghỉ giữa hiệp.', '2026-06-14 10:06:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (13, 36, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2025-08-26 17:51:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (9, 43, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2026-07-08 16:05:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (10, 69, N'Kỹ thuật squat đã cải thiện rõ rệt so với tuần trước.', '2025-06-19 17:55:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (3, 78, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2026-06-29 11:16:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (2, 59, N'Hội viên báo mệt mỏi, giảm cường độ buổi tập và tăng thời gian nghỉ giữa hiệp.', '2026-07-07 19:07:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (10, 23, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2025-12-11 15:37:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (2, 25, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2026-01-08 20:12:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (3, 35, N'Kỹ thuật squat đã cải thiện rõ rệt so với tuần trước.', '2026-06-28 12:33:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (13, 77, N'Hội viên còn yếu phần core, bổ sung thêm bài tập plank và crunch trong tuần tới.', '2026-07-04 19:54:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (10, 36, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2026-07-06 11:26:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (14, 83, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-07-04 11:55:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (9, 43, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-07-06 13:21:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (11, 42, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-06-24 16:03:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (15, 82, N'Hội viên đạt mục tiêu giảm 1kg trong tháng này, tiếp tục duy trì chế độ hiện tại.', '2026-05-14 09:48:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (9, 39, N'Hội viên báo mệt mỏi, giảm cường độ buổi tập và tăng thời gian nghỉ giữa hiệp.', '2026-07-08 18:10:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (6, 30, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2025-03-22 15:27:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (8, 79, N'Hội viên báo mệt mỏi, giảm cường độ buổi tập và tăng thời gian nghỉ giữa hiệp.', '2025-11-24 12:30:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (4, 17, N'Cần theo dõi thêm nhịp tim khi tập cardio cường độ cao.', '2026-01-04 14:12:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (15, 71, N'Hội viên đạt mục tiêu giảm 1kg trong tháng này, tiếp tục duy trì chế độ hiện tại.', '2026-06-29 20:20:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (8, 93, N'Buổi tập hôm nay hội viên thực hiện tốt các bài tập được giao, cần tăng dần khối lượng tạ.', '2026-06-02 06:43:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (13, 50, N'Kỹ thuật squat đã cải thiện rõ rệt so với tuần trước.', '2026-06-11 17:56:00');
INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES (2, 62, N'Đề nghị hội viên bổ sung thêm protein sau buổi tập để hỗ trợ phục hồi cơ.', '2025-12-25 07:24:00');

-- 11. PT Comments: de trong theo yeu cau cua nguoi dung (khong insert du lieu)

-- 12. Insert Diets (chi ap dung cho hoi vien goi VIP co has_meal_plan)
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (96, 15, 'SPECIFIC_DATE', '2026-05-31', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-07-02', N'3 quả trứng luộc + bánh mì nguyên cám', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (96, 15, 'SPECIFIC_DATE', '2026-06-14', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (37, 6, 'SPECIFIC_DATE', '2026-07-05', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (37, 6, 'SPECIFIC_DATE', '2026-07-06', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (37, 6, 'SPECIFIC_DATE', '2026-07-03', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (86, 13, 'SPECIFIC_DATE', '2026-07-03', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (24, 15, 'SPECIFIC_DATE', '2026-07-04', N'Sinh tố protein + hạt óc chó', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (96, 15, 'SPECIFIC_DATE', '2026-05-15', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (27, 2, 'SPECIFIC_DATE', '2026-06-01', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (77, 13, 'SPECIFIC_DATE', '2026-07-07', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-07-08', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (24, 15, 'SPECIFIC_DATE', '2026-07-02', N'3 quả trứng luộc + bánh mì nguyên cám', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (86, 13, 'SPECIFIC_DATE', '2026-07-05', N'Sinh tố protein + hạt óc chó', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (96, 15, 'SPECIFIC_DATE', '2026-05-19', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-05', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (86, 13, 'SPECIFIC_DATE', '2026-07-04', N'3 quả trứng luộc + bánh mì nguyên cám', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-06-28', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-07-06', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (24, 15, 'SPECIFIC_DATE', '2026-06-28', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (96, 15, 'SPECIFIC_DATE', '2026-06-23', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (86, 13, 'SPECIFIC_DATE', '2026-07-02', N'Sinh tố protein + hạt óc chó', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-06-30', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-02', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-07-04', N'Sinh tố protein + hạt óc chó', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (27, 2, 'SPECIFIC_DATE', '2026-06-06', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-06-25', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (27, 2, 'SPECIFIC_DATE', '2026-06-25', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (77, 13, 'SPECIFIC_DATE', '2026-07-09', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (37, 6, 'SPECIFIC_DATE', '2026-07-01', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (24, 15, 'SPECIFIC_DATE', '2026-06-30', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-09', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (64, 16, 'SPECIFIC_DATE', '2026-07-09', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (37, 6, 'SPECIFIC_DATE', '2026-07-07', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-06-29', N'3 quả trứng luộc + bánh mì nguyên cám', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (77, 13, 'SPECIFIC_DATE', '2026-07-05', N'Sinh tố protein + hạt óc chó', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (24, 15, 'SPECIFIC_DATE', '2026-07-08', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-04', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-06-24', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-07-01', N'Sinh tố protein + hạt óc chó', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-06-16', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-03', N'3 quả trứng luộc + bánh mì nguyên cám', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (27, 2, 'SPECIFIC_DATE', '2026-07-01', N'Sinh tố protein + hạt óc chó', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (86, 13, 'SPECIFIC_DATE', '2026-07-01', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (59, 2, 'SPECIFIC_DATE', '2026-07-09', N'Sinh tố protein + hạt óc chó', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Cá diêu hồng hấp + rau muống luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (83, 14, 'SPECIFIC_DATE', '2026-07-07', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (27, 2, 'SPECIFIC_DATE', '2026-06-14', N'Yến mạch + sữa tươi không đường + 1 quả chuối', N'Cá hồi nướng + khoai lang + salad', N'Súp rau củ + trứng luộc');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (77, 13, 'SPECIFIC_DATE', '2026-07-04', N'3 quả trứng luộc + bánh mì nguyên cám', N'Thịt bò xào + cơm gạo lứt + bông cải xanh', N'Ức gà luộc + rau củ hấp');
INSERT INTO diets (member_id, pt_id, day_type, diet_date, breakfast, lunch, dinner) VALUES (42, 11, 'SPECIFIC_DATE', '2026-06-17', N'3 quả trứng luộc + bánh mì nguyên cám', N'Ức gà áp chảo 200g + cơm gạo lứt + rau xanh luộc', N'Súp rau củ + trứng luộc');

-- 13. Insert Reviews
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (44, 10, 4, N'PT rất tận tâm và nhiệt tình hướng dẫn kỹ thuật.', '2026-06-22 07:59:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (35, 3, 3, N'Cần cải thiện thêm về việc theo dõi chế độ ăn.', '2026-06-14 17:37:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (23, 10, 4, N'Cần cải thiện thêm về việc theo dõi chế độ ăn.', '2025-11-16 14:59:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (90, 5, 3, N'Giáo án phù hợp với thể trạng, cảm thấy tiến bộ rõ rệt.', '2026-05-21 16:11:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (36, 13, 5, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-02-17 16:56:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (36, 10, 5, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-07-09 11:53:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (97, 16, 3, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-06-19 10:13:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (42, 11, 4, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-07-04 12:45:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (34, 6, 3, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-01-21 16:36:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (78, 3, 4, NULL, '2026-07-06 11:56:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (79, 8, 3, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2025-10-17 07:24:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (37, 6, 5, N'PT rất tận tâm và nhiệt tình hướng dẫn kỹ thuật.', '2026-07-03 11:48:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (38, 12, 1, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-06-19 09:53:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (24, 13, 4, N'PT đúng giờ, chuyên nghiệp, sẽ tiếp tục đăng ký buổi sau.', '2026-02-21 19:34:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (21, 12, 5, N'Cần cải thiện thêm về việc theo dõi chế độ ăn.', '2026-07-01 16:55:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (100, 2, 3, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-07-08 13:15:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (44, 9, 4, NULL, '2026-07-08 20:28:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (47, 6, 2, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-04-21 17:23:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (96, 15, 4, N'Giáo án phù hợp với thể trạng, cảm thấy tiến bộ rõ rệt.', '2026-06-30 18:12:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (53, 4, 5, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-01-24 11:00:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (63, 14, 4, NULL, '2026-02-17 17:39:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (60, 15, 4, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-04-19 08:50:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (62, 7, 4, N'Giáo án phù hợp với thể trạng, cảm thấy tiến bộ rõ rệt.', '2026-07-08 08:50:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (88, 3, 5, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2025-12-13 13:05:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (66, 11, 5, N'Giáo án phù hợp với thể trạng, cảm thấy tiến bộ rõ rệt.', '2026-07-04 12:23:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (59, 2, 5, NULL, '2026-07-09 14:35:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (18, 8, 5, N'PT đúng giờ, chuyên nghiệp, sẽ tiếp tục đăng ký buổi sau.', '2026-07-08 10:57:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (50, 13, 2, N'PT giải thích kỹ nguyên lý từng bài tập, dễ hiểu.', '2026-06-17 13:58:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (92, 6, 3, N'PT đúng giờ, chuyên nghiệp, sẽ tiếp tục đăng ký buổi sau.', '2026-07-08 19:32:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (82, 15, 4, N'PT đúng giờ, chuyên nghiệp, sẽ tiếp tục đăng ký buổi sau.', '2026-06-23 17:12:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (95, 12, 5, N'PT rất tận tâm và nhiệt tình hướng dẫn kỹ thuật.', '2025-10-13 17:40:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (46, 12, 3, N'Cần cải thiện thêm về việc theo dõi chế độ ăn.', '2026-06-05 10:34:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (80, 12, 4, N'PT rất tận tâm và nhiệt tình hướng dẫn kỹ thuật.', '2026-03-30 07:29:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (64, 16, 5, N'PT rất tận tâm và nhiệt tình hướng dẫn kỹ thuật.', '2026-07-09 07:11:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (52, 4, 4, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-05-22 10:58:00');
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at) VALUES (24, 15, 3, N'Rất hài lòng, đã đạt được mục tiêu giảm cân đề ra.', '2026-06-28 18:54:00');

-- Dong bo diem trung binh trong ho so PT voi cac danh gia mau vua chen.
UPDATE profile
SET rating_score = review_summary.average_rating
FROM pt_profiles AS profile
INNER JOIN (
    SELECT pt_id, CAST(ROUND(AVG(CAST(rating_star AS DECIMAL(5,2))), 1) AS DECIMAL(3,1)) AS average_rating
    FROM reviews
    GROUP BY pt_id
) AS review_summary ON review_summary.pt_id = profile.user_id;

-- 14. Insert Blogs
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (4, N'5 bài tập ngực hiệu quả cho người mới', CONCAT(N'Một buổi tập ngực tốt không cần quá nhiều động tác. Người mới nên ưu tiên kỹ thuật, biên độ kiểm soát và khả năng tiến bộ đều đặn thay vì chạy theo mức tạ.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Năm lựa chọn dễ áp dụng gồm Bench Press để phát triển sức mạnh tổng thể; Incline Dumbbell Press nhấn vào phần ngực trên; Push Up giúp làm chủ trọng lượng cơ thể; Chest Press Machine tạo quỹ đạo ổn định; Cable Fly bổ sung chuyển động khép tay. Hãy chọn ba đến bốn bài, thực hiện hai đến ba hiệp mỗi bài và dừng hiệp khi vẫn còn giữ được kỹ thuật tốt.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Khởi động vai và ngực trước buổi tập, giữ bả vai ổn định, không bật tạ khỏi ngực. Nếu xuất hiện đau nhói ở vai hoặc khuỷu tay, hãy dừng bài và trao đổi với PT.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/strength-training/art-20046670'), 'https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-03-03 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (5, N'Chế độ dinh dưỡng tăng cơ giảm mỡ khoa học', CONCAT(N'Tăng cơ và giảm mỡ là quá trình cần phối hợp giữa tập kháng lực, tổng năng lượng phù hợp và khẩu phần có đủ chất. Không có một thực phẩm riêng lẻ nào quyết định kết quả.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Hãy xây dựng mỗi bữa quanh một nguồn đạm như thịt nạc, cá, trứng, sữa, đậu hũ hoặc các loại đậu; bổ sung rau, trái cây và nguồn tinh bột ít tinh chế. Với người tập luyện khỏe mạnh, tài liệu của ISSN cho biết khoảng 1,4–2,0 g protein trên mỗi kg cân nặng mỗi ngày thường đáp ứng mục tiêu duy trì và phát triển cơ. Con số cụ thể vẫn cần điều chỉnh theo thể trạng và mục tiêu năng lượng.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Để giảm mỡ, chọn mức thâm hụt năng lượng vừa phải và theo dõi xu hướng cân nặng trong nhiều tuần. Đừng cắt bỏ hoàn toàn tinh bột hoặc chất béo; ưu tiên chế độ có thể duy trì lâu dài, ngủ đủ và tập đều.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://pubmed.ncbi.nlm.nih.gov/28642676/'), 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-01-15 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (11, N'Hướng dẫn tập Squat đúng kỹ thuật, tránh chấn thương', CONCAT(N'Squat là chuyển động nền tảng cho chân và hông, nhưng không có một độ rộng chân duy nhất phù hợp với mọi người. Mục tiêu là tìm tư thế giúp bàn chân bám sàn, đầu gối ổn định và cột sống được kiểm soát.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Bắt đầu với chân rộng gần bằng vai và mũi chân xoay nhẹ ra ngoài. Hít vào, siết thân người, đồng thời gập gối và hông để hạ xuống. Đầu gối nên đi cùng hướng mũi chân; trọng lượng phân bố trên toàn bàn chân. Đứng lên bằng cách đẩy sàn, giữ ngực và hông chuyển động đồng bộ.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Người mới nên tập Bodyweight Squat hoặc Goblet Squat trước khi dùng thanh đòn. Tăng tải từ từ và chỉ hạ sâu trong biên độ vẫn kiểm soát được. Đau nhói không phải dấu hiệu bình thường; khi đó hãy dừng lại và nhờ PT kiểm tra kỹ thuật.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/weight-training/art-20045842'), 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-04-28 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (16, N'Lợi ích của Yoga đối với sức khỏe tinh thần', CONCAT(N'Yoga kết hợp tư thế, nhịp thở và sự tập trung. Khi luyện tập đều đặn với cường độ phù hợp, hoạt động này có thể hỗ trợ quản lý căng thẳng, cân bằng, giấc ngủ và sức khỏe cảm xúc.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Người mới có thể bắt đầu bằng buổi 15–20 phút với các tư thế cơ bản, tập trung vào nhịp thở chậm và chuyển động có kiểm soát. Không cần ép cơ thể đạt hình dáng giống người hướng dẫn; phạm vi chuyển động nên phù hợp với khả năng hiện tại.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Yoga nhìn chung an toàn khi được hướng dẫn đúng, nhưng vẫn có nguy cơ chấn thương. Người có bệnh nền, đang mang thai hoặc có vấn đề cơ xương khớp nên hỏi chuyên gia y tế và báo cho giáo viên trước buổi tập. Yoga là phương thức hỗ trợ sức khỏe, không thay thế điều trị chuyên môn.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.nccih.nih.gov/health/tips/things-you-should-know-about-yoga'), 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-01-21 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (2, N'Cách xây dựng lịch tập gym cho người bận rộn', CONCAT(N'Lịch tập hiệu quả nhất là lịch bạn có thể duy trì. Với người bận rộn, hai hoặc ba buổi toàn thân mỗi tuần thường thực tế hơn một lịch chia quá nhiều ngày.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Mỗi buổi 40–60 phút có thể gồm một động tác chân, một động tác đẩy, một động tác kéo, một bài cho hông và một bài core. Ưu tiên bài đa khớp, chuẩn bị sẵn trang phục và đặt lịch tập như một cuộc hẹn. Nếu chỉ có 20 phút, hãy tập một phiên ngắn thay vì bỏ cả buổi.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'CDC khuyến nghị người trưởng thành tích lũy ít nhất 150 phút hoạt động aerobic mức vừa mỗi tuần và tập tăng cường cơ ít nhất hai ngày. Bạn có thể chia nhỏ thời lượng trong tuần; hoạt động ít vẫn tốt hơn không vận động. Hãy dành ngày nghỉ phù hợp cho nhóm cơ vừa tập.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.cdc.gov/physical-activity-basics/guidelines/adults.html'), 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-02-23 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (9, N'10 thực phẩm giàu protein dễ đưa vào thực đơn', CONCAT(N'Protein hỗ trợ sửa chữa và duy trì mô cơ, nhưng khẩu phần tốt vẫn cần đa dạng. Mười lựa chọn dễ tìm gồm ức gà, thịt bò nạc, cá, trứng, sữa chua Hy Lạp, sữa, đậu hũ, đậu lăng, đậu gà và cá ngừ.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Hãy phân bổ nguồn đạm qua các bữa thay vì dồn toàn bộ vào buổi tối. Kết hợp protein với rau, trái cây, tinh bột và chất béo phù hợp giúp bữa ăn cân bằng hơn. Khi chọn thực phẩm đóng hộp, nên xem lượng natri và khẩu phần trên nhãn.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Whey có thể tiện lợi nhưng không bắt buộc nếu thực đơn đã đáp ứng nhu cầu. Người có bệnh thận, bệnh chuyển hóa hoặc chế độ ăn đặc biệt cần trao đổi với bác sĩ hoặc chuyên gia dinh dưỡng trước khi tăng mạnh lượng protein.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://pubmed.ncbi.nlm.nih.gov/28642676/'), 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-03-31 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (11, N'Vì sao bạn nên khởi động trước khi tập nặng?', CONCAT(N'Khởi động giúp cơ thể chuyển dần từ trạng thái nghỉ sang vận động bằng cách tăng nhiệt độ cơ và lưu lượng máu. Một phần khởi động tốt cũng cho bạn cơ hội kiểm tra mức độ sẵn sàng của khớp và kỹ thuật trong ngày.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Hãy dành 5–10 phút cho vận động nhẹ, sau đó thực hiện các chuyển động gần giống bài chính. Trước Squat có thể dùng squat không tạ và vài hiệp tăng dần; trước Bench Press có thể xoay vai, kích hoạt lưng trên và nâng thanh nhẹ. Mỗi hiệp khởi động nên chuẩn bị cho hiệp làm việc, không làm bạn kiệt sức.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Giãn cơ tĩnh kéo dài ngay trước khi nâng nặng không phải lúc nào cũng cần thiết. Ưu tiên khởi động động và tăng tải theo từng bước. Nếu một chuyển động gây đau bất thường, không nên cố vượt qua chỉ vì đã lên lịch tập.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/exercise/art-20045517'), 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=1200&q=80', 'DRAFT', '2026-06-03 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (11, N'Cardio buổi sáng hay buổi tối tốt hơn?', CONCAT(N'Không có khung giờ duy nhất tốt nhất cho tất cả mọi người. Thời điểm phù hợp là thời điểm bạn tỉnh táo, có thể duy trì đều và không làm ảnh hưởng đáng kể đến giấc ngủ hoặc buổi tập sức mạnh.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Cardio buổi sáng có ưu điểm là dễ hoàn thành trước khi lịch làm việc phát sinh. Cardio buổi tối có thể phù hợp với người có nhiệt độ cơ thể và mức năng lượng cao hơn vào cuối ngày. Nếu tập gần giờ ngủ khiến bạn khó thư giãn, hãy giảm cường độ hoặc chuyển buổi tập sớm hơn.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Thay vì tranh luận về giờ tập, hãy theo dõi ba yếu tố: mức năng lượng, hiệu suất và khả năng duy trì trong vài tuần. Mục tiêu sức khỏe tổng quát quan trọng hơn việc chọn sáng hay tối; bạn có thể chia nhỏ thời gian vận động trong tuần.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.cdc.gov/physical-activity-basics/guidelines/adults.html'), 'https://images.unsplash.com/photo-1538805060514-97d9cc17730c?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-01-04 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (13, N'Cách phục hồi cơ bắp sau buổi tập nặng', CONCAT(N'Phục hồi không chỉ là nằm nghỉ. Cơ thể cần thời gian, dinh dưỡng, nước và giấc ngủ để thích nghi với kích thích tập luyện. Đau cơ nhẹ sau buổi tập có thể xảy ra, nhưng đau nhói, sưng rõ hoặc giảm khả năng vận động cần được chú ý.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Sau buổi tập, hãy ăn một bữa cân bằng có protein và carbohydrate, uống nước theo cảm giác khát và mức mất mồ hôi. Tránh tập nặng cùng một nhóm cơ vào hai ngày liên tiếp; có thể đi bộ nhẹ hoặc thực hiện vận động phục hồi nếu cảm thấy dễ chịu.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Theo dõi hiệu suất, giấc ngủ và mức mệt qua nhiều ngày. Nếu sức mạnh liên tục giảm, nhịp tim nghỉ tăng hoặc mất hứng thú tập, hãy giảm tải và trao đổi với PT. Phục hồi tốt giúp bạn duy trì tiến bộ thay vì chỉ hoàn thành thật nhiều buổi.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/strength-training/art-20046670'), 'https://images.unsplash.com/photo-1517838277536-f5f99be501cd?auto=format&fit=crop&w=1200&q=80', 'DRAFT', '2026-07-07 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (6, N'Những sai lầm phổ biến khi mới tập Gym', CONCAT(N'Người mới thường muốn thấy kết quả nhanh nên dễ tăng tạ quá sớm, tập quá nhiều buổi hoặc sao chép lịch của người có kinh nghiệm. Điều này làm kỹ thuật xuống cấp và khiến việc duy trì trở nên khó khăn.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Hãy tránh năm lỗi chính: bỏ qua khởi động; dùng đà thay cho cơ mục tiêu; nín thở khi nâng tạ; chỉ tập nhóm cơ yêu thích; và tiếp tục khi xuất hiện đau nhói. Bắt đầu với mức tạ có thể kiểm soát khoảng 12–15 lần, học biên độ đúng rồi mới tăng dần.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Ghi lại bài tập, số hiệp, số lần và cảm nhận sau buổi tập. Một chương trình đơn giản được thực hiện đều sẽ tốt hơn chương trình phức tạp nhưng liên tục thay đổi. Khi chưa chắc về tư thế, hãy nhờ PT quan sát trực tiếp.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/weight-training/art-20045842'), 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-07-08 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (14, N'Bí quyết duy trì động lực tập luyện lâu dài', CONCAT(N'Động lực thay đổi theo từng ngày, vì vậy thói quen và môi trường mới là nền tảng giúp bạn đi đường dài. Đừng đặt mục tiêu chỉ dựa trên con số cân nặng; hãy thêm mục tiêu hành vi có thể kiểm soát.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Ví dụ: hoàn thành ba buổi tập mỗi tuần, đi bộ sau bữa tối hoặc chuẩn bị đồ tập từ tối hôm trước. Chọn bài tập bạn thấy phù hợp, đặt lịch cố định và theo dõi những tiến bộ nhỏ như kỹ thuật tốt hơn, thêm một lần lặp hoặc ngủ ngon hơn.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Khi bỏ lỡ một buổi, hãy quay lại ở cơ hội gần nhất thay vì chờ đến tuần mới. Có bạn tập hoặc PT giúp tăng trách nhiệm, nhưng lịch tập vẫn cần phù hợp cuộc sống của bạn. Một mức vận động nhỏ được duy trì đều vẫn mang lại lợi ích.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.cdc.gov/physical-activity-basics/guidelines/adults.html'), 'https://images.unsplash.com/photo-1534258936925-c58bed479fcb?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-06-18 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (11, N'Gym truyền thống và Calisthenics: nên chọn gì?', CONCAT(N'Gym truyền thống dùng tạ và máy để điều chỉnh tải tương đối chính xác; Calisthenics dùng trọng lượng cơ thể và chú trọng khả năng kiểm soát chuyển động. Cả hai đều có thể phát triển sức mạnh nếu chương trình có tiến triển phù hợp.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Gym thuận lợi khi mục tiêu là tăng tải từng bước hoặc tập trung một nhóm cơ. Calisthenics linh hoạt về địa điểm, ít thiết bị và tạo động lực qua các kỹ năng như pull-up, dip hay handstand. Tuy nhiên, một số kỹ năng nâng cao vẫn cần tiến trình lâu dài và kỹ thuật tốt.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Bạn không bắt buộc phải chọn một bên. Có thể dùng Squat và Press với tạ để tăng sức mạnh, kết hợp Push Up, Pull Up và Plank để cải thiện kiểm soát cơ thể. Lựa chọn tốt nhất phụ thuộc mục tiêu, sở thích, chấn thương và thiết bị sẵn có.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.mayoclinic.org/healthy-lifestyle/fitness/in-depth/strength-training/art-20046670'), 'https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-01-01 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (13, N'Hướng dẫn uống nước khi tập luyện', CONCAT(N'Nhu cầu nước thay đổi theo cơ thể, thời tiết, thời lượng và cường độ vận động. Vì vậy không nên áp dụng một con số cố định cho mọi buổi tập.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Hãy bắt đầu buổi tập trong trạng thái không khát, mang theo nước và uống từng ngụm trong quá trình tập. Với buổi tập thông thường dưới một giờ, nước lọc thường là lựa chọn phù hợp. Khi tập kéo dài, thời tiết nóng hoặc ra nhiều mồ hôi, đồ uống có điện giải có thể hữu ích tùy trường hợp.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Màu nước tiểu vàng nhạt và cân nặng trước–sau buổi tập có thể giúp theo dõi tình trạng bù nước, nhưng không phải công cụ chẩn đoán. Tránh uống một lượng quá lớn trong thời gian ngắn. Người có bệnh tim, thận hoặc được giới hạn dịch cần tuân theo hướng dẫn y tế riêng.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://pubmed.ncbi.nlm.nih.gov/17277604/'), 'https://images.unsplash.com/photo-1523362628745-0c100150b504?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-01-25 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (6, N'Vai trò của giấc ngủ trong quá trình tăng cơ', CONCAT(N'Tập luyện tạo ra kích thích, còn sự thích nghi diễn ra trong quá trình phục hồi. Giấc ngủ hỗ trợ cơ thể sửa chữa mô, điều hòa năng lượng và duy trì khả năng tập trung cho buổi tập tiếp theo.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'CDC khuyến nghị người trưởng thành từ 18–60 tuổi ngủ từ 7 giờ mỗi ngày. Chất lượng cũng quan trọng: cố gắng giữ giờ ngủ và thức ổn định, hạn chế màn hình và chất kích thích gần giờ ngủ, đồng thời giữ phòng ngủ tối, yên và mát.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nếu thường xuyên thiếu ngủ, hãy giảm kỳ vọng về cường độ thay vì cố bù bằng caffeine hoặc tập quá sức. Khi khó ngủ kéo dài, ngáy lớn hoặc buồn ngủ ban ngày ảnh hưởng sinh hoạt, nên trao đổi với nhân viên y tế. Dinh dưỡng và chương trình tập tốt không thể thay thế giấc ngủ.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn tham khảo: https://www.cdc.gov/sleep/about/index.html'), 'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-05-14 00:00:00');
INSERT INTO blogs (author_id, title, content, thumbnail, status, created_at) VALUES (10, N'Tổng quan các gói tập tại GymPro và quyền lợi', CONCAT(N'GymPro cung cấp nhiều gói để hội viên chọn theo thời gian và nhu cầu tập luyện. Trước khi đăng ký, hãy xem rõ thời hạn, số lần bảo lưu, quyền gia hạn và việc gói có kèm huấn luyện viên cá nhân hay không.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Gói ngắn hạn phù hợp khi bạn muốn trải nghiệm hoặc lịch sinh hoạt chưa ổn định. Gói dài hạn thường thuận lợi cho mục tiêu cần nhiều tháng, nhưng chỉ nên chọn khi bạn có kế hoạch sử dụng thực tế. Các quyền lợi chính thức và giá thanh toán luôn là thông tin hiển thị trên màn hình xác nhận giao dịch tại thời điểm mua.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Sau khi kích hoạt, hội viên có thể theo dõi ngày bắt đầu, ngày hết hạn, số ngày còn lại, PT phụ trách và lượt bảo lưu trong mục Quản lý gói tập. Nếu thông tin không khớp giao dịch, hãy giữ mã giao dịch và liên hệ quản trị viên để được kiểm tra.', CHAR(13), CHAR(10), CHAR(13), CHAR(10), N'Nguồn: Quy định và dữ liệu gói tập nội bộ GymPro.'), 'https://images.unsplash.com/photo-1571902943202-507ec2618e8f?auto=format&fit=crop&w=1200&q=80', 'PUBLISHED', '2026-05-24 00:00:00');
-- 15. Insert PT Schedules (lich tap linh hoat: schedule_date, start_time, end_time)
-- recurring_group_id: cac buoi cung nhom co cung UUID
-- PT id=2 (pt1) voi member id=59
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-07-21', '08:00', '09:30', N'Ngực', 'rg-001-aaaa', 'ACTIVE', '2026-07-10 08:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-07-28', '08:00', '09:30', N'Ngực', 'rg-001-aaaa', 'ACTIVE', '2026-07-10 08:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-07-23', '14:00', '15:30', N'Lưng/Xô', NULL, 'ACTIVE', '2026-07-10 08:05:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-07-25', '19:00', '20:15', N'Đùi/Mông/Chân', NULL, 'ACTIVE', '2026-07-10 08:10:00');
-- Du lieu demo tuan 03/08-09/08/2026: pt1 co 4/5 hoc vien va lich trai deu trong tuan.
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-08-03', '06:00', '07:15', N'Ngực và tay sau', NULL, 'ACTIVE', '2026-08-01 08:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 21, '2026-08-03', '18:30', '19:45', N'Lưng và tay trước', NULL, 'ACTIVE', '2026-08-01 08:05:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 45, '2026-08-04', '08:00', '09:15', N'Chân và mông', NULL, 'ACTIVE', '2026-08-01 08:10:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 84, '2026-08-05', '14:00', '15:15', N'Vai và bụng', NULL, 'ACTIVE', '2026-08-01 08:15:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 59, '2026-08-06', '06:00', '07:15', N'Chân và sức mạnh', NULL, 'ACTIVE', '2026-08-01 08:20:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 21, '2026-08-06', '16:00', '17:15', N'Cardio và thể lực', NULL, 'ACTIVE', '2026-08-01 08:25:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 45, '2026-08-07', '08:00', '09:15', N'Ngực và vai', NULL, 'ACTIVE', '2026-08-01 08:30:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (2, 84, '2026-08-08', '18:30', '19:45', N'Toàn thân và giãn cơ', NULL, 'ACTIVE', '2026-08-01 08:35:00');
-- PT id=3 (pt2) voi member id=35
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (3, 35, '2026-07-22', '09:00', '10:30', N'Toàn thân', 'rg-002-bbbb', 'ACTIVE', '2026-07-11 09:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (3, 35, '2026-07-29', '09:00', '10:30', N'Toàn thân', 'rg-002-bbbb', 'ACTIVE', '2026-07-11 09:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (3, 35, '2026-07-24', '16:00', '17:00', N'Cardio/Thể lực', NULL, 'ACTIVE', '2026-07-11 09:05:00');
-- PT id=6 (pt5) voi member id=37
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-21', '10:00', '11:30', N'Vai', NULL, 'ACTIVE', '2026-07-12 10:00:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-23', '10:00', '11:30', N'Tay Trước/Sau', NULL, 'ACTIVE', '2026-07-12 10:05:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-22', '15:00', '16:30', N'Bụng/Core', NULL, 'ACTIVE', '2026-07-12 10:10:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-25', '08:00', '09:00', N'Giãn cơ/Phục hồi', NULL, 'ACTIVE', '2026-07-12 10:15:00');
-- PT id=5 (pt4) voi member id=65
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (5, 65, '2026-07-21', '13:00', '14:30', N'Tập lưng - vai', NULL, 'ACTIVE', '2026-07-07 08:28:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (5, 65, '2026-07-23', '18:30', '19:45', N'Tập chân', NULL, 'ACTIVE', '2026-07-07 12:22:00');
-- PT id=6 (pt5) voi member id=37
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-24', '18:00', '19:30', N'Cardio/Thể lực', 'rg-003-cccc', 'ACTIVE', '2026-07-04 07:13:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-31', '18:00', '19:30', N'Cardio/Thể lực', 'rg-003-cccc', 'ACTIVE', '2026-07-04 07:13:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (6, 37, '2026-07-22', '19:30', '20:30', N'Cardio/Thể lực', NULL, 'ACTIVE', '2026-06-30 09:59:00');
-- PT id=9 (pt8) voi member id=39
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (9, 39, '2026-07-23', '08:00', '09:15', N'Tập ngực - tay', NULL, 'ACTIVE', '2026-06-20 19:32:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (9, 39, '2026-07-22', '09:30', '10:45', N'Tập lưng - vai', NULL, 'ACTIVE', '2026-07-01 15:45:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (9, 39, '2026-07-25', '09:00', '10:00', N'Yoga/Giãn cơ', NULL, 'ACTIVE', '2026-06-20 09:02:00');
-- PT id=11 (pt10) voi member id=42
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (11, 42, '2026-07-22', '18:00', '19:15', N'Bụng & Core', NULL, 'ACTIVE', '2026-06-25 08:43:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (11, 42, '2026-07-21', '14:00', '15:00', N'Toàn thân', NULL, 'ACTIVE', '2026-06-21 06:42:00');
-- PT id=13 (pt12) voi member id=33, 86
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (13, 33, '2026-07-22', '19:00', '20:15', N'Toàn thân', NULL, 'ACTIVE', '2026-06-06 08:42:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (13, 33, '2026-07-23', '08:00', '09:00', N'Tập chân', NULL, 'ACTIVE', '2026-06-04 13:28:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (13, 33, '2026-07-22', '08:00', '09:15', N'Bụng & Core', NULL, 'ACTIVE', '2026-07-08 19:08:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (13, 86, '2026-07-23', '09:30', '10:30', N'Phục hồi nhẹ', NULL, 'ACTIVE', '2026-07-09 14:05:00');
-- PT id=14 (pt13) voi member id=83, 74, 56
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 83, '2026-07-25', '07:00', '08:00', N'Phục hồi nhẹ', NULL, 'ACTIVE', '2026-07-03 10:42:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 83, '2026-07-23', '09:00', '10:15', N'Toàn thân', NULL, 'ACTIVE', '2026-07-07 12:10:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 83, '2026-07-22', '18:00', '19:15', N'Toàn thân', NULL, 'ACTIVE', '2026-07-03 13:43:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 74, '2026-07-25', '09:00', '10:00', N'Phục hồi nhẹ', NULL, 'ACTIVE', '2026-07-08 06:14:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 74, '2026-07-22', '09:00', '10:15', N'Cardio/Thể lực', NULL, 'ACTIVE', '2026-07-09 08:03:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (14, 56, '2026-07-26', '18:00', '19:00', N'Toàn thân', NULL, 'ACTIVE', '2026-06-25 16:57:00');
-- PT id=15 (pt14) voi member id=71, 96
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (15, 71, '2026-07-24', '09:00', '10:15', N'Toàn thân', NULL, 'ACTIVE', '2026-07-05 09:57:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (15, 96, '2026-07-23', '18:00', '19:15', N'Phục hồi nhẹ', NULL, 'ACTIVE', '2026-07-09 13:56:00');
-- PT id=16 (pt15) voi member id=64, 97
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (16, 64, '2026-07-24', '19:00', '20:15', N'Toàn thân', NULL, 'ACTIVE', '2026-07-09 18:19:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (16, 64, '2026-07-24', '09:00', '10:00', N'Tập chân', NULL, 'ACTIVE', '2026-07-09 19:26:00');
INSERT INTO pt_schedules (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id, status, created_at) VALUES (16, 97, '2026-07-25', '18:00', '19:00', N'Tập chân', NULL, 'ACTIVE', '2026-06-08 14:16:00');

-- 16. Insert Notifications
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (56, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-07-01 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (43, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-05-27 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (42, 1, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 0, '2026-05-13 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (84, 1, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-05-21 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (35, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-06-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (25, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-07-02 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (40, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-05-24 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (20, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-06-20 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (73, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-05-11 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (12, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-07-03 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (33, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-05-31 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (95, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-06-25 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (93, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-06-17 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (27, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 1, '2026-06-07 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (94, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-06-28 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (66, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-27 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (45, 1, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-07-09 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (51, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-06-27 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (14, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-06-11 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (95, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 1, '2026-06-10 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (48, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-06-01 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (12, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 1, '2026-05-31 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (13, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-05-29 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (8, 1, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 1, '2026-06-10 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (22, 1, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-05-23 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (22, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-05-30 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (23, 1, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-05-14 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (55, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-21 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (95, 1, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-06-30 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (76, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-07 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (31, 1, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-05-13 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (7, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-06-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (74, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-05-13 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (9, 1, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (6, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-05-25 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (11, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-06-11 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (98, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-06-15 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (11, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-06-15 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (87, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-05-12 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (59, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-06-30 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (37, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-07-06 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (70, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 0, '2026-06-11 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (53, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-07-01 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (42, 1, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-07-09 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (75, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 1, '2026-05-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (17, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-02 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (2, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-07-04 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (93, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-06-04 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (52, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-05-18 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (43, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-05-29 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (61, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 0, '2026-05-28 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (90, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-07-04 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (23, 1, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-06-29 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (4, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-05-26 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (93, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-06-10 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (13, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 0, '2026-07-04 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (16, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-06-15 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (25, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 0, '2026-06-21 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (77, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-06-22 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (59, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-05-26 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (43, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-05-24 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (97, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-05-26 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (94, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-05-16 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (57, NULL, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-05-31 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (62, 1, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-06-06 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (11, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-06-28 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (95, 1, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 1, '2026-06-27 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (17, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-05-20 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (32, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 1, '2026-05-31 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (70, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-06-04 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (98, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-05-26 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (53, NULL, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 1, '2026-06-20 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (81, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 0, '2026-07-02 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (5, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-06-03 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (95, 1, N'Đánh giá buổi tập', N'Vui lòng để lại đánh giá cho PT sau buổi tập vừa qua.', 1, '2026-06-18 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (60, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-05-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (21, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-05-30 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (75, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-05-18 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (69, NULL, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 0, '2026-06-20 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (89, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 1, '2026-06-05 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (19, 1, N'Bài viết blog mới', N'Một bài viết mới về dinh dưỡng vừa được đăng tải, mời bạn tham khảo.', 0, '2026-05-19 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (62, 1, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-07-08 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (60, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-06-29 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (90, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-07-02 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (14, NULL, N'Nhắc nhở tập luyện', N'Đã lâu bạn chưa đến phòng tập, hãy quay lại để duy trì phong độ nhé!', 0, '2026-05-25 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (21, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 1, '2026-05-14 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (47, NULL, N'Khuyến mãi mới', N'GymPro vừa ra mắt chương trình khuyến mãi mới, xem chi tiết ngay trong ứng dụng.', 0, '2026-06-20 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (44, NULL, N'Lịch hẹn với PT', N'Bạn có lịch hẹn tập với PT vào ngày mai, vui lòng đến đúng giờ.', 1, '2026-06-16 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (21, 1, N'Gói tập sắp hết hạn', N'Gói tập của bạn sẽ hết hạn trong 7 ngày tới, vui lòng gia hạn để tiếp tục sử dụng dịch vụ.', 1, '2026-07-08 00:00:00');
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES (78, NULL, N'Xác nhận thanh toán thành công', N'Giao dịch của bạn đã được xác nhận thành công. Cảm ơn bạn đã tin tưởng GymPro.', 1, '2026-05-11 00:00:00');

-- ============================================================
-- DU LIEU NGHIEP VU BO SUNG: CHINH SACH, SALE, DANH GIA, BAO LUU
-- ============================================================

-- Chuan hoa trang thai lich cu theo mo hinh SCHEDULED/COMPLETED/CANCELLED/NO_SHOW.
UPDATE pt_schedules SET status = 'SCHEDULED' WHERE status = 'ACTIVE';

INSERT INTO policy_versions (policy_type, version_number, title, content, is_active, effective_at)
VALUES
('MEMBERSHIP_TERMS', 1, N'Điều khoản thành viên GymPro',
 N'1. Gói tập chỉ được kích hoạt sau khi GymPro xác nhận thanh toán. 2. Hội viên chịu trách nhiệm cung cấp thông tin chính xác và tuân thủ nội quy an toàn. 3. Bảo lưu áp dụng theo loại gói và thời lượng đã mua; số ngày bảo lưu hợp lệ được cộng lại 100%. 4. Gói tập không tự ý hủy để hoàn tiền; hội viên có thể dùng chức năng chuyển nhượng theo chính sách hiện hành. 5. GymPro lưu phiên bản điều khoản, thời điểm và thông tin kỹ thuật của lần đồng ý để đối chiếu giao dịch.', 1, '2026-08-01'),
('TRANSFER_POLICY', 1, N'Chính sách chuyển nhượng gói tập',
 N'1. Mỗi tài khoản được chuyển nhượng thành công tối đa 3 lần. 2. Mỗi lần phải chuyển toàn bộ thời gian còn lại và chỉ được có một yêu cầu đang chờ. 3. Số ngày khấu trừ bằng 10% số ngày còn lại, tối thiểu 3 ngày và tối đa 30 ngày. 4. Người nhận có 60 ngày để xác nhận. Trong thời gian chờ, gói người gửi vẫn hoạt động và tiếp tục giảm ngày. 5. Cùng loại gói sẽ được cộng ngày; khác loại gói sẽ ghi đè và làm mất toàn bộ gói hiện tại của người nhận sau khi xác nhận OTP.', 1, '2026-08-01'),
('HOLD_POLICY', 1, N'Chính sách bảo lưu GymPro',
 N'Bảo lưu chỉ áp dụng cho gói Premium và VIP theo thời lượng mua. Khi kết thúc bảo lưu, hội viên được giữ nguyên 100% số ngày chưa sử dụng. Hệ thống tự kích hoạt lại khi hết thời hạn đã đăng ký bảo lưu.', 1, '2026-08-01'),
('PRIVACY_POLICY', 1, N'Chính sách quyền riêng tư GymPro',
 N'GymPro chỉ xử lý dữ liệu tài khoản và dữ liệu thể chất nhằm cung cấp dịch vụ. Dữ liệu thể chất chỉ được gửi đến AI khi hội viên chủ động đồng ý trong từng cuộc trò chuyện.', 1, '2026-08-01');

-- Premium (package 2).
INSERT INTO package_hold_policies (package_id, min_duration_days, max_duration_days, max_hold_times, max_days_per_hold, max_total_hold_days) VALUES
(2, 30, 89, 1, 7, 7),
(2, 90, 179, 1, 14, 14),
(2, 180, 364, 2, 30, 60),
(2, 365, NULL, 3, 60, 180);
-- VIP (package 3).
INSERT INTO package_hold_policies (package_id, min_duration_days, max_duration_days, max_hold_times, max_days_per_hold, max_total_hold_days) VALUES
(3, 30, 89, 1, 14, 14),
(3, 90, 179, 1, 30, 30),
(3, 180, 364, 2, 30, 60),
(3, 365, NULL, 3, 60, 180);

-- Gan snapshot chinh sach bao luu cho cac membership seed hien co.
UPDATE m SET
    hold_max_times = ISNULL(p.max_hold_times, 0),
    hold_max_days_per_time = ISNULL(p.max_days_per_hold, 0),
    hold_max_total_days = ISNULL(p.max_total_hold_days, 0)
FROM memberships m
OUTER APPLY (
    SELECT TOP 1 hp.max_hold_times, hp.max_days_per_hold, hp.max_total_hold_days
    FROM package_hold_policies hp
    WHERE hp.package_id = m.package_id
      AND hp.min_duration_days <= m.duration_days
      AND (hp.max_duration_days IS NULL OR hp.max_duration_days >= m.duration_days)
      AND hp.is_active = 1
    ORDER BY hp.min_duration_days DESC
) p;

UPDATE memberships SET
    hold_count = CASE WHEN hold_count > hold_max_times THEN hold_max_times ELSE hold_count END,
    total_hold_days = CASE WHEN total_hold_days > hold_max_total_days THEN hold_max_total_days ELSE total_hold_days END;
UPDATE memberships SET
    hold_until = DATEADD(DAY, hold_max_days_per_time, paused_at),
    end_date = DATEADD(DAY, hold_max_days_per_time, end_date)
WHERE status = 'PAUSED' AND paused_at IS NOT NULL AND hold_max_days_per_time > 0;

-- Tai khoan SALE demo. Mat khau: 123456.
INSERT INTO users (role_id, email, password, full_name, phone, avatar, status, provider)
VALUES ((SELECT id FROM roles WHERE name = 'SALE'), 'sale1@gympro.com',
'$2b$10$X0n/GVUIslKxD1dTSXoD/.7MGe9Tz9oPfNx07hTmQ2PuE4qWU0ROi',
N'Nguyễn Minh Anh', '0908123456', 'https://i.pravatar.cc/300?img=47', 1, 'LOCAL');
DECLARE @sale_user_id INT = SCOPE_IDENTITY();
INSERT INTO sales_profiles (user_id, level_number, successful_customers, is_online, max_concurrent_chats)
VALUES (@sale_user_id, 2, 12, 1, 3);
DECLARE @sale_profile_id INT = SCOPE_IDENTITY();
INSERT INTO sales_referral_codes (sales_profile_id, code, description, discount_percent, one_time_per_member, is_active)
VALUES (@sale_profile_id, 'MINHANH10', N'Mã ưu đãi cấp 2 của nhân viên Nguyễn Minh Anh', 10, 1, 1);

-- Danh gia dich vu phuc vu trang quan ly va khu vuc cam nhan tren trang chu.
-- Tao co dinh 16 danh gia de Admin co 3 trang du lieu (7 ban ghi/trang).
;WITH confirmed_reviews AS (
    SELECT TOP (16) t.id transaction_id, m.user_id member_id,
           ROW_NUMBER() OVER (ORDER BY t.created_at DESC, t.id DESC) rn
    FROM transactions t
    JOIN memberships m ON m.id = t.membership_id
    WHERE t.status = 'CONFIRMED'
    ORDER BY t.created_at DESC, t.id DESC
)
INSERT INTO service_reviews (member_id, transaction_id, rating_star, comment, display_name, is_featured, created_at, updated_at)
SELECT member_id, transaction_id,
       CASE WHEN rn IN (4, 8, 12, 15) THEN 4 WHEN rn = 16 THEN 3 ELSE 5 END,
       CASE rn
         WHEN 1 THEN N'Lịch tập rõ ràng, PT theo sát và điều chỉnh bài tập theo thể trạng. Tôi cảm nhận được tiến bộ sau từng tuần.'
         WHEN 2 THEN N'Quy trình đăng ký minh bạch, theo dõi gói tập và thực đơn rất thuận tiện. Nhân viên hỗ trợ nhanh và lịch sự.'
         WHEN 3 THEN N'Tôi thích cách GymPro gom lịch tập, hồ sơ thể chất và tư vấn vào một nơi. Giao diện dễ dùng và thông tin rất trực quan.'
         WHEN 4 THEN N'PT hướng dẫn kỹ tư thế, chủ động giảm mức tạ khi tôi đau vai và luôn ghi chú kết quả sau buổi tập.'
         WHEN 5 THEN N'Khâu đăng ký gói nhanh, giá và thời hạn được trình bày rõ ràng. Email xác nhận giao dịch cũng đầy đủ thông tin.'
         WHEN 6 THEN N'Thực đơn theo ngày tập khá thực tế, nguyên liệu dễ tìm và lượng calories phù hợp với mục tiêu giảm cân của tôi.'
         WHEN 7 THEN N'Tôi có thể xem lịch, hồ sơ thể chất và trao đổi với AI ngay trên điện thoại nên việc theo dõi tiến độ thuận tiện hơn.'
         WHEN 8 THEN N'Nhân viên tư vấn nhiệt tình và không gây áp lực mua gói. Tôi mong phòng gym bổ sung thêm khung giờ cuối tuần.'
         WHEN 9 THEN N'Chức năng bảo lưu trình bày rõ số ngày còn lại, giúp tôi yên tâm khi phải nghỉ tập do đi công tác.'
         WHEN 10 THEN N'PT sắp xếp lịch linh hoạt, bài tập có độ khó tăng dần và luôn hỏi tình trạng sức khỏe trước khi bắt đầu.'
         WHEN 11 THEN N'Trang quản lý gói giúp tôi kiểm tra ngày hết hạn, số lần bảo lưu và lịch sử giao dịch mà không phải hỏi lễ tân.'
         WHEN 12 THEN N'Trải nghiệm nhìn chung tốt, thao tác thanh toán dễ hiểu. Một vài thời điểm tải ảnh bài viết còn hơi chậm.'
         WHEN 13 THEN N'AI tư vấn dựa trên hồ sơ thể chất khá sát mục tiêu của tôi và vẫn nhắc nên trao đổi với PT khi có chấn thương.'
         WHEN 14 THEN N'Tôi đánh giá cao việc lịch của từng học viên có màu riêng, nhìn một lượt là nhận ra ngay buổi tập của mình.'
         WHEN 15 THEN N'Các bài tập có video minh họa dễ theo dõi. Tôi muốn có thêm nhiều bài giãn cơ dành cho người ngồi văn phòng.'
         ELSE N'Sau gần hai tháng tập, sức bền của tôi cải thiện rõ rệt. Quy trình hỗ trợ và thông báo lịch tập đều chuyên nghiệp.'
       END,
       CASE WHEN rn IN (8, 14) THEN 0 ELSE 1 END,
       CASE WHEN rn IN (1, 3, 6, 10, 14) THEN 1 ELSE 0 END,
       DATEADD(HOUR, -13 * rn, CAST('2026-09-05 20:00:00' AS DATETIME2)),
       DATEADD(HOUR, -13 * rn, CAST('2026-09-05 20:00:00' AS DATETIME2))
FROM confirmed_reviews;

IF (SELECT COUNT(*) FROM service_reviews) < 16
    THROW 51001, N'Seed data cần tối thiểu 16 đánh giá dịch vụ để phục vụ demo.', 1;

-- Du lieu mau cho thong ke bai tap cua PT1 trong tuan dau thang 8/2026.
UPDATE pt_schedules SET status = 'COMPLETED', actual_note = N'Học viên hoàn thành đúng kỹ thuật và đủ khối lượng.', completed_at = DATEADD(HOUR, 2, CAST(schedule_date AS DATETIME2))
WHERE pt_id = 2 AND member_id IN (59, 21, 45) AND schedule_date BETWEEN '2026-08-03' AND '2026-08-04';
UPDATE pt_schedules SET status = 'NO_SHOW', actual_note = N'Học viên báo vắng sát giờ.'
WHERE pt_id = 2 AND member_id = 84 AND schedule_date = '2026-08-05';
INSERT INTO schedule_exercises (schedule_id, exercise_id, set_count, rep_count, weight_kg, duration_minutes, note)
SELECT s.id, (SELECT TOP 1 id FROM exercises ORDER BY id), 4, 10, 30, NULL, N'Hoàn thành đủ số hiệp'
FROM pt_schedules s
WHERE s.pt_id = 2 AND s.status = 'COMPLETED' AND s.schedule_date BETWEEN '2026-08-03' AND '2026-08-04';

-- ============================================================
-- KICH BAN DEMO BAO VE: 03/09/2026 - 08/09/2026
-- Moc du lieu co dinh giup dashboard on dinh khi demo tren may khac.
-- ============================================================
DECLARE @demo_date DATE = '2026-09-05';
DECLARE @demo_admin_id INT = (SELECT id FROM users WHERE email = 'admin@gympro.com');
DECLARE @demo_pt1_id INT = (SELECT id FROM users WHERE email = 'pt1@gympro.com');
DECLARE @demo_member1_id INT = (SELECT id FROM users WHERE email = 'member1@gympro.com');
DECLARE @demo_member2_id INT = (SELECT id FROM users WHERE email = 'member2@gympro.com');
DECLARE @demo_member3_id INT = (SELECT id FROM users WHERE email = 'member3@gympro.com');
DECLARE @demo_member4_id INT = (SELECT id FROM users WHERE email = 'member4@gympro.com');
DECLARE @demo_member5_id INT = (SELECT id FROM users WHERE email = 'member5@gympro.com');
DECLARE @demo_member10_id INT = (SELECT id FROM users WHERE email = 'member10@gympro.com');
DECLARE @demo_member11_id INT = (SELECT id FROM users WHERE email = 'member11@gympro.com');
DECLARE @demo_member29_id INT = (SELECT id FROM users WHERE email = 'member29@gympro.com');
DECLARE @demo_member43_id INT = (SELECT id FROM users WHERE email = 'member43@gympro.com');
DECLARE @demo_member68_id INT = (SELECT id FROM users WHERE email = 'member68@gympro.com');
DECLARE @demo_basic_id INT = (SELECT id FROM packages WHERE name = 'BASIC');
DECLARE @demo_premium_id INT = (SELECT id FROM packages WHERE name = 'PREMIUM');
DECLARE @demo_vip_id INT = (SELECT id FROM packages WHERE name = 'VIP');
DECLARE @demo_terms_id INT = (SELECT id FROM policy_versions WHERE policy_type = 'MEMBERSHIP_TERMS' AND is_active = 1);
DECLARE @demo_transfer_policy_id INT = (SELECT id FROM policy_versions WHERE policy_type = 'TRANSFER_POLICY' AND is_active = 1);

-- Don dep cac trang thai cu da het han de dashboard khong con hien goi ACTIVE/PENDING sai.
UPDATE transactions
SET status = 'CANCELLED', version = version + 1
WHERE status = 'PENDING' AND created_at < '2026-08-01';

UPDATE memberships
SET status = 'CANCELLED', version = version + 1
WHERE status = 'PENDING' AND created_at < '2026-08-01';

UPDATE memberships
SET status = 'EXPIRED', version = version + 1
WHERE status = 'ACTIVE' AND end_date < @demo_date;

-- Khuyen mai phuc vu kich ban thanh toan trong dot bao ve.
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active)
VALUES
('BAOVE15', 15, NULL, '2026-09-01', '2026-09-15', 100, 18, 1),
('VIPSEP20', 20, @demo_vip_id, '2026-09-01', '2026-09-30', 50, 7, 1);
DECLARE @demo_promotion_id INT = (SELECT id FROM promotions WHERE code = 'BAOVE15');

-- Ho so the chat co du thong tin de demo AI, thuc don va quyen xem cua PT1.
UPDATE member_profiles SET height_cm = 168, weight_kg = 72.5, date_of_birth = '2001-04-12', biological_sex = 'FEMALE',
    chest_cm = 92, waist_cm = 78, hip_cm = 99, body_fat_percentage = 29.4, body_fat_source = 'ESTIMATED',
    activity_level = 'LIGHT', fitness_goal = 'WEIGHT_LOSS', target_weight_kg = 65,
    training_experience = N'Đã tập không liên tục khoảng 6 tháng, ưu tiên bài máy và cardio nhẹ.',
    injury_history = N'Từng đau nhẹ đầu gối phải khi chạy nhanh.', medical_conditions = N'Không có bệnh nền; hạn chế động tác bật nhảy cường độ cao.'
WHERE user_id = @demo_member1_id;

UPDATE member_profiles SET height_cm = 175, weight_kg = 78.2, date_of_birth = '1999-08-21', biological_sex = 'MALE',
    chest_cm = 101, waist_cm = 86, hip_cm = 98, body_fat_percentage = 20.1, body_fat_source = 'ESTIMATED',
    activity_level = 'MODERATE', fitness_goal = 'MUSCLE_GAIN', target_weight_kg = 82,
    training_experience = N'Đã tập 1 năm, nắm được kỹ thuật các bài compound cơ bản.',
    injury_history = N'Không có chấn thương đáng kể.', medical_conditions = N'Không có hạn chế vận động.'
WHERE user_id = @demo_member5_id;

UPDATE member_profiles SET height_cm = 162, weight_kg = 61.4, date_of_birth = '2002-11-03', biological_sex = 'FEMALE',
    chest_cm = 88, waist_cm = 72, hip_cm = 96, body_fat_percentage = 27.8, body_fat_source = 'MANUAL',
    activity_level = 'LIGHT', fitness_goal = 'HEALTH_IMPROVEMENT', target_weight_kg = 58,
    training_experience = N'Mới tập dưới 3 tháng, cần PT hướng dẫn kỹ thuật và nhịp thở.',
    injury_history = N'Căng cơ lưng dưới vào năm 2025, hiện đã hồi phục.', medical_conditions = N'Tránh nâng tạ tối đa và cần khởi động lưng kỹ.'
WHERE user_id = @demo_member29_id;

UPDATE member_profiles SET height_cm = 180, weight_kg = 84.6, date_of_birth = '1998-02-16', biological_sex = 'MALE',
    chest_cm = 108, waist_cm = 88, hip_cm = 102, body_fat_percentage = 18.6, body_fat_source = 'ESTIMATED',
    activity_level = 'HIGH', fitness_goal = 'MUSCLE_GAIN', target_weight_kg = 88,
    training_experience = N'Đã tập hơn 2 năm, đang theo chương trình tăng cơ có kiểm soát.',
    injury_history = N'Từng viêm gân vai trái; hiện tập bình thường với mức tạ vừa.', medical_conditions = N'Hạn chế đẩy vai quá đầu khi có dấu hiệu đau.'
WHERE user_id = @demo_member43_id;

UPDATE member_profiles SET height_cm = 171, weight_kg = 69.8, date_of_birth = '2000-06-25', biological_sex = 'MALE',
    chest_cm = 96, waist_cm = 80, hip_cm = 94, body_fat_percentage = 17.9, body_fat_source = 'ESTIMATED',
    activity_level = 'MODERATE', fitness_goal = 'MAINTENANCE', target_weight_kg = 70,
    training_experience = N'Đã tập đều 8 tháng, mục tiêu duy trì sức mạnh và vóc dáng.',
    injury_history = N'Không có chấn thương.', medical_conditions = N'Không có hạn chế vận động.'
WHERE user_id = @demo_member68_id;

-- Member2: goi dang hoat dong de demo tao yeu cau chuyen nhuong cho member1.
DECLARE @demo_transfer_source_id INT = (
    SELECT TOP 1 id FROM memberships
    WHERE user_id = @demo_member2_id
    ORDER BY id DESC
);
UPDATE memberships SET package_id = @demo_premium_id, pt_id = NULL, start_date = '2026-08-15', end_date = '2026-12-13',
    status = 'ACTIVE', pause_reason = NULL, duration_days = 120, daily_price = 50000,
    hold_count = 0, paused_at = NULL, total_hold_days = 0, hold_until = NULL,
    hold_max_times = 1, hold_max_days_per_time = 14, hold_max_total_days = 14, version = version + 1
WHERE id = @demo_transfer_source_id;

INSERT INTO policy_acceptances
    (user_id, policy_version_id, transaction_id, acceptance_context, accepted_at, accepted_ip, accepted_user_agent)
VALUES
    (@demo_member2_id, @demo_transfer_policy_id, NULL, 'TRANSFER_SEND', '2026-09-02 08:59:00',
     '127.0.0.1', N'Chrome Demo - GymPro Defense');

-- Member3: goi dang bao luu, con day du gioi han de demo chinh sach.
INSERT INTO memberships
    (user_id, package_id, pt_id, start_date, end_date, status, pause_reason, duration_days, daily_price,
     hold_count, paused_at, total_hold_days, hold_until, hold_max_times, hold_max_days_per_time, hold_max_total_days, created_at)
VALUES
    (@demo_member3_id, @demo_premium_id, @demo_pt1_id, '2026-08-01', '2026-12-29', 'PAUSED',
     N'Đi công tác ngắn hạn từ 29/08 đến 12/09', 120, 50000, 1, '2026-08-29', 14, '2026-09-12', 1, 14, 14,
     '2026-08-01 08:15:00');

-- Member4: giao dich mua VIP dang cho Admin duyet trong buoi demo.
INSERT INTO memberships
    (user_id, package_id, pt_id, start_date, end_date, status, duration_days, daily_price,
     hold_max_times, hold_max_days_per_time, hold_max_total_days, created_at)
VALUES
    (@demo_member4_id, @demo_vip_id, @demo_pt1_id, '2026-09-05', '2026-12-03', 'PENDING', 90, 83000,
     1, 30, 30, '2026-09-04 20:05:00');
DECLARE @demo_pending_membership_id INT = SCOPE_IDENTITY();

INSERT INTO transactions
    (membership_id, promotion_id, requested_duration_days, requested_package_id, requested_pt_id,
     operation_applied, amount, original_amount, payment_method, status, type, accepted_terms,
     terms_accepted_at, terms_version, accepted_ip, accepted_user_agent, customer_discount_percent, created_at)
VALUES
    (@demo_pending_membership_id, @demo_promotion_id, 90, @demo_vip_id, @demo_pt1_id,
     0, 6349500, 7470000, 'BANK', 'PENDING', 'NEW', 1,
     '2026-09-04 20:04:00', 1, '127.0.0.1', N'Chrome Demo - GymPro Defense', 15, '2026-09-04 20:05:00');
DECLARE @demo_pending_transaction_id INT = SCOPE_IDENTITY();

INSERT INTO policy_acceptances
    (user_id, policy_version_id, transaction_id, acceptance_context, accepted_at, accepted_ip, accepted_user_agent)
VALUES
    (@demo_member4_id, @demo_terms_id, @demo_pending_transaction_id, 'PURCHASE', '2026-09-04 20:04:00',
     '127.0.0.1', N'Chrome Demo - GymPro Defense');

-- Lich su giao dich moi: gia han thanh cong, nang cap thanh cong va mot giao dich bi huy.
DECLARE @demo_member5_membership_id INT = (
    SELECT TOP 1 id FROM memberships WHERE user_id = @demo_member5_id AND status = 'ACTIVE' ORDER BY id DESC
);
DECLARE @demo_member29_membership_id INT = (
    SELECT TOP 1 id FROM memberships WHERE user_id = @demo_member29_id AND status = 'ACTIVE' ORDER BY id DESC
);
DECLARE @demo_member68_membership_id INT = (
    SELECT TOP 1 id FROM memberships WHERE user_id = @demo_member68_id AND status = 'ACTIVE' ORDER BY id DESC
);

INSERT INTO transactions
    (membership_id, requested_duration_days, requested_package_id, requested_pt_id, operation_applied,
     amount, original_amount, payment_method, status, type, confirmed_by, accepted_terms, terms_accepted_at,
     terms_version, accepted_ip, accepted_user_agent, customer_discount_percent, created_at)
VALUES
    (@demo_member5_membership_id, 30, @demo_premium_id, @demo_pt1_id, 1,
     1350000, 1500000, 'BANK', 'CONFIRMED', 'RENEW', @demo_admin_id, 1, '2026-08-20 09:20:00',
     1, '127.0.0.1', N'Chrome 127 / Windows 11', 10, '2026-08-20 09:21:00');
DECLARE @demo_renew_transaction_id INT = SCOPE_IDENTITY();

INSERT INTO transactions
    (membership_id, requested_duration_days, requested_package_id, requested_pt_id, operation_applied,
     amount, original_amount, payment_method, status, type, confirmed_by, accepted_terms, terms_accepted_at,
     terms_version, accepted_ip, accepted_user_agent, customer_discount_percent, created_at)
VALUES
    (@demo_member29_membership_id, 90, @demo_vip_id, @demo_pt1_id, 1,
     6723000, 7470000, 'ONLINE', 'CONFIRMED', 'UPGRADE', @demo_admin_id, 1, '2026-08-25 14:10:00',
     1, '127.0.0.1', N'Chrome Mobile Demo', 10, '2026-08-25 14:12:00');
DECLARE @demo_upgrade_transaction_id INT = SCOPE_IDENTITY();
UPDATE memberships SET package_id = @demo_vip_id, daily_price = 83000, version = version + 1
WHERE id = @demo_member29_membership_id;

INSERT INTO transactions
    (membership_id, requested_duration_days, requested_package_id, requested_pt_id, operation_applied,
     amount, original_amount, payment_method, status, type, accepted_terms, terms_accepted_at,
     terms_version, accepted_ip, accepted_user_agent, customer_discount_percent, created_at)
VALUES
    (@demo_member68_membership_id, 30, @demo_premium_id, @demo_pt1_id, 0,
     1275000, 1500000, 'BANK', 'CANCELLED', 'RENEW', 1, '2026-09-02 19:40:00',
     1, '127.0.0.1', N'Chrome Demo - GymPro Defense', 15, '2026-09-02 19:42:00');

INSERT INTO policy_acceptances
    (user_id, policy_version_id, transaction_id, acceptance_context, accepted_at, accepted_ip, accepted_user_agent)
VALUES
    (@demo_member5_id, @demo_terms_id, @demo_renew_transaction_id, 'PURCHASE', '2026-08-20 09:20:00', '127.0.0.1', N'Chrome 127 / Windows 11'),
    (@demo_member29_id, @demo_terms_id, @demo_upgrade_transaction_id, 'PURCHASE', '2026-08-25 14:10:00', '127.0.0.1', N'Chrome Mobile Demo');

-- Mot ca chuyen nhuong da hoan tat de xem lich su nghiep vu.
INSERT INTO memberships
    (user_id, package_id, pt_id, start_date, end_date, status, duration_days, daily_price, created_at)
VALUES
    (@demo_member10_id, @demo_basic_id, NULL, '2026-06-01', '2026-10-31', 'TRANSFERRED', 150, 16000, '2026-06-01 08:00:00');
DECLARE @demo_accepted_source_id INT = SCOPE_IDENTITY();

INSERT INTO memberships
    (user_id, package_id, pt_id, start_date, end_date, status, duration_days, daily_price, created_at)
VALUES
    (@demo_member11_id, @demo_basic_id, NULL, '2026-08-25', '2026-10-20', 'ACTIVE', 57, 16000, '2026-08-25 10:15:00');

INSERT INTO membership_transfers
    (source_membership_id, sender_id, recipient_id, status, remaining_days_at_request, remaining_days_at_accept,
     deducted_days, transferred_days, expires_at, created_at, accepted_at)
VALUES
    (@demo_accepted_source_id, @demo_member10_id, @demo_member11_id, 'ACCEPTED', 70, 64,
     7, 57, '2026-10-18 09:00:00', '2026-08-19 09:00:00', '2026-08-25 10:15:00');

INSERT INTO policy_acceptances
    (user_id, policy_version_id, transaction_id, acceptance_context, accepted_at, accepted_ip, accepted_user_agent)
VALUES
    (@demo_member10_id, @demo_transfer_policy_id, NULL, 'TRANSFER_SEND', '2026-08-19 08:59:00', '127.0.0.1', N'Chrome 127 / Windows 11'),
    (@demo_member11_id, @demo_transfer_policy_id, NULL, 'TRANSFER_RECEIVE', '2026-08-25 10:14:00', '127.0.0.1', N'Edge 127 / Windows 11');

-- Sale cap 2: 12 khach hang thanh cong, co du hoa hong o ba trang thai.
INSERT INTO sales_referral_codes
    (sales_profile_id, code, description, discount_percent, one_time_per_member, is_active, expires_at)
VALUES
    (@sale_profile_id, 'MINHANHVIP10', N'Ưu đãi 10% dành cho khách hàng VIP của Minh Anh', 10, 1, 1, '2026-12-31 23:59:59'),
    (@sale_profile_id, 'MINHANHPLUS10', N'Mã ưu đãi linh hoạt của nhân viên Nguyễn Minh Anh', 10, 0, 1, NULL);
DECLARE @demo_sale_code_id INT = (SELECT id FROM sales_referral_codes WHERE code = 'MINHANH10');

DECLARE @demo_sale_transactions TABLE (
    rn INT IDENTITY(1,1), transaction_id INT PRIMARY KEY, member_id INT,
    amount DECIMAL(12,0), confirmed_at DATETIME2
);
;WITH one_transaction_per_member AS (
    SELECT t.id transaction_id, m.user_id member_id, t.amount, t.created_at,
           ROW_NUMBER() OVER (PARTITION BY m.user_id ORDER BY t.created_at DESC, t.id DESC) member_rn
    FROM transactions t
    JOIN memberships m ON m.id = t.membership_id
    WHERE t.status = 'CONFIRMED'
      AND t.promotion_id IS NULL
      AND NOT EXISTS (SELECT 1 FROM sales_code_redemptions r WHERE r.transaction_id = t.id)
)
INSERT INTO @demo_sale_transactions (transaction_id, member_id, amount)
SELECT TOP (12) transaction_id, member_id, amount
FROM one_transaction_per_member
WHERE member_rn = 1
ORDER BY created_at DESC, transaction_id DESC;

-- Moc thoi gian duoc chia theo 3 nhom de trang thai hoa hong nhat quan tai ngay demo 05/09/2026:
-- PAID da qua han cho va da thanh toan; PAYABLE da qua 7 ngay; PENDING van con trong 7 ngay cho.
UPDATE @demo_sale_transactions
SET confirmed_at = CASE
    WHEN rn <= 4 THEN DATEADD(DAY, rn - 20, CAST('2026-09-05 10:30:00' AS DATETIME2))
    WHEN rn <= 8 THEN DATEADD(DAY, rn - 15, CAST('2026-09-05 10:30:00' AS DATETIME2))
    ELSE DATEADD(DAY, rn - 12, CAST('2026-09-05 10:30:00' AS DATETIME2))
END;

UPDATE t SET sale_code_id = @demo_sale_code_id
FROM transactions t
JOIN @demo_sale_transactions d ON d.transaction_id = t.id;

INSERT INTO sales_code_redemptions
    (sale_code_id, member_id, transaction_id, status, created_at, confirmed_at)
SELECT @demo_sale_code_id, member_id, transaction_id, 'CONFIRMED',
       DATEADD(MINUTE, -30, confirmed_at), confirmed_at
FROM @demo_sale_transactions;

INSERT INTO commission_records
    (transaction_id, sales_profile_id, base_amount, commission_rate, commission_amount,
     status, payable_at, paid_at, created_at)
SELECT transaction_id, @sale_profile_id, amount, 4, ROUND(amount * 0.04, 0),
       CASE WHEN rn <= 4 THEN 'PAID' WHEN rn <= 8 THEN 'PAYABLE' ELSE 'PENDING' END,
       DATEADD(DAY, 7, confirmed_at),
       CASE WHEN rn <= 4 THEN DATEADD(DAY, 10, confirmed_at) END,
       confirmed_at
FROM @demo_sale_transactions;

IF EXISTS (
    SELECT 1 FROM commission_records
    WHERE status IN ('PENDING', 'PAYABLE', 'PAID') AND payable_at IS NULL
)
    THROW 50021, N'Seed hoa hồng không hợp lệ: thiếu thời điểm đủ điều kiện chi trả.', 1;

IF EXISTS (SELECT 1 FROM commission_records WHERE status = 'PAID' AND paid_at IS NULL)
    THROW 50022, N'Seed hoa hồng không hợp lệ: trạng thái PAID thiếu thời điểm thanh toán.', 1;

UPDATE sales_profiles SET level_number = 2, successful_customers = 12, is_online = 1
WHERE id = @sale_profile_id;

-- Lam moi dong thoi gian bai viet de trang chu/Admin co noi dung gan ngay bao ve,
-- van giu nguyen tieu de, noi dung, tac gia va anh minh hoa da bien soan.
;WITH recent_blogs AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id DESC) rn
    FROM blogs
    WHERE status = 'PUBLISHED'
)
UPDATE b
SET created_at = DATEADD(DAY, -r.rn, CAST('2026-09-05 07:30:00' AS DATETIME2))
FROM blogs b
JOIN recent_blogs r ON r.id = b.id
WHERE r.rn <= 8;

;WITH recent_drafts AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id DESC) rn
    FROM blogs
    WHERE status = 'DRAFT'
)
UPDATE b
SET created_at = DATEADD(DAY, -r.rn, CAST('2026-09-03 16:00:00' AS DATETIME2))
FROM blogs b
JOIN recent_drafts r ON r.id = b.id
WHERE r.rn <= 3;

-- Lich PT1 day du theo tuan/thang: hoan thanh, vang, huy, sap toi va lich lap.
DELETE FROM pt_schedules
WHERE pt_id = @demo_pt1_id AND schedule_date BETWEEN '2026-08-17' AND '2026-09-12';

INSERT INTO pt_schedules
    (pt_id, member_id, schedule_date, start_time, end_time, exercise_note, recurring_group_id,
     status, actual_note, completed_at, created_at)
VALUES
(@demo_pt1_id, @demo_member5_id,  '2026-08-17', '07:00', '08:30', N'Ngực - tay sau, tăng tải có kiểm soát', NULL, 'COMPLETED', N'Hoàn thành toàn bộ bài, Bench Press tăng thêm 2,5 kg.', '2026-08-17 08:32:00', '2026-08-14 09:00:00'),
(@demo_pt1_id, @demo_member29_id, '2026-08-18', '18:00', '19:00', N'Full body làm quen kỹ thuật', NULL, 'COMPLETED', N'Thực hiện tốt, cần giữ lưng trung lập khi Squat.', '2026-08-18 19:03:00', '2026-08-14 09:05:00'),
(@demo_pt1_id, @demo_member43_id, '2026-08-20', '16:00', '17:30', N'Lưng - tay trước', NULL, 'COMPLETED', N'Hoàn thành đủ khối lượng, vai trái không đau.', '2026-08-20 17:33:00', '2026-08-15 10:00:00'),
(@demo_pt1_id, @demo_member68_id, '2026-08-22', '09:00', '10:00', N'Cardio và core', NULL, 'NO_SHOW', N'Học viên báo vắng sát giờ do việc gia đình.', NULL, '2026-08-16 08:00:00'),
(@demo_pt1_id, @demo_member5_id,  '2026-08-24', '07:00', '08:30', N'Chân - mông', 'pt1-member5-sep-2026', 'COMPLETED', N'Hoàn thành tốt, giảm tải Leg Press ở hiệp cuối.', '2026-08-24 08:34:00', '2026-08-20 09:00:00'),
(@demo_pt1_id, @demo_member29_id, '2026-08-26', '18:00', '19:00', N'Cardio nhẹ và thân dưới', NULL, 'COMPLETED', N'Đầu gối ổn định, nhịp tim trong vùng mục tiêu.', '2026-08-26 19:02:00', '2026-08-21 10:00:00'),
(@demo_pt1_id, @demo_member43_id, '2026-08-28', '16:00', '17:30', N'Push day', NULL, 'CANCELLED', N'PT hủy và đã báo trước do lịch họp chuyên môn.', NULL, '2026-08-22 08:00:00'),
(@demo_pt1_id, @demo_member68_id, '2026-08-29', '09:00', '10:00', N'Full body duy trì', NULL, 'COMPLETED', N'Kỹ thuật ổn định, mức gắng sức RPE 7/10.', '2026-08-29 10:02:00', '2026-08-23 08:00:00'),
(@demo_pt1_id, @demo_member5_id,  '2026-08-31', '07:00', '08:30', N'Ngực - vai - tay sau', 'pt1-member5-sep-2026', 'COMPLETED', N'Bench Press đạt mục tiêu tuần, chưa cần tăng tải.', '2026-08-31 08:31:00', '2026-08-27 09:00:00'),
(@demo_pt1_id, @demo_member29_id, '2026-09-02', '18:00', '19:00', N'Thân dưới và phục hồi lưng', NULL, 'COMPLETED', N'Hoàn thành tốt, không xuất hiện đau lưng dưới.', '2026-09-02 19:03:00', '2026-08-29 10:00:00'),
(@demo_pt1_id, @demo_member43_id, '2026-09-03', '16:00', '17:30', N'Pull day tăng cơ', NULL, 'COMPLETED', N'Tăng 5 kg Deadlift, biên độ và nhịp thở tốt.', '2026-09-03 17:32:00', '2026-08-30 08:00:00'),
(@demo_pt1_id, @demo_member68_id, '2026-09-04', '09:00', '10:00', N'Cardio ngắt quãng và core', NULL, 'COMPLETED', N'Hoàn thành 8 vòng interval và 3 hiệp Plank.', '2026-09-04 10:02:00', '2026-08-30 08:05:00'),
(@demo_pt1_id, @demo_member5_id,  '2026-09-07', '07:00', '08:30', N'Chân - mông, theo dõi tiến độ tuần', 'pt1-member5-sep-2026', 'SCHEDULED', NULL, NULL, '2026-09-01 09:00:00'),
(@demo_pt1_id, @demo_member29_id, '2026-09-07', '18:00', '19:00', N'Full body cơ bản', NULL, 'SCHEDULED', NULL, NULL, '2026-09-01 09:05:00'),
(@demo_pt1_id, @demo_member43_id, '2026-09-08', '16:00', '17:30', N'Ngực - vai, hạn chế biên độ vai trái', NULL, 'SCHEDULED', NULL, NULL, '2026-09-01 09:10:00'),
(@demo_pt1_id, @demo_member68_id, '2026-09-09', '09:00', '10:00', N'Cardio và mobility', NULL, 'SCHEDULED', NULL, NULL, '2026-09-01 09:15:00'),
(@demo_pt1_id, @demo_member5_id,  '2026-09-11', '07:00', '08:30', N'Ngực - tay sau', 'pt1-member5-sep-2026', 'SCHEDULED', NULL, NULL, '2026-09-01 09:20:00');

-- Bai tap chi tiet cho cac buoi da hoan thanh, phuc vu thong ke tuan/thang.
INSERT INTO schedule_exercises
    (schedule_id, exercise_id, set_count, rep_count, weight_kg, duration_minutes, note)
SELECT s.id, e.id, v.set_count, v.rep_count, v.weight_kg, v.duration_minutes, v.note
FROM (VALUES
    (@demo_member5_id,  CAST('2026-08-17' AS DATE), CAST('07:00' AS TIME), N'Bench Press', 4, 8, 55.0, NULL, N'RPE 8/10'),
    (@demo_member5_id,  CAST('2026-08-17' AS DATE), CAST('07:00' AS TIME), N'Tricep Pushdown', 3, 12, 20.0, NULL, N'Kiểm soát pha hạ'),
    (@demo_member29_id, CAST('2026-08-18' AS DATE), CAST('18:00' AS TIME), N'Squat', 3, 10, 20.0, NULL, N'Tập trung kỹ thuật'),
    (@demo_member43_id, CAST('2026-08-20' AS DATE), CAST('16:00' AS TIME), N'Deadlift', 4, 6, 70.0, NULL, N'Không đau vai'),
    (@demo_member5_id,  CAST('2026-08-24' AS DATE), CAST('07:00' AS TIME), N'Leg Press', 4, 10, 90.0, NULL, N'Giảm tải hiệp cuối'),
    (@demo_member29_id, CAST('2026-08-26' AS DATE), CAST('18:00' AS TIME), N'Cycling', NULL, NULL, NULL, 25, N'Nhịp tim vùng 2'),
    (@demo_member68_id, CAST('2026-08-29' AS DATE), CAST('09:00' AS TIME), N'Kettlebell Swing', 4, 15, 16.0, NULL, N'RPE 7/10'),
    (@demo_member5_id,  CAST('2026-08-31' AS DATE), CAST('07:00' AS TIME), N'Bench Press', 4, 8, 57.5, NULL, N'Đạt mục tiêu tuần'),
    (@demo_member29_id, CAST('2026-09-02' AS DATE), CAST('18:00' AS TIME), N'Leg Curl', 3, 12, 25.0, NULL, N'Không đau lưng'),
    (@demo_member43_id, CAST('2026-09-03' AS DATE), CAST('16:00' AS TIME), N'Deadlift', 4, 6, 75.0, NULL, N'Tăng 5 kg'),
    (@demo_member43_id, CAST('2026-09-03' AS DATE), CAST('16:00' AS TIME), N'Barbell Row', 4, 10, 45.0, NULL, N'Giữ thân người ổn định'),
    (@demo_member68_id, CAST('2026-09-04' AS DATE), CAST('09:00' AS TIME), N'Running (Treadmill)', NULL, NULL, NULL, 24, N'8 vòng interval'),
    (@demo_member68_id, CAST('2026-09-04' AS DATE), CAST('09:00' AS TIME), N'Plank', 3, NULL, NULL, 2, N'Mỗi hiệp 2 phút')
) v(member_id, schedule_date, start_time, exercise_name, set_count, rep_count, weight_kg, duration_minutes, note)
JOIN pt_schedules s ON s.pt_id = @demo_pt1_id AND s.member_id = v.member_id
    AND s.schedule_date = v.schedule_date AND s.start_time = v.start_time AND s.status = 'COMPLETED'
JOIN exercises e ON e.name = v.exercise_name;

INSERT INTO pt_notes (pt_id, member_id, content, created_at) VALUES
(@demo_pt1_id, @demo_member5_id, N'Tiến độ tháng 8 tốt. Tuần bảo vệ giữ Bench Press 57,5 kg, ưu tiên đủ biên độ trước khi tăng tải.', '2026-09-01 08:00:00'),
(@demo_pt1_id, @demo_member29_id, N'Đầu gối và lưng dưới ổn định. Duy trì cardio vùng 2 và tăng tải Squat từng 2,5 kg.', '2026-09-02 19:10:00'),
(@demo_pt1_id, @demo_member43_id, N'Vai trái không đau trong hai tuần gần nhất. Vẫn tránh Overhead Press mức tạ cao.', '2026-09-03 17:40:00'),
(@demo_pt1_id, @demo_member68_id, N'Thể lực duy trì tốt; có thể tăng interval từ 8 lên 10 vòng vào tuần sau.', '2026-09-04 10:10:00');

-- Thuc don hoan chinh cho member VIP cua PT1.
DELETE FROM diets WHERE member_id = @demo_member43_id
  AND (day_type IN ('TRAINING_DAY', 'REST_DAY') OR diet_date = '2026-09-08');
INSERT INTO diets
    (pt_id, member_id, day_type, diet_date, title, breakfast, snack_morning, lunch,
     snack_afternoon, dinner, calories, protein_g, carbs_g, fat_g, note, created_at)
VALUES
(@demo_pt1_id, @demo_member43_id, 'TRAINING_DAY', NULL, N'Thực đơn tăng cơ ngày tập',
 N'80 g yến mạch, 250 ml sữa ít béo, 2 quả trứng và 1 quả chuối.',
 N'1 hũ sữa chua Hy Lạp và 15 g hạnh nhân.',
 N'180 g cơm, 180 g ức gà áp chảo, rau xanh và canh.',
 N'1 lát bánh mì nguyên cám, 1 quả chuối trước tập; whey sau tập nếu thiếu đạm.',
 N'200 g khoai lang, 180 g cá hồi và salad.', 2550, 175, 300, 72,
 N'Uống 2,5-3 lít nước; có thể đổi nguồn đạm tương đương, không bỏ bữa sau tập.', '2026-09-01 08:30:00'),
(@demo_pt1_id, @demo_member43_id, 'REST_DAY', NULL, N'Thực đơn phục hồi ngày nghỉ',
 N'3 quả trứng, 2 lát bánh mì nguyên cám và rau củ.',
 N'1 quả táo và 200 ml sữa không đường.',
 N'150 g cơm, 180 g thịt bò nạc và rau luộc.',
 N'1 hũ sữa chua Hy Lạp.',
 N'150 g khoai lang, 180 g cá trắng và salad.', 2250, 170, 230, 70,
 N'Giảm tinh bột so với ngày tập nhưng giữ đủ protein để phục hồi.', '2026-09-01 08:32:00'),
(@demo_pt1_id, @demo_member43_id, 'SPECIFIC_DATE', '2026-09-08', N'Thực đơn cho buổi tập chiều 08/09',
 N'Yến mạch, trứng và chuối.', N'Sữa chua Hy Lạp.',
 N'Cơm, ức gà và rau xanh.', N'Chuối trước tập; sữa ít béo sau tập.',
 N'Khoai lang, cá hồi và salad.', 2500, 175, 290, 70,
 N'Ăn bữa trưa trước buổi tập ít nhất 3 giờ; bữa phụ trước tập 45-60 phút.', '2026-09-04 11:00:00');

-- Danh gia PT1: chi them nhung cap member/PT chua tung danh gia.
INSERT INTO reviews (member_id, pt_id, rating_star, comment, created_at)
SELECT v.member_id, @demo_pt1_id, v.rating_star, v.comment, v.created_at
FROM (VALUES
    (@demo_member5_id, 5, N'PT theo sát mức tạ từng tuần, giải thích kỹ thuật rõ ràng và điều chỉnh lịch linh hoạt.', CAST('2026-08-31 20:00:00' AS DATETIME2)),
    (@demo_member29_id, 4, N'Buổi tập phù hợp người mới và PT chú ý tình trạng lưng dưới của tôi.', CAST('2026-09-02 20:00:00' AS DATETIME2)),
    (@demo_member68_id, 5, N'Lịch tập thực tế, có ghi nhận kết quả sau buổi nên tôi dễ theo dõi tiến bộ.', CAST('2026-09-04 20:00:00' AS DATETIME2))
) v(member_id, rating_star, comment, created_at)
WHERE NOT EXISTS (
    SELECT 1 FROM reviews r WHERE r.member_id = v.member_id AND r.pt_id = @demo_pt1_id
);

-- Thong bao moi de dashboard member/Admin/PT co du trang thai da doc/chua doc.
INSERT INTO notifications (user_id, sender_id, title, message, is_read, created_at) VALUES
(@demo_member1_id, NULL, N'Bạn có gói tập đang chờ nhận', N'Member2 đã gửi yêu cầu chuyển nhượng gói Premium. Yêu cầu có hiệu lực đến 01/11/2026.', 0, '2026-09-02 09:01:00'),
(@demo_member3_id, @demo_admin_id, N'Bảo lưu đã được ghi nhận', N'Gói Premium đang bảo lưu đến hết ngày 12/09/2026 và sẽ tự động hoạt động trở lại.', 0, '2026-08-29 08:30:00'),
(@demo_member4_id, @demo_admin_id, N'Giao dịch đang chờ xác nhận', N'Yêu cầu mua gói VIP 90 ngày đã được tiếp nhận. GymPro sẽ thông báo sau khi đối soát.', 0, '2026-09-04 20:06:00'),
(@demo_member5_id, @demo_admin_id, N'Gia hạn thành công', N'Giao dịch gia hạn Premium 30 ngày đã được xác nhận; biên nhận đã gửi về email đăng ký.', 1, '2026-08-20 09:30:00'),
(@demo_member29_id, @demo_admin_id, N'Nâng cấp gói VIP thành công', N'Gói tập của bạn đã được nâng cấp lên VIP và tiếp tục do PT Trần Đức Việt phụ trách.', 0, '2026-08-25 14:20:00'),
(@demo_member43_id, @demo_pt1_id, N'Lịch tập ngày 08/09', N'Bạn có buổi tập Push lúc 16:00 ngày 08/09. Vui lòng đến sớm 10 phút để khởi động vai.', 0, '2026-09-04 08:00:00'),
(@demo_pt1_id, @demo_admin_id, N'Lịch tuần bảo vệ đã sẵn sàng', N'Bạn có 4 học viên đang quản lý và 4 buổi tập đã lên lịch trong tuần 07-11/09.', 0, '2026-09-05 07:00:00'),
(@sale_user_id, @demo_admin_id, N'Cấp Sale đã được cập nhật', N'Bạn đã đạt cấp 2 với 12 khách hàng thành công. Mức ưu đãi hiện tại là 10%.', 0, '2026-08-30 11:00:00');

-- Dat yeu cau dang cho o cuoi batch de day la trang thai nghiep vu moi nhat khi demo.
INSERT INTO membership_transfers
    (source_membership_id, sender_id, recipient_id, status, remaining_days_at_request, expires_at, created_at)
VALUES
    (@demo_transfer_source_id, @demo_member2_id, @demo_member1_id, 'PENDING_RECIPIENT',
     DATEDIFF(DAY, '2026-09-02', '2026-12-13'), '2026-11-01 09:00:00', '2026-09-02 09:00:00');

IF NOT EXISTS (
    SELECT 1 FROM membership_transfers
    WHERE source_membership_id = @demo_transfer_source_id AND status = 'PENDING_RECIPIENT'
)
    THROW 50020, 'Seed demo transfer request was not created.', 1;

PRINT N'Đã nạp kịch bản demo GymPro cho giai đoạn bảo vệ 03/09/2026 - 08/09/2026.';

-- ============================================================
-- HET FILE
-- ============================================================
