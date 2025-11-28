import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { HiMenu, HiX } from 'react-icons/hi';
import './DashboardLayout.css';

function DashboardLayout({ children, menuItems }) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getRoleBadgeClass = (role) => {
    switch (role) {
      case 'ADMIN':
      case 'SUPER_ADMIN':
        return 'badge-admin';
      case 'ENSEIGNANT':
        return 'badge-enseignant';
      case 'CHEF_DEPARTEMENT':
        return 'badge-chef';
      case 'ETUDIANT':
        return 'badge-etudiant';
      default:
        return '';
    }
  };

  const getRoleLabel = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'Administrateur';
      case 'SUPER_ADMIN':
        return 'Super Admin';
      case 'ENSEIGNANT':
        return 'Enseignant';
      case 'CHEF_DEPARTEMENT':
        return 'Chef de Département';
      case 'ETUDIANT':
        return 'Étudiant';
      default:
        return role;
    }
  };

  return (
    <div className="dashboard-layout">
      {/* Navbar */}
      <nav className="navbar">
        <div className="navbar-left">
          <button
            className="menu-toggle"
            onClick={() => setSidebarOpen(!sidebarOpen)}
          >
            {sidebarOpen ? <HiX /> : <HiMenu />}
          </button>
          <h1>Gestion des Absences</h1>
        </div>

        <div className="navbar-right">
          <div className="user-info">
            <span className="user-name">{user?.prenom} {user?.nom}</span>
            <span className={`role-badge ${getRoleBadgeClass(user?.role)}`}>
              {getRoleLabel(user?.role)}
            </span>
          </div>
          <button className="btn-logout" onClick={handleLogout}>
            Déconnexion
          </button>
        </div>
      </nav>

      <div className="dashboard-container">
        {/* Sidebar */}
        <aside className={`sidebar ${sidebarOpen ? 'open' : 'closed'}`}>
          <div className="sidebar-menu">
            {menuItems && menuItems.map((item, index) => (
              <button
                key={index}
                className={`menu-item ${item.active ? 'active' : ''}`}
                onClick={item.onClick}
              >
                {item.icon && <span className="menu-icon">{item.icon}</span>}
                {sidebarOpen && <span className="menu-label">{item.label}</span>}
              </button>
            ))}
          </div>
        </aside>

        {/* Main Content */}
        <main className="main-content">
          {children}
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
