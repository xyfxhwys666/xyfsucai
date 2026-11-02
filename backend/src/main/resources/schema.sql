CREATE TABLE IF NOT EXISTS templates (
  id VARCHAR(64) PRIMARY KEY, title VARCHAR(120) NOT NULL, category VARCHAR(80) NOT NULL,
  cover_url VARCHAR(255) NOT NULL, description VARCHAR(500) NOT NULL, required_image_count INT NOT NULL,
  accepted_mime_types VARCHAR(255) NOT NULL, max_file_size_bytes BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS tasks (
  id CHAR(36) PRIMARY KEY, user_id VARCHAR(80) NOT NULL, template_id VARCHAR(64) NOT NULL,
  status VARCHAR(24) NOT NULL, stage VARCHAR(32) NOT NULL, error_message VARCHAR(500),
  result_url VARCHAR(255), created_at TIMESTAMP(6) NOT NULL, updated_at TIMESTAMP(6) NOT NULL
);
CREATE TABLE IF NOT EXISTS task_assets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, task_id CHAR(36) NOT NULL, file_url VARCHAR(255) NOT NULL,
  original_name VARCHAR(255) NOT NULL, mime_type VARCHAR(100) NOT NULL,
  CONSTRAINT fk_asset_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);
