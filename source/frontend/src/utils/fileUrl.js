export const resolveFileUrl = (value) => {
  if (!value) return '';
  if (/^(https?:|data:|blob:)/i.test(value)) return value;
  if (value.startsWith('/api/')) return value;

  const fileName = value.replace(/^.*[\\/]/, '');
  return `/api/files/download/${encodeURIComponent(fileName)}`;
};
