IF DB_ID('GymProDB') IS NOT NULL
BEGIN
    THROW 50001, 'GymProDB already exists. Use a migration script instead of recreating the database.', 1;
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
    provider    NVARCHAR(20) DEFAULT 'LOCAL',
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
    max_members     INT DEFAULT 5,
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
    daily_price     DECIMAL(12,0) NOT NULL,
    description     NVARCHAR(MAX),
    has_pt          BIT DEFAULT 0,
    can_choose_pt   BIT DEFAULT 0,
    has_meal_plan   BIT DEFAULT 0,
    min_days        INT DEFAULT 1,
    max_hold_times  INT DEFAULT 0,
    hold_return_percent INT DEFAULT 0,
    is_active       BIT DEFAULT 1
);

-- ============================================================
-- 5.5. PACKAGE_DISCOUNTS
-- ============================================================
CREATE TABLE package_discounts (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    package_id       INT,
    min_days         INT NOT NULL,
    discount_percent INT NOT NULL,
    FOREIGN KEY (package_id) REFERENCES packages(id)
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
    pt_id           INT,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          NVARCHAR(20) DEFAULT 'ACTIVE'
                    CHECK (status IN ('PENDING', 'ACTIVE', 'EXPIRED', 'PAUSED', 'CANCELLED')),
    pause_reason    NVARCHAR(255),
    duration_days   INT,
    daily_price     DECIMAL(12,0),
    hold_count      INT DEFAULT 0,
    paused_at       DATE,
    total_hold_days INT DEFAULT 0,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (pt_id) REFERENCES users(id)
);

-- ============================================================
-- 8. TRANSACTIONS
-- ============================================================
CREATE TABLE transactions (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    membership_id           INT NOT NULL,
    promotion_id            INT,
    requested_duration_days INT,
    requested_package_id    INT,
    requested_pt_id         INT,
    operation_applied       BIT NOT NULL DEFAULT 0,
    amount                  DECIMAL(12,0) NOT NULL,
    original_amount         DECIMAL(12,0),
    payment_method          NVARCHAR(20)
                            CHECK (payment_method IN ('CASH', 'BANK', 'ONLINE')),
    status                  NVARCHAR(20) DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    type                    NVARCHAR(20) DEFAULT 'NEW'
                            CHECK (type IN ('NEW', 'RENEW', 'UPGRADE')),
    confirmed_by            INT,
    version                 BIGINT NOT NULL DEFAULT 0,
    created_at              DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (membership_id) REFERENCES memberships(id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (requested_package_id) REFERENCES packages(id),
    FOREIGN KEY (requested_pt_id) REFERENCES users(id),
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
    is_active       BIT DEFAULT 1,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ============================================================
-- 10. ATTENDANCES
-- ============================================================
CREATE TABLE attendances (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT NOT NULL,
    check_in_time   DATETIME2 DEFAULT GETDATE(),
    status          BIT DEFAULT 1,         -- 1 = co mat, 0 = vang
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 11. PT_NOTES
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
-- 12. PT_COMMENTS  (da bo cot plan_id vi training_plans khong con)
-- ============================================================
CREATE TABLE pt_comments (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    member_id       INT NOT NULL,
    content         NVARCHAR(MAX) NOT NULL,
    created_at      DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 13. DIETS  (Khau phan an: mau Ngay Tap / Ngay Nghi / Ngay cu the)
-- ============================================================
CREATE TABLE diets (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    pt_id               INT NOT NULL,
    member_id           INT NOT NULL,
    day_type            NVARCHAR(20) NOT NULL DEFAULT 'REST_DAY'
                        CHECK (day_type IN ('TRAINING_DAY', 'REST_DAY', 'SPECIFIC_DATE')),
    diet_date           DATE,                        -- NULL neu la mau TRAINING_DAY / REST_DAY
    title               NVARCHAR(100),               -- VD: Thuc don tang co ngay tap
    breakfast           NVARCHAR(MAX),
    snack_morning       NVARCHAR(MAX),               -- Bua phu sang / Pre-workout
    lunch               NVARCHAR(MAX),
    snack_afternoon     NVARCHAR(MAX),               -- Bua phu chieu / Post-workout
    dinner              NVARCHAR(MAX),
    calories            INT DEFAULT 0,               -- Tong Calo (kcal)
    protein_g           INT DEFAULT 0,
    carbs_g             INT DEFAULT 0,
    fat_g               INT DEFAULT 0,
    note                NVARCHAR(MAX),
    created_at          DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 14. REVIEWS
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
-- 15. BLOGS
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
-- 16. PT_SCHEDULES  (Lich tap linh hoat: ngay cu the + gio bat dau/ket thuc tu do)
-- ============================================================
CREATE TABLE pt_schedules (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    pt_id               INT NOT NULL,
    member_id           INT NOT NULL,
    schedule_date       DATE NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME NOT NULL,
    exercise_note       NVARCHAR(200),
    recurring_group_id  VARCHAR(36),
    status              NVARCHAR(20) DEFAULT 'ACTIVE',
    created_at          DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 17. NOTIFICATIONS
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
-- 18. OTPS
-- ============================================================
CREATE TABLE otps (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    email            NVARCHAR(100) NOT NULL,
    otp              NVARCHAR(6) NOT NULL,
    expiration_time  DATETIME2 NOT NULL,
    created_at       DATETIME2 NOT NULL DEFAULT GETDATE(),
    used             BIT NOT NULL DEFAULT 0,
    failed_attempts  INT NOT NULL DEFAULT 0
);

-- ============================================================
-- 19. AI CONVERSATIONS
-- ============================================================
CREATE TABLE ai_conversations (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    user_id                 INT NOT NULL,
    title                   NVARCHAR(120) NOT NULL,
    physical_data_consent   BIT NOT NULL DEFAULT 0,
    created_at              DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at              DATETIME2 NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 20. AI MESSAGES
-- ============================================================
CREATE TABLE ai_messages (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    conversation_id     INT NOT NULL,
    role                NVARCHAR(20) NOT NULL
                        CHECK (role IN ('USER', 'ASSISTANT')),
    content             NVARCHAR(MAX) NOT NULL,
    model               NVARCHAR(80),
    input_tokens        INT,
    output_tokens       INT,
    total_tokens        INT,
    created_at          DATETIME2 NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id)
        ON DELETE CASCADE
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX IX_users_role ON users(role_id);
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_memberships_user ON memberships(user_id);
CREATE INDEX IX_memberships_status ON memberships(status);
CREATE INDEX IX_transactions_status ON transactions(status);
CREATE INDEX IX_notifications_user ON notifications(user_id);
CREATE INDEX IX_notifications_read ON notifications(is_read);
CREATE INDEX IX_blogs_status ON blogs(status);
CREATE INDEX IX_diets_member ON diets(member_id);
CREATE INDEX IX_diets_date ON diets(diet_date);
CREATE INDEX IX_pt_schedules_pt_date ON pt_schedules(pt_id, schedule_date, start_time);
CREATE INDEX IX_pt_schedules_member_date ON pt_schedules(member_id, schedule_date);
CREATE INDEX IX_pt_schedules_recurring ON pt_schedules(recurring_group_id);
CREATE INDEX IX_attendances_member ON attendances(member_id);
CREATE INDEX IX_otps_email_expiration ON otps(email, expiration_time DESC);
CREATE INDEX IX_ai_conversations_user_updated ON ai_conversations(user_id, updated_at DESC);
CREATE INDEX IX_ai_messages_conversation_created ON ai_messages(conversation_id, created_at ASC);

GO

