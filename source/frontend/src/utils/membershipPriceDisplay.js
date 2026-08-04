const PERIOD_LABELS = new Map([
  [1, 'ngày'],
  [7, 'tuần'],
  [30, 'tháng'],
  [90, '3 tháng'],
  [180, '6 tháng'],
  [365, 'năm'],
  [730, '2 năm'],
]);

const toNumber = (value) => {
  const number = Number(value);
  return Number.isFinite(number) ? number : null;
};

const periodLabel = (days) => PERIOD_LABELS.get(days) || `${days} ngày`;

/**
 * Hiển thị kỳ mua/gia hạn đã thực sự có hiệu lực, không dùng giao dịch
 * gia hạn đang chờ duyệt và không coi phí nâng cấp là giá của một chu kỳ.
 */
export const getMembershipPriceDisplay = (membership, history = []) => {
  const dailyPrice = toNumber(membership?.dailyPrice) ?? 0;
  const fallback = {
    amount: dailyPrice,
    unit: 'ngày',
    caption: 'Đơn giá hiện tại',
    periodDays: 1,
    source: 'DAILY_RATE',
  };

  if (!membership) return fallback;

  const expectedStatus = membership.status === 'PENDING'
    ? 'PENDING'
    : 'CONFIRMED';
  const transaction = history.find((item) =>
    item.id === membership.id
    && item.packageId === membership.packageId
    && item.transactionStatus === expectedStatus
    && ['NEW', 'RENEW'].includes(item.transactionType),
  );

  if (!transaction) return fallback;

  const grossAmount = toNumber(transaction.originalAmount);
  const transactionDailyPrice = toNumber(transaction.dailyPrice);
  if (!grossAmount || !transactionDailyPrice) return fallback;

  const calculatedDays = grossAmount / transactionDailyPrice;
  const periodDays = Math.round(calculatedDays);
  if (periodDays <= 0 || Math.abs(calculatedDays - periodDays) > 0.001) {
    return fallback;
  }

  const finalAmount = toNumber(transaction.finalAmount);
  return {
    amount: finalAmount !== null ? finalAmount : grossAmount,
    unit: periodLabel(periodDays),
    caption: expectedStatus === 'PENDING'
      ? 'Giá đang chờ xác nhận'
      : transaction.transactionType === 'RENEW'
        ? 'Giá kỳ gia hạn gần nhất'
        : 'Giá kỳ đăng ký',
    periodDays,
    source: transaction.transactionType,
  };
};

