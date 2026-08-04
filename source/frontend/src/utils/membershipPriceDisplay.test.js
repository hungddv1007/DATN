import { describe, expect, it } from 'vitest';
import { getMembershipPriceDisplay } from './membershipPriceDisplay';

const membership = {
  id: 10,
  packageId: 3,
  dailyPrice: 83000,
  status: 'ACTIVE',
};

const transaction = (overrides = {}) => ({
  id: 10,
  packageId: 3,
  dailyPrice: 83000,
  originalAmount: 7470000,
  finalAmount: 6723000,
  transactionType: 'NEW',
  transactionStatus: 'CONFIRMED',
  ...overrides,
});

describe('getMembershipPriceDisplay', () => {
  it('shows the actual amount paid for the confirmed registration period', () => {
    expect(getMembershipPriceDisplay(membership, [transaction()])).toEqual({
      amount: 6723000,
      unit: '3 tháng',
      caption: 'Giá kỳ đăng ký',
      periodDays: 90,
      source: 'NEW',
    });
  });

  it('uses the latest confirmed renewal and ignores a pending renewal', () => {
    const result = getMembershipPriceDisplay(membership, [
      transaction({
        originalAmount: 2490000,
        finalAmount: 2490000,
        transactionType: 'RENEW',
        transactionStatus: 'PENDING',
      }),
      transaction({
        originalAmount: 2490000,
        finalAmount: 2241000,
        transactionType: 'RENEW',
      }),
    ]);

    expect(result.amount).toBe(2241000);
    expect(result.unit).toBe('tháng');
    expect(result.caption).toBe('Giá kỳ gia hạn gần nhất');
  });

  it('shows a pending new membership using its selected period', () => {
    const result = getMembershipPriceDisplay(
      { ...membership, status: 'PENDING' },
      [transaction({ transactionStatus: 'PENDING' })],
    );

    expect(result.unit).toBe('3 tháng');
    expect(result.caption).toBe('Giá đang chờ xác nhận');
  });

  it('falls back to the daily rate after an upgrade without a billing period', () => {
    const result = getMembershipPriceDisplay(membership, [
      transaction({ transactionType: 'UPGRADE' }),
    ]);

    expect(result).toMatchObject({
      amount: 83000,
      unit: 'ngày',
      caption: 'Đơn giá hiện tại',
      source: 'DAILY_RATE',
    });
  });
});

