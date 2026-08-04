IF OBJECT_ID(N'dbo.training_plans', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.training_plans (
        id INT IDENTITY(1,1) PRIMARY KEY,
        pt_id INT NOT NULL,
        member_id INT NOT NULL,
        title NVARCHAR(150) NOT NULL,
        goal NVARCHAR(500) NULL,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,
        status NVARCHAR(20) NOT NULL CONSTRAINT DF_training_plans_status DEFAULT 'ACTIVE',
        created_at DATETIME2 NOT NULL CONSTRAINT DF_training_plans_created DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL CONSTRAINT DF_training_plans_updated DEFAULT GETDATE(),
        CONSTRAINT FK_training_plans_pt FOREIGN KEY (pt_id) REFERENCES dbo.users(id),
        CONSTRAINT FK_training_plans_member FOREIGN KEY (member_id) REFERENCES dbo.users(id),
        CONSTRAINT CK_training_plans_dates CHECK (end_date >= start_date)
    );
END;

IF OBJECT_ID(N'dbo.training_plan_exercises', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.training_plan_exercises (
        id INT IDENTITY(1,1) PRIMARY KEY,
        training_plan_id INT NOT NULL,
        exercise_id INT NOT NULL,
        day_label NVARCHAR(50) NOT NULL,
        target_sets INT NOT NULL,
        target_reps NVARCHAR(30) NOT NULL,
        target_weight_kg DECIMAL(7,2) NULL,
        note NVARCHAR(500) NULL,
        sort_order INT NOT NULL,
        completed BIT NOT NULL CONSTRAINT DF_training_plan_exercises_completed DEFAULT 0,
        actual_sets INT NULL,
        actual_reps NVARCHAR(30) NULL,
        actual_weight_kg DECIMAL(7,2) NULL,
        completed_at DATETIME2 NULL,
        CONSTRAINT FK_training_plan_exercises_plan FOREIGN KEY (training_plan_id)
            REFERENCES dbo.training_plans(id) ON DELETE CASCADE,
        CONSTRAINT FK_training_plan_exercises_exercise FOREIGN KEY (exercise_id)
            REFERENCES dbo.exercises(id),
        CONSTRAINT CK_training_plan_exercises_sets CHECK (target_sets > 0)
    );
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'IX_training_plans_pt_member' AND object_id = OBJECT_ID('dbo.training_plans')
)
    CREATE INDEX IX_training_plans_pt_member ON dbo.training_plans(pt_id, member_id);
