IF OBJECT_ID(N'dbo.training_plan_exercises', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.training_plan_exercises;
END;

IF OBJECT_ID(N'dbo.training_plans', N'U') IS NOT NULL
BEGIN
    DROP TABLE dbo.training_plans;
END;
