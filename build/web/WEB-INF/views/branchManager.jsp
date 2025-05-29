<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Quản Lý Chi Nhánh - Spazone</title>
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

        .dashboard-container {
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
            position: fixed;
            height: 100vh;
            overflow-y: auto;
        }

        .logo {
            text-align: center;
            padding: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            margin-bottom: 30px;
        }

        .logo h1 {
            font-size: 28px;
            font-weight: bold;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .logo p {
            font-size: 14px;
            opacity: 0.9;
            margin-top: 5px;
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
            position: relative;
            overflow: hidden;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(5px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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
            border: 1px solid rgba(255, 107, 157, 0.1);
        }

        .header-top {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .header h2 {
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

        .branch-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .info-item {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .info-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(45deg, #ffb3c6, #fde2e4);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ff6b9d;
            font-size: 20px;
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
            border: 1px solid rgba(255, 107, 157, 0.1);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #ff6b9d, #ff8fab, #ffb3c6);
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 40px rgba(255, 107, 157, 0.2);
        }

        .stat-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .stat-title {
            color: #666;
            font-size: 16px;
            font-weight: 500;
        }

        .stat-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            border-radius: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
        }

        .stat-value {
            font-size: 36px;
            font-weight: 700;
            color: #ff6b9d;
            margin-bottom: 10px;
        }

        .stat-change {
            display: flex;
            align-items: center;
            gap: 5px;
            font-size: 14px;
        }

        .stat-change.positive {
            color: #10b981;
        }

        .stat-change.negative {
            color: #ef4444;
        }

        /* Content Grid */
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
            border: 1px solid rgba(255, 107, 157, 0.1);
            overflow: hidden;
        }

        .card-header {
            padding: 25px 30px;
            border-bottom: 1px solid rgba(255, 107, 157, 0.1);
            background: linear-gradient(135deg, #fde2e4, #ffb3c6);
        }

        .card-title {
            color: #ff6b9d;
            font-size: 20px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .card-content {
            padding: 30px;
        }

        /* Tables */
        .table-container {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid rgba(255, 107, 157, 0.1);
        }

        th {
            background: linear-gradient(135deg, #fde2e4, #ffb3c6);
            color: #ff6b9d;
            font-weight: 600;
        }

        tr:hover {
            background: rgba(255, 107, 157, 0.05);
        }

        /* Quick Actions */
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .action-card {
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 20px rgba(255, 107, 157, 0.1);
            border: 1px solid rgba(255, 107, 157, 0.1);
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .action-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 30px rgba(255, 107, 157, 0.2);
        }

        .action-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(45deg, #ff6b9d, #ff8fab);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 32px;
            margin: 0 auto 20px;
        }

        .action-title {
            color: #ff6b9d;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 10px;
        }

        .action-desc {
            color: #666;
            font-size: 14px;
        }

        /* Responsive */
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
            
            .header h2 {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
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
                        Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-users"></i>
                        Quản lý nhân viên
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-user-friends"></i>
                        Khách hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-calendar-alt"></i>
                        Lịch hẹn
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-spa"></i>
                        Dịch vụ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-line"></i>
                        Báo cáo doanh thu
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-warehouse"></i>
                        Kho hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cog"></i>
                        Cài đặt
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-sign-out-alt"></i>
                        đăng xuất
                    </a>
                </li>
            </ul>
        </nav>

        <!-- Main Content -->
        <main class="main-content">
            <!-- Header -->
            <div class="header">
                <div class="header-top">
                    <h2>Dashboard Quản Lý Chi Nhánh</h2>
                    <div class="user-info">
                        <div class="user-avatar">QT</div>
                        <div>
                            <div style="font-weight: 600; color: #ff6b9d;">Qu?n lý Tu?n</div>
                            <div style="font-size: 14px; color: #666;">Chi nhánh Qu?n 1</div>
                        </div>
                    </div>
                </div>
                
                <div class="branch-info">
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-map-marker-alt"></i>
                        </div>
                        <div>
                            <div style="font-weight: 600; color: #ff6b9d;">Chi nhánh Qu?n 1</div>
                            <div style="font-size: 14px; color: #666;">123 Nguy?n Hu?, Q1, TP.HCM</div>
                        </div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div>
                            <div style="font-weight: 600; color: #ff6b9d;">Gi? ho?t ??ng</div>
                            <div style="font-size: 14px; color: #666;">8:00 - 22:00 hàng ngày</div>
                        </div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-icon">
                            <i class="fas fa-phone"></i>
                        </div>
                        <div>
                            <div style="font-weight: 600; color: #ff6b9d;">Liên h?</div>
                            <div style="font-size: 14px; color: #666;">(028) 1234 5678</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Doanh thu hôm nay</div>
                        <div class="stat-icon">
                            <i class="fas fa-money-bill-wave"></i>
                        </div>
                    </div>
                    <div class="stat-value">15.2M</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        +12.5% so với hôm qua
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Khách hàng hôm nay</div>
                        <div class="stat-icon">
                            <i class="fas fa-users"></i>
                        </div>
                    </div>
                    <div class="stat-value">42</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        +8 khách so với hôm qua
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Nhân viên đang làm</div>
                        <div class="stat-icon">
                            <i class="fas fa-user-check"></i>
                        </div>
                    </div>
                    <div class="stat-value">18/25</div>
                    <div class="stat-change">
                        <i class="fas fa-info-circle"></i>
                        7 nhân viên nghỉ phép
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-header">
                        <div class="stat-title">Lịch hẹn hôm nay</div>
                        <div class="stat-icon">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                    </div>
                    <div class="stat-value">56</div>
                    <div class="stat-change positive">
                        <i class="fas fa-arrow-up"></i>
                        +5 lịch hẹn mới
                    </div>
                </div>
            </div>

            <!-- Content Grid -->
            <div class="content-grid">
                <!-- Recent Appointments -->
                <div class="content-card">
                    <div class="card-header">
                        <div class="card-title">
                            <i class="fas fa-calendar-alt"></i>
                            Lịch hẹn gần đây
                        </div>
                    </div>
                    <div class="card-content">
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Khách hàng</th>
                                        <th>Dịch vụ</th>
                                        <th>Thời gian</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Nguy?n Th? Lan</td>
                                        <td>Massage th? giãn</td>
                                        <td>14:30</td>
                                        <td><span style="background: #10b981; color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px;">Hoàn thành</span></td>
                                    </tr>
                                    <tr>
                                        <td>Tr?n V?n Nam</td>
                                        <td>Ch?m sóc da m?t</td>
                                        <td>15:00</td>
                                        <td><span style="background: #f59e0b; color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px;">?ang th?c hi?n</span></td>
                                    </tr>
                                    <tr>
                                        <td>Lê Th? Hoa</td>
                                        <td>G?i ??u d??ng sinh</td>
                                        <td>15:30</td>
                                        <td><span style="background: #6b7280; color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px;">Ch? x? lý</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Staff Status -->
                <div class="content-card">
                    <div class="card-header">
                        <div class="card-title">
                            <i class="fas fa-users-cog"></i>
                            Trạng thái nhân viên
                        </div>
                    </div>
                    <div class="card-content">
                        <div style="margin-bottom: 20px;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <span>Lễ tân</span>
                                <span style="color: #ff6b9d; font-weight: 600;">3/4</span>
                            </div>
                            <div style="background: #fde2e4; height: 8px; border-radius: 4px;">
                                <div style="background: #ff6b9d; height: 100%; width: 75%; border-radius: 4px;"></div>
                            </div>
                        </div>
                        
                        <div style="margin-bottom: 20px;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <span>Kĩ thuật viên</span>
                                <span style="color: #ff6b9d; font-weight: 600;">12/15</span>
                            </div>
                            <div style="background: #fde2e4; height: 8px; border-radius: 4px;">
                                <div style="background: #ff8fab; height: 100%; width: 80%; border-radius: 4px;"></div>
                            </div>
                        </div>
                        
                        <div style="margin-bottom: 20px;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <span>Nhân viên dọn dẹp</span>
                                <span style="color: #ff6b9d; font-weight: 600;">3/6</span>
                            </div>
                            <div style="background: #fde2e4; height: 8px; border-radius: 4px;">
                                <div style="background: #ffb3c6; height: 100%; width: 50%; border-radius: 4px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="content-card">
                <div class="card-header">
                    <div class="card-title">
                        <i class="fas fa-bolt"></i>
                        Thao tác nhanh
                    </div>
                </div>
                <div class="card-content">
                    <div class="quick-actions">
                        <div class="action-card">
                            <div class="action-icon">
                                <i class="fas fa-plus"></i>
                            </div>
                            <div class="action-title">Thêm l?ch h?n</div>
                            <div class="action-desc">Tạo lịch hẹn mới cho khách hàng</div>
                        </div>
                        
                        <div class="action-card">
                            <div class="action-icon">
                                <i class="fas fa-user-plus"></i>
                            </div>
                            <div class="action-title">Thêm khách hàng</div>
                            <div class="action-desc">đăng ký thông tin khách hàng mới</div>
                        </div>
                        
                        <div class="action-card">
                            <div class="action-icon">
                                <i class="fas fa-chart-bar"></i>
                            </div>
                            <div class="action-title">Xem báo cáo</div>
                            <div class="action-desc">Thống kê doanh thu và hiệu suất</div>
                        </div>
                        
                        <div class="action-card">
                            <div class="action-icon">
                                <i class="fas fa-bell"></i>
                            </div>
                            <div class="action-title">Thông báo</div>
                            <div class="action-desc">Gởi thông báo đến nhân viên</div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script>
        // Add some interactive effects
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
                this.classList.add('active');
            });
        });

        document.querySelectorAll('.action-card').forEach(card => {
            card.addEventListener('click', function() {
                this.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    this.style.transform = '';
                }, 150);
            });
        });

        // Auto refresh stats every 30 seconds (placeholder)
        setInterval(() => {
            console.log('Auto refreshing dashboard data...');
        }, 30000);
    </script>
</body>
</html>