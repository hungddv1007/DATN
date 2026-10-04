import { describe, expect, it } from 'vitest';
import { calculateAge, estimateBodyFatPercentage } from './physicalProfile';

describe('physicalProfile utilities', () => {
  const referenceDate = new Date('2026-08-04T12:00:00');

  it('tính tuổi theo ngày sinh đã qua hay chưa trong năm', () => {
    expect(calculateAge('2000-01-01', referenceDate)).toBe(26);
    expect(calculateAge('2000-12-01', referenceDate)).toBe(25);
  });

  it('ước tính tỷ lệ mỡ người trưởng thành bằng công thức Deurenberg', () => {
    expect(estimateBodyFatPercentage({
      heightCm: 172,
      weightKg: 70,
      dateOfBirth: '2000-01-01',
      biologicalSex: 'MALE',
    }, referenceDate)).toBe(18.2);
  });

  it('không tự ước tính khi thiếu dữ liệu hoặc chưa đủ 18 tuổi', () => {
    expect(estimateBodyFatPercentage({ heightCm: 172, weightKg: 70 }, referenceDate)).toBeNull();
    expect(estimateBodyFatPercentage({
      heightCm: 172,
      weightKg: 70,
      dateOfBirth: '2010-01-01',
      biologicalSex: 'FEMALE',
    }, referenceDate)).toBeNull();
  });
});
