<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Kỹ Thuật Viên - Spazone</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
            color: white;
            padding: 20px 0;
            box-shadow: 4px 0 15px rgba(255, 107, 157, 0.3);
        }

        .logo {
            text-align: center;
            padding: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            margin-bottom: 30px;
        }

        .logo h2 {
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .logo p {
            font-size: 14px;
            opacity: 0.8;
        }

        .nav-menu {
            list-style: none;
            padding: 0 20px;
        }

        .nav-item {
            margin-bottom: 5px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            padding: 15px 20px;
            color: white;
            text-decoration: none;
            border-radius: 12px;
            transition: all 0.3s ease;
            font-weight: 500;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(5px);
        }

        .nav-link i {
            margin-right: 12px;
            width: 20px;
            text-align: center;
        }

        /* Main Content */
        .main-content {
            flex: 1;
            padding: 30px;
            overflow-y: auto;
        }

        .header {
            background: white;
            padding: 25px 30px;
            border-radius: 20px;
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.15);
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: #ff6b9d;
            font-size: 32px;
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
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        .user-details h3 {
            color: #ff6b9d;
            font-size: 16px;
            margin-bottom: 2px;
        }

        .user-details p {
            color: #888;
            font-size: 14px;
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
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.15);
            text-align: center;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-icon {
            width: 70px;
            height: 70px;
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 30px;
            color: white;
        }

        .stat-number {
            font-size: 36px;
            font-weight: 700;
            color: #ff6b9d;
            margin-bottom: 10px;
        }

        .stat-label {
            color: #666;
            font-size: 16px;
            font-weight: 500;
        }

        /* Schedule Section */
        .schedule-section {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
            margin-bottom: 30px;
        }

        .schedule-card, .notifications-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.15);
        }

        .card-header {
            display: flex;
            align-items: center;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px solid #fde2e4;
        }

        .card-header h3 {
            color: #ff6b9d;
            font-size: 24px;
            font-weight: 600;
            margin-left: 15px;
        }

        .card-header i {
            color: #ff6b9d;
            font-size: 24px;
        }

        .appointment-item {
            display: flex;
            align-items: center;
            padding: 20px;
            margin-bottom: 15px;
            background: linear-gradient(135deg, #fde2e4, #ffb3c6);
            border-radius: 15px;
            border-left: 5px solid #ff6b9d;
        }

        .appointment-time {
            background: #ff6b9d;
            color: white;
            padding: 8px 15px;
            border-radius: 10px;
            font-weight: 600;
            margin-right: 20px;
            min-width: 80px;
            text-align: center;
        }

        .appointment-details h4 {
            color: #ff6b9d;
            font-size: 16px;
            margin-bottom: 5px;
        }

        .appointment-details p {
            color: #666;
            font-size: 14px;
        }

        .appointment-status {
            margin-left: auto;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-upcoming {
            background: #fff3cd;
            color: #856404;
        }

        .status-in-progress {
            background: #d4edda;
            color: #155724;
        }

        /* Notifications */
        .notification-item {
            display: flex;
            align-items: flex-start;
            padding: 15px;
            margin-bottom: 15px;
            background: #fde2e4;
            border-radius: 12px;
            border-left: 4px solid #ff8fab;
        }

        .notification-icon {
            width: 40px;
            height: 40px;
            background: #ff8fab;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            margin-right: 15px;
            flex-shrink: 0;
        }

        .notification-content h5 {
            color: #ff6b9d;
            font-size: 14px;
            margin-bottom: 5px;
        }

        .notification-content p {
            color: #666;
            font-size: 13px;
            line-height: 1.4;
        }

        .notification-time {
            color: #888;
            font-size: 12px;
            margin-top: 5px;
        }

        /* Services Section */
        .services-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 25px;
        }

        .service-card {
            background: white;
            border-radius: 20px;
            padding: 25px;
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.15);
            transition: transform 0.3s ease;
        }

        .service-card:hover {
            transform: translateY(-3px);
        }

        .service-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }

        .service-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(45deg, #ff8fab, #ffb3c6);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 20px;
            color: white;
        }

        .service-info h4 {
            color: #ff6b9d;
            font-size: 18px;
            margin-bottom: 5px;
        }

        .service-info p {
            color: #888;
            font-size: 14px;
        }

        .service-stats {
            display: flex;
            justify-content: space-between;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #fde2e4;
        }

        .service-stat {
            text-align: center;
        }

        .service-stat .number {
            font-size: 20px;
            font-weight: 700;
            color: #ff6b9d;
        }

        .service-stat .label {
            font-size: 12px;
            color: #888;
            margin-top: 2px;
        }

        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }
            
            .sidebar {
                width: 100%;
                height: auto;
            }
            
            .schedule-section {
                grid-template-columns: 1fr;
            }
            
            .main-content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="logo">
                <h2>Spazone</h2>
                <p>Kỹ Thuật Viên</p>
            </div>
            
            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="#" class="nav-link active">
                        <i class="fas fa-tachometer-alt"></i>
                        Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-calendar-alt"></i>
                        Lịch Làm Việc
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-user-friends"></i>
                        Khách Hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-spa"></i>
                        Dịch Vụ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-bar"></i>
                        Thống Kê
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-star"></i>
                        Đánh Giá
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cog"></i>
                        Cài Đặt
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-sign-out-alt"></i>
                        Đăng Xuất
                    </a>
                </li>
            </ul>
        </div>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <div class="header">
                <h1>Dashboard Kỹ Thuật Viên</h1>
                <div class="user-info">
                    <div class="user-details">
                        <h3>Nguyễn Thị Lan Anh</h3>
                        <p>Kỹ thuật viên massage</p>
                    </div>
                    <div class="user-avatar">LA</div>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-number">8</div>
                    <div class="stat-label">Lịch Hẹn Hôm Nay</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-number">6.5h</div>
                    <div class="stat-label">Giờ Làm Việc</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="stat-number">12</div>
                    <div class="stat-label">Khách Đã Phục Vụ</div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-star"></i>
                    </div>
                    <div class="stat-number">4.9</div>
                    <div class="stat-label">Điểm Đánh Giá</div>
                </div>
            </div>

            <!-- Schedule and Notifications -->
            <div class="schedule-section">
                <div class="schedule-card">
                    <div class="card-header">
                        <i class="fas fa-calendar-alt"></i>
                        <h3>Lịch Hẹn Hôm Nay</h3>
                    </div>
                    
                    <div class="appointment-item">
                        <div class="appointment-time">09:00</div>
                        <div class="appointment-details">
                            <h4>Massage Body - Chị Nguyễn Thị Mai</h4>
                            <p>Phòng VIP 1 • Thời gian: 90 phút</p>
                        </div>
                        <span class="appointment-status status-in-progress">Đang thực hiện</span>
                    </div>
                    
                    <div class="appointment-item">
                        <div class="appointment-time">11:00</div>
                        <div class="appointment-details">
                            <h4>Chăm sóc da mặt - Cô Trần Lan Phương</h4>
                            <p>Phòng 3 • Thời gian: 60 phút</p>
                        </div>
                        <span class="appointment-status status-upcoming">Sắp tới</span>
                    </div>
                    
                    <div class="appointment-item">
                        <div class="appointment-time">14:00</div>
                        <div class="appointment-details">
                            <h4>Massage foot - Anh Lê Văn Nam</h4>
                            <p>Phòng 5 • Thời gian: 45 phút</p>
                        </div>
                        <span class="appointment-status status-upcoming">Sắp tới</span>
                    </div>
                    
                    <div class="appointment-item">
                        <div class="appointment-time">15:30</div>
                        <div class="appointment-details">
                            <h4>Tắm trắng - Chị Phạm Thu Hà</h4>
                            <p>Phòng VIP 2 • Thời gian: 120 phút</p>
                        </div>
                        <span class="appointment-status status-upcoming">Sắp tới</span>
                    </div>
                </div>
                
                <div class="notifications-card">
                    <div class="card-header">
                        <i class="fas fa-bell"></i>
                        <h3>Thông Báo</h3>
                    </div>
                    
                    <div class="notification-item">
                        <div class="notification-icon">
                            <i class="fas fa-exclamation"></i>
                        </div>
                        <div class="notification-content">
                            <h5>Khách hàng thay đổi lịch</h5>
                            <p>Chị Mai đã dời lịch từ 10:00 sang 11:00</p>
                            <div class="notification-time">5 phút trước</div>
                        </div>
                    </div>
                    
                    <div class="notification-item">
                        <div class="notification-icon">
                            <i class="fas fa-gift"></i>
                        </div>
                        <div class="notification-content">
                            <h5>Khuyến mãi mới</h5>
                            <p>Gói combo massage + chăm sóc da giảm 20%</p>
                            <div class="notification-time">1 giờ trước</div>
                        </div>
                    </div>
                    
                    <div class="notification-item">
                        <div class="notification-icon">
                            <i class="fas fa-star"></i>
                        </div>
                        <div class="notification-content">
                            <h5>Đánh giá mới</h5>
                            <p>Bạn nhận được đánh giá 5 sao từ khách hàng</p>
                            <div class="notification-time">2 giờ trước</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Services -->
            <div class="services-grid">
                <div class="service-card">
                    <div class="service-header">
                        <div class="service-icon">
                            <i class="fas fa-hand-paper"></i>
                        </div>
                        <div class="service-info">
                            <h4>Massage Body</h4>
                            <p>Dịch vụ chính của bạn</p>
                        </div>
                    </div>
                    <div class="service-stats">
                        <div class="service-stat">
                            <div class="number">45</div>
                            <div class="label">Tuần này</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">4.9</div>
                            <div class="label">Đánh giá</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">180</div>
                            <div class="label">Tháng này</div>
                        </div>
                    </div>
                </div>
                
                <div class="service-card">
                    <div class="service-header">
                        <div class="service-icon">
                            <i class="fas fa-leaf"></i>
                        </div>
                        <div class="service-info">
                            <h4>Chăm Sóc Da</h4>
                            <p>Facial & Skincare</p>
                        </div>
                    </div>
                    <div class="service-stats">
                        <div class="service-stat">
                            <div class="number">23</div>
                            <div class="label">Tuần này</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">4.8</div>
                            <div class="label">Đánh giá</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">95</div>
                            <div class="label">Tháng này</div>
                        </div>
                    </div>
                </div>
                
                <div class="service-card">
                    <div class="service-header">
                        <div class="service-icon">
                            <i class="fas fa-spa"></i>
                        </div>
                        <div class="service-info">
                            <h4>Tắm Trắng</h4>
                            <p>Whitening Treatment</p>
                        </div>
                    </div>
                    <div class="service-stats">
                        <div class="service-stat">
                            <div class="number">12</div>
                            <div class="label">Tuần này</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">4.7</div>
                            <div class="label">Đánh giá</div>
                        </div>
                        <div class="service-stat">
                            <div class="number">48</div>
                            <div class="label">Tháng này</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>