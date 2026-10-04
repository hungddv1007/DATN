import { expect, test } from '@playwright/test';

test('OTP sai không được mở bước đặt mật khẩu', async ({ page }) => {
  await page.route('**/api/auth/forgot-password', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ message: 'Nếu email tồn tại, OTP đã được gửi.' }),
  }));
  await page.route('**/api/auth/verify-otp', async (route) => {
    const body = route.request().postDataJSON();
    await route.fulfill({
      status: body.otp === '567128' ? 200 : 400,
      contentType: 'application/json',
      body: JSON.stringify({ message: body.otp === '567128' ? 'OTP hợp lệ' : 'Mã OTP không đúng hoặc đã hết hạn' }),
    });
  });

  await page.goto('/forgot-password');
  await page.locator('#fp-email').fill('member@gympro.test');
  await page.locator('form').evaluate((form) => form.requestSubmit());
  await expect(page.locator('.fp-otp-input')).toHaveCount(6);

  for (const [index, digit] of [...'111111'].entries()) {
    await page.locator('.fp-otp-input').nth(index).fill(digit);
  }
  await page.locator('form').evaluate((form) => form.requestSubmit());
  await expect(page.getByText('Mã OTP không đúng hoặc đã hết hạn')).toBeVisible();
  await expect(page.locator('#fp-newpwd')).toHaveCount(0);

  for (const [index, digit] of [...'567128'].entries()) {
    await page.locator('.fp-otp-input').nth(index).fill(digit);
  }
  await page.locator('form').evaluate((form) => form.requestSubmit());
  await expect(page.locator('#fp-newpwd')).toBeVisible();
});
