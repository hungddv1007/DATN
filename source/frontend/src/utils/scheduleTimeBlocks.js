export const SCHEDULE_TIME_BLOCKS = [
  {
    id: 'early-morning',
    label: 'RẠNG SÁNG',
    displayName: 'Rạng sáng',
    range: '00:00 – 06:00',
    startMinute: 0,
    endMinute: 6 * 60,
    defaultStart: '00:00',
  },
  {
    id: 'daytime',
    label: 'BAN NGÀY',
    displayName: 'Ban ngày',
    range: '06:00 – 18:00',
    startMinute: 6 * 60,
    endMinute: 18 * 60,
    defaultStart: '08:00',
  },
  {
    id: 'evening',
    label: 'BUỔI TỐI',
    displayName: 'Buổi tối',
    range: '18:00 – 24:00',
    startMinute: 18 * 60,
    endMinute: 24 * 60,
    defaultStart: '19:00',
  },
];

export function timeToMinutes(time) {
  const [hour, minute] = time.split(':').map(Number);
  return hour * 60 + minute;
}

export function isInTimeBlock(startTime, block) {
  const minutes = timeToMinutes(startTime);
  return minutes >= block.startMinute && minutes < block.endMinute;
}

export function getScheduleTransitionLabel(startTime, endTime) {
  const startMinutes = timeToMinutes(startTime);
  const endMinutes = timeToMinutes(endTime);

  if (endMinutes <= startMinutes) return null;

  const startBlock = SCHEDULE_TIME_BLOCKS.find(
    block => startMinutes >= block.startMinute && startMinutes < block.endMinute,
  );
  // Thời điểm kết thúc là cận phải: 18:00 vẫn thuộc trọn khung kết thúc lúc 18:00.
  const lastOccupiedMinute = Math.max(startMinutes, endMinutes - 1);
  const endBlock = SCHEDULE_TIME_BLOCKS.find(
    block => lastOccupiedMinute >= block.startMinute && lastOccupiedMinute < block.endMinute,
  );

  if (!startBlock || !endBlock || startBlock.id === endBlock.id) return null;
  return `Kéo dài đến ${endBlock.displayName.toLowerCase()}`;
}
