<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Sprint 1 - Enregistrement Demande Visa</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; background: #f5f7fb; }
        .card { background: #fff; border-radius: 8px; padding: 18px; margin-bottom: 18px; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
        h2, h3 { margin-top: 0; }
        .grid { display: grid; grid-template-columns: repeat(2, minmax(220px, 1fr)); gap: 10px 14px; }
        label { font-size: 12px; color: #333; display: block; margin-bottom: 4px; }
        input, select, textarea { width: 100%; padding: 8px; box-sizing: border-box; }
        textarea { min-height: 60px; }
        .row-full { grid-column: 1 / -1; }
        .error { color: #8a1f11; background: #fde6e2; border: 1px solid #f3c8c0; padding: 8px; margin-bottom: 10px; }
        .success { color: #165c2f; background: #e3f6e8; border: 1px solid #b5e1c2; padding: 8px; margin-bottom: 10px; }
        .pieces { padding: 10px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; }
        .btn { margin-top: 12px; padding: 10px 14px; background: #0b63f3; color: white; border: 0; border-radius: 6px; cursor: pointer; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #d7deea; padding: 8px; text-align: left; }
        th { background: #eef3fb; }
    </style>
</head>
<body>

<div class="card">
    <h2>Sprint 1 - Enregistrement d'une demande de transformation</h2>

    <c:if test="${not empty successMessage}">
        <div class="success">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errors}">
        <div class="error">
            <ul>
                <c:forEach var="err" items="${errors}">
                    <li>${err}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form action="demandes/enregistrer" method="post">
        <h3>1. Etat civil</h3>
        <div class="grid">
            <div><label>Nom</label><input type="text" name="nom" value="${form.nom}"/></div>
            <div><label>Prenom</label><input type="text" name="prenom" value="${form.prenom}"/></div>
            <div><label>Nom de jeune fille</label><input type="text" name="nomJeuneFille" value="${form.nomJeuneFille}"/></div>
            <div><label>Nom du pere</label><input type="text" name="nomPere" value="${form.nomPere}"/></div>
            <div><label>Date de naissance</label><input type="date" name="dateNaissance" value="${form.dateNaissance}"/></div>
            <div><label>Situation familiale</label><input type="text" name="situationFamiliale" value="${form.situationFamiliale}"/></div>
            <div><label>Nationalite</label><input type="text" name="nationalite" value="${form.nationalite}"/></div>
            <div><label>Profession</label><input type="text" name="profession" value="${form.profession}"/></div>
            <div class="row-full"><label>Adresse</label><textarea name="adresse">${form.adresse}</textarea></div>
            <div><label>Email</label><input type="email" name="email" value="${form.email}"/></div>
            <div><label>Telephone</label><input type="text" name="telephone" value="${form.telephone}"/></div>
            <div><label>Numero de passeport</label><input type="text" name="numeroPasseport" value="${form.numeroPasseport}"/></div>
            <div><label>Date de delivrance du passeport</label><input type="date" name="dateDelivrancePasseport" value="${form.dateDelivrancePasseport}"/></div>
            <div><label>Date d'expiration du passeport</label><input type="date" name="dateExpirationPasseport" value="${form.dateExpirationPasseport}"/></div>
        </div>

        <h3>2. Visa transformable</h3>
        <div class="grid">
            <div><label>Numero visa</label><input type="text" name="numeroVisa" value="${form.numeroVisa}"/></div>
            <div><label>Date d'entree (Ivato)</label><input type="date" name="dateEntree" value="${form.dateEntree}"/></div>
            <div><label>Lieu d'entree</label><input type="text" name="lieuEntree" value="${form.lieuEntree}"/></div>
            <div><label>Date d'expiration visa</label><input type="date" name="dateExpirationVisa" value="${form.dateExpirationVisa}"/></div>
        </div>

        <h3>3. Demande</h3>
        <div class="grid">
            <div>
                <label>Type de titre demande</label>
                <select name="typeDemande">
                    <c:forEach var="type" items="${typesDemande}">
                        <option value="${type}" ${form.typeDemande == type ? 'selected="selected"' : ''}>${type}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label>Type de profil</label>
                <select name="typeProfil">
                    <c:forEach var="profil" items="${typesProfil}">
                        <option value="${profil}" ${form.typeProfil == profil ? 'selected="selected"' : ''}>${profil}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="row-full">
                <label>
                    <input type="checkbox" name="sansDonnees" ${form.sansDonnees ? 'checked="checked"' : ''} />
                    Sans donnees (autorise pour duplicata et transfert)
                </label>
            </div>
        </div>

        <h3>4. Pieces justificatives (checklist)</h3>
        <div class="pieces">
            <c:forEach var="piece" items="${pieces}">
                <label>
                    <input type="checkbox" name="pieceIds" value="${piece.id}"
                        ${form.pieceIds.contains(piece.id) ? 'checked="checked"' : ''}
                    />
                    ${piece.libelle}
                    <c:if test="${piece.obligatoire}">(obligatoire)</c:if>
                </label>
            </c:forEach>
        </div>

        <button class="btn" type="submit">Enregistrer la demande</button>
    </form>
</div>

<div class="card">
    <h3>Dernieres demandes</h3>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Demandeur</th>
            <th>Type demande</th>
            <th>Profil</th>
            <th>Statut</th>
            <th>Date</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="d" items="${demandesRecentes}">
            <tr>
                <td>${d.id}</td>
                <td>${d.personne.nom} ${d.personne.prenom}</td>
                <td>${d.typeDemande.libelle}</td>
                <td>${d.typeProfil.libelle}</td>
                <td>${d.statut}</td>
                <td>${d.dateDemande}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
