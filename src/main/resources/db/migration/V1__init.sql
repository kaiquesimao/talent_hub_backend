CREATE TABLE countries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    iso_code VARCHAR(2) NOT NULL UNIQUE
);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    role VARCHAR(120) NOT NULL,
    country_id BIGINT NOT NULL REFERENCES countries (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(20) NOT NULL CHECK (category IN ('HARD', 'SOFT', 'LANGUAGE'))
);

CREATE TABLE employee_skills (
    employee_id BIGINT NOT NULL REFERENCES employees (id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills (id) ON DELETE CASCADE,
    proficiency_level VARCHAR(20) NOT NULL CHECK (proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    years_experience INT NOT NULL CHECK (years_experience >= 0),
    validated_by VARCHAR(120),
    PRIMARY KEY (employee_id, skill_id)
);

CREATE INDEX idx_employees_country_id ON employees (country_id);
CREATE INDEX idx_employee_skills_skill_id ON employee_skills (skill_id);
CREATE INDEX idx_employee_skills_level ON employee_skills (proficiency_level);
