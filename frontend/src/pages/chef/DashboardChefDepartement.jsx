import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import Pagination from '../../components/common/Pagination';
import usePagination from '../../hooks/usePagination';
import ModalInformationsEtudiant from '../../components/chef/ModalInformationsEtudiant';
import {
  MdDescription,
  MdWarning,
  MdBarChart,
  MdPeople,
  MdHistory
} from 'react-icons/md';
import { justificatifService, avertissementService, chefDepartementService, formationService } from '../../services/api';
import './DashboardChefDepartement.css';

function DashboardChefDepartement() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('justificatifs');
  const [justificatifs, setJustificatifs] = useState([]);
  const [avertissements, setAvertissements] = useState([]);
  const [etudiants, setEtudiants] = useState([]);
  const [formations, setFormations] = useState([]);
  const [selectedFormation, setSelectedFormation] = useState('all');
  const [selectedEtudiant, setSelectedEtudiant] = useState(null);
  const [etudiantDetails, setEtudiantDetails] = useState(null);
  const [traitements, setTraitements] = useState([]);
  const [selectedJustif, setSelectedJustif] = useState(null);
  const [commentaire, setCommentaire] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Pagination
  const justificatifsPagination = usePagination(justificatifs, 5);
  const traitementsPagination = usePagination(traitements, 5);
  const avertissementsPagination = usePagination(avertissements, 5);
  const etudiantsPagination = usePagination(etudiants, 5);

  // Utilise le vrai nom du fichier stocké côté backend
  const justificatifNom = (justif) => {
    return justif.fichierPath || `justificatif-${justif.id}`;
  };

  const handleViewJustificatif = async (justifId, filename) => {
    try {
      setLoading(true);
      const response = await justificatifService.download(justifId);
      const blob = new Blob([response.data], { type: response.headers['content-type'] || 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank');
      setTimeout(() => window.URL.revokeObjectURL(url), 10000);
    } catch (error) {
      console.error('Erreur ouverture justificatif:', error);
      setMessage({ type: 'error', text: 'Impossible d\'ouvrir le justificatif' });
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
      setMessage({ type: 'error', text: 'Impossible de télécharger le justificatif' });
    } finally {
      setLoading(false);
    }
  };

  const menuItems = [
    {
      icon: <MdDescription />,
      label: 'Justificatifs',
      active: activeView === 'justificatifs',
      onClick: () => setActiveView('justificatifs')
    },
    {
      icon: <MdHistory />,
      label: 'Historique',
      active: activeView === 'historique',
      onClick: () => setActiveView('historique')
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
      loadTraitements();
      loadFormations();
    }
  }, [user]);

  useEffect(() => {
    if (user && selectedFormation) {
      loadEtudiants();
    }
  }, [selectedFormation]);

  const loadTraitements = async () => {
    try {
      const response = await justificatifService.getTraitesByValidateur(user.id);
      setTraitements(response.data);
    } catch (error) {
      console.error('Erreur chargement historique de traitements:', error);
    }
  };

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

  const loadFormations = async () => {
    try {
      if (user?.departement?.id) {
        const response = await formationService.getByDepartement(user.departement.id);
        setFormations(response.data);
      }
    } catch (error) {
      console.error('Erreur chargement formations:', error);
    }
  };

  const loadEtudiants = async () => {
    try {
      if (selectedFormation === 'all') {
        const response = await chefDepartementService.getEtudiantsDuDepartement(user.id);
        setEtudiants(response.data);
      } else {
        const response = await chefDepartementService.getEtudiantsByFormation(user.id, selectedFormation);
        setEtudiants(response.data);
      }
    } catch (error) {
      console.error('Erreur chargement étudiants:', error);
    }
  };

  const handleVoirInformations = async (etudiantId) => {
    try {
      setLoading(true);
      const response = await chefDepartementService.getEtudiantDetails(user.id, etudiantId);
      setEtudiantDetails(response.data);
      setSelectedEtudiant(etudiantId);
    } catch (error) {
      console.error('Erreur chargement détails étudiant:', error);
      setMessage({ type: 'error', text: 'Impossible de charger les informations de l\'étudiant' });
    } finally {
      setLoading(false);
    }
  };

  const handleCloseModal = () => {
    setSelectedEtudiant(null);
    setEtudiantDetails(null);
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
      <h2>Justificatifs en Attente ({justificatifs.length})</h2>

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      {justificatifs.length > 0 ? (
        <>
          <div className="justificatifs-grid">
            {justificatifsPagination.paginatedItems.map((justif) => (
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
              <div style={{ marginTop: '0.75rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <button
                  className="btn btn-secondary"
                  onClick={() => handleViewJustificatif(justif.id, justificatifNom(justif))}
                  disabled={loading}
                >
                  Voir
                </button>
                <button
                  className="btn"
                  onClick={() => handleDownloadJustificatif(justif.id, justificatifNom(justif))}
                  disabled={loading}
                  style={{ background: '#6c757d', color: '#fff' }}
                >
                  Télécharger
                </button>
              </div>
            </div>
            ))}
          </div>
          {justificatifs.length > 5 && (
            <Pagination
              currentPage={justificatifsPagination.currentPage}
              totalPages={justificatifsPagination.totalPages}
              onPageChange={justificatifsPagination.goToPage}
              hasNextPage={justificatifsPagination.hasNextPage}
              hasPreviousPage={justificatifsPagination.hasPreviousPage}
            />
          )}
        </>
      ) : (
        <p className="no-data">Aucun justificatif en attente</p>
      )}
    </div>
  );

  const renderHistoriqueView = () => (
    <div className="historique-section">
      <h2>Historique des Traitements ({traitements.length})</h2>

      {traitements.length > 0 ? (
        <>
          <div className="justificatifs-list">
          {traitementsPagination.paginatedItems.map((justif) => (
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
                <p><strong>Déposé le:</strong> {justif.dateDepot ? new Date(justif.dateDepot).toLocaleDateString('fr-FR') : '-'}</p>
                {justif.dateValidation && (
                  <p><strong>Traitée le:</strong> {new Date(justif.dateValidation).toLocaleDateString('fr-FR')}</p>
                )}
                {justif.commentaireValidation && (
                  <p><strong>Commentaire:</strong> {justif.commentaireValidation}</p>
                )}
                {justif.absence && (
                  <>
                    <p><strong>Absence du:</strong> {new Date(justif.absence.seance?.dateDebut).toLocaleDateString('fr-FR')}</p>
                    <p><strong>Matière:</strong> {justif.absence.seance?.matiere?.nom}</p>
                  </>
                )}
                <div style={{ marginTop: '0.75rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <button
                    className="btn btn-secondary"
                    onClick={() => handleViewJustificatif(justif.id, justificatifNom(justif))}
                    disabled={loading}
                  >
                    Voir
                  </button>
                  <button
                    className="btn"
                    onClick={() => handleDownloadJustificatif(justif.id, justificatifNom(justif))}
                    disabled={loading}
                    style={{ background: '#6c757d', color: '#fff' }}
                  >
                    Télécharger
                  </button>
                </div>
              </div>
            </div>
          ))}
          </div>
          {traitements.length > 5 && (
            <Pagination
              currentPage={traitementsPagination.currentPage}
              totalPages={traitementsPagination.totalPages}
              onPageChange={traitementsPagination.goToPage}
              hasNextPage={traitementsPagination.hasNextPage}
              hasPreviousPage={traitementsPagination.hasPreviousPage}
            />
          )}
        </>
      ) : (
        <p className="no-data">Aucun traitement réalisé</p>
      )}
    </div>
  );

  const renderAvertissementsView = () => (
    <div className="avertissements-section">
      <h2>Avertissements ({avertissements.length})</h2>
      {avertissements.length > 0 ? (
        <>
          <div className="avertissements-list">
            {avertissementsPagination.paginatedItems.map((avert) => (
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
          {avertissements.length > 5 && (
            <Pagination
              currentPage={avertissementsPagination.currentPage}
              totalPages={avertissementsPagination.totalPages}
              onPageChange={avertissementsPagination.goToPage}
              hasNextPage={avertissementsPagination.hasNextPage}
              hasPreviousPage={avertissementsPagination.hasPreviousPage}
            />
          )}
        </>
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

  const renderEtudiantsView = () => {
    const getStatutColor = (taux) => {
      if (taux >= 80) return '#10b981';
      if (taux >= 60) return '#f59e0b';
      return '#ef4444';
    };

    return (
      <div className="etudiants-section">
        <div className="etudiants-header">
          <h2>Liste des Étudiants ({etudiants.length})</h2>
          <div className="filter-section">
            <label htmlFor="formation-filter">Filtrer par formation:</label>
            <select
              id="formation-filter"
              value={selectedFormation}
              onChange={(e) => setSelectedFormation(e.target.value)}
              className="formation-select"
            >
              <option value="all">Toutes les formations</option>
              {formations.map((formation) => (
                <option key={formation.id} value={formation.id}>
                  {formation.nom}
                </option>
              ))}
            </select>
          </div>
        </div>

        {etudiants.length > 0 ? (
          <>
            <div className="etudiants-table">
              <table>
                <thead>
                  <tr>
                    <th>Numéro</th>
                    <th>Nom</th>
                    <th>Prénom</th>
                    <th>Email</th>
                    <th>Formation</th>
                    <th>Taux Présence</th>
                    <th>Absences</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {etudiantsPagination.paginatedItems.map((etudiant) => {
                    const stats = etudiant.statistiques || {};
                    const tauxPresence = stats.tauxPresence || 0;

                    return (
                      <tr key={etudiant.id}>
                        <td>{etudiant.numeroEtudiant}</td>
                        <td>{etudiant.nom}</td>
                        <td>{etudiant.prenom}</td>
                        <td>{etudiant.email}</td>
                        <td>{etudiant.formation?.nom || 'N/A'}</td>
                        <td>
                          <span
                            className="taux-badge"
                            style={{
                              backgroundColor: getStatutColor(tauxPresence),
                              color: 'white',
                              padding: '0.25rem 0.75rem',
                              borderRadius: '12px',
                              fontWeight: '600'
                            }}
                          >
                            {tauxPresence}%
                          </span>
                        </td>
                        <td>
                          <div style={{ fontSize: '0.9rem' }}>
                            <div style={{ color: '#ef4444' }}>
                              Non justifiées: {stats.absencesNonJustifiees || 0}
                            </div>
                            <div style={{ color: '#6b7280' }}>
                              Justifiées: {stats.absencesJustifiees || 0}
                            </div>
                          </div>
                        </td>
                        <td>
                          <button
                            className="btn btn-sm btn-info"
                            onClick={() => handleVoirInformations(etudiant.id)}
                            disabled={loading}
                          >
                            Voir Informations
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
            {etudiants.length > 5 && (
              <Pagination
                currentPage={etudiantsPagination.currentPage}
                totalPages={etudiantsPagination.totalPages}
                onPageChange={etudiantsPagination.goToPage}
                hasNextPage={etudiantsPagination.hasNextPage}
                hasPreviousPage={etudiantsPagination.hasPreviousPage}
              />
            )}
          </>
        ) : (
          <p className="no-data">Aucun étudiant</p>
        )}
      </div>
    );
  };

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-chef">
        <div className="dashboard-header">
          <h1>Tableau de Bord Chef de Département</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {activeView === 'justificatifs' && renderJustificatifsView()}
        {activeView === 'historique' && renderHistoriqueView()}
        {activeView === 'avertissements' && renderAvertissementsView()}
        {activeView === 'statistiques' && renderStatistiquesView()}
        {activeView === 'etudiants' && renderEtudiantsView()}
      </div>

      {selectedEtudiant && etudiantDetails && (
        <ModalInformationsEtudiant
          etudiant={etudiantDetails}
          onClose={handleCloseModal}
        />
      )}
    </DashboardLayout>
  );
}

export default DashboardChefDepartement;
