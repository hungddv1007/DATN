-- ============================================================
-- GYMPRO DATABASE - SQL Server 2025
-- Website Quan Ly Phong Tap Gym
-- 19 bang
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

-- Roles
INSERT INTO roles (name) VALUES (N'ADMIN'), (N'PT'), (N'MEMBER');

-- 3 goi tap co dinh
INSERT INTO packages (name, price, duration_days, description, has_pt, can_choose_pt, has_meal_plan) VALUES
(N'BASIC', 500000, 30,
 N'Tap tu do. Xem trang chu, blog, video cong khai. Dich vu khan, nuoc tinh phi rieng.',
 0, 0, 0),
(N'PREMIUM', 1500000, 30,
 N'Duoc gan PT ngau nhien. Xem lo trinh, diem danh buoi tap. Nuoc, khan, giat do mien phi.',
 1, 0, 0),
(N'VIP', 3000000, 30,
 N'Duoc chon PT. Khau phan an do PT len. Tat ca dich vu mien phi.',
 1, 1, 1);

-- Admin mac dinh
INSERT INTO users (role_id, email, password, full_name, phone) VALUES
(1, N'admin@gympro.vn', N'$2a$10$hashed_password_here', N'Admin GymPro', N'0901234567');

PRINT N'=== GymProDB created successfully ===';
PRINT N'19 tables, 13 indexes, 3 roles, 3 packages, 1 admin account';
GO
