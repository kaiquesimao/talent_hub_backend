CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'DISABLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'DISABLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_login_at TIMESTAMPTZ
);

CREATE TABLE company_memberships (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role VARCHAR(40) NOT NULL CHECK (role IN ('COMPANY_OWNER', 'COMPANY_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE_VIEWER', 'EMPLOYEE_SELF_SERVICE')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'DISABLED')),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_company_memberships_company_user UNIQUE (company_id, user_id)
);

CREATE TABLE company_invites (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    invited_by_user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    country_id BIGINT NOT NULL REFERENCES countries (id),
    email VARCHAR(180) NOT NULL,
    full_name VARCHAR(180) NOT NULL,
    employee_role VARCHAR(120) NOT NULL,
    membership_role VARCHAR(40) NOT NULL CHECK (membership_role IN ('COMPANY_OWNER', 'COMPANY_ADMIN', 'HR_MANAGER', 'MANAGER', 'EMPLOYEE_VIEWER', 'EMPLOYEE_SELF_SERVICE')),
    token VARCHAR(120) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    accepted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE employees ADD COLUMN company_id BIGINT;
ALTER TABLE employees ADD COLUMN user_id BIGINT;

ALTER TABLE employees DROP CONSTRAINT IF EXISTS employees_email_key;
ALTER TABLE employees
    ADD CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE;
ALTER TABLE employees
    ADD CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE employees
    ADD CONSTRAINT uk_employees_company_email UNIQUE (company_id, email);
ALTER TABLE employees
    ADD CONSTRAINT uk_employees_company_user UNIQUE (company_id, user_id);

CREATE INDEX idx_company_memberships_user_id ON company_memberships (user_id);
CREATE INDEX idx_company_memberships_company_status ON company_memberships (company_id, status);
CREATE INDEX idx_company_invites_company_active ON company_invites (company_id, accepted_at, expires_at);
CREATE INDEX idx_employees_company_id ON employees (company_id);
CREATE INDEX idx_employees_user_id ON employees (user_id);
