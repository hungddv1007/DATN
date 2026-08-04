IF OBJECT_ID(N'dbo.member_profiles', N'U') IS NOT NULL
   AND COL_LENGTH('dbo.member_profiles', 'physical_condition') IS NOT NULL
   AND COL_LENGTH('dbo.member_profiles', 'medical_conditions') IS NULL
    EXEC sp_rename 'dbo.member_profiles.physical_condition', 'medical_conditions', 'COLUMN';
GO

IF OBJECT_ID(N'dbo.member_profiles', N'U') IS NOT NULL
   AND COL_LENGTH('dbo.member_profiles', 'height_cm') IS NULL
    ALTER TABLE dbo.member_profiles ADD height_cm DECIMAL(5,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'weight_kg') IS NULL
    ALTER TABLE dbo.member_profiles ADD weight_kg DECIMAL(6,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'chest_cm') IS NULL
    ALTER TABLE dbo.member_profiles ADD chest_cm DECIMAL(5,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'waist_cm') IS NULL
    ALTER TABLE dbo.member_profiles ADD waist_cm DECIMAL(5,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'hip_cm') IS NULL
    ALTER TABLE dbo.member_profiles ADD hip_cm DECIMAL(5,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'body_fat_percentage') IS NULL
    ALTER TABLE dbo.member_profiles ADD body_fat_percentage DECIMAL(5,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'activity_level') IS NULL
    ALTER TABLE dbo.member_profiles ADD activity_level VARCHAR(30) NULL;
IF COL_LENGTH('dbo.member_profiles', 'fitness_goal') IS NULL
    ALTER TABLE dbo.member_profiles ADD fitness_goal VARCHAR(30) NULL;
IF COL_LENGTH('dbo.member_profiles', 'target_weight_kg') IS NULL
    ALTER TABLE dbo.member_profiles ADD target_weight_kg DECIMAL(6,2) NULL;
IF COL_LENGTH('dbo.member_profiles', 'training_experience') IS NULL
    ALTER TABLE dbo.member_profiles ADD training_experience NVARCHAR(500) NULL;
IF COL_LENGTH('dbo.member_profiles', 'injury_history') IS NULL
    ALTER TABLE dbo.member_profiles ADD injury_history NVARCHAR(2000) NULL;
IF COL_LENGTH('dbo.member_profiles', 'medical_conditions') IS NULL
    ALTER TABLE dbo.member_profiles ADD medical_conditions NVARCHAR(2000) NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_height')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_height
        CHECK (height_cm IS NULL OR height_cm BETWEEN 50 AND 300);
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_weight')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_weight
        CHECK (weight_kg IS NULL OR weight_kg BETWEEN 20 AND 500);
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_measurements')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_measurements
        CHECK ((chest_cm IS NULL OR chest_cm BETWEEN 20 AND 300)
            AND (waist_cm IS NULL OR waist_cm BETWEEN 20 AND 300)
            AND (hip_cm IS NULL OR hip_cm BETWEEN 20 AND 300));
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_body_fat')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_body_fat
        CHECK (body_fat_percentage IS NULL OR body_fat_percentage BETWEEN 0 AND 100);
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_target_weight')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_target_weight
        CHECK (target_weight_kg IS NULL OR target_weight_kg BETWEEN 20 AND 500);
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_activity_level')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_activity_level
        CHECK (activity_level IS NULL OR activity_level IN ('SEDENTARY', 'LIGHT', 'MODERATE', 'HIGH', 'VERY_HIGH'));
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_fitness_goal')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_fitness_goal
        CHECK (fitness_goal IS NULL OR fitness_goal IN ('WEIGHT_LOSS', 'MUSCLE_GAIN', 'MAINTENANCE', 'HEALTH_IMPROVEMENT'));
