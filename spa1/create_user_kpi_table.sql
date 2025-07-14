-- Script tạo bảng user_kpi cho chức năng quản lý KPI
-- Chạy script này trong database để tạo bảng user_kpi

CREATE TABLE user_kpi (
    kpi_id INT AUTO_INCREMENT PRIMARY KEY,
    technician_id INT NOT NULL,
    manager_id INT NOT NULL,
    month INT NOT NULL CHECK (month >= 1 AND month <= 12),
    year INT NOT NULL CHECK (year >= 2020),
    target_appointments INT NOT NULL CHECK (target_appointments > 0),
    actual_appointments INT DEFAULT 0 CHECK (actual_appointments >= 0),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_user_kpi_technician FOREIGN KEY (technician_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_kpi_manager FOREIGN KEY (manager_id) REFERENCES users(user_id) ON DELETE CASCADE,

    -- Unique constraint để đảm bảo mỗi technician chỉ có 1 KPI cho 1 tháng/năm
    CONSTRAINT uk_technician_month_year UNIQUE (technician_id, month, year),

    -- Index để tối ưu query
    INDEX idx_technician_date (technician_id, year, month),
    INDEX idx_manager_date (manager_id, year, month),
    INDEX idx_status (status)
);

-- Insert dữ liệu mẫu (tuỳ chọn)
-- Thay đổi các ID phù hợp với dữ liệu thực tế trong hệ thống

-- INSERT INTO user_kpi (technician_id, manager_id, month, year, target_appointments) VALUES
-- (2, 1, 7, 2025, 50),  -- Technician có ID 2, Manager có ID 1, tháng 7/2025, mục tiêu 50 lịch hẹn
-- (3, 1, 7, 2025, 45),  -- Technician có ID 3, Manager có ID 1, tháng 7/2025, mục tiêu 45 lịch hẹn
-- (4, 1, 7, 2025, 40);  -- Technician có ID 4, Manager có ID 1, tháng 7/2025, mục tiêu 40 lịch hẹn
