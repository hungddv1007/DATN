import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import WorkoutResultDetails from './WorkoutResultDetails';

describe('WorkoutResultDetails', () => {
  it('hiển thị ghi chú từng bài tập và nhận xét chung của PT', () => {
    render(
      <WorkoutResultDetails
        session={{
          exercises: [{
            exerciseId: 1,
            exerciseName: 'Push Up',
            setCount: 3,
            repCount: 12,
            note: 'Giữ lưng thẳng trong toàn bộ động tác.',
          }],
          actualNote: 'Buổi tập hoàn thành tốt.',
        }}
      />,
    );

    expect(screen.getByText('Push Up')).toBeInTheDocument();
    expect(screen.getByText('3 hiệp · 12 lần')).toBeInTheDocument();
    expect(screen.getByText(/Giữ lưng thẳng/)).toBeInTheDocument();
    expect(screen.getByText('Nhận xét của PT sau buổi tập')).toBeInTheDocument();
    expect(screen.getByText('Buổi tập hoàn thành tốt.')).toBeInTheDocument();
  });

  it('hiển thị trạng thái rỗng khi chưa có kết quả chi tiết', () => {
    render(<WorkoutResultDetails session={{ exercises: [] }} />);
    expect(screen.getByText('Buổi tập chưa có kết quả chi tiết.')).toBeInTheDocument();
  });
});
