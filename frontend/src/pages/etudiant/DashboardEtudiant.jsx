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
  const [message, setMessage] = useState({ type: '', text: '' });

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
    setMessage({ type: '', text: '' });

    try {
      // Appel correct : (seanceId, code, userId)
      await presenceService.validateCode(
        parseInt(seanceId),
        seanceCode.toUpperCase(),
        user.id
      );
      setMessage({ type: 'success', text: 'Présence validée avec succès!' });
      setSeanceCode('');
      setSeanceId('');
      loadStatistics();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Code invalide ou expiré'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUploadJustificatif = async (absenceId, file, motif) => {
    const formData = new FormData();
    formData.append('fichier', file);
    formData.append('etudiantId', user.id);
    formData.append('absenceId', absenceId);
    formData.append('motif', motif);

    try {
      await justificatifService.create(formData);
      setMessage({ type: 'success', text: 'Justificatif déposé avec succès!' });
      loadJustificatifs();
      loadAbsences();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors du dépôt'
      });
    }
  };

  const getStatutBadgeClass = (statut) => {
    switch (statut) {
      case 'EN_ATTENTE': return 'badge-warning';
      case 'VALIDE': return 'badge-success';
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
          {message.text && (
            <div className={`message ${message.type}`}>
              {message.text}
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
                <p><strong>Déposé le:</strong> {new Date(justif.dateDepot).toLocaleDateString('fr-FR')}</p>
                {justif.dateValidation && (
                  <p><strong>Validé le:</strong> {new Date(justif.dateValidation).toLocaleDateString('fr-FR')}</p>
                )}
                {justif.commentaireValidateur && (
                  <p><strong>Commentaire:</strong> {justif.commentaireValidateur}</p>
                )}
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
