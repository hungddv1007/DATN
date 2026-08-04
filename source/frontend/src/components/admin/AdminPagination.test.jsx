import { fireEvent, render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import AdminPagination from './AdminPagination';
import useClientPagination from '../../hooks/useClientPagination';

function PaginationHarness() {
  const items = Array.from({ length: 10 }, (_, index) => `Bản ghi ${index + 1}`);
  const { page, setPage, totalPages, pageItems } = useClientPagination(items);

  return (
    <>
      <ul>{pageItems.map(item => <li key={item}>{item}</li>)}</ul>
      <AdminPagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </>
  );
}

describe('AdminPagination', () => {
  it('hiển thị tối đa 7 bản ghi và chuyển sang phần còn lại ở trang sau', () => {
    render(<PaginationHarness />);

    expect(screen.getAllByRole('listitem')).toHaveLength(7);
    expect(screen.getByText('Trang 1 / 2')).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: 'Sau' }));

    expect(screen.getAllByRole('listitem')).toHaveLength(3);
    expect(screen.getByText('Trang 2 / 2')).toBeInTheDocument();
  });
});
