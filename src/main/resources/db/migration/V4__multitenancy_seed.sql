INSERT INTO companies (name, slug, status)
VALUES ('TalentHub Demo', 'talenthub-demo', 'ACTIVE')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO users (name, email, password_hash, status, created_at)
SELECT e.name,
       LOWER(e.email),
       '{noop}TalentHub@123',
       'ACTIVE',
       e.created_at
FROM employees e
ON CONFLICT (email) DO NOTHING;

UPDATE employees e
SET company_id = c.id,
    user_id = u.id,
    email = LOWER(e.email)
FROM companies c,
     users u
WHERE c.slug = 'talenthub-demo'
  AND u.email = LOWER(e.email)
  AND (e.company_id IS NULL OR e.user_id IS NULL);

ALTER TABLE employees ALTER COLUMN company_id SET NOT NULL;
ALTER TABLE employees ALTER COLUMN user_id SET NOT NULL;

INSERT INTO company_memberships (company_id, user_id, role, status, is_default, created_at)
SELECT c.id,
       u.id,
       CASE
           WHEN u.email = 'kaique.simao@talenthub.com' THEN 'COMPANY_OWNER'
           ELSE 'EMPLOYEE_SELF_SERVICE'
       END,
       'ACTIVE',
       TRUE,
       COALESCE(u.created_at, NOW())
FROM users u
CROSS JOIN companies c
WHERE c.slug = 'talenthub-demo'
ON CONFLICT (company_id, user_id) DO NOTHING;
