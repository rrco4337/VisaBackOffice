# Visa Status - Frontend

Application Vue 3 (Vite) pour consulter le statut des demandes via une API Spring Boot.

## Installation

1. Ouvrir un terminal à `frontend`.
2. Installer les dépendances :

```bash
npm install
```

3. Configurer l'URL de l'API (optionnel) : créer un fichier `.env` à la racine `frontend` avec :

```
VITE_API_BASE_URL=http://localhost:8080/api
```

4. Lancer en développement :

```bash
npm run dev
```

Le frontend sera accessible sur `http://localhost:5173`.

## Fonctionnalités

- **Recherche par numéro de passeport** : affiche toutes les demandes associées (triées par date chronologique).
- **Recherche par numéro de demande** : affiche uniquement la demande correspondante.
- **QR Code** : généré automatiquement après chaque recherche, permet une redirection directe vers la page de statut.
- **Page de statut** : affiche les détails complets de la demande (id, statut, date).
- **Design responsive** : interface adaptée aux écrans mobiles (480px et plus).

## API Backend - Endpoints attendus

L'application consomme les endpoints suivants :
- `GET /api/demandes?passportNumber={passport}` → liste des demandes pour un passeport
- `GET /api/demandes/{id}` → détail d'une demande

Format de réponse attendu :
```json
{
  "id": 1,
  "status": "CREER",
  "date": "2026-04-28T06:12:20Z"
}
```

Adaptez `src/api.js` si votre API a une structure différente.

## Structure du projet

```
frontend/
├── src/
│   ├── main.js           # Point d'entrée
│   ├── App.vue           # Composant racine
│   ├── api.js            # Client Axios
│   ├── styles.css        # Styles globaux (amélioré)
│   ├── router/
│   │   └── index.js      # Configuration Vue Router
│   ├── views/
│   │   ├── HomeView.vue  # Page d'accueil (recherche)
│   │   └── StatusView.vue # Page détail du statut
│   └── components/
│       ├── SearchForm.vue  # Formulaire de recherche + QR code
│       └── ResultList.vue  # Tableau des résultats
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## Améliorations CSS apportées

✅ **Thème moderne** : couleurs professionnelles et palette cohérente  
✅ **Système de variables CSS** : personnalisation facile  
✅ **Responsive design** : adapté aux mobiles (480px+) et tablettes (768px)  
✅ **Accessibilité** : focus states, contraste amélioré  
✅ **Animations subtiles** : transitions fluides et professionnelles  
✅ **Hiérarchie visuelle** : typographie structurée et espacements cohérents  
✅ **Composants redessinés** : formulaires, boutons, tableaux, QR code  

## Lancer en production

```bash
npm run build
# Les fichiers seront dans le dossier dist/
```

Servez le contenu du dossier `dist/` avec un serveur HTTP (Apache, Nginx, Express, etc.)

## Notes

- CORS doit être activé sur le backend pour accepter les requêtes cross-origin.
- En production, restreindre `@CrossOrigin` à l'URL du frontend spécifique.
- Les URLs QR code pointent vers `#/status/{id}` et supposent que le frontend est accessible via le navigateur.
