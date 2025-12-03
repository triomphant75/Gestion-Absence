import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import {
  MdCheckCircle,
  MdBarChart,
  MdEventBusy,
  MdAttachFile,
  MdBook,
  MdAccessTime
} from 'react-icons/md';
import { presenceService, justificatifService, groupeEtudiantService, seanceService } from '../../services/api';
import './DashboardEtudiant.css';

function DashboardEtudiant() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('validate');
  const [statistics, setStatistics] = useState(null);
  const [mesAbsences, setMesAbsences] = useState([]);
  const [mesJustificatifs, setMesJustificatifs] = useState([]);
  const [seanceCode, setSeanceCode] = useState('');
  const [seanceId, setSeanceId] = useState('');
  const [loading, setLoading] = useState(false);
  const [validateMessage, setValidateMessage] = useState({ type: '', text: '' });
  const [justifMessage, setJustifMessage] = useState({ type: '', text: '' });
  const [uploadFiles, setUploadFiles] = useState({});
  const [uploadMotifs, setUploadMotifs] = useState({});

  // Utilise le vrai nom du fichier stocké côté backend
  const justificatifNom = (justif) => {
    return justif.fichierPath || `justificatif-${justif.id}`;
  };

  const menuItems = [
    {
      icon: <MdCheckCircle />,
      label: 'Valider Présence',
      active: activeView === 'validate',
      onClick: () => setActiveView('validate')
    },
    {
      icon: <MdBarChart />,
      label: 'Mes Statistiques',
      active: activeView === 'stats',
      onClick: () => setActiveView('stats')
    },
    {
      icon: <MdEventBusy />,
      label: 'Mes Absences',
      active: activeView === 'absences',
      onClick: () => setActiveView('absences')
    },
    {
      icon: <MdAttachFile />,
      label: 'Justificatifs',
      active: activeView === 'justificatifs',
      onClick: () => setActiveView('justificatifs')
    }
  ];

  useEffect(() => {
    if (user) {
      loadStatistics();
      loadAbsences();
      loadJustificatifs();
    }
  }, [user]);

  const loadStatistics = async () => {
    try {
      const response = await presenceService.getStatistiques(user.id);
      setStatistics(response.data);
    } catch (error) {
      console.error('Erreur chargement statistiques:', error);
    }
  };

  const loadAbsences = async () => {
    try {
      const response = await presenceService.getByEtudiant(user.id);
      const absences = response.data.filter(p => p.statut === 'ABSENT');
      setMesAbsences(absences);
    } catch (error) {
      console.error('Erreur chargement absences:', error);
    }
  };

  const loadJustificatifs = async () => {
    try {
      const response = await justificatifService.getByEtudiant(user.id);
      setMesJustificatifs(response.data);
    } catch (error) {
      console.error('Erreur chargement justificatifs:', error);
    }
  };

  const handleValidatePresence = async (e) => {
    e.preventDefault();
    setLoading(true);
    setValidateMessage({ type: '', text: '' });

    try {
      // Appel correct : (seanceId, code, userId)
      await presenceService.validateCode(
        parseInt(seanceId),
        seanceCode.toUpperCase(),
        user.id
      );
      setValidateMessage({ type: 'success', text: 'Présence validée avec succès!' });
      setSeanceCode('');
      setSeanceId('');
      loadStatistics();
    } catch (error) {
      setValidateMessage({
        type: 'error',
        text: error.response?.data?.message || 'Code invalide ou expiré'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUploadJustificatif = async (absenceId) => {
    const file = uploadFiles[absenceId];
    const motif = uploadMotifs[absenceId] || '';

    if (!file) {
      setJustifMessage({ type: 'error', text: 'Veuillez sélectionner un fichier' });
      return;
    }

    try {
      setLoading(true);
      await justificatifService.deposer(user.id, absenceId, motif, file);
      setJustifMessage({ type: 'success', text: 'Justificatif déposé avec succès!' });
      // reset inputs for this absence
      setUploadFiles(prev => ({ ...prev, [absenceId]: null }));
      setUploadMotifs(prev => ({ ...prev, [absenceId]: '' }));
      loadJustificatifs();
      loadAbsences();
    } catch (error) {
      let msg = 'Erreur lors du dépôt';
      if (error.response?.status === 409) {
        msg = error.response.data || 'Un justificatif a déjà été déposé pour cette absence.';
      } else if (error.response?.data) {
        msg = error.response.data;
      }
      setJustifMessage({
        type: 'error',
        text: msg
      });
    } finally {
      setLoading(false);
    }
  };

  const handleViewJustificatif = async (justifId, filename) => {
    try {
      setLoading(true);
      const response = await justificatifService.download(justifId);
      const blob = new Blob([response.data], { type: response.headers['content-type'] || 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);
      // Open in new tab to allow viewing (PDF or image)
      window.open(url, '_blank');
      // release object URL after some time
      setTimeout(() => window.URL.revokeObjectURL(url), 10000);
    } catch (error) {
      console.error('Erreur ouverture justificatif:', error);
      setJustifMessage({ type: 'error', text: 'Impossible d\u00e9ouvrir le justificatif' });
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadJustificatif = async (justifId, filename) => {
    try {
      setLoading(true);
      const response = await justificatifService.download(justifId);
      const blob = new Blob([response.data], { type: response.headers['content-type'] || 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename || `justificatif-${justifId}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      setTimeout(() => window.URL.revokeObjectURL(url), 10000);
    } catch (error) {
      console.error('Erreur téléchargement justificatif:', error);
      setJustifMessage({ type: 'error', text: 'Impossible de télécharger le justificatif' });
    } finally {
      setLoading(false);
    }
  };

  const getStatutBadgeClass = (statut) => {
    switch (statut) {
      case 'EN_ATTENTE': return 'badge-warning';
      case 'ACCEPTE': return 'badge-success';
      case 'REFUSE': return 'badge-error';
      default: return '';
    }
  };

  const renderValidateView = () => (
    <div className="validate-section">
      <h2>Valider ma Présence</h2>
      <div className="validate-card">
        <p className="validate-info">
          Entrez le code affiché par votre enseignant et l'ID de la séance pour valider votre présence.
        </p>
        <form onSubmit={handleValidatePresence}>
          <div className="form-group">
            <label>ID Séance *</label>
            <input
              type="number"
              value={seanceId}
              onChange={(e) => setSeanceId(e.target.value)}
              placeholder="Ex: 1"
              required
            />
          </div>
          <div className="form-group">
            <label>Code de Présence *</label>
            <input
              type="text"
              value={seanceCode}
              onChange={(e) => setSeanceCode(e.target.value.toUpperCase())}
              placeholder="Ex: A3X9K2"
              maxLength={6}
              required
              className="code-input"
            />
            <small>Le code contient 6 caractères et change toutes les 30 secondes</small>
          </div>
          {validateMessage.text && (
            <div className={`message ${validateMessage.type}`}>
              {validateMessage.text}
            </div>
          )}
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Validation...' : 'Valider ma Présence'}
          </button>
        </form>
      </div>
    </div>
  );

  const renderStatsView = () => (
    <div className="stats-section">
      <h2>Mes Statistiques</h2>
      {statistics ? (
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon"><MdBook /></div>
            <div className="stat-content">
              <h3>{statistics.totalSeances}</h3>
              <p>Séances Total</p>
            </div>
          </div>
          <div className="stat-card success">
            <div className="stat-icon"><MdCheckCircle /></div>
            <div className="stat-content">
              <h3>{statistics.totalPresences}</h3>
              <p>Présences</p>
            </div>
          </div>
          <div className="stat-card error">
            <div className="stat-icon"><MdEventBusy /></div>
            <div className="stat-content">
              <h3>{statistics.totalAbsences}</h3>
              <p>Absences</p>
            </div>
          </div>
          <div className="stat-card warning">
            <div className="stat-icon"><MdAccessTime /></div>
            <div className="stat-content">
              <h3>{statistics.totalRetards}</h3>
              <p>Retards</p>
            </div>
          </div>
          <div className="stat-card info">
            <div className="stat-icon">%</div>
            <div className="stat-content">
              <h3>{statistics.tauxAbsence.toFixed(1)}%</h3>
              <p>Taux d'Absence</p>
            </div>
          </div>
          <div className="stat-card danger">
            <div className="stat-icon">⚠</div>
            <div className="stat-content">
              <h3>{statistics.nombreAvertissements}</h3>
              <p>Avertissements</p>
            </div>
          </div>
        </div>
      ) : (
        <p>Chargement des statistiques...</p>
      )}
    </div>
  );

  const renderAbsencesView = () => (
    <div className="absences-section">
      <h2>Mes Absences</h2>
      {mesAbsences.length > 0 ? (
        <div className="absences-list">
          {mesAbsences.map((absence) => (
            <div key={absence.id} className="absence-card">
              <div className="absence-header">
                <h3>{absence.seance?.matiere?.nom || 'Matière'}</h3>
                <span className="badge badge-error">Absent</span>
              </div>
              <div className="absence-details">
                <p><strong>Date:</strong> {new Date(absence.seance?.dateDebut).toLocaleString('fr-FR')}</p>
                <p><strong>Type:</strong> {absence.seance?.typeSeance}</p>
                <p><strong>Enseignant:</strong> {absence.seance?.enseignant?.nom} {absence.seance?.enseignant?.prenom}</p>
              </div>
              {!absence.justificatif && (
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    setActiveView('justificatifs');
                    document.getElementById(`upload-${absence.id}`)?.scrollIntoView();
                  }}
                >
                  Déposer un Justificatif
                </button>
              )}
              {absence.justificatif && (
                <div className="justificatif-status">
                  <span className={`badge ${getStatutBadgeClass(absence.justificatif.statut)}`}>
                    {absence.justificatif.statut}
                  </span>
                </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <p className="no-data">Aucune absence enregistrée</p>
      )}
    </div>
  );

  const renderJustificatifsView = () => (
    <div className="justificatifs-section">
      <h2>Mes Justificatifs</h2>
      {/* Déposer un justificatif pour une absence */}
      {mesAbsences.length > 0 && (
        <div className="depot-justificatif-list">
          <h3>Déposer un justificatif</h3>
          {justifMessage.text && (
            <div className={`message ${justifMessage.type}`}>
              {justifMessage.text}
            </div>
          )}
          {mesAbsences.map((absence) => (
            !absence.justificatif && (
              <div key={absence.id} id={`upload-${absence.id}`} className="depot-justificatif-card">
                <p><strong>{absence.seance?.matiere?.nom}</strong> - {new Date(absence.seance?.dateDebut).toLocaleString('fr-FR')}</p>
                <div className="depot-justificatif-controls">
                  <input
                    id={`file-absence-${absence.id}`}
                    type="file"
                    accept="application/pdf,image/*"
                    style={{ display: 'none' }}
                    onChange={e => {
                      const file = e.target.files[0];
                      if (file && !['application/pdf', 'image/png', 'image/jpeg', 'image/jpg', 'image/gif', 'image/webp'].includes(file.type)) {
                        setJustifMessage({ type: 'error', text: 'Seuls les fichiers PDF ou image sont autorisés.' });
                        return;
                      }
                      setUploadFiles(prev => ({ ...prev, [absence.id]: file }));
                    }}
                  />
                  <label 
                    htmlFor={`file-absence-${absence.id}`} 
                    className="file-btn"
                    style={{ 
                      display: 'inline-flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      cursor: 'pointer', 
                      background: '#ff9800', 
                      color: '#fff', 
                      padding: '0.5rem 1rem', 
                      borderRadius: '8px', 
                      fontWeight: 600,
                      whiteSpace: 'nowrap',
                      minHeight: '40px',
                      border: 'none',
                      flexShrink: 0
                    }}
                  >
                    Choisir un fichier
                  </label>
                  <span className="file-name">{uploadFiles[absence.id]?.name || 'Aucun fichier choisi'}</span>
                  <input
                    type="text"
                    className="justif-motif-input"
                    placeholder="Motif (optionnel)"
                    value={uploadMotifs[absence.id] || ''}
                    onChange={(e) => setUploadMotifs(prev => ({ ...prev, [absence.id]: e.target.value }))}
                  />
                  <button className="btn btn-primary" onClick={() => handleUploadJustificatif(absence.id)} disabled={loading}>
                    {loading ? 'Envoi...' : 'Déposer'}
                  </button>
                </div>
              </div>
            )
          ))}
        </div>
      )}
      {mesJustificatifs.length > 0 ? (
        <div className="justificatifs-list">
          {mesJustificatifs.map((justif) => (
            <div key={justif.id} className="justificatif-card">
              <div className="justificatif-header">
                <h3>Justificatif #{justif.id}</h3>
                <span className={`badge ${getStatutBadgeClass(justif.statut)}`}>
                  {justif.statut}
                </span>
              </div>
              <div className="justificatif-details">
                <p><strong>Motif:</strong> {justif.motif}</p>
                <p><strong>Déposé le:</strong> {justif.createdAt ? new Date(justif.createdAt).toLocaleDateString('fr-FR') : '-'}</p>
                {justif.dateValidation && (
                  <p><strong>Validé le:</strong> {new Date(justif.dateValidation).toLocaleDateString('fr-FR')}</p>
                )}
                {justif.commentaireValidation && (
                  <p><strong>Commentaire:</strong> {justif.commentaireValidation}</p>
                )}
                <div style={{ marginTop: '0.75rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <button
                    className="btn btn-secondary"
                    onClick={() => handleViewJustificatif(justif.id, justificatifNom(justif))}
                  >
                    Voir
                  </button>
                  <button
                    className="btn"
                    onClick={() => handleDownloadJustificatif(justif.id, justificatifNom(justif))}
                  >
                    Télécharger
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="no-data">Aucun justificatif déposé</p>
      )}
    </div>
  );

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-etudiant">
        <div className="dashboard-header">
          <h1>Tableau de Bord Étudiant</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {activeView === 'validate' && renderValidateView()}
        {activeView === 'stats' && renderStatsView()}
        {activeView === 'absences' && renderAbsencesView()}
        {activeView === 'justificatifs' && renderJustificatifsView()}
      </div>
    </DashboardLayout>
  );
}

export default DashboardEtudiant;
