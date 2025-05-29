<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Lễ Tân - Spazone</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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

        .container {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar */
        .sidebar {
            width: 280px;
            background: linear-gradient(180deg, #ff6b9d 0%, #ff8fab 100%);
            box-shadow: 4px 0 20px rgba(255, 107, 157, 0.3);
            position: fixed;
            height: 100vh;
            overflow-y: auto;
        }

        .logo {
            padding: 30px 25px;
            text-align: center;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        }

        .logo h1 {
            color: white;
            font-size: 28px;
            font-weight: 700;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        }

        .logo p {
            color: rgba(255, 255, 255, 0.9);
            font-size: 14px;
            margin-top: 5px;
        }

        .nav-menu {
            padding: 20px 0;
        }

        .nav-item {
            margin: 5px 15px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: white;
            text-decoration: none;
            border-radius: 12px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(5px);
            backdrop-filter: blur(10px);
        }

        .nav-link i {
            font-size: 18px;
            margin-right: 15px;
            width: 20px;
        }

        /* Main Content */
        .main-content {
            margin-left: 280px;
            flex: 1;
            padding: 30px;
            background: transparent;
        }

        .header {
            background: white;
            padding: 25px 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
        }

        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .welcome {
            color: #ff6b9d;
            font-size: 28px;
            font-weight: 700;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        /* Stats Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            padding: 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            position: relative;
            overflow: hidden;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
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
            margin-bottom: 20px;
        }

        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: white;
        }

        .icon-customers { background: linear-gradient(45deg, #ff6b9d, #ff8fab); }
        .icon-appointments { background: linear-gradient(45deg, #ff8fab, #ffb3c6); }
        .icon-services { background: linear-gradient(45deg, #ffb3c6, #fde2e4); color: #ff6b9d; }
        .icon-revenue { background: linear-gradient(45deg, #ff6b9d, #ffb3c6); }

        .stat-number {
            font-size: 36px;
            font-weight: 700;
            color: #ff6b9d;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #666;
            font-size: 16px;
            font-weight: 500;
        }

        /* Quick Actions */
        .quick-actions {
            background: white;
            padding: 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
            margin-bottom: 30px;
        }

        .section-title {
            color: #ff6b9d;
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .actions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .action-btn {
            background: linear-gradient(135deg, #ff6b9d, #ff8fab);
            color: white;
            padding: 20px;
            border-radius: 15px;
            text-decoration: none;
            text-align: center;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
        }

        .action-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.4);
            text-decoration: none;
            color: white;
        }

        .action-btn i {
            font-size: 24px;
            margin-bottom: 10px;
            display: block;
        }

        /* Recent Activities */
        .recent-activities {
            background: white;
            padding: 30px;
            border-radius: 20px;
            box-shadow: 0 8px 32px rgba(255, 107, 157, 0.1);
        }

        .activity-list {
            max-height: 400px;
            overflow-y: auto;
        }

        .activity-item {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #f0f0f0;
            transition: background-color 0.3s ease;
        }

        .activity-item:hover {
            background-color: #fde2e4;
            border-radius: 10px;
            padding-left: 15px;
            padding-right: 15px;
        }

        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 16px;
            color: white;
        }

        .activity-content {
            flex: 1;
        }

        .activity-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .activity-time {
            color: #999;
            font-size: 14px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .sidebar {
                width: 70px;
            }

            .main-content {
                margin-left: 70px;
                padding: 20px;
            }

            .nav-link span {
                display: none;
            }

            .logo h1, .logo p {
                display: none;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .actions-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        /* Animation */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .stat-card, .quick-actions, .recent-activities {
            animation: fadeInUp 0.6s ease-out;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Sidebar -->
        <nav class="sidebar">
            <div class="logo">
                <h1>Spazone</h1>
                <p>Hệ thống quản lý</p>
            </div>
            
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="#" class="nav-link active">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-calendar-check"></i>
                        <span>Đặt lịch</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-users"></i>
                        <span>Khách hàng</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-spa"></i>
                        <span>Dịch vụ</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-user-tie"></i>
                        <span>Kỹ thuật viên</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-receipt"></i>
                        <span>Hóa đơn</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-gift"></i>
                        <span>Khuyến mãi</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-bar"></i>
                        <span>Báo cáo</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cog"></i>
                        <span>Cài đặt</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Đăng xuất</span>
                    </a>
                </li>
            </ul>
        </nav>

        <!-- Main Content -->
        <main class="main-content">
            <!-- Header -->
            <header class="header">
                <div class="header-content">
                    <div>
                        <h1 class="welcome">Chào mừng, Lễ tân!</h1>
                        <p style="color: #666; margin-top: 5px;">Hôm nay là ${pageContext.request.getAttribute('currentDate')}</p>
                    </div>
                    <div class="user-info">
                        <div class="user-avatar">
                            <i class="fas fa-user"></i>
                        </div>
                        <div>
                            <div style="font-weight: 600; color: #333;">Nguyễn Thị Mai</div>
                            <div style="color: #999; font-size: 14px;">Lễ tân</div>
                        </div>
                    </div>
                </div>
            </header>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-header">
                        <div>
                            <div class="stat-number">128</div>
                            <div class="stat-label">Khách hàng hôm nay</div>
                        </div>
                        <div class="stat-icon icon-customers">
                            <i class="fas fa-users"></i>
                        </div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div>
                            <div class="stat-number">47</div>
                            <div class="stat-label">Lịch hẹn hôm nay</div>
                        </div>
                        <div class="stat-icon icon-appointments">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div>
                            <div class="stat-number">15</div>
                            <div class="stat-label">Dịch vụ đang thực hiện</div>
                        </div>
                        <div class="stat-icon icon-services">
                            <i class="fas fa-spa"></i>
                        </div>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div>
                            <div class="stat-number">24.5M</div>
                            <div class="stat-label">Doanh thu hôm nay</div>
                        </div>
                        <div class="stat-icon icon-revenue">
                            <i class="fas fa-chart-line"></i>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="quick-actions">
                <h2 class="section-title">
                    <i class="fas fa-bolt"></i>
                    Thao tác nhanh
                </h2>
                <div class="actions-grid">
                    <a href="#" class="action-btn">
                        <i class="fas fa-plus"></i>
                        Đặt lịch mới
                    </a>
                    <a href="#" class="action-btn">
                        <i class="fas fa-user-plus"></i>
                        Thêm khách hàng
                    </a>
                    <a href="#" class="action-btn">
                        <i class="fas fa-search"></i>
                        Tìm kiếm lịch hẹn
                    </a>
                    <a href="#" class="action-btn">
                        <i class="fas fa-receipt"></i>
                        Tạo hóa đơn
                    </a>
                </div>
            </div>

            <!-- Recent Activities -->
            <div class="recent-activities">
                <h2 class="section-title">
                    <i class="fas fa-clock"></i>
                    Hoạt động gần đây
                </h2>
                <div class="activity-list">
                    <div class="activity-item">
                        <div class="activity-icon" style="background: linear-gradient(45deg, #ff6b9d, #ff8fab);">
                            <i class="fas fa-user-plus"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Khách hàng mới: Nguyễn Thị Lan đã đăng ký</div>
                            <div class="activity-time">5 phút trước</div>
                        </div>
                    </div>

                    <div class="activity-item">
                        <div class="activity-icon" style="background: linear-gradient(45deg, #ff8fab, #ffb3c6);">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Lịch hẹn mới: Massage thư giãn - 14:30</div>
                            <div class="activity-time">10 phút trước</div>
                        </div>
                    </div>

                    <div class="activity-item">
                        <div class="activity-icon" style="background: linear-gradient(45deg, #ffb3c6, #fde2e4); color: #ff6b9d;">
                            <i class="fas fa-spa"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Hoàn thành dịch vụ: Chăm sóc da mặt cho Ms. Hoa</div>
                            <div class="activity-time">15 phút trước</div>
                        </div>
                    </div>

                    <div class="activity-item">
                        <div class="activity-icon" style="background: linear-gradient(45deg, #ff6b9d, #ffb3c6);">
                            <i class="fas fa-money-bill-wave"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Thanh toán thành công: 1.200.000 VND</div>
                            <div class="activity-time">20 phút trước</div>
                        </div>
                    </div>

                    <div class="activity-item">
                        <div class="activity-icon" style="background: linear-gradient(45deg, #ff8fab, #ffb3c6);">
                            <i class="fas fa-phone"></i>
                        </div>
                        <div class="activity-content">
                            <div class="activity-title">Cuộc gọi nhỡ từ: 0901234567</div>
                            <div class="activity-time">25 phút trước</div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script>
        // Simple interaction for demo
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
                this.classList.add('active');
            });
        });

        // Update time
        function updateTime() {
            const now = new Date();
            const options = { 
                weekday: 'long', 
                year: 'numeric', 
                month: 'long', 
                day: 'numeric' 
            };
            const dateStr = now.toLocaleDateString('vi-VN', options);
            const timeElements = document.querySelectorAll('.current-date');
            timeElements.forEach(el => el.textContent = dateStr);
        }

        updateTime();
        setInterval(updateTime, 60000);
    </script>
</body>
</html>