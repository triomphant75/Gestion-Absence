import React from 'react';
import { MdChevronLeft, MdChevronRight } from 'react-icons/md';
import './Pagination.css';

function Pagination({ 
  currentPage, 
  totalPages, 
  onPageChange, 
  hasNextPage, 
  hasPreviousPage 
}) {
  if (totalPages <= 1) return null;

  return (
    <div className="pagination-container">
      <button
        className="pagination-btn"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={!hasPreviousPage}
        title="Page précédente"
      >
        <MdChevronLeft />
      </button>

      <div className="pagination-info">
        <span>Page {currentPage} sur {totalPages}</span>
      </div>

      <button
        className="pagination-btn"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={!hasNextPage}
        title="Page suivante"
      >
        <MdChevronRight />
      </button>
    </div>
  );
}

export default Pagination;
