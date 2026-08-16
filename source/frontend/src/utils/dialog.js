export const CONFIRM_DIALOG_EVENT = 'gympro:confirm';
export const PROMPT_DIALOG_EVENT = 'gympro:prompt';

export const confirmDialog = (message, options = {}) => new Promise(resolve => {
  window.dispatchEvent(new CustomEvent(CONFIRM_DIALOG_EVENT, {
    detail: {
      message: String(message ?? ''),
      title: options.title || 'Xác nhận thao tác',
      confirmText: options.confirmText || 'Xác nhận',
      cancelText: options.cancelText || 'Hủy',
      danger: Boolean(options.danger),
      requireAcknowledgement: Boolean(options.requireAcknowledgement),
      acknowledgementText: options.acknowledgementText || '',
      resolve,
    },
  }));
});

export const promptDialog = (message, options = {}) => new Promise(resolve => {
  window.dispatchEvent(new CustomEvent(PROMPT_DIALOG_EVENT, {
    detail: {
      message: String(message ?? ''),
      title: options.title || 'Nhập thông tin',
      confirmText: options.confirmText || 'Tiếp tục',
      cancelText: options.cancelText || 'Hủy',
      defaultValue: String(options.defaultValue ?? ''),
      placeholder: options.placeholder || '',
      inputType: options.inputType || 'text',
      inputMode: options.inputMode,
      min: options.min,
      max: options.max,
      maxLength: options.maxLength,
      required: Boolean(options.required),
      multiline: Boolean(options.multiline),
      resolve,
    },
  }));
});
