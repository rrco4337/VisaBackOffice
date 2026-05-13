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
        .capture-preview {
            width: 100%;
            max-width: 320px;
            border: 1px solid #dee2e6;
            border-radius: 0.5rem;
            background-color: #fff;
        }
        .signature-canvas {
            width: 100%;
            max-width: 420px;
            height: 200px;
            border: 1px solid #dee2e6;
            border-radius: 0.5rem;
            background-color: #fff;
            touch-action: none;
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
                                <span id="scan-status-badge" class="badge bg-success rounded-pill scan-badge locked-badge">
                                    <i class="bi bi-lock-fill me-1"></i> Verrouillé (Scan terminé)
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span id="scan-status-badge" class="badge bg-primary rounded-pill scan-badge">
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
                    <span id="scan-count-badge" class="badge bg-primary">${scannedCount}/${totalCount}</span>
                </div>
                <div class="progress" style="height: 2rem;">
                    <div id="scan-progress" class="progress-bar bg-success" role="progressbar"
                         style="width: ${totalCount > 0 ? (scannedCount * 100 / totalCount) : 0}%"
                         aria-valuenow="${scannedCount}" aria-valuemin="0" aria-valuemax="${totalCount}">
                        ${totalCount > 0 ? (scannedCount * 100 / totalCount) : 0}%
                    </div>
                </div>
                <small id="scan-progress-text" class="text-muted mt-2 d-block">
                    ${scannedCount} pièce(s) scannée(s) sur ${totalCount}
                </small>
            </div>
        </div>
    </div>

    <!-- Photo et signature -->
    <div class="row mb-4">
        <div class="col-12">
            <h4 class="mb-3"><i class="bi bi-camera me-2"></i> Photo et signature</h4>

            <div class="card border-0 shadow-sm">
                <div class="card-body">
                    <div class="row g-4">
                        <div class="col-lg-6">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h6 class="mb-0">Photo (webcam)</h6>
                                <span id="photo-status" class="badge ${photoDone ? 'bg-success' : 'bg-secondary'}">
                                    ${photoDone ? 'Terminee' : 'Non terminee'}
                                </span>
                            </div>
                            <div class="d-flex flex-column gap-2">
                                <video id="photo-video" class="capture-preview" autoplay playsinline style="display:none;"></video>
                                <c:if test="${photoFileId != null}">
                                    <c:url var="photoUrl" value="/demandes/${demande.id}/fichiers/${photoFileId}/download" />
                                </c:if>
                                <img id="photo-preview" class="capture-preview" alt="Apercu photo"
                                     src="${photoUrl}"
                                     style="${photoFileId != null ? '' : 'display:none;'}">
                                <canvas id="photo-canvas" class="capture-preview" style="display:none;"></canvas>
                                <div class="d-flex flex-wrap gap-2">
                                    <button id="photo-start" class="btn btn-outline-primary btn-sm" type="button" ${isScanned ? 'disabled' : ''}>
                                        <i class="bi bi-camera-video me-1"></i> Activer webcam
                                    </button>
                                    <button id="photo-capture" class="btn btn-primary btn-sm" type="button" ${isScanned ? 'disabled' : 'disabled'}>
                                        <i class="bi bi-camera me-1"></i> Capturer
                                    </button>
                                    <button id="photo-upload" class="btn btn-success btn-sm" type="button" ${isScanned ? 'disabled' : 'disabled'}>
                                        <i class="bi bi-cloud-upload me-1"></i> Enregistrer
                                    </button>
                                    <button id="photo-reset" class="btn btn-outline-secondary btn-sm" type="button" ${isScanned ? 'disabled' : 'disabled'}>
                                        <i class="bi bi-arrow-counterclockwise me-1"></i> Reprendre
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-6">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h6 class="mb-0">Signature (souris)</h6>
                                <span id="signature-status" class="badge ${signatureDone ? 'bg-success' : 'bg-secondary'}">
                                    ${signatureDone ? 'Terminee' : 'Non terminee'}
                                </span>
                            </div>
                            <div class="d-flex flex-column gap-2">
                                <canvas id="signature-canvas" class="signature-canvas" style="${signatureDone ? 'display:none;' : ''}"></canvas>
                                <c:if test="${signatureFileId != null}">
                                    <c:url var="signatureUrl" value="/demandes/${demande.id}/fichiers/${signatureFileId}/download" />
                                </c:if>
                                <img id="signature-preview" class="capture-preview" alt="Apercu signature"
                                     src="${signatureUrl}"
                                     style="${signatureFileId != null ? '' : 'display:none;'}">
                                <div class="d-flex flex-wrap gap-2">
                                    <button id="signature-clear" class="btn btn-outline-secondary btn-sm" type="button" ${isScanned || signatureDone ? 'disabled' : ''}>
                                        <i class="bi bi-eraser me-1"></i> Effacer
                                    </button>
                                    <button id="signature-upload" class="btn btn-success btn-sm" type="button" ${isScanned || signatureDone ? 'disabled' : ''}>
                                        <i class="bi bi-cloud-upload me-1"></i> Enregistrer
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
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
                                <div id="piece-row-${demandePiece.piece.id}" class="piece-row ${demandePiece.scanned ? 'scanned' : ''}">
                                    <div class="d-flex justify-content-between align-items-center mb-3">
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
                                            <small id="piece-status-${demandePiece.piece.id}" class="text-muted">
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
                                                    <button id="scan-btn-${demandePiece.piece.id}" class="btn btn-sm btn-success" disabled>
                                                        <i class="bi bi-check-circle-fill"></i> Scannée
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button id="scan-btn-${demandePiece.piece.id}" class="btn btn-sm btn-outline-primary"
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

                                    <!-- Upload PDF pour cette pièce -->
                                    <div class="mt-3 pt-3 border-top">
                                        <small class="text-muted fw-bold">Fichiers PDF:</small>
                                        <div id="files-piece-${demandePiece.piece.id}" class="mt-2 mb-3">
                                            <!-- Fichiers pour cette pièce seront chargés ici -->
                                        </div>
                                        <c:if test="${!isScanned}">
                                            <div class="input-group input-group-sm">
                                                <input type="file" class="form-control" accept=".pdf"
                                                       onchange="uploadPieceFile(${demande.id}, ${demandePiece.piece.id}, this)">
                                                <button class="btn btn-outline-primary" type="button"
                                                        onclick="uploadPieceFile(${demande.id}, ${demandePiece.piece.id}, this.previousElementSibling)">
                                                    <i class="bi bi-upload me-1"></i> Uploader
                                                </button>
                                            </div>
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
                    <form method="POST" action="/demandes/${demande.id}/finaliser-scan" style="display: inline;">
                        <button id="finalize-btn" type="submit" class="btn btn-success ${allPiecesScanned && photoDone && signatureDone ? '' : 'disabled-btn'}"
                                ${allPiecesScanned && photoDone && signatureDone ? '' : 'disabled'}>
                            <span id="finalize-text">
                                <c:choose>
                                    <c:when test="${allPiecesScanned && photoDone && signatureDone}">
                                        <i class="bi bi-check-lg me-1"></i> Finaliser le scan
                                    </c:when>
                                    <c:when test="${!allPiecesScanned}">
                                        <i class="bi bi-hourglass me-1"></i> Finaliser le scan (${totalCount - scannedCount} pièce(s) restante(s))
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-hourglass me-1"></i> Finaliser le scan (photo ou signature manquante)
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </button>
                    </form>
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
const demandeId = ${demande.id};
let isLocked = ${isScanned ? 'true' : 'false'};
let allPiecesScanned = ${allPiecesScanned ? 'true' : 'false'};
let scannedCount = ${scannedCount};
const totalCount = ${totalCount};
let photoStream = null;
let photoDone = ${photoDone ? 'true' : 'false'};
let signatureDone = ${signatureDone ? 'true' : 'false'};
let photoFileId = ${photoFileId != null ? photoFileId : 'null'};
let signatureFileId = ${signatureFileId != null ? signatureFileId : 'null'};

document.addEventListener('DOMContentLoaded', () => {
    initPhotoCapture();
    initSignatureCanvas();
});

function initPhotoCapture() {
        if (isLocked) {
            return;
        }
    const video = document.getElementById('photo-video');
    const canvas = document.getElementById('photo-canvas');
    const preview = document.getElementById('photo-preview');
    const startBtn = document.getElementById('photo-start');
    const captureBtn = document.getElementById('photo-capture');
    const uploadBtn = document.getElementById('photo-upload');
    const resetBtn = document.getElementById('photo-reset');

    startBtn.addEventListener('click', async () => {
        try {
            photoStream = await navigator.mediaDevices.getUserMedia({ video: true });
            video.srcObject = photoStream;
            video.style.display = 'block';
            canvas.style.display = 'none';
            preview.style.display = photoDone ? 'block' : 'none';
            captureBtn.disabled = false;
            uploadBtn.disabled = true;
            resetBtn.disabled = false;
        } catch (err) {
            alert('Impossible d\'acceder a la webcam: ' + err.message);
        }
    });

    captureBtn.addEventListener('click', () => {
        if (!photoStream) {
            return;
        }
        const track = photoStream.getVideoTracks()[0];
        const settings = track.getSettings();
        canvas.width = settings.width || 640;
        canvas.height = settings.height || 480;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        canvas.style.display = 'block';
        video.style.display = 'none';
        stopPhotoStream();
        uploadBtn.disabled = false;
    });

    uploadBtn.addEventListener('click', () => {
        canvas.toBlob(blob => {
            if (!blob) {
                alert('Capture invalide');
                return;
            }
            const formData = new FormData();
            formData.append('file', blob, 'photo.jpg');

            fetch('/demandes/' + demandeId + '/upload-photo', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (!data.success) {
                    alert('Erreur: ' + (data.error || 'Upload impossible'));
                    return;
                }
                photoDone = true;
                photoFileId = data.fileId;
                setCaptureStatus('photo-status', true);
                preview.src = '/demandes/' + demandeId + '/fichiers/' + data.fileId + '/download';
                preview.style.display = 'block';
                canvas.style.display = 'none';
                uploadBtn.disabled = true;
                captureBtn.disabled = true;
                updateFinalizeButton();
            })
            .catch(err => alert('Erreur: ' + err.message));
        }, 'image/jpeg', 0.9);
    });

    resetBtn.addEventListener('click', () => {
        if (photoDone) {
            deleteCapture('photo').then(success => {
                if (success) {
                    photoDone = false;
                    photoFileId = null;
                    preview.style.display = 'none';
                    setCaptureStatus('photo-status', false);
                    updateFinalizeButton();
                }
            });
        }
        uploadBtn.disabled = true;
        captureBtn.disabled = true;
        canvas.style.display = 'none';
        startBtn.click();
    });

    if (!photoDone) {
        preview.style.display = 'none';
    }
}

function stopPhotoStream() {
    if (photoStream) {
        photoStream.getTracks().forEach(t => t.stop());
        photoStream = null;
    }
}

function initSignatureCanvas() {
    const canvas = document.getElementById('signature-canvas');
    const preview = document.getElementById('signature-preview');
    const clearBtn = document.getElementById('signature-clear');
    const uploadBtn = document.getElementById('signature-upload');

    if (signatureDone) {
        preview.style.display = 'block';
        return;
    }

    const ctx = canvas.getContext('2d');
    let drawing = false;
    let hasInk = false;

    function resizeCanvas() {
        const rect = canvas.getBoundingClientRect();
        const ratio = window.devicePixelRatio || 1;
        canvas.width = rect.width * ratio;
        canvas.height = rect.height * ratio;
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.scale(ratio, ratio);
        ctx.lineWidth = 2;
        ctx.lineCap = 'round';
        ctx.strokeStyle = '#111';
    }

    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);

    if (isLocked) {
        return;
    }

    function getPoint(evt) {
        const rect = canvas.getBoundingClientRect();
        const clientX = evt.touches ? evt.touches[0].clientX : evt.clientX;
        const clientY = evt.touches ? evt.touches[0].clientY : evt.clientY;
        return { x: clientX - rect.left, y: clientY - rect.top };
    }

    function startDraw(evt) {
        drawing = true;
        const p = getPoint(evt);
        ctx.beginPath();
        ctx.moveTo(p.x, p.y);
        evt.preventDefault();
    }

    function draw(evt) {
        if (!drawing) return;
        const p = getPoint(evt);
        ctx.lineTo(p.x, p.y);
        ctx.stroke();
        hasInk = true;
        evt.preventDefault();
    }

    function endDraw() {
        drawing = false;
        ctx.closePath();
    }

    canvas.addEventListener('mousedown', startDraw);
    canvas.addEventListener('mousemove', draw);
    canvas.addEventListener('mouseup', endDraw);
    canvas.addEventListener('mouseleave', endDraw);
    canvas.addEventListener('touchstart', startDraw, { passive: false });
    canvas.addEventListener('touchmove', draw, { passive: false });
    canvas.addEventListener('touchend', endDraw);

    clearBtn.addEventListener('click', () => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        hasInk = false;
    });

    uploadBtn.addEventListener('click', () => {
        if (!hasInk) {
            alert('Veuillez signer avant d\'enregistrer');
            return;
        }
        canvas.toBlob(blob => {
            if (!blob) {
                alert('Signature invalide');
                return;
            }
            const formData = new FormData();
            formData.append('file', blob, 'signature.png');

            fetch('/demandes/' + demandeId + '/upload-signature', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (!data.success) {
                    alert('Erreur: ' + (data.error || 'Upload impossible'));
                    return;
                }
                signatureDone = true;
                signatureFileId = data.fileId;
                setCaptureStatus('signature-status', true);
                preview.src = '/demandes/' + demandeId + '/fichiers/' + data.fileId + '/download';
                preview.style.display = 'block';
                canvas.style.display = 'none';
                clearBtn.disabled = true;
                uploadBtn.disabled = true;
                updateFinalizeButton();
            })
            .catch(err => alert('Erreur: ' + err.message));
        }, 'image/png');
    });

    if (!signatureDone) {
        preview.style.display = 'none';
    }
}

function setCaptureStatus(elementId, done) {
    const badge = document.getElementById(elementId);
    badge.classList.remove('bg-success', 'bg-secondary');
    badge.classList.add(done ? 'bg-success' : 'bg-secondary');
    badge.textContent = done ? 'Terminee' : 'Non terminee';
}

function updateFinalizeButton() {
    const button = document.getElementById('finalize-btn');
    const text = document.getElementById('finalize-text');
    if (!button || !text) {
        return;
    }

    if (allPiecesScanned && photoDone && signatureDone) {
        button.disabled = false;
        button.classList.remove('disabled-btn');
        text.innerHTML = '<i class="bi bi-check-lg me-1"></i> Finaliser le scan';
        return;
    }

    button.disabled = true;
    button.classList.add('disabled-btn');

    if (!allPiecesScanned) {
        text.innerHTML = '<i class="bi bi-hourglass me-1"></i> Finaliser le scan (' + (totalCount - scannedCount) + ' pièce(s) restante(s))';
    } else {
        text.innerHTML = '<i class="bi bi-hourglass me-1"></i> Finaliser le scan (photo ou signature manquante)';
    }
}

function deleteCapture(categorie) {
    return fetch('/demandes/' + demandeId + '/captures/' + categorie, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (!data.success) {
            alert('Erreur: ' + (data.error || 'Suppression impossible'));
            return false;
        }
        return true;
    })
    .catch(err => {
        alert('Erreur: ' + err.message);
        return false;
    });
}

function updateProgressUI() {
    const badge = document.getElementById('scan-count-badge');
    const progress = document.getElementById('scan-progress');
    const text = document.getElementById('scan-progress-text');
    const percent = totalCount > 0 ? Math.round((scannedCount * 100) / totalCount) : 0;

    if (badge) {
        badge.textContent = scannedCount + '/' + totalCount;
    }
    if (progress) {
        progress.style.width = percent + '%';
        progress.setAttribute('aria-valuenow', scannedCount);
        progress.textContent = percent + '%';
    }
    if (text) {
        text.textContent = scannedCount + ' pièce(s) scannée(s) sur ' + totalCount;
    }
}

function updatePieceScanned(pieceId) {
    const row = document.getElementById('piece-row-' + pieceId);
    const status = document.getElementById('piece-status-' + pieceId);
    const button = document.getElementById('scan-btn-' + pieceId);

    if (row) {
        row.classList.add('scanned');
    }
    if (status) {
        status.innerHTML = '<i class="bi bi-check-circle-fill text-success me-1"></i> Scannée';
    }
    if (button) {
        button.classList.remove('btn-outline-primary');
        button.classList.add('btn-success');
        button.disabled = true;
        button.innerHTML = '<i class="bi bi-check-circle-fill"></i> Scannée';
    }
}

function setLockedState() {
    const badge = document.getElementById('scan-status-badge');
    if (badge) {
        badge.classList.remove('bg-primary');
        badge.classList.add('bg-success', 'locked-badge');
        badge.innerHTML = '<i class="bi bi-lock-fill me-1"></i> Verrouillé (Scan terminé)';
    }

    ['photo-start','photo-capture','photo-upload','photo-reset','signature-clear','signature-upload'].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.disabled = true;
        }
    });

    document.querySelectorAll('[id^="scan-btn-"]').forEach(btn => {
        btn.disabled = true;
    });
}

// Upload file for a specific piece - AJAX
function uploadPieceFile(demandeId, pieceId, fileInput) {
    if (!fileInput.files || !fileInput.files[0]) {
        alert('Veuillez sélectionner un fichier');
        return;
    }

    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append('file', file);
    formData.append('pieceId', pieceId);

    const uploadBtn = fileInput.nextElementSibling;
    uploadBtn.disabled = true;

    fetch('/demandes/' + demandeId + '/upload-fichier?pieceId=' + pieceId, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        uploadBtn.disabled = false;

        if (data.success) {
            alert('Fichier téléchargé avec succès');
            fileInput.value = '';
            // Refresh files for this piece
            loadPieceFiles(demandeId, pieceId);
        } else {
            alert('Erreur: ' + (data.error || 'Impossible de télécharger le fichier'));
        }
    })
    .catch(error => {
        uploadBtn.disabled = false;
        alert('Erreur lors de l\'upload: ' + error.message);
    });
}

// Load files for a specific piece
function loadPieceFiles(demandeId, pieceId) {
    fetch('/demandes/' + demandeId + '/piece/' + pieceId + '/fichiers')
    .then(response => response.json())
    .then(fichiers => {
        const container = document.getElementById('files-piece-' + pieceId);
        container.innerHTML = '';

        const filesList = document.createElement('div');
        filesList.className = 'small';

        if (fichiers.length === 0) {
            filesList.innerHTML = '<div class="text-muted">Aucun fichier</div>';
        } else {
            fichiers.forEach(f => {
                const sizeMB = (f.tailleFichier / (1024 * 1024)).toFixed(2);
                const fileRow = document.createElement('div');
                fileRow.className = 'd-flex justify-content-between align-items-center mb-2 p-2 bg-light rounded';
                fileRow.innerHTML = '<div>' +
                    '<i class="bi bi-filetype-pdf text-danger me-2"></i>' +
                    '<strong>' + f.nomFichier + '</strong> (' + sizeMB + ' MB)' +
                    '</div>' +
                    '<div class="gap-1">' +
                    '<a href="/demandes/' + demandeId + '/fichiers/' + f.id + '/download" class="btn btn-xs btn-link text-decoration-none" title="Télécharger">' +
                    '<i class="bi bi-download"></i>' +
                    '</a>' +
                    '<button class="btn btn-xs btn-link text-danger text-decoration-none" onclick="deleteFile(' + f.id + ', ' + demandeId + ', ' + pieceId + ')" title="Supprimer">' +
                    '<i class="bi bi-trash"></i>' +
                    '</button>' +
                    '</div>';
                filesList.appendChild(fileRow);
            });
        }

        container.appendChild(filesList);
    })
    .catch(error => {
        console.error('Erreur lors du chargement des fichiers:', error);
    });
}

// Delete file
function deleteFile(fichierDossierId, demandeId, pieceId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce fichier ?')) {
        return;
    }

    fetch('/demandes/' + demandeId + '/fichiers/' + fichierDossierId, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Refresh files for this piece
            loadPieceFiles(demandeId, pieceId);
        } else {
            alert('Erreur: ' + (data.error || 'Impossible de supprimer le fichier'));
        }
    })
    .catch(error => {
        alert('Erreur lors de la suppression: ' + error.message);
    });
}

// Load files on page load
document.addEventListener('DOMContentLoaded', function() {
    const demandeId = ${demande.id};
    <c:forEach var="demandePiece" items="${demande.demandePieces}">
        loadPieceFiles(demandeId, ${demandePiece.piece.id});
    </c:forEach>
});

function markAsScanned(demandeId, pieceId) {
    console.log('Marking as scanned - Demande:', demandeId, 'Piece:', pieceId);

    fetch('/demandes/' + demandeId + '/marquer-scannee?pieceId=' + pieceId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('HTTP error, status = ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        console.log('Data received:', data);
        if (data.success) {
            scannedCount = data.scannedCount;
            allPiecesScanned = data.allPiecesScanned;
            updatePieceScanned(pieceId);
            updateProgressUI();
            updateFinalizeButton();
        } else {
            alert('Erreur : ' + (data.error || 'Impossible de marquer la pièce comme scannée'));
        }
    })
    .catch(error => {
        console.error('Erreur complète:', error);
        alert('Erreur lors de la communication avec le serveur: ' + error.message);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const finalizeBtn = document.getElementById('finalize-btn');
    if (!finalizeBtn) {
        return;
    }

    finalizeBtn.addEventListener('click', evt => {
        if (finalizeBtn.disabled || isLocked) {
            evt.preventDefault();
            return;
        }
        evt.preventDefault();

        fetch('/demandes/' + demandeId + '/finaliser-scan-ajax', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                alert('Erreur : ' + (data.error || 'Finalisation impossible'));
                return;
            }
            isLocked = true;
            setLockedState();
            finalizeBtn.disabled = true;
            finalizeBtn.classList.add('disabled-btn');
            const text = document.getElementById('finalize-text');
            if (text) {
                text.innerHTML = '<i class="bi bi-check-lg me-1"></i> Scan finalisé';
            }
        })
        .catch(error => {
            alert('Erreur lors de la communication avec le serveur: ' + error.message);
        });
    });
});
</script>

</body>
</html>
