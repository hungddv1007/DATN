export default function AdminPagination({ page, totalPages, onPageChange, alwaysVisible = false }) {
  if (totalPages <= 0 || (!alwaysVisible && totalPages <= 1)) return null;

  return (
    <nav className="pagination" aria-label="Phân trang danh sách quản lý">
      <button
        type="button"
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        Trước
      </button>
      <span>Trang {page + 1} / {totalPages}</span>
      <button
        type="button"
        disabled={page >= totalPages - 1}
        onClick={() => onPageChange(page + 1)}
      >
        Sau
      </button>
    </nav>
  );
}
