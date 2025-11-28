import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [motDePasse, setMotDePasse] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await login(email, motDePasse);

    if (result.success) {
      // Redirection gérée automatiquement par App.jsx
      navigate('/');
    } else {
      setError(result.message || 'Email ou mot de passe incorrect');
    }

    setLoading(false);
  };

  // Comptes de test rapides
  const quickLogin = (testEmail, testPassword) => {
    setEmail(testEmail);
    setMotDePasse(testPassword);
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-header">
          <h1>Système de Gestion des Absences</h1>
          <p>Connexion</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="exemple@university.com"
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="motDePasse">Mot de passe</label>
            <input
              type="password"
              id="motDePasse"
              value={motDePasse}
              onChange={(e) => setMotDePasse(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button
            type="submit"
            className="btn-login"
            disabled={loading}
          >
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <div className="test-accounts">
          <p><strong>Comptes de test :</strong></p>
          <div className="test-buttons">
            <button
              type="button"
              onClick={() => quickLogin('admin@university.com', 'password123')}
              className="test-btn admin"
            >
              Admin
            </button>
            <button
              type="button"
              onClick={() => quickLogin('sophie.martin@university.com', 'password123')}
              className="test-btn enseignant"
            >
              Enseignant
            </button>
            <button
              type="button"
              onClick={() => quickLogin('pierre.dubois@university.com', 'password123')}
              className="test-btn chef"
            >
              Chef Dép.
            </button>
            <button
              type="button"
              onClick={() => quickLogin('marie.dubois@university.com', 'password123')}
              className="test-btn etudiant"
            >
              Étudiant
            </button>
          </div>
          <p className="test-note">Mot de passe : password123</p>
        </div>
      </div>
    </div>
  );
}

export default Login;
