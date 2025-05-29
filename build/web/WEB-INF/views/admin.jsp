<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spazone - Dashboard Admin</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #fde2e4 0%, #ffb3c6 100%);
            min-height: 100vh;
        }

        .dashboard-container {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 280px;
            background: linear-gradient(180deg, #ff6b9d 0%, #ff8fab 100%);
            color: white;
            box-shadow: 4px 0 15px rgba(255, 107, 157, 0.3);
            position: fixed;
            height: 100vh;
            overflow-y: auto;
            z-index: 1000;
        }

        .sidebar-header {
            padding: 30px 25px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            text-align: center;
        }

        .logo {
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 5px;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
        }

        .logo-subtitle {
            font-size: 14px;
            opacity: 0.9;
            font-weight: 300;
        }

        .nav-menu {
            padding: 20px 0;
        }

        .nav-item {
            margin: 5px 20px;
            border-radius: 12px;
            transition: all 0.3s ease;
        }

        .nav-item:hover {
            background: rgba(255, 255, 255, 0.15);
            transform: translateX(5px);
        }

        .nav-item.active {
            background: rgba(255, 255, 255, 0.2);
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: white;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .nav-link i {
            width: 20px;
            margin-right: 15px;
            font-size: 18px;
        }

        /* Main Content */
        .main-content {
            flex: 1;
            margin-left: 280px;
            padding: 30px;
        }

        .header {
            background: white;
            padding: 25px 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header-title {
            color: #ff6b9d;
            font-size: 32px;
            font-weight: 700;
        }

        .header-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 12px;
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            padding: 12px 20px;
            border-radius: 50px;
            color: white;
            font-weight: 500;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: white;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ff6b9d;
            font-size: 18px;
        }

        /* Stats Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }

        .stat-card {
            background: white;
            padding: 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            position: relative;
            overflow: hidden;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(255, 107, 157, 0.2);
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #ff6b9d, #ff8fab);
        }

        .stat-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .stat-title {
            color: #666;
            font-size: 16px;
            font-weight: 500;
        }

        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: white;
        }

        .stat-number {
            font-size: 36px;
            font-weight: 700;
            color: #ff6b9d;
            margin-bottom: 10px;
        }

        .stat-change {
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .stat-change.positive {
            color: #10b981;
        }

        .stat-change.negative {
            color: #ef4444;
        }

        /* Content Sections */
        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
            margin-bottom: 30px;
        }

        .content-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            overflow: hidden;
        }

        .card-header {
            padding: 25px 30px;
            border-bottom: 1px solid #f0f0f0;
            display: flex;
            justify-content: between;
            align-items: center;
        }

        .card-title {
            font-size: 20px;
            font-weight: 600;
            color: #ff6b9d;
        }

        .card-body {
            padding: 30px;
        }

        /* Table Styles */
        .table-container {
            overflow-x: auto;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
        }

        .data-table th {
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            font-size: 14px;
        }

        .data-table td {
            padding: 15px;
            border-bottom: 1px solid #f0f0f0;
            color: #666;
        }

        .data-table tr:hover {
            background: #fde2e4;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 500;
        }

        .status-active {
            background: #d1fae5;
            color: #065f46;
        }

        .status-inactive {
            background: #fee2e2;
            color: #991b1b;
        }

        /* Quick Actions */
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .action-btn {
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            color: white;
            padding: 20px;
            border-radius: 15px;
            text-decoration: none;
            text-align: center;
            font-weight: 600;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
        }

        .action-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(255, 107, 157, 0.3);
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .sidebar {
                width: 100%;
                height: auto;
                position: relative;
            }

            .main-content {
                margin-left: 0;
                padding: 20px;
            }

            .content-grid {
                grid-template-columns: 1fr;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .header {
                flex-direction: column;
                gap: 20px;
                text-align: center;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="sidebar-header">
                <div class="logo">Spazone</div>
                <div class="logo-subtitle">Admin Dashboard</div>
            </div>
            
            <nav class="nav-menu">
                <div class="nav-item active">
                    <a href="#" class="nav-link">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Dashboard</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-users"></i>
                        <span>Khách Hàng</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-concierge-bell"></i>
                        <span>Lễ Tân</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-user-md"></i>
                        <span>Kỹ Thuật Viên</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-building"></i>
                        <span>Chi Nhánh</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-user-tie"></i>
                        <span>Quản Lý Chi Nhánh</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-spa"></i>
                        <span>Dịch Vụ</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Lịch Hẹn</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-bar"></i>
                        <span>Báo Cáo</span>
                    </a>
                </div>
                <div class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cog"></i>
                        <span>Cài Đặt</span>
                    </a>
                </div>
            </nav>
        </div>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <div class="header">
                <h1 class="header-title">Dashboard Admin</h1>
                <div class="header-info">
                    <div class="user-info">
                        <div class="user-avatar">
                            <i class="fas fa-user"></i>
                        </div>
                        <span>Admin User</span>
                    </div>
                </div>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Tổng Chi Nhánh</div>
                        <div class="stat-icon" style="background: linear-gradient(45deg, #ff6b9d, #ff8fab);">
                            <i class="fas fa-building"></i>
                        </div>
                    </div>
                    <div class="stat-number">12</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        <span>+2 tháng này</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Tổng Nhân Viên</div>
                        <div class="stat-icon" style="background: linear-gradient(45deg, #ff8fab, #ffb3c6);">
                            <i class="fas fa-users"></i>
                        </div>
                    </div>
                    <div class="stat-number">286</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        <span>+15 tháng này</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Khách Hàng</div>
                        <div class="stat-icon" style="background: linear-gradient(45deg, #ffb3c6, #fde2e4);">
                            <i class="fas fa-user-friends"></i>
                        </div>
                    </div>
                    <div class="stat-number">2,547</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        <span>+124 tháng này</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Doanh Thu Tháng</div>
                        <div class="stat-icon" style="background: linear-gradient(45deg, #ff6b9d, #ffb3c6);">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                    </div>
                    <div class="stat-number">2.8B</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        <span>+12.5% so với tháng trước</span>
                    </div>
                </div>
            </div>

            <!-- Content Grid -->
            <div class="content-grid">
                <!-- Recent Activities -->
                <div class="content-card">
                    <div class="card-header">
                        <h3 class="card-title">Chi Nhánh Gần Đây</h3>
                    </div>
                    <div class="card-body">
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Chi Nhánh</th>
                                        <th>Địa Chỉ</th>
                                        <th>Quản Lý</th>
                                        <th>Trạng Thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Coco Spa Quận 1</td>
                                        <td>123 Nguyễn Huệ, Q.1</td>
                                        <td>Nguyễn Văn A</td>
                                        <td><span class="status-badge status-active">Hoạt động</span></td>
                                    </tr>
                                    <tr>
                                        <td>Coco Spa Quận 3</td>
                                        <td>456 Võ Văn Tần, Q.3</td>
                                        <td>Trần Thị B</td>
                                        <td><span class="status-badge status-active">Hoạt động</span></td>
                                    </tr>
                                    <tr>
                                        <td>Coco Spa Quận 7</td>
                                        <td>789 Nguyễn Thị Thảo, Q.7</td>
                                        <td>Lê Văn C</td>
                                        <td><span class="status-badge status-inactive">Bảo trì</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="content-card">
                    <div class="card-header">
                        <h3 class="card-title">Thao Tác Nhanh</h3>
                    </div>
                    <div class="card-body">
                        <div class="quick-actions">
                            <button class="action-btn">
                                <i class="fas fa-plus" style="margin-right: 8px;"></i>
                                Thêm Chi Nhánh
                            </button>
                            <button class="action-btn">
                                <i class="fas fa-user-plus" style="margin-right: 8px;"></i>
                                Thêm Nhân Viên
                            </button>
                            <button class="action-btn">
                                <i class="fas fa-chart-line" style="margin-right: 8px;"></i>
                                Xem Báo Cáo
                            </button>
                            <button class="action-btn">
                                <i class="fas fa-cogs" style="margin-right: 8px;"></i>
                                Cài Đặt Hệ Thống
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>