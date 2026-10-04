import { describe, expect, it } from 'vitest';
import {
  getScheduleTransitionLabel,
  isInTimeBlock,
  SCHEDULE_TIME_BLOCKS,
} from './scheduleTimeBlocks';

describe('scheduleTimeBlocks', () => {
  it('xếp lịch theo đúng thứ tự ba khung trong một ngày lịch', () => {
    expect(isInTimeBlock('00:00', SCHEDULE_TIME_BLOCKS[0])).toBe(true);
    expect(isInTimeBlock('05:59', SCHEDULE_TIME_BLOCKS[0])).toBe(true);
    expect(isInTimeBlock('06:00', SCHEDULE_TIME_BLOCKS[1])).toBe(true);
    expect(isInTimeBlock('17:59', SCHEDULE_TIME_BLOCKS[1])).toBe(true);
    expect(isInTimeBlock('18:00', SCHEDULE_TIME_BLOCKS[2])).toBe(true);
    expect(isInTimeBlock('23:59', SCHEDULE_TIME_BLOCKS[2])).toBe(true);
  });

  it('đánh dấu lịch kéo dài qua khung giờ khác nhưng không nhân đôi lịch', () => {
    expect(getScheduleTransitionLabel('16:00', '23:00'))
      .toBe('Kéo dài đến buổi tối');
    expect(getScheduleTransitionLabel('16:00', '18:00')).toBeNull();
  });
});
