import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import { MdCalendarToday, MdCheckCircle, MdPeople } from 'react-icons/md';
import { seanceService, presenceService } from '../../services/api';
import './DashboardEnseignant.css';

function DashboardEnseignant() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('seances');
  const [mesSeances, setMesSeances] = useState([]);
  const [seanceActive, setSeanceActive] = useState(null);
  const [currentCode, setCurrentCode] = useState(null);
  const [codeExpiration, setCodeExpiration] = useState(null);
  const [countdown, setCountdown] = useState(30);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [presences, setPresences] = useState([]);
  const [selectedSeanceForPresences, setSelectedSeanceForPresences] = useState(null);
  
  //états pour la liste des étudiants
  const [etudiantsInscrits, setEtudiantsInscrits] = useState([]);
  const [selectedSeanceForEtudiants, setSelectedSeanceForEtudiants] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const menuItems = [
    {
      icon: <MdCalendarToday />,
      label: 'Mes Séances',
      active: activeView === 'seances',
      onClick: () => setActiveView('seances')
    },
    {
      icon: <MdCheckCircle />,
      label: 'Présences',
      active: activeView === 'presences',
      onClick: () => setActiveView('presences')
    },
    {
      icon: <MdPeople />,
      label: 'Étudiants Inscrits',
      active: activeView === 'etudiants',
      onClick: () => setActiveView('etudiants')
    }
  ];

  useEffect(() => {
    if (user) {
      loadSeances();
    }
  }, [user]);

  useEffect(() => {
    if (message.text) {
      const timer = setTimeout(() => {
        setMessage({ type: '', text: '' });
      }, 6000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  useEffect(() => {
    let interval;
    if (seanceActive && currentCode) {
      interval = setInterval(() => {
        const now = new Date().getTime();
        const expiration = new Date(codeExpiration).getTime();
        const remaining = Math.max(0, Math.floor((expiration - now) / 1000));

        setCountdown(remaining);

        if (remaining === 0) {
          renewCode();
        }
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [seanceActive, currentCode, codeExpiration]);

  const loadSeances = async () => {
    try {
      const response = await seanceService.getByEnseignant(user.id);
      setMesSeances(response.data);
    } catch (error) {
      console.error('Erreur chargement séances:', error);
    }
  };

  const startSeance = async (seanceId) => {
    try {
      const response = await seanceService.start(seanceId);
      setSeanceActive(seanceId);
      setCurrentCode(response.data.code);
      setCodeExpiration(response.data.expiration);
      setMessage({ type: 'success', text: 'Séance lancée avec succès!' });
      loadSeances();
    } catch (error) {
      const errorMessage = error.response?.data || error.response?.data?.message || '';
      let displayMessage = 'Erreur lors du lancement';

      if (errorMessage.includes('déjà terminée')) {
        displayMessage = '⚠️ Attention : Vous ne pouvez plus lancer cette séance car elle est déjà terminée.';
      } else if (errorMessage.includes('annulée')) {
        displayMessage = '⚠️ Attention : Vous ne pouvez plus lancer cette séance car elle a été annulée.';
      } else if (errorMessage) {
        displayMessage = errorMessage;
      }

      setMessage({
        type: 'warning',
        text: displayMessage
      });
    }
  };

  const stopSeance = async (seanceId) => {
    try {
      await seanceService.stop(seanceId);
      setSeanceActive(null);
      setCurrentCode(null);
      setCodeExpiration(null);
      setMessage({ type: 'success', text: 'Séance arrêtée. Absences enregistrées automatiquement.' });
      loadSeances();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de l\'arrêt'
      });
    }
  };

  const renewCode = async () => {
    if (!seanceActive) return;

    try {
      const response = await seanceService.renewCode(seanceActive);
      setCurrentCode(response.data.code);
      setCodeExpiration(response.data.expiration);
    } catch (error) {
      console.error('Erreur renouvellement code:', error);
    }
  };

  const viewPresences = async (seanceId) => {
    try {
      const response = await presenceService.getBySeance(seanceId);
      setPresences(response.data);
      setSelectedSeanceForPresences(mesSeances.find(s => s.id === seanceId));
      setActiveView('presences');
      setMessage({ type: '', text: '' });
    } catch (error) {
      setMessage({
        type: 'error',
        text: 'Erreur lors du chargement des présences'
      });
    }
  };

  // liste des étudiants inscrits
  const viewEtudiantsInscrits = async (seanceId) => {
    try {
      setLoading(true);
      const response = await seanceService.getEtudiantsInscrits(seanceId);
      setEtudiantsInscrits(response.data.etudiants);
      setSelectedSeanceForEtudiants(mesSeances.find(s => s.id === seanceId));
      setActiveView('etudiants');
      setSearchTerm('');
      setMessage({ type: '', text: '' });
    } catch (error) {
      setMessage({
        type: 'error',
        text: 'Erreur lors du chargement des étudiants inscrits'
      });
    } finally {
      setLoading(false);
    }
  };

  // Filtrer les étudiants selon la recherche
  const filteredEtudiants = etudiantsInscrits.filter(etudiant => {
    const searchLower = searchTerm.toLowerCase();
    return (
      etudiant.nom.toLowerCase().includes(searchLower) ||
      etudiant.prenom.toLowerCase().includes(searchLower) ||
      etudiant.numeroEtudiant.toLowerCase().includes(searchLower) ||
      etudiant.email.toLowerCase().includes(searchLower)
    );
  });

  const renderSeancesView = () => (
    <div className="seances-section">
      <h2>Mes Séances</h2>

      {seanceActive && currentCode && (
        <div className="active-seance-banner">
          <div className="banner-content">
            <h3>Séance en cours</h3>
            <div className="seance-id-display">
              <span className="id-label">ID Séance:</span>
              <span className="id-value">{seanceActive}</span>
            </div>
            <div className="code-display">
              <span className="code-label">Code de présence:</span>
              <span className="code-value">{currentCode}</span>
            </div>
            <div className="code-timer">
              <div className="timer-bar" style={{ width: `${(countdown / 30) * 100}%` }}></div>
              <span className="timer-text">Expire dans {countdown}s</span>
            </div>
            <button
              className="btn btn-danger"
              onClick={() => stopSeance(seanceActive)}
            >
              Arrêter la Séance
            </button>
          </div>
        </div>
      )}

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      <div className="seances-list">
        {mesSeances.length > 0 ? (
          mesSeances.map((seance) => (
            <div key={seance.id} className="seance-card">
              <div className="seance-header">
                <h3>{seance.matiere?.nom}</h3>
                <div className="seance-badges">
                  <span className={`badge ${
                    seance.statut === 'PREVUE' ? 'badge-info' :
                    seance.statut === 'REPORTEE' ? 'badge-warning' :
                    seance.statut === 'EN_COURS' ? 'badge-success' :
                    seance.statut === 'TERMINEE' ? 'badge-secondary' :
                    'badge-danger'
                  }`}>
                    {seance.statut === 'PREVUE' ? 'Prévue' :
                     seance.statut === 'REPORTEE' ? 'Reportée' :
                     seance.statut === 'EN_COURS' ? 'En cours' :
                     seance.statut === 'TERMINEE' ? 'Terminée' :
                     'Annulée'}
                  </span>
                </div>
              </div>
              <div className="seance-details">
                <p><strong>Type:</strong> {seance.typeSeance}</p>
                <p><strong>Date:</strong> {new Date(seance.dateDebut).toLocaleString('fr-FR')}</p>
                <p><strong>Durée:</strong> {new Date(seance.dateDebut).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })} - {new Date(seance.dateFin).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}</p>
                <p><strong>Salle:</strong> {seance.salle}</p>
                {seance.groupe && <p><strong>Groupe:</strong> {seance.groupe.nom}</p>}
              </div>
              <div className="seance-actions">
                {(seance.statut === 'PREVUE' || seance.statut === 'REPORTEE') && (
                  <button
                    className="btn btn-primary"
                    onClick={() => startSeance(seance.id)}
                  >
                    Lancer la Séance
                  </button>
                )}
                <button
                  className="btn btn-secondary"
                  onClick={() => viewPresences(seance.id)}
                >
                  Voir Présences
                </button>
                <button
                  className="btn btn-info"
                  onClick={() => viewEtudiantsInscrits(seance.id)}
                >
                  <MdPeople style={{ marginRight: '5px' }} />
                  Liste Étudiants
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="no-data">Aucune séance programmée</p>
        )}
      </div>
    </div>
  );

  const renderPresencesView = () => (
    <div className="presences-section">
      <div className="presences-header">
        <h2>Liste de Présences</h2>
        <button className="btn btn-secondary" onClick={() => setActiveView('seances')}>
          Retour aux Séances
        </button>
      </div>

      {selectedSeanceForPresences && (
        <div className="seance-info-card">
          <h3>{selectedSeanceForPresences.matiere?.nom}</h3>
          <p><strong>Date:</strong> {new Date(selectedSeanceForPresences.dateDebut).toLocaleString('fr-FR')}</p>
          <p><strong>Salle:</strong> {selectedSeanceForPresences.salle}</p>
          {selectedSeanceForPresences.groupe && <p><strong>Groupe:</strong> {selectedSeanceForPresences.groupe.nom}</p>}
        </div>
      )}

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      <div className="data-table">
        <h3>Présences enregistrées ({presences.length})</h3>
        {presences.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>Étudiant</th>
                <th>N° Étudiant</th>
                <th>Statut</th>
                <th>Heure de pointage</th>
                <th>Commentaire</th>
              </tr>
            </thead>
            <tbody>
              {presences.map((presence) => (
                <tr key={presence.id}>
                  <td>
                    <strong>{presence.etudiant?.nom} {presence.etudiant?.prenom}</strong>
                  </td>
                  <td>{presence.etudiant?.numeroEtudiant}</td>
                  <td>
                    <span className={`badge ${
                      presence.statut === 'PRESENT' ? 'badge-success' :
                      presence.statut === 'ABSENT' ? 'badge-danger' :
                      presence.statut === 'RETARD' ? 'badge-warning' :
                      'badge-info'
                    }`}>
                      {presence.statut === 'PRESENT' ? 'Présent' :
                       presence.statut === 'ABSENT' ? 'Absent' :
                       presence.statut === 'RETARD' ? 'Retard' :
                       'Justifié'}
                    </span>
                  </td>
                  <td>
                    {presence.heurePointage ?
                      new Date(presence.heurePointage).toLocaleTimeString('fr-FR') :
                      '-'
                    }
                  </td>
                  <td>{presence.commentaire || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">Aucune présence enregistrée pour cette séance</p>
        )}
      </div>
    </div>
  );

  const renderEtudiantsView = () => (
    <div className="etudiants-section">
      <div className="etudiants-header">
        <h2>Étudiants Inscrits</h2>
        <button className="btn btn-secondary" onClick={() => setActiveView('seances')}>
          Retour aux Séances
        </button>
      </div>

      {selectedSeanceForEtudiants && (
        <div className="seance-info-card">
          <h3>{selectedSeanceForEtudiants.matiere?.nom}</h3>
          <div className="seance-info-details">
            <p><strong>Type:</strong> {selectedSeanceForEtudiants.typeSeance}</p>
            <p><strong>Date:</strong> {new Date(selectedSeanceForEtudiants.dateDebut).toLocaleString('fr-FR')}</p>
            <p><strong>Salle:</strong> {selectedSeanceForEtudiants.salle}</p>
            {selectedSeanceForEtudiants.groupe && (
              <p><strong>Groupe:</strong> {selectedSeanceForEtudiants.groupe.nom}</p>
            )}
          </div>
        </div>
      )}

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      <div className="data-table">
        <div className="table-header-controls">
          <h3>Liste des étudiants ({filteredEtudiants.length})</h3>
          <div className="search-box">
            <input
              type="text"
              placeholder="Rechercher un étudiant..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
        </div>

        {loading ? (
          <p className="loading">Chargement...</p>
        ) : filteredEtudiants.length > 0 ? (
          <table>
            <thead>
              <tr>
                <th>N° Étudiant</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th>Téléphone</th>
                {selectedSeanceForEtudiants?.typeSeance !== 'CM' && <th>Groupe</th>}
                <th>Formation</th>
              </tr>
            </thead>
            <tbody>
              {filteredEtudiants.map((etudiant) => (
                <tr key={etudiant.id}>
                  <td><strong>{etudiant.numeroEtudiant}</strong></td>
                  <td>{etudiant.nom}</td>
                  <td>{etudiant.prenom}</td>
                  <td>{etudiant.email}</td>
                  <td>{etudiant.telephone || '-'}</td>
                  {selectedSeanceForEtudiants?.typeSeance !== 'CM' && (
                    <td>
                      <span className="badge badge-info">{etudiant.nomGroupe}</span>
                    </td>
                  )}
                  <td>{etudiant.nomFormation}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="no-data">
            {searchTerm ? 'Aucun étudiant ne correspond à votre recherche' : 'Aucun étudiant inscrit à cette séance'}
          </p>
        )}
      </div>
    </div>
  );

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-enseignant">
        <div className="dashboard-header">
          <h1>Tableau de Bord Enseignant</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {activeView === 'seances' && renderSeancesView()}
        {activeView === 'presences' && renderPresencesView()}
        {activeView === 'etudiants' && renderEtudiantsView()}
      </div>
    </DashboardLayout>
  );
}

export default DashboardEnseignant;