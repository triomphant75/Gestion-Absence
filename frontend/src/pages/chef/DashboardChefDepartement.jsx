import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import {
  MdDescription,
  MdWarning,
  MdBarChart,
  MdPeople
} from 'react-icons/md';
import { justificatifService, avertissementService, userService, presenceService } from '../../services/api';
import './DashboardChefDepartement.css';

function DashboardChefDepartement() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('justificatifs');
  const [justificatifs, setJustificatifs] = useState([]);
  const [avertissements, setAvertissements] = useState([]);
  const [etudiants, setEtudiants] = useState([]);
  const [selectedJustif, setSelectedJustif] = useState(null);
  const [commentaire, setCommentaire] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const menuItems = [
    {
      icon: <MdDescription />,
      label: 'Justificatifs',
      active: activeView === 'justificatifs',
      onClick: () => setActiveView('justificatifs')
    },
    {
      icon: <MdWarning />,
      label: 'Avertissements',
      active: activeView === 'avertissements',
      onClick: () => setActiveView('avertissements')
    },
    {
      icon: <MdBarChart />,
      label: 'Statistiques',
      active: activeView === 'statistiques',
      onClick: () => setActiveView('statistiques')
    },
    {
      icon: <MdPeople />,
      label: 'Étudiants',
      active: activeView === 'etudiants',
      onClick: () => setActiveView('etudiants')
    }
  ];

  useEffect(() => {
    if (user) {
      loadJustificatifs();
      loadAvertissements();
      loadEtudiants();
    }
  }, [user]);

  const loadJustificatifs = async () => {
    try {
      const response = await justificatifService.getEnAttente();
      setJustificatifs(response.data);
    } catch (error) {
      console.error('Erreur chargement justificatifs:', error);
    }
  };

  const loadAvertissements = async () => {
    try {
      const response = await avertissementService.getAll();
      setAvertissements(response.data);
    } catch (error) {
      console.error('Erreur chargement avertissements:', error);
    }
  };

  const loadEtudiants = async () => {
    try {
      const response = await userService.getByRole('ETUDIANT');
      setEtudiants(response.data);
    } catch (error) {
      console.error('Erreur chargement étudiants:', error);
    }
  };

  const handleValiderJustificatif = async (justifId) => {
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      await justificatifService.valider(justifId, user.id, commentaire);
      setMessage({ type: 'success', text: 'Justificatif validé avec succès!' });
      setSelectedJustif(null);
      setCommentaire('');
      loadJustificatifs();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la validation'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleRefuserJustificatif = async (justifId) => {
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      await justificatifService.refuser(justifId, user.id, commentaire);
      setMessage({ type: 'success', text: 'Justificatif refusé!' });
      setSelectedJustif(null);
      setCommentaire('');
      loadJustificatifs();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors du refus'
      });
    } finally {
      setLoading(false);
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

  const renderJustificatifsView = () => (
    <div className="justificatifs-section">
      <h2>Justificatifs en Attente</h2>

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      {justificatifs.length > 0 ? (
        <div className="justificatifs-grid">
          {justificatifs.map((justif) => (
            <div key={justif.id} className="justificatif-card">
              <div className="justificatif-header">
                <h3>Justificatif #{justif.id}</h3>
                <span className={`badge ${getStatutBadgeClass(justif.statut)}`}>
                  {justif.statut}
                </span>
              </div>
              <div className="justificatif-details">
                <p><strong>Étudiant:</strong> {justif.etudiant?.nom} {justif.etudiant?.prenom}</p>
                <p><strong>Motif:</strong> {justif.motif}</p>
                <p><strong>Déposé le:</strong> {new Date(justif.dateDepot).toLocaleDateString('fr-FR')}</p>
                {justif.absence && (
                  <>
                    <p><strong>Absence du:</strong> {new Date(justif.absence.seance?.dateDebut).toLocaleDateString('fr-FR')}</p>
                    <p><strong>Matière:</strong> {justif.absence.seance?.matiere?.nom}</p>
                  </>
                )}
              </div>
              <div className="justificatif-actions">
                {selectedJustif === justif.id ? (
                  <div className="action-form">
                    <textarea
                      value={commentaire}
                      onChange={(e) => setCommentaire(e.target.value)}
                      placeholder="Commentaire (optionnel)..."
                      rows={3}
                    />
                    <div className="action-buttons">
                      <button
                        className="btn btn-success"
                        onClick={() => handleValiderJustificatif(justif.id)}
                        disabled={loading}
                      >
                        Valider
                      </button>
                      <button
                        className="btn btn-danger"
                        onClick={() => handleRefuserJustificatif(justif.id)}
                        disabled={loading}
                      >
                        Refuser
                      </button>
                      <button
                        className="btn btn-secondary"
                        onClick={() => {
                          setSelectedJustif(null);
                          setCommentaire('');
                        }}
                      >
                        Annuler
                      </button>
                    </div>
                  </div>
                ) : (
                  <button
                    className="btn btn-primary"
                    onClick={() => setSelectedJustif(justif.id)}
                  >
                    Traiter
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="no-data">Aucun justificatif en attente</p>
      )}
    </div>
  );

  const renderAvertissementsView = () => (
    <div className="avertissements-section">
      <h2>Avertissements</h2>
      {avertissements.length > 0 ? (
        <div className="avertissements-list">
          {avertissements.map((avert) => (
            <div key={avert.id} className="avertissement-card">
              <div className="avertissement-header">
                <h3>Avertissement #{avert.id}</h3>
                <span className={`badge ${avert.typeAvertissement === 'AUTOMATIQUE' ? 'badge-warning' : 'badge-info'}`}>
                  {avert.typeAvertissement}
                </span>
              </div>
              <div className="avertissement-details">
                <p><strong>Étudiant:</strong> {avert.etudiant?.nom} {avert.etudiant?.prenom}</p>
                <p><strong>Matière:</strong> {avert.matiere?.nom}</p>
                <p><strong>Nombre d'absences:</strong> {avert.nombreAbsences}</p>
                <p><strong>Date:</strong> {new Date(avert.dateAvertissement).toLocaleDateString('fr-FR')}</p>
                <p><strong>Motif:</strong> {avert.motif}</p>
                {avert.createur && (
                  <p><strong>Créé par:</strong> {avert.createur.nom} {avert.createur.prenom}</p>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="no-data">Aucun avertissement</p>
      )}
    </div>
  );

  const renderStatistiquesView = () => (
    <div className="statistiques-section">
      <h2>Statistiques du Département</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon"><MdPeople /></div>
          <div className="stat-content">
            <h3>{etudiants.length}</h3>
            <p>Étudiants Total</p>
          </div>
        </div>
        <div className="stat-card warning">
          <div className="stat-icon"><MdDescription /></div>
          <div className="stat-content">
            <h3>{justificatifs.length}</h3>
            <p>Justificatifs en Attente</p>
          </div>
        </div>
        <div className="stat-card danger">
          <div className="stat-icon"><MdWarning /></div>
          <div className="stat-content">
            <h3>{avertissements.length}</h3>
            <p>Avertissements Total</p>
          </div>
        </div>
      </div>
    </div>
  );

  const renderEtudiantsView = () => (
    <div className="etudiants-section">
      <h2>Liste des Étudiants</h2>
      {etudiants.length > 0 ? (
        <div className="etudiants-table">
          <table>
            <thead>
              <tr>
                <th>Numéro</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th>Formation</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {etudiants.map((etudiant) => (
                <tr key={etudiant.id}>
                  <td>{etudiant.numeroEtudiant}</td>
                  <td>{etudiant.nom}</td>
                  <td>{etudiant.prenom}</td>
                  <td>{etudiant.email}</td>
                  <td>{etudiant.formation?.nom || 'N/A'}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-info"
                      onClick={async () => {
                        try {
                          const stats = await presenceService.getStatistiques(etudiant.id);
                          console.log('Statistiques étudiant:', stats.data);
                        } catch (error) {
                          console.error('Erreur:', error);
                        }
                      }}
                    >
                      Voir Stats
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <p className="no-data">Aucun étudiant</p>
      )}
    </div>
  );

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-chef">
        <div className="dashboard-header">
          <h1>Tableau de Bord Chef de Département</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {activeView === 'justificatifs' && renderJustificatifsView()}
        {activeView === 'avertissements' && renderAvertissementsView()}
        {activeView === 'statistiques' && renderStatistiquesView()}
        {activeView === 'etudiants' && renderEtudiantsView()}
      </div>
    </DashboardLayout>
  );
}

export default DashboardChefDepartement;
