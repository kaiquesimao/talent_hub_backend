INSERT INTO countries (name, iso_code)
VALUES ('Brazil', 'BR'),
       ('United States', 'US'),
       ('Germany', 'DE'),
       ('India', 'IN')
ON CONFLICT (iso_code) DO NOTHING;

INSERT INTO skills (name, category)
VALUES ('Java', 'HARD'),
       ('Spring Boot', 'HARD'),
       ('Vue.js', 'HARD'),
       ('Leadership', 'SOFT'),
       ('Communication', 'SOFT'),
       ('English', 'LANGUAGE'),
       ('Portuguese', 'LANGUAGE')
ON CONFLICT (name) DO NOTHING;

INSERT INTO employees (name, email, role, country_id)
VALUES ('Ana Souza', 'ana.souza@talenthub.internal', 'Senior Software Engineer', (SELECT id FROM countries WHERE iso_code = 'BR')),
       ('Michael Carter', 'michael.carter@talenthub.internal', 'Engineering Manager', (SELECT id FROM countries WHERE iso_code = 'US')),
       ('Priya Sharma', 'priya.sharma@talenthub.internal', 'Product Manager', (SELECT id FROM countries WHERE iso_code = 'IN')),
       ('Lukas Weber', 'lukas.weber@talenthub.internal', 'Data Engineer', (SELECT id FROM countries WHERE iso_code = 'DE')),
       ('Kaique Simão', 'kaique.simao@talenthub.com', 'Software Engineer', (SELECT id FROM countries WHERE iso_code = 'BR'))
ON CONFLICT (email) DO NOTHING;

INSERT INTO employee_skills (employee_id, skill_id, proficiency_level, years_experience, validated_by)
VALUES
    ((SELECT id FROM employees WHERE email = 'ana.souza@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Java'), 'EXPERT', 9, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'ana.souza@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Spring Boot'), 'ADVANCED', 7, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'ana.souza@talenthub.internal'), (SELECT id FROM skills WHERE name = 'English'), 'ADVANCED', 10, 'language_team'),

    ((SELECT id FROM employees WHERE email = 'michael.carter@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Leadership'), 'EXPERT', 12, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'michael.carter@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Communication'), 'EXPERT', 12, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'michael.carter@talenthub.internal'), (SELECT id FROM skills WHERE name = 'English'), 'EXPERT', 14, 'language_team'),

    ((SELECT id FROM employees WHERE email = 'priya.sharma@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Leadership'), 'ADVANCED', 8, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'priya.sharma@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Communication'), 'ADVANCED', 8, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'priya.sharma@talenthub.internal'), (SELECT id FROM skills WHERE name = 'English'), 'ADVANCED', 9, 'language_team'),

    ((SELECT id FROM employees WHERE email = 'lukas.weber@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Java'), 'ADVANCED', 6, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'lukas.weber@talenthub.internal'), (SELECT id FROM skills WHERE name = 'Vue.js'), 'INTERMEDIATE', 3, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'lukas.weber@talenthub.internal'), (SELECT id FROM skills WHERE name = 'English'), 'INTERMEDIATE', 7, 'language_team'),
    
    ((SELECT id FROM employees WHERE email = 'kaique.simao@talenthub.com'), (SELECT id FROM skills WHERE name = 'Java'), 'ADVANCED', 4, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'kaique.simao@talenthub.com'), (SELECT id FROM skills WHERE name = 'Spring Boot'), 'ADVANCED', 3, 'global_hr'),
    ((SELECT id FROM employees WHERE email = 'kaique.simao@talenthub.com'), (SELECT id FROM skills WHERE name = 'English'), 'INTERMEDIATE', 5, 'language_team')
ON CONFLICT (employee_id, skill_id) DO NOTHING;
