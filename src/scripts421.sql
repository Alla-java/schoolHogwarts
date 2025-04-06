ALTER TABLE student
ADD CONSTRAINT chk_age CHECK (age >= 16);

ALTER TABLE student
ADD CONSTRAINT uq_name UNIQUE (name),
ADD CONSTRAINT nn_name CHECK (name IS NOT NULL);

ALTER TABLE faculty
ADD CONSTRAINT uq_faculty_name_color UNIQUE (name, color);

ALTER TABLE student
MODIFY COLUMN age INT DEFAULT 20;



