import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import MemberPhysicalProfilePage from './MemberPhysicalProfilePage';
import memberProfileService from '../../services/memberProfileService';

vi.mock('../../components/layout/MainLayout', () => ({
  default: ({ children }) => <div>{children}</div>,
}));

vi.mock('../../services/memberProfileService', () => ({
  default: {
    getMyPhysicalProfile: vi.fn(),
    updateMyPhysicalProfile: vi.fn(),
  },
}));

const emptyProfile = {
  heightCm: null,
  weightKg: null,
  dateOfBirth: null,
  biologicalSex: null,
  chestCm: null,
  waistCm: null,
  hipCm: null,
  bodyFatPercentage: null,
  activityLevel: null,
  fitnessGoal: null,
  targetWeightKg: null,
  trainingExperience: null,
  injuryHistory: null,
  medicalConditions: null,
  bodyFatSource: null,
};

describe('MemberPhysicalProfilePage', () => {
  beforeEach(() => {
    memberProfileService.getMyPhysicalProfile.mockResolvedValue(emptyProfile);
    memberProfileService.updateMyPhysicalProfile.mockImplementation(
      async (payload) => payload,
    );
  });

  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('cho phép lưu khi tất cả chỉ số đều để trống', async () => {
    const user = userEvent.setup();
    render(<MemberPhysicalProfilePage />);

    await user.click(await screen.findByRole('button', { name: /Lưu hồ sơ/i }));

    await waitFor(() => expect(memberProfileService.updateMyPhysicalProfile)
      .toHaveBeenCalledWith(expect.objectContaining({
        heightCm: null,
        fitnessGoal: null,
        injuryHistory: null,
        medicalConditions: null,
      })));
  });

  it('chuyển số đo thành number và gửi mục tiêu đã chọn', async () => {
    const user = userEvent.setup();
    render(<MemberPhysicalProfilePage />);

    await user.type(await screen.findByLabelText(/Chiều cao/i), '172.5');
    await user.selectOptions(screen.getByLabelText(/Mục tiêu tập luyện/i), 'MUSCLE_GAIN');
    await user.click(screen.getByRole('button', { name: /Lưu hồ sơ/i }));

    await waitFor(() => expect(memberProfileService.updateMyPhysicalProfile)
      .toHaveBeenCalledWith(expect.objectContaining({
        heightCm: 172.5,
        fitnessGoal: 'MUSCLE_GAIN',
      })));
  });

  it('tự ước tính tỷ lệ mỡ khi đủ chiều cao, cân nặng, ngày sinh và giới tính', async () => {
    const user = userEvent.setup();
    render(<MemberPhysicalProfilePage />);

    await user.type(await screen.findByLabelText(/Chiều cao/i), '172');
    await user.type(screen.getAllByLabelText(/^Cân nặng/i)[0], '70');
    await user.type(screen.getByLabelText(/Ngày sinh/i), '2000-01-01');
    await user.selectOptions(screen.getByLabelText(/Giới tính sinh học/i), 'MALE');

    const bodyFatInput = screen.getByLabelText(/Tỷ lệ mỡ cơ thể/i);
    expect(bodyFatInput).toHaveAttribute('readonly');
    expect(Number(bodyFatInput.value)).toBeGreaterThan(0);

    await user.click(screen.getByRole('button', { name: /Lưu hồ sơ/i }));
    await waitFor(() => expect(memberProfileService.updateMyPhysicalProfile)
      .toHaveBeenCalledWith(expect.objectContaining({
        biologicalSex: 'MALE',
        bodyFatPercentage: expect.any(Number),
      })));
  });
});
