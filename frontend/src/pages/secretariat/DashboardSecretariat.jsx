import { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import Pagination from '../../components/common/Pagination';
import usePagination from '../../hooks/usePagination';
import {
  MdPeople,
  MdGroup,
  MdCalendarToday,
  MdPersonAdd
} from 'react-icons/md';
import {
  userService,
  groupeService,
  seanceService,
  formationService,
  matiereService,
  groupeEtudiantService
} from '../../services/api';
import './DashboardSecretariat.css';

function DashboardSecretariat() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('etudiants');
  const [etudiants, setEtudiants] = useState([]);
  const [groupes, setGroupes] = useState([]);
  const [seances, setSeances] = useState([]);
  const [formations, setFormations] = useState([]);
  const [matieres, setMatieres] = useState([]);
  const [enseignants, setEnseignants] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // États pour formulaires
  const [showAddEtudiantForm, setShowAddEtudiantForm] = useState(false);
  const [showAddGroupeForm, setShowAddGroupeForm] = useState(false);
  const [showAddSeanceForm, setShowAddSeanceForm] = useState(false);

  // États pour gestion des étudiants d'un groupe
  const [selectedGroupeForStudents, setSelectedGroupeForStudents] = useState(null);
  const [groupeEtudiants, setGroupeEtudiants] = useState([]);

  const [newEtudiant, setNewEtudiant] = useState({
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    numeroEtudiant: '',
    formationId: '',
    motDePasse: 'password123'
  });

  const [newGroupe, setNewGroupe] = useState({
    nom: '',
    formationId: '',
    capaciteMax: 30
  });

  const [newSeance, setNewSeance] = useState({
    matiereId: '',
    enseignantId: '',
    typeSeance: 'CM',
    dateDebut: '',
    dateFin: '',
    salle: '',
    commentaire: '',
    groupeId: ''
  });

  // Pagination
  const etudiantsPagination = usePagination(etudiants, 10);
  const groupesPagination = usePagination(groupes, 5);
  const seancesPagination = usePagination(seances, 5);

  const menuItems = [
    {
      icon: <MdPeople />,
      label: 'Étudiants',
      active: activeView === 'etudiants',
      onClick: () => setActiveView('etudiants')
    },
    {
      icon: <MdGroup />,
      label: 'Groupes TD/TP',
      active: activeView === 'groupes',
      onClick: () => setActiveView('groupes')
    },
    {
      icon: <MdCalendarToday />,
      label: 'Séances',
      active: activeView === 'seances',
      onClick: () => setActiveView('seances')
    }
  ];

  useEffect(() => {
    loadData();
  }, []);

  useEffect(() => {
    if (message.text) {
      const timer = setTimeout(() => {
        setMessage({ type: '', text: '' });
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [etudiantsRes, groupesRes, seancesRes, formationsRes, matieresRes, enseignantsRes] = await Promise.all([
        userService.getEtudiants(),
        groupeService.getAll(),
        seanceService.getAll(),
        formationService.getAll(),
        matiereService.getAll(),
        userService.getEnseignants()
      ]);

      setEtudiants(etudiantsRes.data);
      setGroupes(groupesRes.data);
      setSeances(seancesRes.data);
      setFormations(formationsRes.data);
      setMatieres(matieresRes.data);
      setEnseignants(enseignantsRes.data);
    } catch (error) {
      console.error('Erreur chargement données:', error);
      setMessage({ type: 'error', text: 'Erreur lors du chargement des données' });
    } finally {
      setLoading(false);
    }
  };

  const handleAddEtudiant = async (e) => {
    e.preventDefault();
    try {
      await userService.createEtudiant(newEtudiant);
      setMessage({ type: 'success', text: 'Étudiant ajouté avec succès' });
      setShowAddEtudiantForm(false);
      setNewEtudiant({
        nom: '',
        prenom: '',
        email: '',
        telephone: '',
        numeroEtudiant: '',
        formationId: '',
        motDePasse: 'password123'
      });
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Erreur lors de l\'ajout de l\'étudiant' });
    }
  };

  const handleAddGroupe = async (e) => {
    e.preventDefault();
    try {
      await groupeService.create(newGroupe);
      setMessage({ type: 'success', text: 'Groupe créé avec succès' });
      setShowAddGroupeForm(false);
      setNewGroupe({
        nom: '',
        formationId: '',
        capaciteMax: 30
      });
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Erreur lors de la création du groupe' });
    }
  };

  const handleAddSeance = async (e) => {
    e.preventDefault();
    try {
      await seanceService.create(newSeance);
      setMessage({ type: 'success', text: 'Séance créée avec succès' });
      setShowAddSeanceForm(false);
      setNewSeance({
        matiereId: '',
        enseignantId: '',
        typeSeance: 'CM',
        dateDebut: '',
        dateFin: '',
        salle: '',
        commentaire: '',
        groupeId: ''
      });
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Erreur lors de la création de la séance' });
    }
  };

  const handleDeleteGroupe = async (groupeId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce groupe ?')) return;

    try {
      await groupeService.delete(groupeId);
      setMessage({ type: 'success', text: 'Groupe supprimé avec succès' });
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: 'Erreur lors de la suppression du groupe' });
    }
  };

  const handleDeleteSeance = async (seanceId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette séance ?')) return;

    try {
      await seanceService.delete(seanceId);
      setMessage({ type: 'success', text: 'Séance supprimée avec succès' });
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: 'Erreur lors de la suppression de la séance' });
    }
  };

  const loadGroupeEtudiants = async (groupeId) => {
    try {
      const response = await groupeEtudiantService.getEtudiantsGroupe(groupeId);
      setGroupeEtudiants(response.data);
    } catch (error) {
      console.error('Erreur chargement étudiants du groupe:', error);
      setMessage({ type: 'error', text: 'Erreur lors du chargement des étudiants du groupe' });
    }
  };

  const handleAjouterEtudiantAuGroupe = async (groupeId, etudiantId) => {
    try {
      await groupeEtudiantService.affecter(etudiantId, groupeId);
      setMessage({ type: 'success', text: 'Étudiant ajouté au groupe avec succès' });
      loadGroupeEtudiants(groupeId);
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Erreur lors de l\'ajout de l\'étudiant au groupe' });
    }
  };

  const handleRetirerEtudiantDuGroupe = async (groupeId, etudiantId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir retirer cet étudiant du groupe ?')) return;

    try {
      await groupeEtudiantService.retirer(etudiantId, groupeId);
      setMessage({ type: 'success', text: 'Étudiant retiré du groupe avec succès' });
      loadGroupeEtudiants(groupeId);
      loadData();
    } catch (error) {
      setMessage({ type: 'error', text: 'Erreur lors du retrait de l\'étudiant du groupe' });
    }
  };

  const renderEtudiantsView = () => (
    <div className="etudiants-section">
      <div className="section-header">
        <h2>Gestion des Étudiants</h2>
        <button
          className="btn btn-primary"
          onClick={() => setShowAddEtudiantForm(!showAddEtudiantForm)}
        >
          <MdPersonAdd style={{ marginRight: '5px' }} />
          Ajouter un étudiant
        </button>
      </div>

      {showAddEtudiantForm && (
        <div className="form-card">
          <h3>Nouvel Étudiant</h3>
          <form onSubmit={handleAddEtudiant}>
            <div className="form-row">
              <div className="form-group">
                <label>Nom *</label>
                <input
                  type="text"
                  value={newEtudiant.nom}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, nom: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Prénom *</label>
                <input
                  type="text"
                  value={newEtudiant.prenom}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, prenom: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Email *</label>
                <input
                  type="email"
                  value={newEtudiant.email}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, email: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Téléphone</label>
                <input
                  type="tel"
                  value={newEtudiant.telephone}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, telephone: e.target.value })}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Numéro Étudiant *</label>
                <input
                  type="text"
                  value={newEtudiant.numeroEtudiant}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, numeroEtudiant: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Formation *</label>
                <select
                  value={newEtudiant.formationId}
                  onChange={(e) => setNewEtudiant({ ...newEtudiant, formationId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map(f => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setShowAddEtudiantForm(false)}>
                Annuler
              </button>
              <button type="submit" className="btn btn-primary">
                Ajouter
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="data-table">
        <h3>Liste des Étudiants ({etudiants.length})</h3>
        <table>
          <thead>
            <tr>
              <th>N° Étudiant</th>
              <th>Nom</th>
              <th>Prénom</th>
              <th>Email</th>
              <th>Formation</th>
              <th>Téléphone</th>
            </tr>
          </thead>
          <tbody>
            {etudiantsPagination.paginatedItems.map(etudiant => (
              <tr key={etudiant.id}>
                <td><strong>{etudiant.numeroEtudiant}</strong></td>
                <td>{etudiant.nom}</td>
                <td>{etudiant.prenom}</td>
                <td>{etudiant.email}</td>
                <td>
                  {etudiant.formation ? (
                    <span className="badge badge-info">{etudiant.formation.nom}</span>
                  ) : (
                    <span className="badge badge-secondary">Non assigné</span>
                  )}
                </td>
                <td>{etudiant.telephone || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {etudiants.length > 10 && (
          <Pagination
            currentPage={etudiantsPagination.currentPage}
            totalPages={etudiantsPagination.totalPages}
            onPageChange={etudiantsPagination.goToPage}
            hasNextPage={etudiantsPagination.hasNextPage}
            hasPreviousPage={etudiantsPagination.hasPreviousPage}
          />
        )}
      </div>
    </div>
  );

  const renderGroupesView = () => (
    <div className="groupes-section">
      <div className="section-header">
        <h2>Gestion des Groupes TD/TP</h2>
        <button
          className="btn btn-primary"
          onClick={() => setShowAddGroupeForm(!showAddGroupeForm)}
        >
          <MdGroup style={{ marginRight: '5px' }} />
          Créer un groupe
        </button>
      </div>

      {showAddGroupeForm && (
        <div className="form-card">
          <h3>Nouveau Groupe</h3>
          <form onSubmit={handleAddGroupe}>
            <div className="form-row">
              <div className="form-group">
                <label>Nom du groupe *</label>
                <input
                  type="text"
                  value={newGroupe.nom}
                  onChange={(e) => setNewGroupe({ ...newGroupe, nom: e.target.value })}
                  placeholder="Ex: TD1, TP2..."
                  required
                />
              </div>
              <div className="form-group">
                <label>Formation *</label>
                <select
                  value={newGroupe.formationId}
                  onChange={(e) => setNewGroupe({ ...newGroupe, formationId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map(f => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Capacité maximale *</label>
              <input
                type="number"
                value={newGroupe.capaciteMax}
                onChange={(e) => setNewGroupe({ ...newGroupe, capaciteMax: parseInt(e.target.value) })}
                min="1"
                required
              />
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setShowAddGroupeForm(false)}>
                Annuler
              </button>
              <button type="submit" className="btn btn-primary">
                Créer
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="data-table">
        <h3>Liste des Groupes ({groupes.length})</h3>
        <table>
          <thead>
            <tr>
              <th>Nom</th>
              <th>Formation</th>
              <th>Capacité</th>
              <th>Étudiants</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {groupesPagination.paginatedItems.map(groupe => (
              <tr key={groupe.id}>
                <td><strong>{groupe.nom}</strong></td>
                <td>{groupe.formation?.nom || 'N/A'}</td>
                <td>{groupe.capaciteMax} places</td>
                <td>
                  <span className="student-count">
                    {groupe.nombreEtudiants || 0} / {groupe.capaciteMax}
                  </span>
                </td>
                <td>
                  <div className="action-buttons-inline">
                    <button
                      className="btn btn-sm btn-primary"
                      onClick={() => {
                        setSelectedGroupeForStudents(groupe);
                        loadGroupeEtudiants(groupe.id);
                      }}
                    >
                      Gérer Étudiants
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDeleteGroupe(groupe.id)}
                    >
                      Supprimer
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {groupes.length > 5 && (
          <Pagination
            currentPage={groupesPagination.currentPage}
            totalPages={groupesPagination.totalPages}
            onPageChange={groupesPagination.goToPage}
            hasNextPage={groupesPagination.hasNextPage}
            hasPreviousPage={groupesPagination.hasPreviousPage}
          />
        )}
      </div>

      {/* Modal de gestion des étudiants d'un groupe */}
      {selectedGroupeForStudents && (
        <div className="modal-overlay" onClick={() => setSelectedGroupeForStudents(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Gérer les étudiants du groupe {selectedGroupeForStudents.nom}</h3>
              <button className="modal-close" onClick={() => setSelectedGroupeForStudents(null)}>×</button>
            </div>

            <div className="modal-body">
              <div className="form-group">
                <label>Ajouter un étudiant au groupe</label>
                <select
                  onChange={(e) => {
                    if (e.target.value) {
                      handleAjouterEtudiantAuGroupe(selectedGroupeForStudents.id, e.target.value);
                      e.target.value = '';
                    }
                  }}
                  className="form-control"
                >
                  <option value="">Sélectionner un étudiant</option>
                  {etudiants
                    .filter(etudiant => etudiant.formation?.id === selectedGroupeForStudents.formation?.id)
                    .filter(etudiant => !groupeEtudiants.find(ge => ge.etudiant?.id === etudiant.id))
                    .map(etudiant => (
                      <option key={etudiant.id} value={etudiant.id}>
                        {etudiant.prenom} {etudiant.nom} ({etudiant.numeroEtudiant})
                      </option>
                    ))}
                </select>
              </div>

              <div className="groupes-list">
                <h4>Étudiants dans ce groupe ({groupeEtudiants.length})</h4>
                {groupeEtudiants.length > 0 ? (
                  <table>
                    <thead>
                      <tr>
                        <th>N° Étudiant</th>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Formation</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {groupeEtudiants.map(ge => (
                        <tr key={ge.id}>
                          <td><strong>{ge.etudiant?.numeroEtudiant}</strong></td>
                          <td>{ge.etudiant?.nom}</td>
                          <td>{ge.etudiant?.prenom}</td>
                          <td>{ge.etudiant?.formation?.nom}</td>
                          <td>
                            <button
                              className="btn btn-sm btn-danger"
                              onClick={() => handleRetirerEtudiantDuGroupe(selectedGroupeForStudents.id, ge.etudiant?.id)}
                            >
                              Retirer
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <p className="no-data">Aucun étudiant dans ce groupe</p>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  const renderSeancesView = () => (
    <div className="seances-section">
      <div className="section-header">
        <h2>Gestion des Séances</h2>
        <button
          className="btn btn-primary"
          onClick={() => setShowAddSeanceForm(!showAddSeanceForm)}
        >
          <MdCalendarToday style={{ marginRight: '5px' }} />
          Créer une séance
        </button>
      </div>

      {showAddSeanceForm && (
        <div className="form-card">
          <h3>Nouvelle Séance</h3>
          <form onSubmit={handleAddSeance}>
            <div className="form-row">
              <div className="form-group">
                <label>Matière *</label>
                <select
                  value={newSeance.matiereId}
                  onChange={(e) => setNewSeance({ ...newSeance, matiereId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une matière</option>
                  {matieres.map(m => (
                    <option key={m.id} value={m.id}>{m.nom}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Enseignant *</label>
                <select
                  value={newSeance.enseignantId}
                  onChange={(e) => setNewSeance({ ...newSeance, enseignantId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner un enseignant</option>
                  {enseignants.map(e => (
                    <option key={e.id} value={e.id}>{e.prenom} {e.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Type de séance *</label>
                <select
                  value={newSeance.typeSeance}
                  onChange={(e) => setNewSeance({ ...newSeance, typeSeance: e.target.value })}
                  required
                >
                  <option value="CM">CM (Cours Magistral)</option>
                  <option value="TD_TP">TD/TP</option>
                </select>
              </div>
              <div className="form-group">
                <label>Salle *</label>
                <input
                  type="text"
                  value={newSeance.salle}
                  onChange={(e) => setNewSeance({ ...newSeance, salle: e.target.value })}
                  placeholder="Ex: A101"
                  required
                />
              </div>
            </div>

            {newSeance.typeSeance === 'TD_TP' && (
              <div className="form-group">
                <label>Groupe TD/TP *</label>
                <select
                  value={newSeance.groupeId}
                  onChange={(e) => setNewSeance({ ...newSeance, groupeId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner un groupe</option>
                  {groupes.map(g => (
                    <option key={g.id} value={g.id}>{g.nom} - {g.formation?.nom}</option>
                  ))}
                </select>
              </div>
            )}

            <div className="form-row">
              <div className="form-group">
                <label>Date et heure de début *</label>
                <input
                  type="datetime-local"
                  value={newSeance.dateDebut}
                  onChange={(e) => setNewSeance({ ...newSeance, dateDebut: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Date et heure de fin *</label>
                <input
                  type="datetime-local"
                  value={newSeance.dateFin}
                  onChange={(e) => setNewSeance({ ...newSeance, dateFin: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Commentaire</label>
              <textarea
                value={newSeance.commentaire}
                onChange={(e) => setNewSeance({ ...newSeance, commentaire: e.target.value })}
                rows="3"
              />
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setShowAddSeanceForm(false)}>
                Annuler
              </button>
              <button type="submit" className="btn btn-primary">
                Créer
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="data-table">
        <h3>Liste des Séances ({seances.length})</h3>
        <table>
          <thead>
            <tr>
              <th>Matière</th>
              <th>Enseignant</th>
              <th>Type</th>
              <th>Date/Heure</th>
              <th>Salle</th>
              <th>Statut</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {seancesPagination.paginatedItems.map(seance => (
              <tr key={seance.id}>
                <td><strong>{seance.matiere?.nom}</strong></td>
                <td>{seance.enseignant?.prenom} {seance.enseignant?.nom}</td>
                <td>
                  <span className={`badge ${seance.typeSeance === 'CM' ? 'badge-info' : 'badge-warning'}`}>
                    {seance.typeSeance}
                  </span>
                </td>
                <td>
                  {new Date(seance.dateDebut).toLocaleString('fr-FR', {
                    dateStyle: 'short',
                    timeStyle: 'short'
                  })}
                </td>
                <td>{seance.salle}</td>
                <td>
                  <span className={`badge ${
                    seance.statut === 'PREVUE' ? 'badge-info' :
                    seance.statut === 'EN_COURS' ? 'badge-success' :
                    seance.statut === 'TERMINEE' ? 'badge-secondary' :
                    'badge-danger'
                  }`}>
                    {seance.statut}
                  </span>
                </td>
                <td>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteSeance(seance.id)}
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {seances.length > 5 && (
          <Pagination
            currentPage={seancesPagination.currentPage}
            totalPages={seancesPagination.totalPages}
            onPageChange={seancesPagination.goToPage}
            hasNextPage={seancesPagination.hasNextPage}
            hasPreviousPage={seancesPagination.hasPreviousPage}
          />
        )}
      </div>
    </div>
  );

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-secretariat">
        <div className="dashboard-header">
          <h1>Tableau de Bord Secrétariat</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {message.text && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}

        {loading ? (
          <div className="loading">Chargement...</div>
        ) : (
          <>
            {activeView === 'etudiants' && renderEtudiantsView()}
            {activeView === 'groupes' && renderGroupesView()}
            {activeView === 'seances' && renderSeancesView()}
          </>
        )}
      </div>
    </DashboardLayout>
  );
}

export default DashboardSecretariat;
