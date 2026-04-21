<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="layout/layout_header.jsp">
    <jsp:param name="pageTitle" value="Soumission" />
</jsp:include>

<c:if test="${not empty errors}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0" role="alert">
        <h5 class="alert-heading text-danger fw-bold"><i class="bi bi-exclamation-triangle-fill me-2"></i> Veuillez corriger les erreurs suivantes :</h5>
        <hr class="border-danger opacity-25">
        <ul class="mb-0">
            <c:forEach var="err" items="${errors}">
                <li>${err}</li>
            </c:forEach>
        </ul>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="card card-custom mt-2">
    <div class="card-body p-4 p-md-5">
        <form action="demandes/enregistrer" method="post">
            
            <h4 class="mb-4 text-primary fw-bold border-bottom pb-2"><i class="bi bi-person-vcard me-2"></i> 1. État civil</h4>
            <div class="row g-3 mb-5">
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Nom</label>
                    <input type="text" class="form-control bg-light border-0" name="nom" value="${form.nom}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Prénom</label>
                    <input type="text" class="form-control bg-light border-0" name="prenom" value="${form.prenom}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Nom de jeune fille</label>
                    <input type="text" class="form-control bg-light border-0" name="nomJeuneFille" value="${form.nomJeuneFille}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Nom du père</label>
                    <input type="text" class="form-control bg-light border-0" name="nomPere" value="${form.nomPere}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Date de naissance</label>
                    <input type="date" class="form-control bg-light border-0" name="dateNaissance" value="${form.dateNaissance}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Situation familiale</label>
                    <select name="situationFamiliale" class="form-select bg-light border-0">
                        <option value="">-- Sélectionnez --</option>
                        <c:forEach var="sf" items="${situationsFamiliales}">
                            <option value="${sf.id}" ${form.situationFamiliale == sf.id ? 'selected="selected"' : ''}>${sf.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Nationalité</label>
                    <select name="nationalite" class="form-select bg-light border-0">
                        <option value="">-- Sélectionnez --</option>
                        <c:forEach var="nat" items="${nationalites}">
                            <option value="${nat.id}" ${form.nationalite == nat.id ? 'selected="selected"' : ''}>${nat.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Profession</label>
                    <input type="text" class="form-control bg-light border-0" name="profession" value="${form.profession}">
                </div>
                <div class="col-12">
                    <label class="form-label text-muted small fw-bold">Adresse</label>
                    <textarea class="form-control bg-light border-0" name="adresse" rows="2">${form.adresse}</textarea>
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Email</label>
                    <input type="email" class="form-control bg-light border-0" name="email" value="${form.email}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Téléphone</label>
                    <input type="text" class="form-control bg-light border-0" name="telephone" value="${form.telephone}">
                </div>
                <div class="col-md-4">
                    <label class="form-label text-muted small fw-bold">Numéro de passeport</label>
                    <input type="text" class="form-control bg-light border-0" name="numeroPasseport" value="${form.numeroPasseport}">
                </div>
                <div class="col-md-4">
                    <label class="form-label text-muted small fw-bold">Date de délivrance passeport</label>
                    <input type="date" class="form-control bg-light border-0" name="dateDelivrancePasseport" value="${form.dateDelivrancePasseport}">
                </div>
                <div class="col-md-4">
                    <label class="form-label text-muted small fw-bold">Date d'expiration passeport</label>
                    <input type="date" class="form-control bg-light border-0" name="dateExpirationPasseport" value="${form.dateExpirationPasseport}">
                </div>
            </div>

            <h4 class="mb-4 text-primary fw-bold border-bottom pb-2"><i class="bi bi-passport me-2"></i> 2. Visa transformable</h4>
            <div class="row g-3 mb-5">
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Numéro de visa</label>
                    <input type="text" class="form-control bg-light border-0" name="numeroVisa" value="${form.numeroVisa}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Date d'entrée (Ivato)</label>
                    <input type="date" class="form-control bg-light border-0" name="dateEntree" value="${form.dateEntree}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Lieu d'entrée</label>
                    <input type="text" class="form-control bg-light border-0" name="lieuEntree" value="${form.lieuEntree}">
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Date d'expiration visa</label>
                    <input type="date" class="form-control bg-light border-0" name="dateExpirationVisa" value="${form.dateExpirationVisa}">
                </div>
            </div>

            <h4 class="mb-4 text-primary fw-bold border-bottom pb-2"><i class="bi bi-file-text me-2"></i> 3. Demande</h4>
            <div class="row g-3 mb-5 align-items-center">
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Type de titre demandé</label>
                    <select class="form-select bg-light border-0" name="typeDemande" id="typeDemande" onchange="window.location.href='/demande?typeDemande=' + this.value + '&typeProfil=' + document.getElementById('typeProfil').value">
                        <c:forEach var="type" items="${typesDemande}">
                            <option value="${type}" ${form.typeDemande == type ? 'selected="selected"' : ''}>${type}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label text-muted small fw-bold">Type de profil</label>
                    <select class="form-select bg-light border-0" name="typeProfil" id="typeProfil" onchange="window.location.href='/demande?typeDemande=' + document.getElementById('typeDemande').value + '&typeProfil=' + this.value">
                        <c:forEach var="profil" items="${typesProfil}">
                            <option value="${profil}" ${form.typeProfil == profil ? 'selected="selected"' : ''}>${profil}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-12 mt-4">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" role="switch" id="sansDonnees" name="sansDonnees" ${form.sansDonnees ? 'checked="checked"' : ''}>
                        <label class="form-check-label text-dark" for="sansDonnees">Sans données (autorisé pour duplicata et transfert)</label>
                    </div>
                </div>
            </div>

            <h4 class="mb-4 text-primary fw-bold border-bottom pb-2"><i class="bi bi-check2-square me-2"></i> 4. Pièces justificatives</h4>
            <div class="p-4 bg-light rounded-3 mb-5">
                <div class="row g-3">
                    <c:forEach var="piece" items="${pieces}">
                        <div class="col-md-6">
                            <div class="form-check">
                                <input class="form-check-input shadow-sm" type="checkbox" name="pieceIds" value="${piece.id}" id="piece-${piece.id}"
                                    ${form.pieceIds.contains(piece.id) ? 'checked="checked"' : ''}>
                                <label class="form-check-label" for="piece-${piece.id}">
                                    ${piece.libelle}
                                    <c:if test="${piece.obligatoire}">
                                        <span class="badge bg-danger rounded-pill ms-2">Obligatoire</span>
                                    </c:if>
                                </label>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="d-flex justify-content-end gap-3 mt-4">
                <a href="/" class="btn btn-outline-secondary rounded-pill px-4">Annuler</a>
                <button type="submit" class="btn btn-primary rounded-pill px-5 shadow-sm fs-5"><i class="bi bi-send-fill me-2"></i> Enregistrer la demande</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="layout/layout_footer.jsp" />