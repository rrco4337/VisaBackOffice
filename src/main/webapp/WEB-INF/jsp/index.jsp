<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="layout/layout_header.jsp">
    <jsp:param name="pageTitle" value="Dashboard" />
</jsp:include>

<div class="d-flex flex-column flex-md-row justify-content-between align-items-center mb-4">
    <p class="text-muted mb-3 mb-md-0 fs-5">Bienvenue sur le tableau de bord de gestion des visas.</p>
    <a href="/demande" class="btn btn-primary btn-lg rounded-pill shadow-sm px-4">
        <i class="bi bi-plus-lg me-2"></i> Soumettre un dossier
    </a>
</div>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm border-0" role="alert">
        <i class="bi bi-check-circle-fill me-2 fs-5 align-middle"></i> ${successMessage}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="card card-custom mt-2">
    <div class="card-header bg-transparent border-0 pt-4 pb-2 px-4">
        <h5 class="card-title fw-bold text-dark"><i class="bi bi-list-ul me-2 text-primary"></i> Dernières demandes</h5>
    </div>
    <div class="card-body px-4 pb-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light text-secondary">
                <tr>
                    <th class="rounded-start">ID</th>
                    <th>Demandeur</th>
                    <th>Type demande</th>
                    <th>Profil</th>
                    <th>Statut</th>
                    <th class="rounded-end">Date</th>
                </tr>
                </thead>
                <tbody class="border-top-0">
                <c:forEach var="d" items="${demandesRecentes}">
                    <tr>
                        <td><span class="badge bg-secondary opacity-75 rounded-pill">#${d.id}</span></td>
                        <td class="fw-medium text-dark">${d.personne.nom} ${d.personne.prenom}</td>
                        <td class="text-secondary">${d.typeDemande.libelle}</td>
                        <td><span class="badge bg-info text-dark rounded-pill shadow-sm">${d.typeProfil.libelle}</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${d.statut.name() == 'ACTIF'}"><span class="badge bg-success rounded-pill shadow-sm">Actif</span></c:when>
                                <c:when test="${d.statut.name() == 'CREER'}"><span class="badge bg-primary rounded-pill shadow-sm">Créé</span></c:when>
                                <c:otherwise><span class="badge bg-secondary rounded-pill shadow-sm">${d.statut.name()}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-muted small">${d.dateDemande}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty demandesRecentes}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-5">
                            <i class="bi bi-folder2-open fs-1 text-secondary opacity-50 d-block mb-3"></i>
                            Aucune demande enregistrée.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="layout/layout_footer.jsp" />
