<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${param.pageTitle} - VisaBackoffice</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #0d6efd;
            --bg-color: #f8f9fa; /* pastel gray */
            --bs-body-font-family: 'Inter', system-ui, -apple-system, sans-serif;
        }
        body {
            background-color: var(--bg-color);
            overflow-x: hidden;
        }
        .sidebar {
            width: 280px;
            min-height: 100vh;
            transition: all 0.3s;
        }
        @media (max-width: 768px) {
            .sidebar {
                margin-left: -280px;
                position: fixed;
                z-index: 1040;
            }
            .sidebar.active {
                margin-left: 0;
            }
        }
        .main-content {
            width: 100%;
            transition: all 0.3s;
        }
        .sidebar-overlay {
            display: none;
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0,0,0,0.5);
            z-index: 1030;
        }
        .sidebar-overlay.active {
            display: block;
        }
        .card-custom {
            border: none;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            transition: transform 0.2s;
        }
        .nav-pills .nav-link.active, .nav-pills .show>.nav-link {
            background-color: #e7f1ff;
            color: #0c63e4 !important;
            font-weight: 600;
        }
        .nav-link {
            color: #495057;
            font-weight: 500;
            border-radius: 8px;
            margin-bottom: 5px;
        }
        .nav-link:hover {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>

<div class="d-flex h-100">

    <!-- Overlay for mobile sidebar -->
    <div class="sidebar-overlay" id="sidebarOverlay"></div>

    <!-- Sidebar -->
    <div class="sidebar bg-white shadow-sm d-flex flex-column p-4" id="sidebar">
        <a href="/" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-dark text-decoration-none">
            <i class="bi bi-airplane-engines-fill text-primary fs-3 me-3"></i>
            <span class="fs-4 fw-bold">VisaBack</span>
        </a>
        <hr class="text-secondary opacity-25">
        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
                <a href="/" class="nav-link px-3 py-2 ${param.pageTitle == 'Dashboard' ? 'active' : ''}">
                    <i class="bi bi-grid-fill me-2 opacity-75"></i> Tableau de bord
                </a>
            </li>
            <li class="nav-item">
                <a href="/demande" class="nav-link px-3 py-2 ${param.pageTitle == 'Soumission' ? 'active' : ''}">
                    <i class="bi bi-file-earmark-plus-fill me-2 opacity-75"></i> Nouvelle Demande
                </a>
            </li>
        </ul>
        <hr class="text-secondary opacity-25">
        <div class="dropdown">
            <a href="#" class="d-flex align-items-center link-dark text-decoration-none dropdown-toggle px-2" id="dropdownUser" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-person-circle fs-4 me-3 text-secondary"></i>
                <div class="d-flex flex-column">
                    <strong class="lh-sm">Agent Admin</strong>
                    <small class="text-muted">Préfecture</small>
                </div>
            </a>
            <ul class="dropdown-menu text-small shadow" aria-labelledby="dropdownUser">
                <li><a class="dropdown-item" href="#"><i class="bi bi-gear me-2 text-muted"></i> Paramètres</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#"><i class="bi bi-box-arrow-right me-2"></i> Déconnexion</a></li>
            </ul>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content d-flex flex-column vh-100 overflow-y-auto w-100">
        
        <!-- Mobile Top Navbar -->
        <nav class="navbar navbar-light bg-white shadow-sm px-3 py-3 d-flex d-md-none sticky-top">
            <button class="btn btn-light border-0 d-md-none fs-4 px-2" id="sidebarToggle">
                <i class="bi bi-list"></i>
            </button>
            <span class="navbar-brand mb-0 h1 ms-2 fw-bold text-primary">VisaBack</span>
            <div class="ms-auto">
                 <i class="bi bi-person-circle fs-3 text-secondary"></i>
            </div>
        </nav>

        <!-- Page Content -->
        <div class="container-fluid px-3 px-md-5 py-4 py-md-5 mx-auto">
            <h2 class="mb-4 fw-bold text-dark">${param.pageTitle}</h2>
