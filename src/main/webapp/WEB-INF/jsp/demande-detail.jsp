<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dossier #${demande.id} - Visa Back Office</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .scan-badge {
            font-size: 0.85rem;
            padding: 0.4rem 0.8rem;
        }
        .piece-row {
            padding: 1rem;
            border-radius: 0.5rem;
            margin-bottom: 0.5rem;
            background-color: #f8f9fa;
            border-left: 4px solid #dee2e6;
        }
        .piece-row.scanned {
            background-color: #e8f5e9;
            border-left-color: #4caf50;
        }
        .progress-section {
            padding: 1.5rem;
            background-color: #f0f7ff;
            border-radius: 0.5rem;
            margin-bottom: 1.5rem;
            border: 1px solid #d4e4f7;
        }
        .locked-badge {
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.7; }
        }
        .disabled-btn {
            cursor: not-allowed;
        }
    </style>
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container-fluid px-4">
        <a class="navbar-brand fw-bold" href="/">
            <i class="bi bi-file-earmark-check me-2"></i> Visa Back Office
        </a>
        <span class="text-white-50">Gestion du scan des pièces</span>
    </div>
</nav>

<main class="container-lg my-5">
    <!-- Messages d'alerte -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-check-circle me-2"></i> ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-circle me-2"></i>
            <c:forEach var="error" items="${errors}">
                <div>${error}</div>
            </c:forEach>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- En-tête du dossier -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h1 class="card-title mb-0">
                            <i class="bi bi-file-earmark-text me-2"></i> Dossier #${demande.id}
                        </h1>
                        <c:choose>
                            <c:when test="${isScanned}">
                                <span class="badge bg-success rounded-pill scan-badge locked-badge">
                                    <i class="bi bi-lock-fill me-1"></i> Verrouillé (Scan terminé)
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-primary rounded-pill scan-badge">
                                    <i class="bi bi-hourglass me-1"></i> En cours de scan
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="row text-secondary small">
                        <div class="col-md-3">
                            <strong>Demandeur :</strong> ${demande.personne.nom} ${demande.personne.prenom}
                        </div>
                        <div class="col-md-3">
                            <strong>Type de demande :</strong> ${demande.typeDemande.libelle}
                        </div>
                        <div class="col-md-3">
                            <strong>Profil :</strong> ${demande.typeProfil.libelle}
                        </div>
                        <div class="col-md-3">
                            <strong>Date :</strong> ${demande.dateDemande}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Progression du scan -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="progress-section">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="mb-0"><i class="bi bi-graph-up me-2"></i> Progression du scan</h5>
                    <span class="badge bg-primary">${scannedCount}/${totalCount}</span>
                </div>
                <div class="progress" style="height: 2rem;">
                    <div class="progress-bar bg-success" role="progressbar"
                         style="width: ${totalCount > 0 ? (scannedCount * 100 / totalCount) : 0}%"
                         aria-valuenow="${scannedCount}" aria-valuemin="0" aria-valuemax="${totalCount}">
                        ${totalCount > 0 ? (scannedCount * 100 / totalCount) : 0}%
                    </div>
                </div>
                <small class="text-muted mt-2 d-block">
                    ${scannedCount} pièce(s) scannée(s) sur ${totalCount}
                </small>
            </div>
        </div>
    </div>

    <!-- Liste des pièces -->
    <div class="row mb-4">
        <div class="col-12">
            <h4 class="mb-3"><i class="bi bi-file-earmark-check me-2"></i> Pièces justificatives</h4>

            <c:choose>
                <c:when test="${empty demande.demandePieces}">
                    <div class="alert alert-info" role="alert">
                        <i class="bi bi-info-circle me-2"></i> Aucune pièce associée à ce dossier.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card border-0 shadow-sm">
                        <div class="card-body p-0">
                            <c:forEach var="demandePiece" items="${demande.demandePieces}" varStatus="loop">
                                <div class="piece-row ${demandePiece.scanned ? 'scanned' : ''}">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div class="flex-grow-1">
                                            <div>
                                                <strong>${demandePiece.piece.libelle}</strong>
                                                <c:if test="${demandePiece.piece.obligatoire}">
                                                    <span class="badge bg-danger rounded-pill">Obligatoire</span>
                                                </c:if>
                                                <c:if test="${demandePiece.fournie}">
                                                    <span class="badge bg-info rounded-pill">Fournie</span>
                                                </c:if>
                                            </div>
                                            <small class="text-muted">
                                                <c:if test="${demandePiece.scanned}">
                                                    <i class="bi bi-check-circle-fill text-success me-1"></i>
                                                    Scannée le ${demandePiece.dateScan}
                                                </c:if>
                                                <c:if test="${!demandePiece.scanned}">
                                                    <i class="bi bi-circle text-muted me-1"></i>
                                                    Non scannée
                                                </c:if>
                                            </small>
                                        </div>

                                        <!-- Bouton scan -->
                                        <c:if test="${!isScanned}">
                                            <c:choose>
                                                <c:when test="${demandePiece.scanned}">
                                                    <button class="btn btn-sm btn-success" disabled>
                                                        <i class="bi bi-check-circle-fill"></i> Scannée
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="btn btn-sm btn-outline-primary"
                                                            onclick="markAsScanned(${demande.id}, ${demandePiece.piece.id})">
                                                        <i class="bi bi-check-circle"></i> Marquer comme scannée
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <c:if test="${isScanned}">
                                            <span class="text-muted small">
                                                <i class="bi bi-lock-fill"></i> Verrouillé
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                                <c:if test="${!loop.last}"><hr class="my-2"></c:if>
                            </c:forEach>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Boutons d'action -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="d-flex gap-2 justify-content-end">
                <a href="/" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left me-1"></i> Retour à l'accueil
                </a>
                <c:if test="${!isScanned}">
                    <c:choose>
                        <c:when test="${allPiecesScanned}">
                            <form method="POST" action="/demandes/${demande.id}/finaliser-scan" style="display: inline;">
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-check-lg me-1"></i> Finaliser le scan
                                </button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-success disabled-btn" disabled>
                                <i class="bi bi-hourglass me-1"></i> Finaliser le scan (${totalCount - scannedCount} pièce(s) restante(s))
                            </button>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </div>
    </div>
</main>

<footer class="bg-dark text-white-50 py-4 mt-5">
    <div class="container-lg text-center small">
        <p class="mb-0">&copy; 2024 Visa Back Office. Gestion sécurisée des dossiers.</p>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
function markAsScanned(demandeId, pieceId) {
    fetch(`/demandes/${demandeId}/marquer-scannee?pieceId=${pieceId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Recharger la page pour afficher la mise à jour
            location.reload();
        } else {
            alert('Erreur : ' + (data.error || 'Impossible de marquer la pièce comme scannée'));
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        alert('Erreur lors de la communication avec le serveur');
    });
}
</script>

</body>
</html>
