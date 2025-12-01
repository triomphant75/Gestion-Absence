import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import DashboardLayout from '../../components/layout/DashboardLayout';
import {
  MdDashboard,
  MdPeople,
  MdBusiness,
  MdSchool,
  MdBook,
  MdGroup,
  MdCalendarToday,
  MdClose,
  MdWarning,
  MdPerson
} from 'react-icons/md';
import {
  userService,
  departementService,
  formationService,
  matiereService,
  groupeService,
  groupeEtudiantService,
  seanceService,
  presenceService,
  avertissementService
} from '../../services/api';
import './DashboardAdmin.css';

function DashboardAdmin() {
  const { user } = useAuth();
  const [activeView, setActiveView] = useState('dashboard');
  const [users, setUsers] = useState([]);
  const [departements, setDepartements] = useState([]);
  const [formations, setFormations] = useState([]);
  const [matieres, setMatieres] = useState([]);
  const [groupes, setGroupes] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const [newUser, setNewUser] = useState({
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    motDePasse: '',
    role: 'ETUDIANT',
    numeroEtudiant: '',
    formationId: ''
  });

  const [newDepartement, setNewDepartement] = useState({
    nom: '',
    description: '',
    actif: true
  });

  const [newFormation, setNewFormation] = useState({
    nom: '',
    description: '',
    departementId: '',
    niveau: 1,
    actif: true
  });

  const [newMatiere, setNewMatiere] = useState({
    nom: '',
    code: '',
    description: '',
    formationId: '',
    typeSeance: 'CM',
    coefficient: 1,
    heuresTotal: 0,
    seuilAbsences: 3,
    actif: true
  });

  const [editingMatiere, setEditingMatiere] = useState(null);

  const [newGroupe, setNewGroupe] = useState({
    nom: '',
    formationId: '',
    capaciteMax: 30,
    actif: true
  });

  const [editingGroupe, setEditingGroupe] = useState(null);
  const [selectedGroupeForStudents, setSelectedGroupeForStudents] = useState(null);
  const [groupeEtudiants, setGroupeEtudiants] = useState([]);
  const [formationEtudiants, setFormationEtudiants] = useState([]);

  const [seances, setSeances] = useState([]);
  const [enseignants, setEnseignants] = useState([]);
  const [newSeance, setNewSeance] = useState({
    matiereId: '',
    enseignantId: '',
    dateDebut: '',
    dateFin: '',
    salle: '',
    commentaire: '',
    groupeId: ''
  });
  const [editingSeance, setEditingSeance] = useState(null);

  const menuItems = [
    {
      icon: <MdDashboard />,
      label: 'Dashboard',
      active: activeView === 'dashboard',
      onClick: () => setActiveView('dashboard')
    },
    {
      icon: <MdPeople />,
      label: 'Utilisateurs',
      active: activeView === 'users',
      onClick: () => setActiveView('users')
    },
    {
      icon: <MdSchool />,
      label: 'Étudiants',
      active: activeView === 'etudiants',
      onClick: () => setActiveView('etudiants')
    },
    {
      icon: <MdBusiness />,
      label: 'Départements',
      active: activeView === 'departements',
      onClick: () => setActiveView('departements')
    },
    {
      icon: <MdSchool />,
      label: 'Formations',
      active: activeView === 'formations',
      onClick: () => setActiveView('formations')
    },
    {
      icon: <MdBook />,
      label: 'Matières',
      active: activeView === 'matieres',
      onClick: () => setActiveView('matieres')
    },
    {
      icon: <MdGroup />,
      label: 'Groupes',
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
    if (user) {
      loadAllData();
    }
  }, [user]);

  const loadAllData = async () => {
    try {
      await Promise.all([
        loadUsers(),
        loadDepartements(),
        loadFormations(),
        loadMatieres(),
        loadGroupes(),
        loadSeances(),
        loadEnseignants(),
        loadStatistics()
      ]);
    } catch (error) {
      console.error('Erreur chargement données:', error);
    }
  };

  const loadUsers = async () => {
    try {
      const response = await userService.getAll();
      setUsers(response.data);
    } catch (error) {
      console.error('Erreur chargement utilisateurs:', error);
    }
  };

  const loadDepartements = async () => {
    try {
      const response = await departementService.getAll();
      setDepartements(response.data);
    } catch (error) {
      console.error('Erreur chargement départements:', error);
    }
  };

  const loadFormations = async () => {
    try {
      const response = await formationService.getAll();
      setFormations(response.data);
    } catch (error) {
      console.error('Erreur chargement formations:', error);
    }
  };

  const loadMatieres = async () => {
    try {
      const response = await matiereService.getAll();
      setMatieres(response.data);
    } catch (error) {
      console.error('Erreur chargement matières:', error);
    }
  };

  const loadGroupes = async () => {
    try {
      const response = await groupeService.getAll();
      setGroupes(response.data);
    } catch (error) {
      console.error('Erreur chargement groupes:', error);
    }
  };

  const loadSeances = async () => {
    try {
      const response = await seanceService.getAll();
      setSeances(response.data);
    } catch (error) {
      console.error('Erreur chargement séances:', error);
    }
  };

  const loadEnseignants = async () => {
    try {
      const response = await userService.getByRole('ENSEIGNANT');
      setEnseignants(response.data);
    } catch (error) {
      console.error('Erreur chargement enseignants:', error);
    }
  };

  const loadStatistics = async () => {
    try {
      const usersRes = await userService.getAll();
      const etudiants = usersRes.data.filter(u => u.role === 'ETUDIANT');
      const enseignants = usersRes.data.filter(u => u.role === 'ENSEIGNANT');
      const seancesRes = await seanceService.getAll();
      const avertsRes = await avertissementService.getAll();

      setStatistics({
        totalUsers: usersRes.data.length,
        totalEtudiants: etudiants.length,
        totalEnseignants: enseignants.length,
        totalSeances: seancesRes.data.length,
        totalAvertissements: avertsRes.data.length
      });
    } catch (error) {
      console.error('Erreur chargement statistiques:', error);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      const userData = {
        nom: newUser.nom,
        prenom: newUser.prenom,
        email: newUser.email,
        telephone: newUser.telephone,
        motDePasse: newUser.motDePasse,
        role: newUser.role
      };

      // Ajouter les champs spécifiques aux étudiants
      if (newUser.role === 'ETUDIANT') {
        userData.numeroEtudiant = newUser.numeroEtudiant;
        // Envoyer la formation comme objet avec l'ID
        if (newUser.formationId) {
          userData.formation = {
            id: parseInt(newUser.formationId)
          };
        }
      }

      await userService.create(userData);
      setMessage({ type: 'success', text: 'Utilisateur créé avec succès!' });
      setNewUser({
        nom: '',
        prenom: '',
        email: '',
        telephone: '',
        motDePasse: '',
        role: 'ETUDIANT',
        numeroEtudiant: '',
        formationId: ''
      });
      loadUsers();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDepartement = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      await departementService.create(newDepartement);
      setMessage({ type: 'success', text: 'Département créé avec succès!' });
      setNewDepartement({ nom: '', description: '', actif: true });
      loadDepartements();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateFormation = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Validation stricte
      if (!newFormation.departementId || newFormation.departementId === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner un département' });
        setLoading(false);
        return;
      }

      if (!newFormation.nom || newFormation.nom.trim() === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer un nom pour la formation' });
        setLoading(false);
        return;
      }

      if (!newFormation.niveau || newFormation.niveau === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer un niveau' });
        setLoading(false);
        return;
      }

      const departementIdInt = parseInt(newFormation.departementId);
      const niveauInt = parseInt(newFormation.niveau);

      if (isNaN(departementIdInt) || departementIdInt <= 0) {
        setMessage({ type: 'error', text: 'ID de département invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(niveauInt) || niveauInt < 1 || niveauInt > 8) {
        setMessage({ type: 'error', text: 'Niveau invalide (doit être entre 1 et 8)' });
        setLoading(false);
        return;
      }

      await formationService.create({
        ...newFormation,
        departementId: departementIdInt,
        niveau: niveauInt
      });
      setMessage({ type: 'success', text: 'Formation créée avec succès!' });
      setNewFormation({
        nom: '',
        description: '',
        departementId: '',
        niveau: 1,
        actif: true
      });
      loadFormations();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMatiere = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Validation stricte
      if (!newMatiere.formationId || newMatiere.formationId === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner une formation' });
        setLoading(false);
        return;
      }

      if (!newMatiere.nom || newMatiere.nom.trim() === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer un nom pour la matière' });
        setLoading(false);
        return;
      }

      if (!newMatiere.code || newMatiere.code.trim() === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer un code pour la matière' });
        setLoading(false);
        return;
      }

      const formationIdInt = parseInt(newMatiere.formationId);
      const coefficientFloat = parseFloat(newMatiere.coefficient);
      const heuresTotalInt = parseInt(newMatiere.heuresTotal);
      const seuilAbsencesInt = parseInt(newMatiere.seuilAbsences);

      if (isNaN(formationIdInt) || formationIdInt <= 0) {
        setMessage({ type: 'error', text: 'ID de formation invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(coefficientFloat) || coefficientFloat <= 0) {
        setMessage({ type: 'error', text: 'Coefficient invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(heuresTotalInt) || heuresTotalInt < 0) {
        setMessage({ type: 'error', text: 'Nombre d\'heures invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(seuilAbsencesInt) || seuilAbsencesInt < 0) {
        setMessage({ type: 'error', text: 'Seuil d\'absences invalide' });
        setLoading(false);
        return;
      }

      await matiereService.create({
        ...newMatiere,
        formationId: formationIdInt,
        coefficient: coefficientFloat,
        heuresTotal: heuresTotalInt,
        seuilAbsences: seuilAbsencesInt
      });
      setMessage({ type: 'success', text: 'Matière créée avec succès!' });
      setNewMatiere({
        nom: '',
        code: '',
        description: '',
        formationId: '',
        typeSeance: 'CM',
        coefficient: 1,
        heuresTotal: 0,
        seuilAbsences: 3,
        actif: true
      });
      loadMatieres();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateMatiere = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      await matiereService.update(editingMatiere.id, {
        ...editingMatiere,
        formationId: parseInt(editingMatiere.formationId),
        coefficient: parseFloat(editingMatiere.coefficient),
        heuresTotal: parseInt(editingMatiere.heuresTotal),
        seuilAbsences: parseInt(editingMatiere.seuilAbsences)
      });
      setMessage({ type: 'success', text: 'Matière modifiée avec succès!' });
      setEditingMatiere(null);
      loadMatieres();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la modification'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteMatiere = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette matière?')) {
      try {
        await matiereService.delete(id);
        setMessage({ type: 'success', text: 'Matière supprimée!' });
        loadMatieres();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de la suppression' });
      }
    }
  };

  const handleCreateGroupe = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Validation stricte
      if (!newGroupe.formationId || newGroupe.formationId === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner une formation' });
        setLoading(false);
        return;
      }

      if (!newGroupe.nom || newGroupe.nom.trim() === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer un nom pour le groupe' });
        setLoading(false);
        return;
      }

      const formationIdInt = parseInt(newGroupe.formationId);
      const capaciteMaxInt = parseInt(newGroupe.capaciteMax);

      if (isNaN(formationIdInt) || formationIdInt <= 0) {
        setMessage({ type: 'error', text: 'ID de formation invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(capaciteMaxInt) || capaciteMaxInt <= 0) {
        setMessage({ type: 'error', text: 'Capacité maximale invalide' });
        setLoading(false);
        return;
      }

      await groupeService.create({
        ...newGroupe,
        formationId: formationIdInt,
        capaciteMax: capaciteMaxInt
      });
      setMessage({ type: 'success', text: 'Groupe créé avec succès!' });
      setNewGroupe({
        nom: '',
        formationId: '',
        capaciteMax: 30,
        actif: true
      });
      loadGroupes();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateGroupe = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      await groupeService.update(editingGroupe.id, {
        ...editingGroupe,
        formationId: parseInt(editingGroupe.formationId),
        capaciteMax: parseInt(editingGroupe.capaciteMax)
      });
      setMessage({ type: 'success', text: 'Groupe modifié avec succès!' });
      setEditingGroupe(null);
      loadGroupes();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la modification'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteGroupe = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce groupe?')) {
      try {
        await groupeService.delete(id);
        setMessage({ type: 'success', text: 'Groupe supprimé!' });
        loadGroupes();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de la suppression' });
      }
    }
  };

  const loadGroupeEtudiants = async (groupeId) => {
    try {
      // Trouver le groupe pour obtenir sa formation
      const groupe = groupes.find(g => g.id === groupeId);

      if (!groupe || !groupe.formation) {
        setMessage({
          type: 'error',
          text: 'Ce groupe n\'a pas de formation associée'
        });
        return;
      }

      // Charger les étudiants du groupe
      const groupeResponse = await groupeEtudiantService.getEtudiantsGroupe(groupeId);
      setGroupeEtudiants(groupeResponse.data);

      // Charger TOUS les étudiants de la formation
      const allStudentsResponse = await userService.getByRole('ETUDIANT');
      const etudiantsFormation = allStudentsResponse.data.filter(
        s => s.formation?.id === groupe.formation.id
      );
      setFormationEtudiants(etudiantsFormation);

    } catch (error) {
      console.error('Erreur chargement étudiants du groupe:', error);
      setMessage({
        type: 'error',
        text: 'Erreur lors du chargement des étudiants'
      });
    }
  };

  const handleAddStudentToGroupe = async (etudiantId, groupeId) => {
    try {
      await groupeEtudiantService.affecter(etudiantId, groupeId);
      setMessage({ type: 'success', text: 'Étudiant ajouté au groupe!' });
      loadGroupeEtudiants(groupeId);
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de l\'ajout'
      });
    }
  };

  const handleRemoveStudentFromGroupe = async (etudiantId, groupeId) => {
    if (window.confirm('Retirer cet étudiant du groupe?')) {
      try {
        await groupeEtudiantService.retirer(etudiantId, groupeId);
        setMessage({ type: 'success', text: 'Étudiant retiré du groupe!' });
        loadGroupeEtudiants(groupeId);
      } catch (error) {
        setMessage({
          type: 'error',
          text: error.response?.data?.message || 'Erreur lors du retrait'
        });
      }
    }
  };

  const handleCreateSeance = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      // Validation stricte
      if (!newSeance.matiereId || newSeance.matiereId === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner une matière' });
        setLoading(false);
        return;
      }

      if (!newSeance.enseignantId || newSeance.enseignantId === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner un enseignant' });
        setLoading(false);
        return;
      }

      if (!newSeance.dateDebut || newSeance.dateDebut === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner une date de début' });
        setLoading(false);
        return;
      }

      if (!newSeance.dateFin || newSeance.dateFin === '') {
        setMessage({ type: 'error', text: 'Veuillez sélectionner une date de fin' });
        setLoading(false);
        return;
      }

      if (!newSeance.salle || newSeance.salle.trim() === '') {
        setMessage({ type: 'error', text: 'Veuillez entrer une salle' });
        setLoading(false);
        return;
      }

      const matiereIdInt = parseInt(newSeance.matiereId);
      const enseignantIdInt = parseInt(newSeance.enseignantId);

      if (isNaN(matiereIdInt) || matiereIdInt <= 0) {
        setMessage({ type: 'error', text: 'ID de matière invalide' });
        setLoading(false);
        return;
      }

      if (isNaN(enseignantIdInt) || enseignantIdInt <= 0) {
        setMessage({ type: 'error', text: 'ID d\'enseignant invalide' });
        setLoading(false);
        return;
      }

      const matiere = matieres.find(m => m.id === matiereIdInt);

      // Si TD_TP, vérifier qu'un groupe est sélectionné
      if (matiere?.typeSeance === 'TD_TP' && (!newSeance.groupeId || newSeance.groupeId === '')) {
        setMessage({ type: 'error', text: 'Veuillez sélectionner un groupe pour les séances TD/TP' });
        setLoading(false);
        return;
      }

      // Préparer les données de la séance
      const seanceData = {
        matiereId: matiereIdInt,
        enseignantId: enseignantIdInt,
        dateDebut: newSeance.dateDebut,
        dateFin: newSeance.dateFin,
        salle: newSeance.salle,
        commentaire: newSeance.commentaire
      };

      // Ajouter groupeId uniquement si la matière est de type TD_TP
      if (matiere?.typeSeance === 'TD_TP' && newSeance.groupeId) {
        const groupeIdInt = parseInt(newSeance.groupeId);
        if (isNaN(groupeIdInt) || groupeIdInt <= 0) {
          setMessage({ type: 'error', text: 'ID de groupe invalide' });
          setLoading(false);
          return;
        }
        seanceData.groupeId = groupeIdInt;
      }

      await seanceService.create(seanceData);
      setMessage({ type: 'success', text: 'Séance créée avec succès!' });
      setNewSeance({
        matiereId: '',
        enseignantId: '',
        dateDebut: '',
        dateFin: '',
        salle: '',
        commentaire: '',
        groupeId: ''
      });
      loadSeances();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la création'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateSeance = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      const seanceData = {
        matiereId: parseInt(editingSeance.matiereId),
        enseignantId: parseInt(editingSeance.enseignantId),
        dateDebut: editingSeance.dateDebut,
        dateFin: editingSeance.dateFin,
        salle: editingSeance.salle,
        commentaire: editingSeance.commentaire
      };

      const matiere = matieres.find(m => m.id === parseInt(editingSeance.matiereId));
      if (matiere?.typeSeance === 'TD_TP' && editingSeance.groupeId) {
        seanceData.groupeId = parseInt(editingSeance.groupeId);
      }

      await seanceService.update(editingSeance.id, seanceData);
      setMessage({ type: 'success', text: 'Séance modifiée avec succès!' });
      setEditingSeance(null);
      loadSeances();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Erreur lors de la modification'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleCancelSeance = async (id) => {
    const motif = window.prompt('Motif de l\'annulation:');
    if (motif) {
      try {
        await seanceService.cancel(id, motif);
        setMessage({ type: 'success', text: 'Séance annulée!' });
        loadSeances();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de l\'annulation' });
      }
    }
  };

  const handleDeleteSeance = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette séance?')) {
      try {
        await seanceService.delete(id);
        setMessage({ type: 'success', text: 'Séance supprimée!' });
        loadSeances();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de la suppression' });
      }
    }
  };

  const handleDeleteUser = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur?')) {
      try {
        await userService.delete(id);
        setMessage({ type: 'success', text: 'Utilisateur supprimé!' });
        loadUsers();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de la suppression' });
      }
    }
  };

  const handleDeleteDepartement = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce département?')) {
      try {
        await departementService.delete(id);
        setMessage({ type: 'success', text: 'Département supprimé!' });
        loadDepartements();
      } catch (error) {
        setMessage({ type: 'error', text: 'Erreur lors de la suppression' });
      }
    }
  };

  const renderDashboardView = () => (
    <div className="overview-section">
      <h2>Vue d'Ensemble</h2>
      {statistics && (
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon"><MdPeople /></div>
            <div className="stat-content">
              <h3>{statistics.totalUsers}</h3>
              <p>Utilisateurs Total</p>
            </div>
          </div>
          <div className="stat-card success">
            <div className="stat-icon"><MdSchool /></div>
            <div className="stat-content">
              <h3>{statistics.totalEtudiants}</h3>
              <p>Étudiants</p>
            </div>
          </div>
          <div className="stat-card info">
            <div className="stat-icon"><MdPerson /></div>
            <div className="stat-content">
              <h3>{statistics.totalEnseignants}</h3>
              <p>Enseignants</p>
            </div>
          </div>
          <div className="stat-card warning">
            <div className="stat-icon"><MdCalendarToday /></div>
            <div className="stat-content">
              <h3>{statistics.totalSeances}</h3>
              <p>Séances</p>
            </div>
          </div>
          <div className="stat-card danger">
            <div className="stat-icon"><MdWarning /></div>
            <div className="stat-content">
              <h3>{statistics.totalAvertissements}</h3>
              <p>Avertissements</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon"><MdBusiness /></div>
            <div className="stat-content">
              <h3>{departements.length}</h3>
              <p>Départements</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  const renderUsersView = () => (
    <div className="users-section">
      <h2>Gestion des Utilisateurs</h2>

      <div className="create-form-card">
        <h3>Créer un Utilisateur</h3>
        <form onSubmit={handleCreateUser}>
          <div className="form-row">
            <div className="form-group">
              <label>Nom *</label>
              <input
                type="text"
                value={newUser.nom}
                onChange={(e) => setNewUser({ ...newUser, nom: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Prénom *</label>
              <input
                type="text"
                value={newUser.prenom}
                onChange={(e) => setNewUser({ ...newUser, prenom: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Email *</label>
              <input
                type="email"
                value={newUser.email}
                onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Téléphone</label>
              <input
                type="text"
                value={newUser.telephone}
                onChange={(e) => setNewUser({ ...newUser, telephone: e.target.value })}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Mot de Passe *</label>
              <input
                type="password"
                value={newUser.motDePasse}
                onChange={(e) => setNewUser({ ...newUser, motDePasse: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Rôle *</label>
              <select
                value={newUser.role}
                onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                required
              >
                <option value="ETUDIANT">Étudiant</option>
                <option value="ENSEIGNANT">Enseignant</option>
                <option value="CHEF_DEPARTEMENT">Chef de Département</option>
                <option value="ADMIN">Administrateur</option>
              </select>
            </div>
          </div>

          {newUser.role === 'ETUDIANT' && (
            <div className="form-row">
              <div className="form-group">
                <label>Numéro Étudiant *</label>
                <input
                  type="text"
                  value={newUser.numeroEtudiant}
                  onChange={(e) => setNewUser({ ...newUser, numeroEtudiant: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Formation</label>
                <select
                  value={newUser.formationId}
                  onChange={(e) => setNewUser({ ...newUser, formationId: e.target.value })}
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map((f) => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>
          )}

          {message.text && (
            <div className={`message ${message.type}`}>
              {message.text}
            </div>
          )}

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Création...' : 'Créer l\'Utilisateur'}
          </button>
        </form>
      </div>

      <div className="data-table">
        <h3>Liste des Utilisateurs ({users.length})</h3>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nom</th>
              <th>Prénom</th>
              <th>Email</th>
              <th>Rôle</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.nom}</td>
                <td>{u.prenom}</td>
                <td>{u.email}</td>
                <td><span className={`badge badge-${u.role.toLowerCase()}`}>{u.role}</span></td>
                <td>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteUser(u.id)}
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderDepartementsView = () => (
    <div className="departements-section">
      <h2>Gestion des Départements</h2>

      <div className="create-form-card">
        <h3>Créer un Département</h3>
        <form onSubmit={handleCreateDepartement}>
          <div className="form-group">
            <label>Nom *</label>
            <input
              type="text"
              value={newDepartement.nom}
              onChange={(e) => setNewDepartement({ ...newDepartement, nom: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea
              value={newDepartement.description}
              onChange={(e) => setNewDepartement({ ...newDepartement, description: e.target.value })}
              rows={3}
            />
          </div>

          {message.text && (
            <div className={`message ${message.type}`}>
              {message.text}
            </div>
          )}

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Création...' : 'Créer le Département'}
          </button>
        </form>
      </div>

      <div className="data-table">
        <h3>Liste des Départements ({departements.length})</h3>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nom</th>
              <th>Description</th>
              <th>Statut</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {departements.map((d) => (
              <tr key={d.id}>
                <td>{d.id}</td>
                <td>{d.nom}</td>
                <td>{d.description}</td>
                <td>
                  <span className={`badge ${d.actif ? 'badge-success' : 'badge-secondary'}`}>
                    {d.actif ? 'Actif' : 'Inactif'}
                  </span>
                </td>
                <td>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteDepartement(d.id)}
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderFormationsView = () => (
    <div className="formations-section">
      <h2>Gestion des Formations</h2>

      <div className="create-form-card">
        <h3>Créer une Formation</h3>
        <form onSubmit={handleCreateFormation}>
          <div className="form-group">
            <label>Nom *</label>
            <input
              type="text"
              value={newFormation.nom}
              onChange={(e) => setNewFormation({ ...newFormation, nom: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea
              value={newFormation.description}
              onChange={(e) => setNewFormation({ ...newFormation, description: e.target.value })}
              rows={2}
            />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Département *</label>
              <select
                value={newFormation.departementId}
                onChange={(e) => setNewFormation({ ...newFormation, departementId: e.target.value })}
                required
              >
                <option value="">Sélectionner un département</option>
                {departements.map((d) => (
                  <option key={d.id} value={d.id}>{d.nom}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Niveau *</label>
              <input
                type="number"
                min="1"
                max="8"
                value={newFormation.niveau}
                onChange={(e) => setNewFormation({ ...newFormation, niveau: e.target.value })}
                required
              />
            </div>
          </div>

          {message.text && (
            <div className={`message ${message.type}`}>
              {message.text}
            </div>
          )}

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Création...' : 'Créer la Formation'}
          </button>
        </form>
      </div>

      <div className="data-table">
        <h3>Liste des Formations ({formations.length})</h3>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Nom</th>
              <th>Département</th>
              <th>Niveau</th>
              <th>Statut</th>
            </tr>
          </thead>
          <tbody>
            {formations.map((f) => (
              <tr key={f.id}>
                <td>{f.id}</td>
                <td>{f.nom}</td>
                <td>{f.departement?.nom}</td>
                <td>{f.niveau}</td>
                <td>
                  <span className={`badge ${f.actif ? 'badge-success' : 'badge-secondary'}`}>
                    {f.actif ? 'Actif' : 'Inactif'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderMatieresView = () => (
    <div className="matieres-section">
      <h2>Gestion des Matières</h2>

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      {/* Formulaire Création */}
      {!editingMatiere && (
        <div className="create-form-card">
          <h3>Créer une Matière</h3>
          <form onSubmit={handleCreateMatiere}>
            <div className="form-row">
              <div className="form-group">
                <label>Code Matière *</label>
                <input
                  type="text"
                  value={newMatiere.code}
                  onChange={(e) => setNewMatiere({ ...newMatiere, code: e.target.value.toUpperCase() })}
                  placeholder="Ex: INF301"
                  required
                />
              </div>
              <div className="form-group">
                <label>Intitulé *</label>
                <input
                  type="text"
                  value={newMatiere.nom}
                  onChange={(e) => setNewMatiere({ ...newMatiere, nom: e.target.value })}
                  placeholder="Ex: Programmation Orientée Objet"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                value={newMatiere.description}
                onChange={(e) => setNewMatiere({ ...newMatiere, description: e.target.value })}
                placeholder="Description de la matière..."
                rows={2}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Type *</label>
                <select
                  value={newMatiere.typeSeance}
                  onChange={(e) => setNewMatiere({ ...newMatiere, typeSeance: e.target.value })}
                  required
                >
                  <option value="CM">CM (Cours Magistral)</option>
                  <option value="TD_TP">TD/TP</option>
                </select>
              </div>
              <div className="form-group">
                <label>Formation *</label>
                <select
                  value={newMatiere.formationId}
                  onChange={(e) => setNewMatiere({ ...newMatiere, formationId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map((f) => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Coefficient *</label>
                <input
                  type="number"
                  step="0.5"
                  min="0.5"
                  max="10"
                  value={newMatiere.coefficient}
                  onChange={(e) => setNewMatiere({ ...newMatiere, coefficient: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Heures Total *</label>
                <input
                  type="number"
                  min="0"
                  value={newMatiere.heuresTotal}
                  onChange={(e) => setNewMatiere({ ...newMatiere, heuresTotal: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Seuil Absences *</label>
                <input
                  type="number"
                  min="1"
                  value={newMatiere.seuilAbsences}
                  onChange={(e) => setNewMatiere({ ...newMatiere, seuilAbsences: e.target.value })}
                  placeholder="Ex: 3"
                  required
                />
                <small>Nombre d'absences avant avertissement</small>
              </div>
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Création...' : 'Créer la Matière'}
            </button>
          </form>
        </div>
      )}

      {/* Formulaire Modification */}
      {editingMatiere && (
        <div className="create-form-card editing">
          <h3>Modifier la Matière</h3>
          <form onSubmit={handleUpdateMatiere}>
            <div className="form-row">
              <div className="form-group">
                <label>Code Matière *</label>
                <input
                  type="text"
                  value={editingMatiere.code}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, code: e.target.value.toUpperCase() })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Intitulé *</label>
                <input
                  type="text"
                  value={editingMatiere.nom}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, nom: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                value={editingMatiere.description || ''}
                onChange={(e) => setEditingMatiere({ ...editingMatiere, description: e.target.value })}
                rows={2}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Type *</label>
                <select
                  value={editingMatiere.typeSeance}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, typeSeance: e.target.value })}
                  required
                >
                  <option value="CM">CM (Cours Magistral)</option>
                  <option value="TD_TP">TD/TP</option>
                </select>
              </div>
              <div className="form-group">
                <label>Formation *</label>
                <select
                  value={editingMatiere.formationId}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, formationId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map((f) => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Coefficient *</label>
                <input
                  type="number"
                  step="0.5"
                  min="0.5"
                  max="10"
                  value={editingMatiere.coefficient}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, coefficient: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Heures Total *</label>
                <input
                  type="number"
                  min="0"
                  value={editingMatiere.heuresTotal}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, heuresTotal: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Seuil Absences *</label>
                <input
                  type="number"
                  min="1"
                  value={editingMatiere.seuilAbsences}
                  onChange={(e) => setEditingMatiere({ ...editingMatiere, seuilAbsences: e.target.value })}
                  required
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Modification...' : 'Modifier'}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setEditingMatiere(null)}
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Liste des matières */}
      <div className="data-table">
        <h3>Liste des Matières ({matieres.length})</h3>
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Intitulé</th>
              <th>Type</th>
              <th>Formation</th>
              <th>Coef.</th>
              <th>Heures</th>
              <th>Seuil Abs.</th>
              <th>Statut</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {matieres.map((m) => (
              <tr key={m.id}>
                <td><strong>{m.code}</strong></td>
                <td>{m.nom}</td>
                <td>
                  <span className={`badge ${m.typeSeance === 'CM' ? 'badge-info' : 'badge-warning'}`}>
                    {m.typeSeance === 'CM' ? 'CM' : 'TD/TP'}
                  </span>
                </td>
                <td>{m.formation?.nom || 'N/A'}</td>
                <td>{m.coefficient}</td>
                <td>{m.heuresTotal}h</td>
                <td>{m.seuilAbsences}</td>
                <td>
                  <span className={`badge ${m.actif ? 'badge-success' : 'badge-secondary'}`}>
                    {m.actif ? 'Actif' : 'Inactif'}
                  </span>
                </td>
                <td>
                  <div className="action-buttons-inline">
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => setEditingMatiere({ ...m, formationId: m.formation?.id })}
                    >
                      Modifier
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDeleteMatiere(m.id)}
                    >
                      Supprimer
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {matieres.length === 0 && (
          <p className="no-data">Aucune matière créée</p>
        )}
      </div>
    </div>
  );

  const renderSeancesView = () => {
    // Helper pour obtenir le nom du groupe
    const getGroupeNom = (groupeId) => {
      const groupe = groupes.find(g => g.id === groupeId);
      return groupe ? groupe.nom : 'N/A';
    };

    // Helper pour obtenir les groupes d'une formation spécifique
    const getGroupesByFormation = (formationId) => {
      return groupes.filter(g => g.formation?.id === formationId);
    };

    return (
      <div className="seances-section">
        <h2>Gestion des Séances</h2>

        {message.text && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}

        {/* Formulaire Création */}
        {!editingSeance && (
          <div className="create-form-card">
            <h3>Créer une Séance</h3>
            <form onSubmit={handleCreateSeance}>
              <div className="form-row">
                <div className="form-group">
                  <label>Matière *</label>
                  <select
                    value={newSeance.matiereId}
                    onChange={(e) => setNewSeance({ ...newSeance, matiereId: e.target.value, groupeId: '' })}
                    required
                  >
                    <option value="">Sélectionner une matière</option>
                    {matieres.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.code} - {m.nom} ({m.typeSeance === 'CM' ? 'CM' : 'TD/TP'})
                      </option>
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
                    {enseignants.map((ens) => (
                      <option key={ens.id} value={ens.id}>
                        {ens.prenom} {ens.nom}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Afficher groupe uniquement si matière TD_TP */}
              {newSeance.matiereId && matieres.find(m => m.id === parseInt(newSeance.matiereId))?.typeSeance === 'TD_TP' && (
                <div className="form-group">
                  <label>Groupe TD/TP *</label>
                  <select
                    value={newSeance.groupeId}
                    onChange={(e) => setNewSeance({ ...newSeance, groupeId: e.target.value })}
                    required
                  >
                    <option value="">Sélectionner un groupe</option>
                    {(() => {
                      const matiere = matieres.find(m => m.id === parseInt(newSeance.matiereId));
                      const groupesFiltered = matiere?.formation?.id
                        ? getGroupesByFormation(matiere.formation.id)
                        : [];
                      return groupesFiltered.map((g) => (
                        <option key={g.id} value={g.id}>
                          {g.nom} ({g.nombreEtudiants || 0} étudiants)
                        </option>
                      ));
                    })()}
                  </select>
                </div>
              )}

              <div className="form-row">
                <div className="form-group">
                  <label>Date et Heure Début *</label>
                  <input
                    type="datetime-local"
                    value={newSeance.dateDebut}
                    onChange={(e) => setNewSeance({ ...newSeance, dateDebut: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Date et Heure Fin *</label>
                  <input
                    type="datetime-local"
                    value={newSeance.dateFin}
                    onChange={(e) => setNewSeance({ ...newSeance, dateFin: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Salle *</label>
                <input
                  type="text"
                  value={newSeance.salle}
                  onChange={(e) => setNewSeance({ ...newSeance, salle: e.target.value })}
                  placeholder="Ex: Amphi A, Salle TP1"
                  required
                />
              </div>

              <div className="form-group">
                <label>Commentaire</label>
                <textarea
                  value={newSeance.commentaire}
                  onChange={(e) => setNewSeance({ ...newSeance, commentaire: e.target.value })}
                  placeholder="Informations complémentaires..."
                  rows={2}
                />
              </div>

              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Création...' : 'Créer la Séance'}
              </button>
            </form>
          </div>
        )}

        {/* Formulaire Modification */}
        {editingSeance && (
          <div className="create-form-card editing">
            <h3>Modifier la Séance</h3>
            <form onSubmit={handleUpdateSeance}>
              <div className="form-row">
                <div className="form-group">
                  <label>Matière *</label>
                  <select
                    value={editingSeance.matiereId}
                    onChange={(e) => setEditingSeance({ ...editingSeance, matiereId: e.target.value, groupeId: '' })}
                    required
                  >
                    <option value="">Sélectionner une matière</option>
                    {matieres.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.code} - {m.nom} ({m.typeSeance === 'CM' ? 'CM' : 'TD/TP'})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Enseignant *</label>
                  <select
                    value={editingSeance.enseignantId}
                    onChange={(e) => setEditingSeance({ ...editingSeance, enseignantId: e.target.value })}
                    required
                  >
                    <option value="">Sélectionner un enseignant</option>
                    {enseignants.map((ens) => (
                      <option key={ens.id} value={ens.id}>
                        {ens.prenom} {ens.nom}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {editingSeance.matiereId && matieres.find(m => m.id === parseInt(editingSeance.matiereId))?.typeSeance === 'TD_TP' && (
                <div className="form-group">
                  <label>Groupe TD/TP *</label>
                  <select
                    value={editingSeance.groupeId || ''}
                    onChange={(e) => setEditingSeance({ ...editingSeance, groupeId: e.target.value })}
                    required
                  >
                    <option value="">Sélectionner un groupe</option>
                    {(() => {
                      const matiere = matieres.find(m => m.id === parseInt(editingSeance.matiereId));
                      const groupesFiltered = matiere?.formation?.id
                        ? getGroupesByFormation(matiere.formation.id)
                        : [];
                      return groupesFiltered.map((g) => (
                        <option key={g.id} value={g.id}>
                          {g.nom} ({g.nombreEtudiants || 0} étudiants)
                        </option>
                      ));
                    })()}
                  </select>
                </div>
              )}

              <div className="form-row">
                <div className="form-group">
                  <label>Date et Heure Début *</label>
                  <input
                    type="datetime-local"
                    value={editingSeance.dateDebut}
                    onChange={(e) => setEditingSeance({ ...editingSeance, dateDebut: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Date et Heure Fin *</label>
                  <input
                    type="datetime-local"
                    value={editingSeance.dateFin}
                    onChange={(e) => setEditingSeance({ ...editingSeance, dateFin: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Salle *</label>
                <input
                  type="text"
                  value={editingSeance.salle}
                  onChange={(e) => setEditingSeance({ ...editingSeance, salle: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Commentaire</label>
                <textarea
                  value={editingSeance.commentaire || ''}
                  onChange={(e) => setEditingSeance({ ...editingSeance, commentaire: e.target.value })}
                  rows={2}
                />
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Modification...' : 'Modifier'}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setEditingSeance(null)}
                >
                  Annuler
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Liste des séances */}
        <div className="data-table">
          <h3>Liste des Séances ({seances.length})</h3>
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Matière</th>
                <th>Type</th>
                <th>Enseignant</th>
                <th>Salle</th>
                <th>Groupe</th>
                <th>Statut</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {seances.map((s) => (
                <tr key={s.id}>
                  <td>
                    <div>{new Date(s.dateDebut).toLocaleDateString('fr-FR')}</div>
                    <small>
                      {new Date(s.dateDebut).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                      {' - '}
                      {new Date(s.dateFin).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                    </small>
                  </td>
                  <td>
                    <strong>{s.matiere?.code}</strong><br/>
                    <small>{s.matiere?.nom}</small>
                  </td>
                  <td>
                    <span className={`badge ${s.matiere?.typeSeance === 'CM' ? 'badge-info' : 'badge-warning'}`}>
                      {s.matiere?.typeSeance === 'CM' ? 'CM' : 'TD/TP'}
                    </span>
                  </td>
                  <td>{s.enseignant?.prenom} {s.enseignant?.nom}</td>
                  <td>{s.salle}</td>
                  <td>
                    {s.groupe?.nom || (s.matiere?.typeSeance === 'CM' ? s.matiere?.formation?.nom : 'N/A')}
                  </td>
                  <td>
                    <span className={`badge ${
                      s.statut === 'PREVUE' ? 'badge-info' :
                      s.statut === 'REPORTEE' ? 'badge-warning' :
                      s.statut === 'EN_COURS' ? 'badge-success' :
                      s.statut === 'TERMINEE' ? 'badge-secondary' :
                      'badge-danger'
                    }`}>
                      {s.statut === 'PREVUE' ? 'Prévue' :
                       s.statut === 'REPORTEE' ? 'Reportée' :
                       s.statut === 'EN_COURS' ? 'En cours' :
                       s.statut === 'TERMINEE' ? 'Terminée' :
                       'Annulée'}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons-inline">
                      {(s.statut === 'PREVUE' || s.statut === 'REPORTEE') && (
                        <>
                          <button
                            className="btn btn-sm btn-info"
                            onClick={() => setEditingSeance({
                              ...s,
                              matiereId: s.matiere?.id,
                              enseignantId: s.enseignant?.id,
                              groupeId: s.groupe?.id || '',
                              dateDebut: s.dateDebut.slice(0, 16),
                              dateFin: s.dateFin.slice(0, 16)
                            })}
                          >
                            Modifier
                          </button>
                          <button
                            className="btn btn-sm btn-warning"
                            onClick={() => handleCancelSeance(s.id)}
                          >
                            Annuler
                          </button>
                        </>
                      )}
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => handleDeleteSeance(s.id)}
                      >
                        Supprimer
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {seances.length === 0 && (
            <p className="no-data">Aucune séance créée</p>
          )}
        </div>
      </div>
    );
  };

  const renderEtudiantsView = () => {
    // Filtrer uniquement les étudiants
    const etudiants = users.filter(u => u.role === 'ETUDIANT');

    return (
      <div className="etudiants-section">
        <h2>Liste des Étudiants</h2>

        <div className="data-table">
          <h3>Tous les Étudiants ({etudiants.length})</h3>
          <table>
            <thead>
              <tr>
                <th>Numéro Étudiant</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th>Formation</th>
                <th>Téléphone</th>
              </tr>
            </thead>
            <tbody>
              {etudiants.map((etudiant) => (
                <tr key={etudiant.id}>
                  <td><strong>{etudiant.numeroEtudiant || 'N/A'}</strong></td>
                  <td>{etudiant.nom}</td>
                  <td>{etudiant.prenom}</td>
                  <td>{etudiant.email}</td>
                  <td>
                    {etudiant.formation ? (
                      <span className="badge badge-info">
                        {etudiant.formation.nom}
                      </span>
                    ) : (
                      <span className="badge badge-secondary">Non assigné</span>
                    )}
                  </td>
                  <td>{etudiant.telephone || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {etudiants.length === 0 && (
            <p className="no-data">Aucun étudiant trouvé</p>
          )}
        </div>
      </div>
    );
  };

  const renderGroupesView = () => (
    <div className="groupes-section">
      <h2>Gestion des Groupes TD/TP</h2>

      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}

      {/* Modal Gestion Étudiants */}
      {selectedGroupeForStudents && (
        <div className="modal-overlay" onClick={() => setSelectedGroupeForStudents(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Étudiants du Groupe : {selectedGroupeForStudents.nom}</h3>
              <button className="modal-close" onClick={() => setSelectedGroupeForStudents(null)}>
                <MdClose />
              </button>
            </div>

            <div className="modal-body">
              <div className="students-section">
                <h4>Formation : {selectedGroupeForStudents.formation?.nom}</h4>
                <p className="text-muted">
                  Cochez les étudiants qui font partie de ce groupe
                </p>

                {formationEtudiants.length > 0 ? (
                  <div className="students-list">
                    {formationEtudiants.map((etudiant) => {
                      // Vérifier si l'étudiant est dans le groupe
                      const isInGroupe = groupeEtudiants.some(ge => ge.etudiant.id === etudiant.id);

                      return (
                        <div key={etudiant.id} className="student-item">
                          <div className="student-info">
                            <strong>{etudiant.nom} {etudiant.prenom}</strong>
                            <span className="student-number">{etudiant.numeroEtudiant}</span>
                          </div>
                          {isInGroupe ? (
                            <button
                              className="btn btn-sm btn-danger"
                              onClick={() => handleRemoveStudentFromGroupe(etudiant.id, selectedGroupeForStudents.id)}
                            >
                              ✓ Dans le groupe - Retirer
                            </button>
                          ) : (
                            <button
                              className="btn btn-sm btn-success"
                              onClick={() => handleAddStudentToGroupe(etudiant.id, selectedGroupeForStudents.id)}
                            >
                              + Ajouter au groupe
                            </button>
                          )}
                        </div>
                      );
                    })}
                  </div>
                ) : (
                  <p className="no-data-sm">
                    Aucun étudiant dans la formation {selectedGroupeForStudents.formation?.nom}.
                    Créez d'abord des étudiants pour cette formation.
                  </p>
                )}

                <div className="modal-footer">
                  <p className="text-muted">
                    {groupeEtudiants.length} / {formationEtudiants.length} étudiants dans le groupe
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Formulaire Création */}
      {!editingGroupe && (
        <div className="create-form-card">
          <h3>Créer un Groupe TD/TP</h3>
          <form onSubmit={handleCreateGroupe}>
            <div className="form-row">
              <div className="form-group">
                <label>Nom du Groupe *</label>
                <input
                  type="text"
                  value={newGroupe.nom}
                  onChange={(e) => setNewGroupe({ ...newGroupe, nom: e.target.value })}
                  placeholder="Ex: Groupe A, TP1"
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
                  {formations.map((f) => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Capacité Maximale</label>
              <input
                type="number"
                min="1"
                value={newGroupe.capaciteMax}
                onChange={(e) => setNewGroupe({ ...newGroupe, capaciteMax: e.target.value })}
                placeholder="30"
              />
              <small>Nombre maximum d'étudiants</small>
            </div>

            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Création...' : 'Créer le Groupe'}
            </button>
          </form>
        </div>
      )}

      {/* Formulaire Modification */}
      {editingGroupe && (
        <div className="create-form-card editing">
          <h3>Modifier le Groupe</h3>
          <form onSubmit={handleUpdateGroupe}>
            <div className="form-row">
              <div className="form-group">
                <label>Nom du Groupe *</label>
                <input
                  type="text"
                  value={editingGroupe.nom}
                  onChange={(e) => setEditingGroupe({ ...editingGroupe, nom: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Formation *</label>
                <select
                  value={editingGroupe.formationId}
                  onChange={(e) => setEditingGroupe({ ...editingGroupe, formationId: e.target.value })}
                  required
                >
                  <option value="">Sélectionner une formation</option>
                  {formations.map((f) => (
                    <option key={f.id} value={f.id}>{f.nom}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Capacité Maximale</label>
              <input
                type="number"
                min="1"
                value={editingGroupe.capaciteMax}
                onChange={(e) => setEditingGroupe({ ...editingGroupe, capaciteMax: e.target.value })}
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Modification...' : 'Modifier'}
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setEditingGroupe(null)}
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Liste des groupes */}
      <div className="data-table">
        <h3>Liste des Groupes ({groupes.length})</h3>
        <table>
          <thead>
            <tr>
              <th>Nom</th>
              <th>Formation</th>
              <th>Capacité</th>
              <th>Étudiants</th>
              <th>Statut</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {groupes.map((g) => (
              <tr key={g.id}>
                <td><strong>{g.nom}</strong></td>
                <td>{g.formation?.nom || 'N/A'}</td>
                <td>{g.capaciteMax} places</td>
                <td>
                  <span className="student-count">
                    {g.nombreEtudiants || 0} / {g.capaciteMax}
                  </span>
                </td>
                <td>
                  <span className={`badge ${g.actif ? 'badge-success' : 'badge-secondary'}`}>
                    {g.actif ? 'Actif' : 'Inactif'}
                  </span>
                </td>
                <td>
                  <div className="action-buttons-inline">
                    <button
                      className="btn btn-sm btn-primary"
                      onClick={() => {
                        setSelectedGroupeForStudents(g);
                        loadGroupeEtudiants(g.id);
                      }}
                    >
                      Gérer Étudiants
                    </button>
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => setEditingGroupe({ ...g, formationId: g.formation?.id })}
                    >
                      Modifier
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDeleteGroupe(g.id)}
                    >
                      Supprimer
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {groupes.length === 0 && (
          <p className="no-data">Aucun groupe créé</p>
        )}
      </div>
    </div>
  );

  return (
    <DashboardLayout menuItems={menuItems}>
      <div className="dashboard-admin">
        <div className="dashboard-header">
          <h1>Tableau de Bord Administrateur</h1>
          <p>Bienvenue, {user?.prenom} {user?.nom}</p>
        </div>

        {activeView === 'dashboard' && renderDashboardView()}
        {activeView === 'users' && renderUsersView()}
        {activeView === 'etudiants' && renderEtudiantsView()}
        {activeView === 'departements' && renderDepartementsView()}
        {activeView === 'formations' && renderFormationsView()}
        {activeView === 'matieres' && renderMatieresView()}
        {activeView === 'groupes' && renderGroupesView()}
        {activeView === 'seances' && renderSeancesView()}
      </div>
    </DashboardLayout>
  );
}

export default DashboardAdmin;
