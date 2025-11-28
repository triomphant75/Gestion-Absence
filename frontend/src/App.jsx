import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

// Pages
import Login from './pages/Login';
import DashboardEtudiant from './pages/etudiant/DashboardEtudiant';
import DashboardEnseignant from './pages/enseignant/DashboardEnseignant';
import DashboardChefDepartement from './pages/chef/DashboardChefDepartement';
import DashboardAdmin from './pages/admin/DashboardAdmin';

// Composant pour protéger les routes
const PrivateRoute = ({ children, roles }) => {
  const { isAuthenticated, hasRole, loading } = useAuth();

  if (loading) {
    return <div className="loading">Chargement...</div>;
  }

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !hasRole(roles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Composant principal de l'application
function AppContent() {
  const { user } = useAuth();

  // Redirection vers le dashboard approprié selon le rôle
  const getDefaultRoute = () => {
    if (!user) return '/login';

    switch (user.role) {
      case 'ETUDIANT':
        return '/etudiant/dashboard';
      case 'ENSEIGNANT':
        return '/enseignant/dashboard';
      case 'CHEF_DEPARTEMENT':
        return '/chef/dashboard';
      case 'ADMIN':
      case 'SUPER_ADMIN':
        return '/admin/dashboard';
      default:
        return '/login';
    }
  };

  return (
    <Router>
      <Routes>
        {/* Page de connexion */}
        <Route path="/login" element={<Login />} />

        {/* Route par défaut - redirige vers le dashboard approprié */}
        <Route
          path="/"
          element={<Navigate to={getDefaultRoute()} replace />}
        />

        {/* Routes Étudiant */}
        <Route
          path="/etudiant/dashboard"
          element={
            <PrivateRoute roles="ETUDIANT">
              <DashboardEtudiant />
            </PrivateRoute>
          }
        />

        {/* Routes Enseignant */}
        <Route
          path="/enseignant/dashboard"
          element={
            <PrivateRoute roles="ENSEIGNANT">
              <DashboardEnseignant />
            </PrivateRoute>
          }
        />

        {/* Routes Chef de Département */}
        <Route
          path="/chef/dashboard"
          element={
            <PrivateRoute roles="CHEF_DEPARTEMENT">
              <DashboardChefDepartement />
            </PrivateRoute>
          }
        />

        {/* Routes Admin */}
        <Route
          path="/admin/dashboard"
          element={
            <PrivateRoute roles={['ADMIN', 'SUPER_ADMIN']}>
              <DashboardAdmin />
            </PrivateRoute>
          }
        />

        {/* Page non autorisé */}
        <Route
          path="/unauthorized"
          element={
            <div style={{ textAlign: 'center', marginTop: '50px' }}>
              <h1>Accès non autorisé</h1>
              <p>Vous n'avez pas les droits nécessaires pour accéder à cette page.</p>
            </div>
          }
        />

        {/* Page 404 */}
        <Route
          path="*"
          element={
            <div style={{ textAlign: 'center', marginTop: '50px' }}>
              <h1>404 - Page non trouvée</h1>
            </div>
          }
        />
      </Routes>
    </Router>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
