IF COL_LENGTH('dbo.member_profiles', 'date_of_birth') IS NULL
    ALTER TABLE dbo.member_profiles ADD date_of_birth DATE NULL;
IF COL_LENGTH('dbo.member_profiles', 'biological_sex') IS NULL
    ALTER TABLE dbo.member_profiles ADD biological_sex VARCHAR(10) NULL;
IF COL_LENGTH('dbo.member_profiles', 'body_fat_source') IS NULL
    ALTER TABLE dbo.member_profiles ADD body_fat_source VARCHAR(10) NULL;
GO

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_biological_sex')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_biological_sex
        CHECK (biological_sex IS NULL OR biological_sex IN ('MALE', 'FEMALE'));
IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_member_profiles_body_fat_source')
    ALTER TABLE dbo.member_profiles ADD CONSTRAINT CK_member_profiles_body_fat_source
        CHECK (body_fat_source IS NULL OR body_fat_source IN ('MANUAL', 'ESTIMATED'));
