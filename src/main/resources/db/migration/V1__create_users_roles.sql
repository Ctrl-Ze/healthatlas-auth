CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description TEXT
);

CREATE TABLE users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  deleted_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE UNIQUE INDEX ux_users_username_not_deleted ON users (lower(username)) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX ux_users_email_not_deleted ON users (lower(email)) WHERE deleted_at IS NULL;


CREATE TABLE user_roles (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role_id INT NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
  PRIMARY KEY (user_id, role_id)
);

-- trigger to auto update updated_at
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION trigger_set_updated_at();

-- Seed default roles with descriptions
INSERT INTO roles (name, description) VALUES
('USER', 'Default role for regular users, with basic access to the system.'),
('DOCTOR', 'Role for doctors, granting access to patient data and medical functionalities.'),
('ADMIN', 'Administrator role with full access, including user and system management.');
