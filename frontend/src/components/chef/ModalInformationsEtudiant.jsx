import { useState } from 'react';
import { MdClose, MdCheckCircle, MdCancel, MdWarning } from 'react-icons/md';
import './ModalInformationsEtudiant.css';

function ModalInformationsEtudiant({ etudiant, onClose }) {
  const [activeTab, setActiveTab] = useState('general');

  if (!etudiant) return null;

  const renderGeneralTab = () => (
    <div className="tab-content">
      <div className="info-section">
        <h3>Informations Personnelles</h3>
        <div className="info-grid">
          <div className="info-item">
            <label>Nom complet</label>
            <p>{etudiant.prenom} {etudiant.nom}</p>
          </div>
          <div className="info-item">
            <label>Numéro étudiant</label>
            <p>{etudiant.numeroEtudiant}</p>
          </div>
          <div className="info-item">
            <label>Email</label>
            <p>{etudiant.email}</p>
          </div>
          <div className="info-item">
            <label>Téléphone</label>
            <p>{etudiant.telephone || 'Non renseigné'}</p>
          </div>
        </div>
      </div>

      <div className="info-section">
        <h3>Formation</h3>
        <div className="info-grid">
          <div className="info-item">
            <label>Nom de la formation</label>
            <p>{etudiant.formation?.nom || 'N/A'}</p>
          </div>
          <div className="info-item">
            <label>Niveau</label>
            <p>Niveau {etudiant.formation?.niveau || 'N/A'}</p>
          </div>
          <div className="info-item">
            <label>Description</label>
            <p>{etudiant.formation?.description || 'Non renseignée'}</p>
          </div>
        </div>
      </div>
    </div>
  );

  const renderHistoriqueTab = () => {
    const historique = etudiant.historiquePresences || [];

    return (
      <div className="tab-content">
        <h3>Historique des Présences (50 dernières)</h3>
        {historique.length > 0 ? (
          <div className="historique-list">
            {historique.map((presence, index) => (
              <div key={index} className="historique-item">
                <div className="historique-header">
                  <span className="matiere-nom">{presence.seance?.matiere?.nom || 'N/A'}</span>
                  <span className={`statut-badge ${presence.statut?.toLowerCase()}`}>
                    {presence.statut === 'PRESENT' && <MdCheckCircle />}
                    {presence.statut === 'ABSENT' && <MdCancel />}
                    {presence.statut === 'RETARD' && <MdWarning />}
                    {presence.statut}
                  </span>
                </div>
                <div className="historique-details">
                  <span>
                    {presence.seance?.dateDebut ?
                      new Date(presence.seance.dateDebut).toLocaleString('fr-FR', {
                        dateStyle: 'medium',
                        timeStyle: 'short'
                      }) : 'Date inconnue'}
                  </span>
                  <span>Type: {presence.seance?.type || 'N/A'}</span>
                  {presence.justifie && (
                    <span className="justifie">Justifié <MdCheckCircle /></span>
                  )}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="no-data">Aucun historique disponible</p>
        )}
      </div>
    );
  };

  const renderJustificatifsTab = () => {
    const justificatifs = etudiant.justificatifs || [];

    return (
      <div className="tab-content">
        <h3>Justificatifs ({justificatifs.length})</h3>
        {justificatifs.length > 0 ? (
          <div className="justificatifs-list">
            {justificatifs.map((justif, index) => (
              <div key={index} className="justificatif-item">
                <div className="justificatif-header">
                  <span className="justif-id">Justificatif #{justif.id}</span>
                  <span className={`statut-badge ${justif.statut?.toLowerCase()}`}>
                    {justif.statut}
                  </span>
                </div>
                <div className="justificatif-details">
                  <p><strong>Motif:</strong> {justif.motif || 'Non renseigné'}</p>
                  <p><strong>Déposé le:</strong> {
                    justif.createdAt ?
                      new Date(justif.createdAt).toLocaleDateString('fr-FR') :
                      'Date inconnue'
                  }</p>
                  {justif.seance && (
                    <p><strong>Séance:</strong> {justif.seance.matiere} - {
                      justif.seance.dateDebut ?
                        new Date(justif.seance.dateDebut).toLocaleDateString('fr-FR') :
                        'Date inconnue'
                    }</p>
                  )}
                  {justif.commentaireValidation && (
                    <p><strong>Commentaire:</strong> {justif.commentaireValidation}</p>
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
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Informations Étudiant</h2>
          <button className="close-button" onClick={onClose}>
            <MdClose />
          </button>
        </div>

        <div className="modal-tabs">
          <button
            className={`tab ${activeTab === 'general' ? 'active' : ''}`}
            onClick={() => setActiveTab('general')}
          >
            Général
          </button>
          <button
            className={`tab ${activeTab === 'historique' ? 'active' : ''}`}
            onClick={() => setActiveTab('historique')}
          >
            Historique
          </button>
          <button
            className={`tab ${activeTab === 'justificatifs' ? 'active' : ''}`}
            onClick={() => setActiveTab('justificatifs')}
          >
            Justificatifs
          </button>
        </div>

        <div className="modal-body">
          {activeTab === 'general' && renderGeneralTab()}
          {activeTab === 'historique' && renderHistoriqueTab()}
          {activeTab === 'justificatifs' && renderJustificatifsTab()}
        </div>
      </div>
    </div>
  );
}

export default ModalInformationsEtudiant;
