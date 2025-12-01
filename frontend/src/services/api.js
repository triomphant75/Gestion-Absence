import axios from 'axios';

// Configuration de l'instance Axios
// Do not set a global Content-Type header here — allow axios/browser
// to set it per-request (important for FormData multipart uploads).
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Intercepteur pour ajouter le token JWT à chaque requête
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs de réponse
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expiré ou invalide
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ==================== AUTHENTIFICATION ====================
export const authService = {
  login: (email, motDePasse) => api.post('/auth/login', { email, motDePasse }),
  validateToken: () => api.get('/auth/validate')
};

// ==================== UTILISATEURS ====================
export const userService = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  getByEmail: (email) => api.get(`/users/email/${email}`),
  getByRole: (role) => api.get(`/users/role/${role}`),
  create: (user) => api.post('/users', user),
  update: (id, user) => api.put(`/users/${id}`, user),
  delete: (id) => api.delete(`/users/${id}`),
  deactivate: (id) => api.put(`/users/${id}/deactivate`)
};

// ==================== SÉANCES ====================
export const seanceService = {
  getAll: () => api.get('/seances'),
  getById: (id) => api.get(`/seances/${id}`),
  getByEnseignant: (enseignantId) => api.get(`/seances/enseignant/${enseignantId}`),
  getUpcoming: (enseignantId) => api.get(`/seances/enseignant/${enseignantId}/upcoming`),
  getByGroupe: (groupeId) => api.get(`/seances/groupe/${groupeId}`),
  create: (seance) => api.post('/seances', seance),
  update: (id, seance) => api.put(`/seances/${id}`, seance),
  start: (id) => api.post(`/seances/${id}/start`),
  stop: (id) => api.post(`/seances/${id}/stop`),
  renewCode: (id) => api.post(`/seances/${id}/renew-code`),
  getCurrentCode: (id) => api.get(`/seances/${id}/code`),
  cancel: (id) => api.put(`/seances/${id}/cancel`),
  delete: (id) => api.delete(`/seances/${id}`),
  getEtudiantsInscrits: (id) => api.get(`/seances/${id}/etudiants`),
  countEtudiantsInscrits: (id) => api.get(`/seances/${id}/etudiants/count`)
};

// ==================== PRÉSENCES ====================
export const presenceService = {
  validateCode: (seanceId, code, userId) =>
    api.post('/presences/validate-code', { seanceId, code }, {
      headers: { 'X-User-Id': userId }
    }),
  createManual: (seanceId, etudiantId, statut) =>
    api.post(`/presences?seanceId=${seanceId}&etudiantId=${etudiantId}&statut=${statut}`),
  update: (id, statut, commentaire) =>
    api.put(`/presences/${id}?statut=${statut}${commentaire ? '&commentaire=' + commentaire : ''}`),
  getByEtudiant: (etudiantId) => api.get(`/presences/etudiant/${etudiantId}`),
  getBySeance: (seanceId) => api.get(`/presences/seance/${seanceId}`),
  getStatistiques: (etudiantId) => api.get(`/presences/statistiques/${etudiantId}`),
  countAbsences: (etudiantId, matiereId) =>
    api.get(`/presences/absences/count?etudiantId=${etudiantId}&matiereId=${matiereId}`),
  delete: (id) => api.delete(`/presences/${id}`)
};

// ==================== JUSTIFICATIFS ====================
export const justificatifService = {
  getAll: () => api.get('/justificatifs'),
  getById: (id) => api.get(`/justificatifs/${id}`),
  getByEtudiant: (etudiantId) => api.get(`/justificatifs/etudiant/${etudiantId}`),
  getEnAttente: () => api.get('/justificatifs/en-attente'),
  deposer: (etudiantId, absenceId, motif, fichier) => {
    const formData = new FormData();
    formData.append('etudiantId', etudiantId);
    formData.append('absenceId', absenceId);
    formData.append('motif', motif);
    formData.append('fichier', fichier);
    // Let the browser/axios set the Content-Type (including boundary)
    return api.post('/justificatifs', formData);
  },
  valider: (id, validateurId, commentaire) =>
    api.put(`/justificatifs/${id}/valider?validateurId=${validateurId}${commentaire ? '&commentaire=' + commentaire : ''}`),
  refuser: (id, validateurId, commentaire) =>
    api.put(`/justificatifs/${id}/refuser?validateurId=${validateurId}${commentaire ? '&commentaire=' + commentaire : ''}`),
  download: (id) => api.get(`/justificatifs/${id}/download`, { responseType: 'blob' }),
  delete: (id) => api.delete(`/justificatifs/${id}`)
};

// ==================== AVERTISSEMENTS ====================
export const avertissementService = {
  getAll: () => api.get('/avertissements'),
  getById: (id) => api.get(`/avertissements/${id}`),
  getByEtudiant: (etudiantId) => api.get(`/avertissements/etudiant/${etudiantId}`),
  getByMatiere: (matiereId) => api.get(`/avertissements/matiere/${matiereId}`),
  getAutomatiques: () => api.get('/avertissements/automatiques'),
  getManuels: () => api.get('/avertissements/manuels'),
  create: (etudiantId, matiereId, nombreAbsences, motif, createurId) =>
    api.post(`/avertissements?etudiantId=${etudiantId}&matiereId=${matiereId}&nombreAbsences=${nombreAbsences}&motif=${motif}&createurId=${createurId}`),
  updateMotif: (id, motif) => api.put(`/avertissements/${id}/motif?motif=${motif}`),
  count: (etudiantId) => api.get(`/avertissements/etudiant/${etudiantId}/count`),
  delete: (id) => api.delete(`/avertissements/${id}`)
};

// ==================== DÉPARTEMENTS ====================
export const departementService = {
  getAll: () => api.get('/departements'),
  getActifs: () => api.get('/departements/actifs'),
  getById: (id) => api.get(`/departements/${id}`),
  create: (departement) => api.post('/departements', departement),
  update: (id, departement) => api.put(`/departements/${id}`, departement),
  deactivate: (id) => api.put(`/departements/${id}/deactivate`),
  delete: (id) => api.delete(`/departements/${id}`)
};

// ==================== FORMATIONS ====================
export const formationService = {
  getAll: () => api.get('/formations'),
  getActives: () => api.get('/formations/actives'),
  getByDepartement: (departementId) => api.get(`/formations/departement/${departementId}`),
  getByNiveau: (niveau) => api.get(`/formations/niveau/${niveau}`),
  getById: (id) => api.get(`/formations/${id}`),
  create: (formation) => api.post('/formations', formation),
  update: (id, formation) => api.put(`/formations/${id}`, formation),
  deactivate: (id) => api.put(`/formations/${id}/deactivate`),
  delete: (id) => api.delete(`/formations/${id}`)
};

// ==================== MATIÈRES ====================
export const matiereService = {
  getAll: () => api.get('/matieres'),
  getActives: () => api.get('/matieres/actives'),
  getByFormation: (formationId) => api.get(`/matieres/formation/${formationId}`),
  getByCode: (code) => api.get(`/matieres/code/${code}`),
  getById: (id) => api.get(`/matieres/${id}`),
  create: (matiere) => api.post('/matieres', matiere),
  update: (id, matiere) => api.put(`/matieres/${id}`, matiere),
  deactivate: (id) => api.put(`/matieres/${id}/deactivate`),
  delete: (id) => api.delete(`/matieres/${id}`)
};

// ==================== GROUPES ====================
export const groupeService = {
  getAll: () => api.get('/groupes'),
  getActifs: () => api.get('/groupes/actifs'),
  getByFormation: (formationId) => api.get(`/groupes/formation/${formationId}`),
  getById: (id) => api.get(`/groupes/${id}`),
  create: (groupe) => api.post('/groupes', groupe),
  update: (id, groupe) => api.put(`/groupes/${id}`, groupe),
  deactivate: (id) => api.put(`/groupes/${id}/deactivate`),
  delete: (id) => api.delete(`/groupes/${id}`)
};

// ==================== AFFECTATIONS GROUPE-ÉTUDIANT ====================
export const groupeEtudiantService = {
  getAll: () => api.get('/groupe-etudiants'),
  getById: (id) => api.get(`/groupe-etudiants/${id}`),
  getGroupesEtudiant: (etudiantId) => api.get(`/groupe-etudiants/etudiant/${etudiantId}`),
  getEtudiantsGroupe: (groupeId) => api.get(`/groupe-etudiants/groupe/${groupeId}`),
  affecter: (etudiantId, groupeId) =>
    api.post(`/groupe-etudiants/affecter?etudiantId=${etudiantId}&groupeId=${groupeId}`),
  retirer: (etudiantId, groupeId) =>
    api.delete(`/groupe-etudiants/retirer?etudiantId=${etudiantId}&groupeId=${groupeId}`),
  supprimerToutesEtudiant: (etudiantId) =>
    api.delete(`/groupe-etudiants/etudiant/${etudiantId}/all`),
  supprimerToutesGroupe: (groupeId) =>
    api.delete(`/groupe-etudiants/groupe/${groupeId}/all`),
  verifier: (etudiantId, groupeId) =>
    api.get(`/groupe-etudiants/verifier?etudiantId=${etudiantId}&groupeId=${groupeId}`)
};

export default api;
