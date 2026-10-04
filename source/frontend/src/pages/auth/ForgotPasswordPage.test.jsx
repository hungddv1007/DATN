import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';
import ForgotPasswordPage from './ForgotPasswordPage';
import authService from '../../services/authService';

vi.mock('../../components/layout/MainLayout', () => ({
  default: ({ children }) => <div>{children}</div>,
}));

vi.mock('../../services/authService', () => ({
  default: {
    forgotPassword: vi.fn(),
    verifyForgotPasswordOtp: vi.fn(),
    resetPassword: vi.fn(),
  },
}));

describe('ForgotPasswordPage', () => {
  afterEach(() => {
    cleanup();
    vi.clearAllMocks();
  });

  it('không cho sang bước nhập mật khẩu khi backend báo OTP sai', async () => {
    const user = userEvent.setup();
    authService.forgotPassword.mockResolvedValue({ message: 'Nếu email tồn tại, OTP đã được gửi.' });
    authService.verifyForgotPasswordOtp.mockRejectedValue({
      response: { data: { message: 'Mã OTP không đúng hoặc đã hết hạn' } },
    });

    const { container } = render(<MemoryRouter><ForgotPasswordPage /></MemoryRouter>);
    await user.type(screen.getByLabelText(/Email/i), 'member@gympro.test');
    fireEvent.submit(container.querySelector('form'));

    await waitFor(() => expect(authService.forgotPassword).toHaveBeenCalled());
    const otpInputs = await screen.findAllByRole('textbox');
    expect(otpInputs).toHaveLength(6);
    fireEvent.paste(container.querySelector('.fp-otp-inputs'), {
      clipboardData: { getData: () => '123456' },
    });
    fireEvent.submit(container.querySelector('form'));

    expect(await screen.findByText('Mã OTP không đúng hoặc đã hết hạn')).toBeInTheDocument();
    expect(screen.queryAllByLabelText(/mật khẩu/i)).toHaveLength(0);
  });

  it('chỉ mở bước mật khẩu sau khi /verify-otp thành công', async () => {
    const user = userEvent.setup();
    authService.forgotPassword.mockResolvedValue({ message: 'OTP đã được gửi' });
    authService.verifyForgotPasswordOtp.mockResolvedValue({ message: 'OTP hợp lệ' });

    const { container } = render(<MemoryRouter><ForgotPasswordPage /></MemoryRouter>);
    await user.type(screen.getByLabelText(/Email/i), 'member@gympro.test');
    fireEvent.submit(container.querySelector('form'));
    await waitFor(() => expect(container.querySelectorAll('.fp-otp-input')).toHaveLength(6));
    fireEvent.paste(container.querySelector('.fp-otp-inputs'), {
      clipboardData: { getData: () => '111111' },
    });
    fireEvent.submit(container.querySelector('form'));

    await waitFor(() => expect(authService.verifyForgotPasswordOtp)
      .toHaveBeenCalledWith('member@gympro.test', '111111'));
    expect(await screen.findByLabelText(/^Mật khẩu mới$/i)).toBeInTheDocument();
  });
});
