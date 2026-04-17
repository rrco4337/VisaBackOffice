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

<div class="header">
    <h2>Tableau de bord - Visa BackOffice</h2>
    <a href="/demande" class="btn">Soumettre un nouveau dossier</a>
</div>

<c:if test="${not empty successMessage}">
    <div class="success">${successMessage}</div>
</c:if>

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
