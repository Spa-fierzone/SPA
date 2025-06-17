USE studen;  -- Giữ nguyên database, bạn có thể đổi thành USE customerDB nếu muốn

-- Xóa bảng Customer nếu đã tồn tại
IF OBJECT_ID('Customer', 'U') IS NOT NULL
    DROP TABLE Customer;
GO

-- Tạo bảng Customer
CREATE TABLE Customer (
    ID INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(100) NOT NULL,
    Gender NVARCHAR(10) NOT NULL,
    DOB DATE NOT NULL
);
GO

-- Chèn dữ liệu mẫu
INSERT INTO Customer (Name, Gender, DOB)  
VALUES  
    ('Mr A', 'Female', '2016-12-02'),  
    ('Mr B', 'Female', '2016-12-09'),  
    ('Ms C', 'Male', '2016-12-07'),
    ('Mr D', 'Male', '2016-12-10'),
    ('Ms E', 'Female', '2016-12-15'),
    ('Mr F', 'Male', '2016-12-18'),
    ('Ms G', 'Female', '2016-12-22'),
    ('Mr H', 'Male', '2016-12-25'),
    ('Ms I', 'Female', '2016-12-28'),
    ('Mr J', 'Male', '2017-01-02'),
    ('Ms K', 'Female', '2017-01-05'),
    ('Mr L', 'Male', '2017-01-08'),
    ('Ms M', 'Female', '2017-01-12');
GO

-- Kiểm tra dữ liệu
SELECT * FROM Customer;
