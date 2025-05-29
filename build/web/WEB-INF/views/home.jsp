<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Quản Lý Chuỗi Spazone - Trang Chủ</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    
    <style>
        :root {
            --primary-pink: #ff6b9d;
            --secondary-pink: #ff8fab;
            --light-pink: #ffb3c6;
            --pale-pink: #fde2e4;
            --white: #ffffff;
            --dark-gray: #2c2c2c;
            --light-gray: #f8f9fa;
            --gradient-pink: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
            --gradient-light: linear-gradient(135deg, #fde2e4 0%, #ffb3c6 100%);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            background: var(--light-gray);
            color: var(--dark-gray);
            line-height: 1.6;
        }

        /* Header */
        .header {
            background: var(--gradient-pink);
            color: white;
            padding: 1rem 0;
            box-shadow: 0 2px 20px rgba(255, 107, 157, 0.3);
            position: sticky;
            top: 0;
            z-index: 1000;
        }

        .header h1 {
            font-weight: 700;
            font-size: 2rem;
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .header .subtitle {
            font-weight: 300;
            opacity: 0.9;
            font-size: 1rem;
        }

        /* Navigation */
        .main-nav {
            background: white;
            padding: 0;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .nav-link {
            color: var(--dark-gray);
            padding: 1rem 1.5rem !important;
            font-weight: 500;
            transition: all 0.3s ease;
            border-bottom: 3px solid transparent;
        }

        .nav-link:hover {
            color: var(--primary-pink);
            background: var(--pale-pink);
            border-bottom-color: var(--primary-pink);
        }

        .nav-link.active {
            color: var(--primary-pink);
            background: var(--pale-pink);
            border-bottom-color: var(--primary-pink);
        }

        /* Carousel Styles */
        .hero-carousel {
            margin-bottom: 3rem;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 10px 40px rgba(255, 107, 157, 0.2);
        }

        .carousel-item {
            height: 400px;
            position: relative;
        }

        .carousel-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .carousel-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(rgba(255, 107, 157, 0.4), rgba(255, 139, 171, 0.4));
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-align: center;
        }

        .carousel-content h2 {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 1rem;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
        }

        .carousel-content p {
            font-size: 1.2rem;
            opacity: 0.95;
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
        }

        .carousel-control-prev,
        .carousel-control-next {
            width: 5%;
        }

        .carousel-control-prev-icon,
        .carousel-control-next-icon {
            background-color: rgba(255, 107, 157, 0.8);
            border-radius: 50%;
            padding: 20px;
        }

        /* Dashboard Cards */
        .dashboard-section {
            padding: 3rem 0;
        }

        .section-title {
            color: var(--primary-pink);
            font-weight: 600;
            margin-bottom: 2rem;
            text-align: center;
            position: relative;
        }

        .section-title::after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 80px;
            height: 3px;
            background: var(--gradient-pink);
            border-radius: 2px;
        }

        .stats-card {
            background: white;
            border-radius: 20px;
            padding: 2rem;
            text-align: center;
            box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 107, 157, 0.1);
            position: relative;
            overflow: hidden;
        }

        .stats-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: var(--gradient-pink);
        }

        .stats-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(255, 107, 157, 0.2);
        }

        .stats-icon {
            font-size: 3rem;
            background: var(--gradient-pink);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 1rem;
        }

        .stats-number {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--primary-pink);
            margin-bottom: 0.5rem;
        }

        .stats-label {
            color: var(--dark-gray);
            font-weight: 500;
            font-size: 1.1rem;
        }

        /* Service Cards */
        .service-card {
            background: white;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 107, 157, 0.1);
            height: 100%;
        }

        .service-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(255, 107, 157, 0.2);
        }

        .service-image {
            width: 100%;
            height: 250px;
            background: var(--gradient-light);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 4rem;
            color: var(--primary-pink);
            position: relative;
            overflow: hidden;
        }

        .service-image::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="spa-pattern" x="0" y="0" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="2" fill="%23ff6b9d" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23spa-pattern)"/></svg>');
        }

        .service-content {
            padding: 2rem;
            text-align: center;
        }

        .service-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--dark-gray);
            margin-bottom: 1rem;
        }

        .service-desc {
            color: #666;
            margin-bottom: 1.5rem;
        }

        .service-price {
            font-size: 1.25rem;
            font-weight: 700;
            color: var(--primary-pink);
            margin-bottom: 1.5rem;
        }

        .btn-pink {
            background: var(--gradient-pink);
            border: none;
            color: white;
            padding: 0.75rem 2rem;
            border-radius: 50px;
            font-weight: 500;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(255, 107, 157, 0.3);
        }

        .btn-pink:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 107, 157, 0.4);
            color: white;
        }

        /* Recent Activity */
        .recent-activity {
            background: white;
            border-radius: 20px;
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(255, 107, 157, 0.1);
            border: 1px solid rgba(255, 107, 157, 0.1);
        }

        .activity-item {
            padding: 1rem;
            border-left: 4px solid var(--light-pink);
            margin-bottom: 1rem;
            background: var(--pale-pink);
            border-radius: 0 10px 10px 0;
            transition: all 0.3s ease;
        }

        .activity-item:hover {
            border-left-color: var(--primary-pink);
            background: rgba(255, 107, 157, 0.1);
        }

        .activity-time {
            color: var(--primary-pink);
            font-size: 0.9rem;
            font-weight: 500;
        }

        .activity-desc {
            color: var(--dark-gray);
            margin-top: 0.5rem;
        }

        /* Quick Stats Row */
        .quick-stats {
            background: var(--gradient-light);
            border-radius: 20px;
            padding: 2rem;
            margin-bottom: 2rem;
        }

        .quick-stat-item {
            text-align: center;
            color: var(--dark-gray);
        }

        .quick-stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary-pink);
        }

        .quick-stat-label {
            font-size: 0.9rem;
            font-weight: 500;
        }

        /* Footer */
        .footer {
            background: var(--dark-gray);
            color: white;
            padding: 2rem 0;
            margin-top: 4rem;
        }

        .footer-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .footer p {
            margin: 0;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .header h1 {
                font-size: 1.5rem;
            }
            
            .carousel-content h2 {
                font-size: 1.8rem;
            }
            
            .carousel-content p {
                font-size: 1rem;
            }
            
            .stats-card {
                margin-bottom: 1rem;
            }
            
            .footer-content {
                flex-direction: column;
                gap: 1rem;
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

        .fade-in {
            animation: fadeInUp 0.6s ease-out;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1><i class="fas fa-spa me-3"></i>Chuỗi Spa Spazone</h1>
                    <p class="subtitle mb-0">Hệ thống quản lý spa chuyên nghiệp và hiệu quả</p>
                </div>
                <div class="col-md-4 text-end">
                    <div class="d-flex align-items-center justify-content-end">
                        <div class="me-3">
                            <small>Xin chào, <strong>Admin</strong></small><br>
                            <small class="opacity-75">Hôm nay: <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()) %></small>
                        </div>
                        <div class="dropdown">
                            <button class="btn btn-light btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user-circle"></i>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#"><i class="fas fa-user me-2"></i>Hồ sơ</a></li>
                                <li><a class="dropdown-item" href="#"><i class="fas fa-cog me-2"></i>Cài đặt</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="#"><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </header>
    <!-- Navigation -->
    <nav class="main-nav sticky-top">
        <div class="container">
            <ul class="nav nav-pills justify-content-center">
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-home me-2"></i>Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-calendar-check me-2"></i>Lịch hẹn</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-users me-2"></i>Khách hàng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-concierge-bell me-2"></i>Dịch vụ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-user-tie me-2"></i>Nhân viên</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-store me-2"></i>Chi nhánh</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#"><i class="fas fa-chart-bar me-2"></i>Báo cáo</a>
                </li>
            </ul>
        </div>
    </nav>

     <!-- Main Content -->
    <main class="container mt-4">
        <!-- Hero Carousel -->
        <div id="heroCarousel" class="carousel slide hero-carousel fade-in" data-bs-ride="carousel">
            <div class="carousel-indicators">
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
                <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
            </div>
            <div class="carousel-inner">
                <div class="carousel-item active">
                    <img src="https://images.unsplash.com/photo-1544161515-4ab6ce6db874?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80" alt="Spa Relaxation">
                    <div class="carousel-overlay">
                        <div class="carousel-content">
                            <h2>Chào mừng đến với Chuỗi Spa Coco</h2>
                            <p>Trải nghiệm dịch vụ spa cao cấp với công nghệ hiện đại</p>
                        </div>
                    </div>
                </div>
                <div class="carousel-item">
                    <img src="https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80" alt="Professional Service">
                    <div class="carousel-overlay">
                        <div class="carousel-content">
                            <h2>Dịch Vụ Chuyên Nghiệp</h2>
                            <p>Đội ngũ chuyên gia giàu kinh nghiệm mang đến sự hài lòng tuyệt đối</p>
                        </div>
                    </div>
                </div>
                <div class="carousel-item">
                    <img src="https://images.unsplash.com/photo-1540555700478-4be289fbecef?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80" alt="Luxury Experience">
                    <div class="carousel-overlay">
                        <div class="carousel-content">
                            <h2>Không Gian Thư Giãn Tuyệt Vời</h2>
                            <p>Môi trường sang trọng, yên tĩnh cho những phút giây thư giãn hoàn hảo</p>
                        </div>
                    </div>
                </div>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
                <span class="carousel-control-prev-icon"></span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
                <span class="carousel-control-next-icon"></span>
            </button>
        </div>
        <!-- Quick Stats -->
        <div class="quick-stats fade-in">
            <div class="row">
                <div class="col-md-3 col-6">
                    <div class="quick-stat-item">
                        <div class="quick-stat-number">85%</div>
                        <div class="quick-stat-label">Tỷ lệ hoàn thành</div>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="quick-stat-item">
                        <div class="quick-stat-number">4.8/5</div>
                        <div class="quick-stat-label">Đánh giá trung bình</div>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="quick-stat-item">
                        <div class="quick-stat-number">98%</div>
                        <div class="quick-stat-label">Khách hàng hài lòng</div>
                    </div>
                </div>
                <div class="col-md-3 col-6">
                    <div class="quick-stat-item">
                        <div class="quick-stat-number">24/7</div>
                        <div class="quick-stat-label">Hỗ trợ</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Dashboard Stats -->
        <div class="dashboard-section">
            <h2 class="section-title">Thống kê tổng quan</h2>
            <div class="row">
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="stats-card fade-in">
                        <div class="stats-icon">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <div class="stats-number">248</div>
                        <div class="stats-label">Lịch hẹn hôm nay</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="stats-card fade-in">
                        <div class="stats-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stats-number">1,247</div>
                        <div class="stats-label">Khách hàng</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="stats-card fade-in">
                        <div class="stats-icon">
                            <i class="fas fa-store"></i>
                        </div>
                        <div class="stats-number">12</div>
                        <div class="stats-label">Chi nhánh</div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="stats-card fade-in">
                        <div class="stats-icon">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="stats-number">399K</div>
                        <div class="stats-label">Chi phí hợp lý</div>
                    </div>
                </div>
            </div>
        </div>
<div class="dashboard-section">
    <h2 class="section-title">Dịch Vụ Nổi Bật</h2>
    <div class="row">
        <div class="col-lg-4 col-md-6 mb-4">
            <div class="service-card fade-in">
                <div class="service-image">
                    <img src="https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=300&h=200&fit=crop&crop=center" alt="Massage Therapy" class="img-fluid rounded">
                </div>
                <div class="service-content">
                    <h3 class="service-title">Massage Thư Giãn</h3>
                    <p class="service-desc">Massage toàn thân giúp giảm stress và thư giãn cơ thể</p>
                    <div class="service-price">Từ 500.000₫</div>
                    <button class="btn btn-pink">
                        <i class="fas fa-calendar-plus me-2"></i>Đặt lịch
                    </button>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-md-6 mb-4">
            <div class="service-card fade-in">
                <div class="service-image">
                    <img src="https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=300&h=200&fit=crop&crop=center" alt="Facial Treatment" class="img-fluid rounded">
                </div>
                <div class="service-content">
                    <h3 class="service-title">Chăm Sóc Da Mặt</h3>
                    <p class="service-desc">Liệu trình chăm sóc da mặt chuyên sâu với công nghệ hiện đại</p>
                    <div class="service-price">Từ 800.000₫</div>
                    <button class="btn btn-pink">
                        <i class="fas fa-calendar-plus me-2"></i>Đặt lịch
                    </button>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-md-6 mb-4">
            <div class="service-card fade-in">
                <div class="service-image">
                    <img src="https://images.unsplash.com/photo-1604654894610-df63bc536371?w=300&h=200&fit=crop&crop=center" alt="Nail Art" class="img-fluid rounded">
                </div>
                <div class="service-content">
                    <h3 class="service-title">Làm Móng & Nail Art</h3>
                    <p class="service-desc">Dịch vụ làm móng chuyên nghiệp với nhiều mẫu nail art đẹp</p>
                    <div class="service-price">Từ 300.000₫</div>
                    <button class="btn btn-pink">
                        <i class="fas fa-calendar-plus me-2"></i>Đặt lịch
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
        <!-- Recent Activity & Today's Schedule -->
        <div class="row mt-5">
            <div class="col-lg-8 mb-4">
                <div class="recent-activity fade-in">
                    <h3 class="mb-4" style="color: var(--primary-pink);">
                        <i class="fas fa-clock me-2"></i>Hoạt động gần đây
                    </h3>
                    <div class="activity-item">
                        <div class="activity-time">10:30 AM - Hôm nay</div>
                        <div class="activity-desc">
                            <strong>Nguyễn Thị Mai</strong> đã hoàn thành dịch vụ massage thư giãn tại chi nhánh Quận 1
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">09:15 AM - Hôm nay</div>
                        <div class="activity-desc">
                            <strong>Trần Văn Nam</strong> đã đặt lịch hẹn chăm sóc da mặt cho ngày mai
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">08:45 AM - Hôm nay</div>
                        <div class="activity-desc">
                            Chi nhánh <strong>Quận 7</strong> đã cập nhật lịch làm việc cho tuần này
                        </div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">Hôm qua</div>
                        <div class="activity-desc">
                            Dịch vụ <strong>Chăm sóc body</strong> đã được thêm vào danh mục với giá ưu đãi
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 mb-4">
                <div class="recent-activity fade-in">
                    <h3 class="mb-4" style="color: var(--primary-pink);">
                        <i class="fas fa-calendar-day me-2"></i>Lịch hôm nay
                    </h3>
                    <div class="activity-item">
                        <div class="activity-time">11:00 AM</div>
                        <div class="activity-desc">Họp team marketing</div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">02:30 PM</div>
                        <div class="activity-desc">Kiểm tra chất lượng dịch vụ</div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">04:00 PM</div>
                        <div class="activity-desc">Đào tạo nhân viên mới</div>
                    </div>
                    <div class="activity-item">
                        <div class="activity-time">05:30 PM</div>
                        <div class="activity-desc">Báo cáo doanh thu tuần</div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-content">
                <div>
                    <p>&copy; 2024 Hệ Thống Quản Lý Chuỗi Spa. Tất cả quyền được bảo lưu.</p>
                </div>
                <div>
                    <p>Phiên bản 1.0 | Hỗ trợ: support@spa-management.com</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <script>
        // Add animation delays
        document.addEventListener('DOMContentLoaded', function() {
            const fadeElements = document.querySelectorAll('.fade-in');
            fadeElements.forEach((el, index) => {
                el.style.animationDelay = (index * 0.1) + 's';
            });
        });

        // Update time
        function updateTime() {
            const now = new Date();
            const timeString = now.toLocaleTimeString('vi-VN');
            document.querySelector('.current-time')?.textContent = timeString;
        }
        setInterval(updateTime, 1000);
    </script>
</body>
</html>