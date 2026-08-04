import { useEffect, useState } from 'react';

export const ADMIN_PAGE_SIZE = 7;

export default function useClientPagination(items, pageSize = ADMIN_PAGE_SIZE) {
  const [page, setPage] = useState(0);
  const totalPages = Math.ceil(items.length / pageSize);

  useEffect(() => {
    setPage(currentPage => Math.min(currentPage, Math.max(totalPages - 1, 0)));
  }, [totalPages]);

  const startIndex = page * pageSize;
  const pageItems = items.slice(startIndex, startIndex + pageSize);

  return { page, setPage, totalPages, pageItems };
}
