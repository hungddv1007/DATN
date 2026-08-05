-- ============================================================
-- GymProDB - Complete schema for the current GymPro application
--
-- This is the single source of truth for database structure.
-- Drop GymProDB manually before running this file when rebuilding.
-- Run "seed data only.sql" separately after the schema is created.
-- ============================================================
USE master;
GO

IF DB_ID(N'GymProDB') IS NOT NULL
BEGIN
    THROW 50001, 'GymProDB already exists. Drop it manually before running GymProDB.sql.', 1;
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
    name        NVARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT CK_roles_name CHECK (name IN (N'ADMIN', N'PT', N'MEMBER'))
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
    status      BIT NOT NULL CONSTRAINT DF_users_status DEFAULT 1,
    provider    NVARCHAR(20) NOT NULL CONSTRAINT DF_users_provider DEFAULT N'LOCAL',
    created_at  DATETIME2 NOT NULL CONSTRAINT DF_users_created_at DEFAULT GETDATE(),
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
    rating_score    DECIMAL(2,1) NOT NULL CONSTRAINT DF_pt_profiles_rating DEFAULT 0,
    max_members     INT NOT NULL CONSTRAINT DF_pt_profiles_max_members DEFAULT 5,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT CK_pt_profiles_rating CHECK (rating_score BETWEEN 0 AND 5),
    CONSTRAINT CK_pt_profiles_max_members CHECK (max_members > 0)
);

-- ============================================================
-- 4. MEMBER_PROFILES
-- ============================================================
CREATE TABLE member_profiles (
    id                    INT IDENTITY(1,1) PRIMARY KEY,
    user_id               INT NOT NULL UNIQUE,
    height_cm             DECIMAL(5,2) NULL,
    weight_kg             DECIMAL(6,2) NULL,
    date_of_birth         DATE NULL,
    biological_sex       VARCHAR(10) NULL,
    chest_cm              DECIMAL(5,2) NULL,
    waist_cm              DECIMAL(5,2) NULL,
    hip_cm                DECIMAL(5,2) NULL,
    body_fat_percentage   DECIMAL(5,2) NULL,
    body_fat_source       VARCHAR(10) NULL,
    activity_level        VARCHAR(30) NULL,
    fitness_goal          VARCHAR(30) NULL,
    target_weight_kg      DECIMAL(6,2) NULL,
    training_experience   NVARCHAR(500) NULL,
    injury_history        NVARCHAR(2000) NULL,
    medical_conditions    NVARCHAR(2000) NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT CK_member_profiles_height
        CHECK (height_cm IS NULL OR height_cm BETWEEN 50 AND 300),
    CONSTRAINT CK_member_profiles_weight
        CHECK (weight_kg IS NULL OR weight_kg BETWEEN 20 AND 500),
    CONSTRAINT CK_member_profiles_measurements
        CHECK ((chest_cm IS NULL OR chest_cm BETWEEN 20 AND 300)
            AND (waist_cm IS NULL OR waist_cm BETWEEN 20 AND 300)
            AND (hip_cm IS NULL OR hip_cm BETWEEN 20 AND 300)),
    CONSTRAINT CK_member_profiles_body_fat
        CHECK (body_fat_percentage IS NULL OR body_fat_percentage BETWEEN 0 AND 100),
    CONSTRAINT CK_member_profiles_target_weight
        CHECK (target_weight_kg IS NULL OR target_weight_kg BETWEEN 20 AND 500),
    CONSTRAINT CK_member_profiles_activity_level
        CHECK (activity_level IS NULL OR activity_level IN ('SEDENTARY', 'LIGHT', 'MODERATE', 'HIGH', 'VERY_HIGH')),
    CONSTRAINT CK_member_profiles_fitness_goal
        CHECK (fitness_goal IS NULL OR fitness_goal IN ('WEIGHT_LOSS', 'MUSCLE_GAIN', 'MAINTENANCE', 'HEALTH_IMPROVEMENT')),
    CONSTRAINT CK_member_profiles_biological_sex
        CHECK (biological_sex IS NULL OR biological_sex IN ('MALE', 'FEMALE')),
    CONSTRAINT CK_member_profiles_body_fat_source
        CHECK (body_fat_source IS NULL OR body_fat_source IN ('MANUAL', 'ESTIMATED'))
);

-- ============================================================
-- 5. PACKAGES
-- ============================================================
CREATE TABLE packages (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    name            NVARCHAR(50) NOT NULL,
    daily_price     DECIMAL(12,0) NOT NULL,
    description     NVARCHAR(MAX),
    has_pt          BIT NOT NULL CONSTRAINT DF_packages_has_pt DEFAULT 0,
    can_choose_pt   BIT NOT NULL CONSTRAINT DF_packages_can_choose_pt DEFAULT 0,
    has_meal_plan   BIT NOT NULL CONSTRAINT DF_packages_has_meal_plan DEFAULT 0,
    min_days        INT NOT NULL CONSTRAINT DF_packages_min_days DEFAULT 1,
    max_hold_times  INT NOT NULL CONSTRAINT DF_packages_max_hold_times DEFAULT 0,
    hold_return_percent INT NOT NULL CONSTRAINT DF_packages_hold_return DEFAULT 0,
    is_active       BIT NOT NULL CONSTRAINT DF_packages_is_active DEFAULT 1,
    CONSTRAINT CK_packages_daily_price CHECK (daily_price > 0),
    CONSTRAINT CK_packages_min_days CHECK (min_days > 0),
    CONSTRAINT CK_packages_max_hold_times CHECK (max_hold_times >= 0),
    CONSTRAINT CK_packages_hold_return CHECK (hold_return_percent BETWEEN 0 AND 100)
);

-- ============================================================
-- 5.5. PACKAGE_DISCOUNTS
-- ============================================================
CREATE TABLE package_discounts (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    package_id       INT,
    min_days         INT NOT NULL,
    discount_percent INT NOT NULL,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    CONSTRAINT CK_package_discounts_min_days CHECK (min_days > 0),
    CONSTRAINT CK_package_discounts_percent CHECK (discount_percent BETWEEN 0 AND 100)
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
    current_usage       INT NOT NULL CONSTRAINT DF_promotions_current_usage DEFAULT 0,
    is_active           BIT NOT NULL CONSTRAINT DF_promotions_is_active DEFAULT 1,
    version             BIGINT NOT NULL CONSTRAINT DF_promotions_version DEFAULT 0,
    FOREIGN KEY (package_id) REFERENCES packages(id),
    CONSTRAINT CK_promotions_discount CHECK (discount_percent BETWEEN 0 AND 100),
    CONSTRAINT CK_promotions_dates CHECK (end_date >= start_date),
    CONSTRAINT CK_promotions_max_usage CHECK (max_usage IS NULL OR max_usage > 0),
    CONSTRAINT CK_promotions_current_usage CHECK (
        current_usage >= 0 AND (max_usage IS NULL OR current_usage <= max_usage)
    )
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
    status          NVARCHAR(20) NOT NULL CONSTRAINT DF_memberships_status DEFAULT 'ACTIVE',
    pause_reason    NVARCHAR(255),
    duration_days   INT,
    daily_price     DECIMAL(12,0),
    hold_count      INT NOT NULL CONSTRAINT DF_memberships_hold_count DEFAULT 0,
    paused_at       DATE,
    total_hold_days INT NOT NULL CONSTRAINT DF_memberships_total_hold_days DEFAULT 0,
    version         BIGINT NOT NULL CONSTRAINT DF_memberships_version DEFAULT 0,
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_memberships_created_at DEFAULT GETDATE(),
    current_membership_user_id AS (
        CASE WHEN status IN ('PENDING', 'ACTIVE', 'PAUSED') THEN user_id ELSE NULL END
    ) PERSISTED,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (package_id) REFERENCES packages(id),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    CONSTRAINT CK_memberships_status
        CHECK (status IN ('PENDING', 'ACTIVE', 'EXPIRED', 'PAUSED', 'CANCELLED')),
    CONSTRAINT CK_memberships_dates CHECK (end_date >= start_date),
    CONSTRAINT CK_memberships_duration CHECK (duration_days IS NULL OR duration_days > 0),
    CONSTRAINT CK_memberships_daily_price CHECK (daily_price IS NULL OR daily_price > 0),
    CONSTRAINT CK_memberships_hold_count CHECK (hold_count >= 0),
    CONSTRAINT CK_memberships_total_hold_days CHECK (total_hold_days >= 0)
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
    operation_applied       BIT NOT NULL CONSTRAINT DF_transactions_operation_applied DEFAULT 0,
    amount                  DECIMAL(12,0) NOT NULL,
    original_amount         DECIMAL(12,0),
    payment_method          NVARCHAR(20),
    status                  NVARCHAR(20) NOT NULL CONSTRAINT DF_transactions_status DEFAULT 'PENDING',
    type                    NVARCHAR(20) NOT NULL CONSTRAINT DF_transactions_type DEFAULT 'NEW',
    confirmed_by            INT,
    version                 BIGINT NOT NULL CONSTRAINT DF_transactions_version DEFAULT 0,
    created_at              DATETIME2 NOT NULL CONSTRAINT DF_transactions_created_at DEFAULT GETDATE(),
    pending_membership_id AS (
        CASE WHEN status = 'PENDING' THEN membership_id ELSE NULL END
    ) PERSISTED,
    FOREIGN KEY (membership_id) REFERENCES memberships(id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (requested_package_id) REFERENCES packages(id),
    FOREIGN KEY (requested_pt_id) REFERENCES users(id),
    FOREIGN KEY (confirmed_by) REFERENCES users(id),
    CONSTRAINT CK_transactions_requested_duration
        CHECK (requested_duration_days IS NULL OR requested_duration_days >= 0),
    CONSTRAINT CK_transactions_amount CHECK (amount >= 0),
    CONSTRAINT CK_transactions_original_amount
        CHECK (original_amount IS NULL OR original_amount >= 0),
    CONSTRAINT CK_transactions_payment_method
        CHECK (payment_method IS NULL OR payment_method IN ('CASH', 'BANK', 'ONLINE')),
    CONSTRAINT CK_transactions_status
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
    CONSTRAINT CK_transactions_type CHECK (type IN ('NEW', 'RENEW', 'UPGRADE'))
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
    is_active       BIT NOT NULL CONSTRAINT DF_exercises_is_active DEFAULT 1,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ============================================================
-- 10. PT_NOTES
-- ============================================================
CREATE TABLE pt_notes (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    pt_id           INT NOT NULL,
    member_id       INT NOT NULL,
    content         NVARCHAR(MAX) NOT NULL,
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_pt_notes_created_at DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id)
);

-- ============================================================
-- 11. DIETS  (Khau phan an: mau Ngay Tap / Ngay Nghi / Ngay cu the)
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
    calories            INT NOT NULL CONSTRAINT DF_diets_calories DEFAULT 0,
    protein_g           INT NOT NULL CONSTRAINT DF_diets_protein DEFAULT 0,
    carbs_g             INT NOT NULL CONSTRAINT DF_diets_carbs DEFAULT 0,
    fat_g               INT NOT NULL CONSTRAINT DF_diets_fat DEFAULT 0,
    note                NVARCHAR(MAX),
    created_at          DATETIME2 NOT NULL CONSTRAINT DF_diets_created_at DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id),
    CONSTRAINT UQ_diets_member_day UNIQUE (member_id, day_type, diet_date),
    CONSTRAINT CK_diets_macros
        CHECK (calories >= 0 AND protein_g >= 0 AND carbs_g >= 0 AND fat_g >= 0),
    CONSTRAINT CK_diets_specific_date
        CHECK ((day_type = 'SPECIFIC_DATE' AND diet_date IS NOT NULL)
            OR (day_type IN ('TRAINING_DAY', 'REST_DAY') AND diet_date IS NULL))
);

-- ============================================================
-- 12. REVIEWS
-- ============================================================
CREATE TABLE reviews (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT NOT NULL,
    pt_id           INT NOT NULL,
    rating_star     INT NOT NULL CHECK (rating_star BETWEEN 1 AND 5),
    comment         NVARCHAR(MAX),
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_reviews_created_at DEFAULT GETDATE(),
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    CONSTRAINT UQ_reviews_member_pt UNIQUE (member_id, pt_id)
);

-- ============================================================
-- 13. BLOGS
-- ============================================================
CREATE TABLE blogs (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    author_id       INT NOT NULL,
    title           NVARCHAR(300) NOT NULL,
    content         NVARCHAR(MAX) NOT NULL,
    thumbnail       NVARCHAR(500),
    status          NVARCHAR(20) DEFAULT N'PUBLISHED'
                    CHECK (status IN (N'DRAFT', N'PUBLISHED')),
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_blogs_created_at DEFAULT GETDATE(),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ============================================================
-- 14. PT_SCHEDULES  (Lich tap linh hoat: ngay cu the + gio bat dau/ket thuc tu do)
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
    status              NVARCHAR(20) NOT NULL CONSTRAINT DF_pt_schedules_status DEFAULT 'ACTIVE',
    created_at          DATETIME2 NOT NULL CONSTRAINT DF_pt_schedules_created_at DEFAULT GETDATE(),
    FOREIGN KEY (pt_id) REFERENCES users(id),
    FOREIGN KEY (member_id) REFERENCES users(id),
    CONSTRAINT CK_pt_schedules_time CHECK (end_time > start_time),
    CONSTRAINT CK_pt_schedules_status CHECK (status IN ('ACTIVE', 'CANCELLED'))
);

-- ============================================================
-- 15. NOTIFICATIONS
-- ============================================================
CREATE TABLE notifications (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL,
    sender_id       INT,
    title           NVARCHAR(200) NOT NULL,
    message         NVARCHAR(MAX),
    is_read         BIT NOT NULL CONSTRAINT DF_notifications_is_read DEFAULT 0,
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_notifications_created_at DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- ============================================================
-- 16. OTPS
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
-- 17. AI CONVERSATIONS
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
-- 18. AI MESSAGES
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
CREATE INDEX IX_memberships_user_status ON memberships(user_id, status);
CREATE UNIQUE INDEX UX_memberships_one_current_per_user
    ON memberships(current_membership_user_id)
    WHERE current_membership_user_id IS NOT NULL;
CREATE INDEX IX_memberships_pt_status ON memberships(pt_id, status);
CREATE INDEX IX_memberships_status ON memberships(status);
CREATE INDEX IX_transactions_status ON transactions(status);
CREATE UNIQUE INDEX UX_transactions_one_pending_per_membership
    ON transactions(pending_membership_id)
    WHERE pending_membership_id IS NOT NULL;
CREATE INDEX IX_transactions_membership_created ON transactions(membership_id, created_at DESC);
CREATE INDEX IX_transactions_status_created ON transactions(status, created_at);
CREATE INDEX IX_notifications_user_created ON notifications(user_id, created_at DESC);
CREATE INDEX IX_notifications_user_read ON notifications(user_id, is_read);
CREATE INDEX IX_blogs_status ON blogs(status);
CREATE INDEX IX_diets_member_created ON diets(member_id, created_at DESC);
CREATE INDEX IX_diets_pt_member ON diets(pt_id, member_id);
CREATE INDEX IX_diets_date ON diets(diet_date);
CREATE INDEX IX_pt_notes_pt_member ON pt_notes(pt_id, member_id, created_at DESC);
CREATE INDEX IX_reviews_pt_created ON reviews(pt_id, created_at DESC);
CREATE INDEX IX_pt_schedules_pt_date ON pt_schedules(pt_id, schedule_date, start_time);
CREATE INDEX IX_pt_schedules_member_date ON pt_schedules(member_id, schedule_date);
CREATE INDEX IX_pt_schedules_recurring ON pt_schedules(recurring_group_id);
CREATE INDEX IX_otps_email_expiration ON otps(email, expiration_time DESC);
CREATE INDEX IX_ai_conversations_user_updated ON ai_conversations(user_id, updated_at DESC);
CREATE INDEX IX_ai_messages_conversation_created ON ai_messages(conversation_id, created_at ASC);

PRINT N'GymProDB schema created successfully.';
GO

