-- ============================================================
-- GYMPRO DATABASE - SQL Server 2025
-- Website Quan Ly Phong Tap Gym
-- 19 bang (Đã chuẩn hóa Data mẫu theo file Chi tiết dự án)
-- ============================================================

IF DB_ID('GymProDB') IS NOT NULL
BEGIN
    ALTER DATABASE GymProDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GymProDB;
END
GO

CREATE DATABASE GymProDB;
GO
USE GymProDB;
GO

-- ============================================================
-- 1. ROLES
-- ============================================================
CREATE TABLE roles (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(20) NOT NULL UNIQUE   -- ADMIN, PT, MEMBER
);

-- ============================================================
-- 2. USERS
-- ============================================================
CREATE TABLE users (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    role_id     INT NOT NULL,
    email       NVARCHAR(100) NOT NULL UNIQUE,
    password    NVARCHAR(255) NOT NULL,
    full_name   NVARCHAR(100) NOT NULL,
    phone       NVARCHAR(20),
    avatar      NVARCHAR(500),
    status      BIT DEFAULT 1,
    created_at  DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ============================================================
-- 3. PT_PROFILES
-- ============================================================
CREATE TABLE pt_profiles (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL UNIQUE,
    specialization  NVARCHAR(255),
    bio             NVARCHAR(MAX),
    certificates    NVARCHAR(MAX),
    rating_score    DECIMAL(2,1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 4. MEMBER_PROFILES
-- ============================================================
CREATE TABLE member_profiles (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    user_id             INT NOT NULL UNIQUE,
    physical_condition  NVARCHAR(MAX),     -- PT ghi khi danh gia ban dau
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 5. PACKAGES
-- ============================================================
CREATE TABLE packages (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    name            NVARCHAR(50) NOT NULL,
    price           DECIMAL(12,0) NOT NULL,
    duration_days   INT NOT NULL,
    description     NVARCHAR(MAX),
    has_pt          BIT DEFAULT 0,
    can_choose_pt   BIT DEFAULT 0,
    has_meal_plan   BIT DEFAULT 0
);

-- ============================================================
-- 6. PROMOTIONS
-- ============================================================
CREATE TABLE promotions (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    code                NVARCHAR(50) NOT NULL UNIQUE,
    discount_percent    INT NOT NULL,
    package_id          INT,               -- NULL = ap dung tat ca
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    max_usage           INT,
    current_usage       INT DEFAULT 0,
    is_active           BIT DEFAULT 1,
    FOREIGN KEY (package_id) REFERENCES packages(id)
);

-- ============================================================
-- 7. MEMBERSHIPS
-- ============================================================
CREATE TABLE memberships (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL,
    package_id      INT NOT NULL,
    pt_id           INT,                   -- NULL = Basic (khong co PT)
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          NVARCHAR(20) DEFAULT N'ACTIVE'
                    CHECK (status IN (N'ACTIVE', N'EXPIRED', N'PAUSED', N'CANCELLED')),
    pause_reason    NVARCHAR(255),
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (pt_id) REFERENCES users(id)
);

-- ============================================================
-- 8. TRANSACTIONS
-- ============================================================
CREATE TABLE transactions (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    membership_id       INT NOT NULL,
    promotion_id        INT,
    amount              DECIMAL(12,0) NOT NULL,
    original_amount     DECIMAL(12,0),
    payment_method      NVARCHAR(20)
                        CHECK (payment_method IN (N'CASH', N'BANK', N'ONLINE')),
    status              NVARCHAR(20) DEFAULT N'PENDING'
                        CHECK (status IN (N'PENDING', N'CONFIRMED', N'CANCELLED')),
    confirmed_by        INT,
    created_at          DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (membership_id) REFERENCES memberships(id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (confirmed_by) REFERENCES users(id)
);

-- ============================================================
-- 9. EXERCISES
-- ============================================================
CREATE TABLE exercises (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    name            NVARCHAR(100) NOT NULL,
    muscle_group    NVARCHAR(50),
    description     NVARCHAR(MAX),
    video_url       NVARCHAR(500),
    created_by      INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ============================================================
-- 10. TRAINING_ROUTES
-- ============================================================
CREATE TABLE training_routes (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    member_id       INT,                   -- NULL = template
    name            NVARCHAR(200) NOT NULL,
    is_template     BIT DEFAULT 0,
    status          NVARCHAR(20) DEFAULT N'DRAFT'
                    CHECK (status IN (N'DRAFT', N'ASSIGNED', N'COMPLETED')),
    start_date      DATE,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 11. SESSIONS
-- ============================================================
CREATE TABLE sessions (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    route_id        INT NOT NULL,
    week_num        INT NOT NULL,
    day_num         INT NOT NULL,
    name            NVARCHAR(100),         -- "Chest Day", "Rest Day"
    is_rest_day     BIT DEFAULT 0,
    FOREIGN KEY (route_id) REFERENCES training_routes(id) ON DELETE CASCADE
);

-- ============================================================
-- 12. SESSION_EXERCISES
-- ============================================================
CREATE TABLE session_exercises (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    session_id      INT NOT NULL,
    exercise_id     INT NOT NULL,
    sets            INT DEFAULT 3,
    reps            INT DEFAULT 10,
    weight_kg       DECIMAL(5,1),
    notes           NVARCHAR(255),
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);

-- ============================================================
-- 13. ATTENDANCES
-- ============================================================
CREATE TABLE attendances (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT NOT NULL,
    session_id      INT NOT NULL,
    check_in_time   DATETIME2 DEFAULT GETDATE(),
    status          BIT DEFAULT 1,         -- 1 = co mat, 0 = vang
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- ============================================================
-- 14. PT_NOTES
-- ============================================================
CREATE TABLE pt_notes (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    member_id       INT NOT NULL,
    content         NVARCHAR(MAX) NOT NULL,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 15. PT_COMMENTS
-- ============================================================
CREATE TABLE pt_comments (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    member_id       INT NOT NULL,
    route_id        INT,
    content         NVARCHAR(MAX) NOT NULL,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (route_id) REFERENCES training_routes(id)
);

-- ============================================================
-- 16. DIETS
-- ============================================================
CREATE TABLE diets (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT NOT NULL,
    pt_id           INT NOT NULL,
    date            DATE NOT NULL,
    breakfast       NVARCHAR(MAX),
    lunch           NVARCHAR(MAX),
    dinner          NVARCHAR(MAX),
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (pt_id) REFERENCES users(id)
);

-- ============================================================
-- 17. REVIEWS
-- ============================================================
CREATE TABLE reviews (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT NOT NULL,
    pt_id           INT NOT NULL,
    rating_star     INT NOT NULL CHECK (rating_star BETWEEN 1 AND 5),
    comment         NVARCHAR(MAX),
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (pt_id) REFERENCES users(id)
);

-- ============================================================
-- 18. BLOGS
-- ============================================================
CREATE TABLE blogs (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    author_id       INT NOT NULL,
    title           NVARCHAR(300) NOT NULL,
    content         NVARCHAR(MAX) NOT NULL,
    thumbnail       NVARCHAR(500),
    status          NVARCHAR(20) DEFAULT N'PUBLISHED'
                    CHECK (status IN (N'DRAFT', N'PUBLISHED')),
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ============================================================
-- 19. NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL,
    sender_id       INT,
    title           NVARCHAR(200) NOT NULL,
    message         NVARCHAR(MAX),
    is_read         BIT DEFAULT 0,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX IX_users_role ON users(role_id);
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_memberships_user ON memberships(user_id);
CREATE INDEX IX_memberships_status ON memberships(status);
CREATE INDEX IX_transactions_status ON transactions(status);
CREATE INDEX IX_training_routes_pt ON training_routes(pt_id);
CREATE INDEX IX_training_routes_member ON training_routes(member_id);
CREATE INDEX IX_attendances_member ON attendances(member_id);
CREATE INDEX IX_notifications_user ON notifications(user_id);
CREATE INDEX IX_notifications_read ON notifications(is_read);
CREATE INDEX IX_blogs_status ON blogs(status);
CREATE INDEX IX_diets_member ON diets(member_id);
CREATE INDEX IX_diets_date ON diets(date);

-- ============================================================
-- SEED DATA
-- ============================================================

-- ============================================================
-- 1. ROLES
-- ============================================================
INSERT INTO roles (name) VALUES
    (N'ADMIN'),
    (N'PT'),
    (N'MEMBER');
GO

-- ============================================================
-- 2. USERS
-- admin: id 1-2 | PT: id 3-7 | Member: id 8-17
-- ============================================================
INSERT INTO users (role_id, email, password, full_name, phone, status) VALUES
-- ADMIN
(1, 'admin@gympro.vn',        '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Nguyễn Quản Lý',   '0901000001', 1),
(1, 'manager@gympro.vn',      '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Trần Thị Quản',    '0901000002', 1),
-- PT
(2, 'pt.minh@gympro.vn',      '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Lê Văn Minh',      '0902000001', 1),
(2, 'pt.linh@gympro.vn',      '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Phạm Thị Linh',    '0902000002', 1),
(2, 'pt.hung@gympro.vn',      '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Đỗ Mạnh Hùng',     '0902000003', 1),
(2, 'pt.trang@gympro.vn',     '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Nguyễn Thị Trang', '0902000004', 1),
(2, 'pt.khoa@gympro.vn',      '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Vũ Đức Khoa',      '0902000005', 0), 
-- MEMBERS
(3, 'member.an@gmail.com',    '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Nguyễn Văn An',    '0903000001', 1),
(3, 'member.binh@gmail.com',  '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Trần Thị Bình',    '0903000002', 1),
(3, 'member.cuong@gmail.com', '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Lê Văn Cường',     '0903000003', 1),
(3, 'member.dung@gmail.com',  '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Phạm Thị Dung',    '0903000004', 1),
(3, 'member.em@gmail.com',    '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Đỗ Văn Em',        '0903000005', 1),
(3, 'member.phuong@gmail.com','$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Hoàng Thị Phương', '0903000006', 1),
(3, 'member.giang@gmail.com', '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Vũ Văn Giang',     '0903000007', 1),
(3, 'member.hoa@gmail.com',   '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Bùi Thị Hoa',      '0903000008', 1),
(3, 'member.hung@gmail.com',  '$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Đinh Văn Hưng',    '0903000009', 1),
(3, 'member.inactive@gmail.com','$2a$10$UDUoFA/301VIorR5zYq/ZuN5fbuUFaYd6pZoj6h0w6JiSwSx.6iDO', N'Thành Viên Cũ',    '0903000010', 0);
GO

-- ============================================================
-- 3. PT_PROFILES
-- ============================================================
INSERT INTO pt_profiles (user_id, specialization, bio, certificates, rating_score) VALUES
(3, N'Tăng cơ, Giảm cân', N'10 năm kinh nghiệm. Chuyên gia thể hình từng thi đấu quốc gia.', N'ACE-CPT, NSCA-CSCS', 4.8),
(4, N'Yoga, Cardio, Giảm cân', N'HLV yoga được chứng nhận quốc tế, 7 năm kinh nghiệm.', N'RYT-200, ACE', 4.9),
(5, N'Powerlifting, Strength', N'Cựu vận động viên cử tạ. Chuyên sức mạnh và sức bền.', N'NSCA-CSCS', 4.6),
(6, N'Phục hồi chức năng, Người cao tuổi', N'Có bằng vật lý trị liệu. Chuyên phục hồi sau chấn thương.', N'NASM-CES, CPT', 4.7),
(7, N'CrossFit, HIIT', N'HLV CrossFit cấp độ 2.', N'CrossFit L2', 0.0);
GO

-- ============================================================
-- 4. MEMBER_PROFILES
-- ============================================================
INSERT INTO member_profiles (user_id, physical_condition) VALUES
(8,  N'Nam, 25 tuổi, 70kg, 175cm. Mục tiêu: tăng cơ. Không có tiền sử chấn thương.'),
(9,  N'Nữ, 28 tuổi, 55kg, 162cm. Mục tiêu: giảm cân. Đầu gối yếu, cần lưu ý bài tập chân.'),
(10, N'Nam, 30 tuổi, 85kg, 178cm. Mục tiêu: giảm mỡ bụng và tăng sức mạnh.'),
(11, N'Nữ, 22 tuổi, 48kg, 158cm. Mục tiêu: tăng cân, định hình vóc dáng.'),
(12, N'Nam, 35 tuổi, 90kg, 180cm. Mục tiêu: phục hồi sau chấn thương vai.'),
(13, N'Nữ, 26 tuổi, 60kg, 165cm. Mục tiêu: cải thiện sức bền cardio.'),
(14, N'Nam, 27 tuổi, 72kg, 172cm. Mục tiêu: tăng cơ toàn thân.'),
(15, N'Nữ, 32 tuổi, 65kg, 168cm. Mục tiêu: yoga và thư giãn.'),
(16, N'Nam, 40 tuổi, 78kg, 170cm. Mục tiêu: duy trì sức khỏe, tim mạch.'),
(17, N'Nữ, 20 tuổi, 52kg, 160cm. Tài khoản bị khóa - test case.');
GO

-- ============================================================
-- 5. PACKAGES (Cập nhật chuẩn 3 gói theo tài liệu)
-- ============================================================
INSERT INTO packages (name, price, duration_days, description, has_pt, can_choose_pt, has_meal_plan) VALUES
(N'BASIC',   500000,  30, N'Tự tập, xem video công khai. Trả phí dịch vụ phòng tập.', 0, 0, 0),
(N'PREMIUM', 1500000, 30, N'Có PT hỗ trợ (hệ thống gán ngẫu nhiên), điểm danh, đánh giá PT, miễn phí dịch vụ.', 1, 0, 0),
(N'VIP',     2500000, 30, N'Tự chọn PT yêu thích, có thực đơn ăn uống riêng, miễn phí dịch vụ.', 1, 1, 1);
GO

-- ============================================================
-- 6. PROMOTIONS (Update package_id theo 3 gói mới)
-- ============================================================
INSERT INTO promotions (code, discount_percent, package_id, start_date, end_date, max_usage, current_usage, is_active) VALUES
(N'GYMPRO10',  10, NULL, '2025-01-01', '2025-12-31', 100, 15, 1),  
(N'VIP25',     25, 3,    '2025-06-01', '2025-07-31',  20,  2, 1),  
(N'SUMMER20',  20, NULL, '2025-05-01', '2025-05-31',  50, 50, 0),  
(N'BASIC50',   50, 1,    '2025-07-01', '2025-07-15',   5,  0, 1);  
GO

-- ============================================================
-- 7. MEMBERSHIPS 
-- (Phân loại: Gói 1 = Basic ko PT | Gói 2 = Premium PT random | Gói 3 = VIP tự chọn PT)
-- ============================================================
INSERT INTO memberships (user_id, package_id, pt_id, start_date, end_date, status, pause_reason) VALUES
(8,  3, 3,    '2025-06-01', '2025-06-30', N'ACTIVE',    NULL), -- VIP
(9,  2, 4,    '2025-06-10', '2025-07-10', N'ACTIVE',    NULL), -- Premium
(10, 2, 5,    '2025-05-01', '2025-07-31', N'ACTIVE',    NULL), -- Premium 
(11, 3, 6,    '2025-06-15', '2025-07-15', N'ACTIVE',    NULL), -- VIP 
(12, 3, 3,    '2025-06-20', '2025-07-20', N'ACTIVE',    NULL), -- VIP
(13, 1, NULL, '2025-06-01', '2025-06-30', N'ACTIVE',    NULL), -- Basic
(14, 2, 4,    '2025-06-05', '2025-07-05', N'ACTIVE',    NULL), -- Premium
(15, 1, NULL, '2025-05-01', '2025-05-31', N'PAUSED',    N'Đi công tác nước ngoài 2 tuần'),
(16, 1, NULL, '2025-04-01', '2025-04-30', N'EXPIRED',   NULL),
(8,  1, NULL, '2025-03-01', '2025-03-31', N'CANCELLED', NULL);
GO

-- ============================================================
-- 8. TRANSACTIONS (Cập nhật giá theo gói chuẩn mới)
-- ============================================================
INSERT INTO transactions (membership_id, promotion_id, amount, original_amount, payment_method, status, confirmed_by) VALUES
(1, 2,    1875000, 2500000, N'BANK',   N'CONFIRMED', 1), -- VIP (áp mã VIP25)
(2, 1,    1350000, 1500000, N'CASH',   N'CONFIRMED', 1), -- Premium (áp mã GYMPRO10)
(3, NULL, 4500000, 4500000, N'BANK',   N'CONFIRMED', 1), -- Premium 3 tháng (1.5m * 3)
(4, 1,    2250000, 2500000, N'ONLINE', N'CONFIRMED', 2), -- VIP (áp mã GYMPRO10)
(5, NULL, 2500000, 2500000, N'CASH',   N'CONFIRMED', 1), -- VIP
(6, NULL, 500000,  500000,  N'CASH',   N'CONFIRMED', 2), -- Basic
(7, 1,    1350000, 1500000, N'BANK',   N'CONFIRMED', 1), -- Premium (áp mã GYMPRO10)
(8, NULL, 500000,  500000,  N'CASH',   N'CONFIRMED', 2), -- Basic
(9, NULL, 500000,  500000,  N'CASH',   N'CONFIRMED', 1), -- Basic
(10,NULL, 500000,  500000,  N'BANK',   N'PENDING',   NULL); -- Basic
GO

-- ============================================================
-- 9. EXERCISES
-- ============================================================
INSERT INTO exercises (name, muscle_group, description, video_url, created_by) VALUES
(N'Bench Press',       N'Ngực',   N'Nằm ngửa, đẩy tạ thẳng lên. Giữ lưng thẳng.',           'https://www.youtube.com/watch?v=4Y2ZdHCOXok&pp=ygULYmVuY2ggcHJlc3M%3D', 3),
(N'Squat',             N'Chân',   N'Đứng thẳng, hạ người xuống như ngồi ghế.',                'https://www.youtube.com/watch?v=SE_edaFo4Y8&pp=ygUFU3F1YXQ%3D', 3),
(N'Deadlift',          N'Lưng',   N'Kéo tạ từ dưới sàn lên. Kỹ thuật rất quan trọng.',       'https://www.youtube.com/watch?v=XxWcirHIwVo&pp=ygUNZGVhZGxpZnQgZm9ybQ%3D%3D', 5),
(N'Pull-up',           N'Lưng',   N'Kéo người lên thanh xà đơn.',                             'https://www.youtube.com/watch?v=L6ndoM3jNKM&pp=ygUHcHVsbC11cA%3D%3D', 5),
(N'Shoulder Press',    N'Vai',    N'Đẩy tạ từ vai lên trên đầu.',                             'https://www.youtube.com/watch?v=0I10wx3fg3c&pp=ygUOU2hvdWxkZXIgUHJlc3M%3D', 3),
(N'Bicep Curl',        N'Tay',    N'Cuộn tạ tay để tập nhị đầu.',                             'https://www.youtube.com/watch?v=LUUwU0R_kk8&pp=ygUKQmljZXAgQ3VybA%3D%3D', 4),
(N'Tricep Pushdown',   N'Tay',    N'Kéo cáp xuống để tập tam đầu.',                           'https://www.youtube.com/watch?v=7S9D1WUblL0&pp=ygUgdOG6rXAgdGF5IHNhdSB24bubaSBkw6J5IHRo4burbmc%3D', 3),
(N'Leg Press',         N'Chân',   N'Đẩy tạ bằng chân trên máy.',                              'https://www.youtube.com/watch?v=K5n2vg3oZa4&pp=ygUJbGVnIHByZXNz', 5),
(N'Plank',             N'Bụng',   N'Nằm sấp chống hai tay, giữ thẳng người.',                 NULL,                    4),
(N'Crunches',          N'Bụng',   N'Nằm ngửa, gập bụng lên.',                                 NULL,                    4),
(N'Romanian Deadlift', N'Đùi sau',N'Biến thể deadlift tập trọng tâm cơ đùi sau.',             'https://www.youtube.com/watch?v=nE4-LcQ9px4&pp=ygURUm9tYW5pYW4gRGVhZGxpZnQ%3D', 5),
(N'Lat Pulldown',      N'Lưng',   N'Kéo cáp xuống bằng máy pulldown.',                        'https://www.youtube.com/watch?v=ZXb5HGjK1f8&pp=ygUMTGF0IFB1bGxkb3du',3),
(N'Treadmill',         N'Cardio', N'Chạy bộ trên máy. Điều chỉnh tốc độ và độ dốc.',          NULL,                    4),
(N'Mountain Climber',  N'Cardio', N'Chống đẩy, đổi chân liên tục nhanh.',                     NULL,                    4),
(N'Hip Thrust',        N'Mông',   N'Tựa vai ghế, đẩy hông lên cao với tạ đòn trên bụng.',     'https://www.youtube.com/watch?v=pUdIL5x0fWg&pp=ygUKSGlwIFRocnVzdA%3D%3D',4);
GO

-- ============================================================
-- 10. TRAINING_ROUTES
-- ============================================================
INSERT INTO training_routes (pt_id, member_id, name, is_template, status, start_date) VALUES
(3, 8,    N'Lộ trình tăng cơ 4 tuần - Nguyễn Văn An',    0, N'ASSIGNED',  '2025-06-01'), 
(4, 9,    N'Lộ trình giảm cân 4 tuần - Trần Thị Bình',   0, N'ASSIGNED',  '2025-06-10'), 
(5, 10,   N'Lộ trình sức mạnh 8 tuần - Lê Văn Cường',    0, N'ASSIGNED',  '2025-05-01'), 
(6, 12,   N'Phục hồi vai 6 tuần - Đỗ Văn Em',            0, N'ASSIGNED',  '2025-06-20'), 
(3, 14,   N'Tăng cơ toàn thân - Vũ Văn Giang',           0, N'DRAFT',     NULL),         
(3, NULL, N'[MẪU] Tăng cơ cơ bản 4 tuần',                1, N'ASSIGNED',  NULL),         
(4, NULL, N'[MẪU] Giảm cân 4 tuần',                      1, N'ASSIGNED',  NULL),         
(3, 8,    N'Lộ trình nhập môn - Nguyễn Văn An',          0, N'COMPLETED', '2025-03-01'); 
GO

-- ============================================================
-- 11. SESSIONS
-- ============================================================
INSERT INTO sessions (route_id, week_num, day_num, name, is_rest_day) VALUES
(1, 1, 1, N'Ngực + Vai + Tay',  0),  
(1, 1, 2, N'Lưng + Tay',        0),  
(1, 1, 3, N'Nghỉ ngơi',         1),  
(1, 1, 4, N'Chân + Mông',       0),  
(1, 1, 5, N'Cardio + Bụng',     0),  
(1, 1, 6, N'Nghỉ ngơi',         1),  
(1, 1, 7, N'Nghỉ ngơi',         1),  
(1, 2, 1, N'Ngực + Vai + Tay',  0),  
(1, 2, 2, N'Lưng + Tay',        0),  
(1, 2, 3, N'Nghỉ ngơi',         1),  
(1, 2, 4, N'Chân + Mông',       0),  
(1, 2, 5, N'Cardio + Bụng',     0),  
(1, 2, 6, N'Nghỉ ngơi',         1),  
(1, 2, 7, N'Nghỉ ngơi',         1),  
(2, 1, 1, N'Cardio + Bụng',     0),  
(2, 1, 2, N'Chân + Mông',       0),  
(2, 1, 3, N'Nghỉ ngơi',         1),  
(2, 1, 4, N'Full body nhẹ',     0),  
(2, 1, 5, N'Cardio dài',        0),  
(2, 1, 6, N'Nghỉ ngơi',         1),  
(2, 1, 7, N'Nghỉ ngơi',         1);  
GO

-- ============================================================
-- 12. SESSION_EXERCISES
-- ============================================================
INSERT INTO session_exercises (session_id, exercise_id, sets, reps, weight_kg, notes) VALUES
(1, 1,  4, 10, 60.0,  N'Nghỉ 90 giây giữa hiệp'),
(1, 5,  3, 12, 30.0,  NULL),
(1, 7,  3, 15, 25.0,  NULL),
(2, 4,  4, 8,  NULL,  N'Dùng đai lưng nếu cần'),
(2, 12, 3, 12, 45.0,  NULL),
(2, 6,  3, 12, 15.0,  NULL),
(4, 2,  4, 10, 80.0,  N'Gối không vượt qua ngón chân'),
(4, 8,  3, 12, 100.0, NULL),
(4, 15, 3, 15, 40.0,  NULL),
(5, 13, 1, 1,  NULL,  N'Chạy 20 phút tốc độ vừa'),
(5, 9,  3, 30, NULL,  N'Nghỉ 45 giây giữa hiệp'),
(5, 10, 3, 20, NULL,  NULL),
(15,13, 1, 1,  NULL,  N'Chạy 30 phút, nhịp tim 130-150'),
(15,14, 3, 20, NULL,  N'3 set x 20 reps'),
(15,9,  3, 45, NULL,  N'Giữ 45 giây mỗi hiệp'),
(16,2,  3, 15, 50.0,  N'Tạ nhẹ, nhiều reps'),
(16,8,  3, 15, 70.0,  NULL),
(16,15, 3, 20, 20.0,  NULL);
GO

-- ============================================================
-- 13. ATTENDANCES
-- ============================================================
INSERT INTO attendances (member_id, session_id, status) VALUES
(8, 1, 1), (8, 2, 1), (8, 3, 1), (8, 4, 1), (8, 5, 1),
(8, 8, 1), (8, 9, 1), (8, 10, 1), (8, 11, 0), (8, 12, 1),
(9, 15, 1), (9, 16, 1), (9, 17, 1), (9, 18, 0), (9, 19, 1),
(10, 1, 1), (10, 2, 1), (10, 4, 1);
GO

-- ============================================================
-- 14. PT_NOTES
-- ============================================================
INSERT INTO pt_notes (pt_id, member_id, content) VALUES
(3, 8,  N'An tiến bộ tốt sau 2 tuần. Tăng mức tạ Bench Press từ 60kg lên 65kg từ tuần tới.'),
(3, 8,  N'Kỹ thuật Squat cần cải thiện. Gối đang bị vạy vào trong, nhắc nhở thường xuyên.'),
(3, 12, N'Vai phải vẫn còn đau khi nâng tay ngang. Chưa cho tập Shoulder Press. Chuyển sang bài phục hồi nhẹ.'),
(4, 9,  N'Bình giảm 1.5kg sau 3 tuần. Tiếp tục duy trì chế độ ăn và tăng thời gian cardio.'),
(5, 10, N'Cường deadlift 120kg clean. Sẵn sàng tăng volume tuần tới.');
GO

-- ============================================================
-- 15. PT_COMMENTS
-- ============================================================
INSERT INTO pt_comments (pt_id, member_id, route_id, content) VALUES
(3, 8,  1, N'PT Minh nhiệt tình, hướng dẫn kỹ thuật rõ ràng. Tôi đã thấy kết quả rõ sau 3 tuần!'),
(4, 9,  2, N'Chị Linh rất tận tâm và có kiến thức chuyên sâu về dinh dưỡng. Rất hài lòng.'),
(3, 8,  8, N'Lộ trình nhập môn giúp tôi làm quen với gym rất tốt. 5 sao!');
GO

-- ============================================================
-- 16. DIETS (Chỉ cấp cho hội viên gói VIP - ID 8, 11, 12)
-- ============================================================
INSERT INTO diets (member_id, pt_id, date, breakfast, lunch, dinner) VALUES
(8, 3, '2025-06-23',
    N'3 quả trứng luộc + bánh mì ngũ cốc + sữa không đường (450 kcal)',
    N'Cơm gạo lứt 150g + ức gà 200g + rau xanh luộc (600 kcal)',
    N'Cá hồi 150g áp chảo + khoai lang 100g + salad (500 kcal)'),
(8, 3, '2025-06-24',
    N'Yến mạch 80g + chuối 1 quả + protein shake (420 kcal)',
    N'Cơm gạo lứt 150g + thịt bò 150g + bông cải xanh (580 kcal)',
    N'Ức gà 200g hấp + khoai tây luộc + dưa leo (480 kcal)'),
(11, 6, '2025-06-23',
    N'2 quả trứng + avocado toast + nước ép cam (400 kcal)',
    N'Cơm trắng 150g + đậu phụ + rau cải xào (450 kcal)',
    N'Cháo yến mạch + hạt hạnh nhân + sữa chua (350 kcal)');
GO

-- ============================================================
-- 17. REVIEWS
-- ============================================================
INSERT INTO reviews (member_id, pt_id, rating_star, comment) VALUES
(8,  3, 5, N'Tuyệt vời! Kỹ thuật tốt, nhiệt tình, đúng giờ.'),
(9,  4, 5, N'Chị Linh rất chuyên nghiệp. Kế hoạch ăn uống rất khoa học.'),
(10, 5, 4, N'Anh Hùng giỏi về sức mạnh, có thể giải thích thêm về dinh dưỡng.'),
(12, 6, 5, N'Nhờ PT Trang vai tôi đã hồi phục hoàn toàn sau 5 tuần!'),
(14, 4, 5, N'Trước đây tôi hay bị chấn thương, từ khi tập với chị Linh không còn vấn đề gì.');
GO

-- ============================================================
-- 18. BLOGS
-- ============================================================
INSERT INTO blogs (author_id, title, content, status) VALUES
(3, N'5 lỗi kỹ thuật phổ biến khi tập Squat',
   N'Squat là bài tập nền tảng nhưng cũng là bài dễ chấn thương nhất nếu sai kỹ thuật. Dưới đây là 5 lỗi phổ biến nhất...',
   N'PUBLISHED'),
(4, N'Chế độ ăn giảm mỡ hiệu quả: Không cần nhịn ăn',
   N'Nhiều người nghĩ giảm mỡ = nhịn ăn. Sự thật là bạn cần ăn đúng, không phải ăn ít...',
   N'PUBLISHED'),
(1, N'GymPro khai trương cơ sở mới tại Quận 7',
   N'Chúng tôi vui mừng thông báo cơ sở GymPro thứ 3 tại 123 Nguyễn Lương Bằng sẽ chính thức hoạt động từ 01/07/2025...',
   N'PUBLISHED'),
(5, N'Powerlifting cho người mới bắt đầu',
   N'Bài viết đang được soạn thảo...',
   N'DRAFT');
GO

-- ============================================================
-- 19. NOTIFICATIONS
-- ============================================================
INSERT INTO notifications (user_id, sender_id, title, message, is_read) VALUES
(8,  NULL, N'Chào mừng đến với GymPro!',
     N'Tài khoản của bạn đã được kích hoạt thành công. Bắt đầu hành trình luyện tập ngay hôm nay!', 1),
(9,  NULL, N'Gói tập sắp hết hạn',
     N'Gói tập của bạn sẽ hết hạn sau 7 ngày. Gia hạn ngay để không gián đoạn lịch tập!', 0),
(16, NULL, N'Gói tập đã hết hạn',
     N'Gói tập của bạn đã hết hạn từ 30/04/2025. Vui lòng đăng ký gói mới để tiếp tục.', 0),
(8,  3,    N'Lộ trình tuần 3 đã sẵn sàng',
    N'Chào An, mình đã cập nhật lộ trình tuần 3 cho bạn. Hãy xem và cho mình biết nếu có thắc mắc nhé!', 1),
(9,  4,    N'Nhắc nhở: Uống đủ 2.5L nước mỗi ngày',
    N'Chào Bình, trong tuần này đừng quên uống đủ nước nhé. Rất quan trọng cho quá trình giảm cân!', 0),
(12, 3,    N'Cần điều chỉnh bài tập vai',
    N'Chào Em, mình thấy vai bạn vẫn còn đau. Buổi tập tới mình sẽ thay thế Shoulder Press bằng bài nhẹ hơn.', 0),
(8,  1,    N'Phòng gym đóng cửa bảo trì 25/06',
    N'GymPro sẽ đóng cửa để bảo trì thiết bị vào ngày 25/06/2025 (Thứ Tư). Xin lỗi vì sự bất tiện này.', 1),
(9,  1,    N'Phòng gym đóng cửa bảo trì 25/06',
    N'GymPro sẽ đóng cửa để bảo trì thiết bị vào ngày 25/06/2025 (Thứ Tư). Xin lỗi vì sự bất tiện này.', 0),
(10, 1,    N'Phòng gym đóng cửa bảo trì 25/06',
    N'GymPro sẽ đóng cửa để bảo trì thiết bị vào ngày 25/06/2025 (Thứ Tư). Xin lỗi vì sự bất tiện này.', 0);
GO

-- ============================================================
-- VERIFY
-- ============================================================
SELECT 'roles'             AS TableName, COUNT(*) AS RecordCount FROM roles             UNION ALL
SELECT 'users',                          COUNT(*)                FROM users             UNION ALL
SELECT 'pt_profiles',                    COUNT(*)                FROM pt_profiles       UNION ALL
SELECT 'member_profiles',                COUNT(*)                FROM member_profiles   UNION ALL
SELECT 'packages',                       COUNT(*)                FROM packages          UNION ALL
SELECT 'promotions',                     COUNT(*)                FROM promotions        UNION ALL
SELECT 'memberships',                    COUNT(*)                FROM memberships       UNION ALL
SELECT 'transactions',                   COUNT(*)                FROM transactions      UNION ALL
SELECT 'exercises',                      COUNT(*)                FROM exercises         UNION ALL
SELECT 'training_routes',                COUNT(*)                FROM training_routes   UNION ALL
SELECT 'sessions',                       COUNT(*)                FROM sessions          UNION ALL
SELECT 'session_exercises',              COUNT(*)                FROM session_exercises UNION ALL
SELECT 'attendances',                    COUNT(*)                FROM attendances       UNION ALL
SELECT 'pt_notes',                       COUNT(*)                FROM pt_notes          UNION ALL
SELECT 'pt_comments',                    COUNT(*)                FROM pt_comments       UNION ALL
SELECT 'diets',                          COUNT(*)                FROM diets             UNION ALL
SELECT 'reviews',                        COUNT(*)                FROM reviews           UNION ALL
SELECT 'blogs',                          COUNT(*)                FROM blogs             UNION ALL
SELECT 'notifications',                  COUNT(*)                FROM notifications;
GO