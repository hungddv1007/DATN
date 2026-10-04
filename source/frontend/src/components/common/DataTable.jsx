import React from 'react';
import './DataTable.css';

const DataTable = ({ columns, data, loading }) => {
  if (loading) {
    return <div className="table-loading">Đang tải dữ liệu...</div>;
  }

  if (!data || data.length === 0) {
    return <div className="table-empty">Không có dữ liệu</div>;
  }

  return (
    <div className="table-responsive">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((col, index) => (
              <th key={index} style={{ width: col.width }}>{col.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, rowIndex) => (
            <tr key={rowIndex}>
              {columns.map((col, colIndex) => (
                <td key={colIndex}>
                  {col.render ? col.render(row) : row[col.accessor]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DataTable;
