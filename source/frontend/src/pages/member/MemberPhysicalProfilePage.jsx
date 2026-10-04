import { useCallback, useEffect, useState } from 'react';
import { Activity, HeartPulse, Ruler, Save, ShieldCheck, Target } from 'lucide-react';
import MainLayout from '../../components/layout/MainLayout';
import memberProfileService from '../../services/memberProfileService';
import {
  ACTIVITY_LEVEL_OPTIONS,
  BIOLOGICAL_SEX_OPTIONS,
  FITNESS_GOAL_OPTIONS,
  estimateBodyFatPercentage,
} from '../../utils/physicalProfile';
import './MemberPhysicalProfilePage.css';

const EMPTY_FORM = {
  heightCm: '',
  weightKg: '',
  dateOfBirth: '',
  biologicalSex: '',
  chestCm: '',
  waistCm: '',
  hipCm: '',
  bodyFatPercentage: '',
  activityLevel: '',
  fitnessGoal: '',
  targetWeightKg: '',
  trainingExperience: '',
  injuryHistory: '',
  medicalConditions: '',
};

const NUMBER_FIELDS = new Set([
  'heightCm',
  'weightKg',
  'chestCm',
  'waistCm',
  'hipCm',
  'bodyFatPercentage',
  'targetWeightKg',
]);

const toFormData = (profile) => Object.keys(EMPTY_FORM).reduce((form, field) => ({
  ...form,
  [field]: profile?.[field] ?? '',
}), {});

const NumberInput = ({
  id,
  label,
  unit,
  value,
  onChange,
  min,
  max,
  placeholder,
  readOnly = false,
  helperText = '',
}) => (
  <div className="physical-form-group">
    <label htmlFor={id}>{label}<span>{unit}</span></label>
    <div className="physical-number-input">
      <input
        id={id}
        name={id}
        type="number"
        min={min}
        max={max}
        step="0.01"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        readOnly={readOnly}
        className={readOnly ? 'is-estimated' : ''}
      />
      <span>{unit}</span>
    </div>
    {helperText && <small className="physical-field-helper">{helperText}</small>}
  </div>
);

const MemberPhysicalProfilePage = () => {
  const [formData, setFormData] = useState(EMPTY_FORM);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const estimatedBodyFat = estimateBodyFatPercentage(formData);

  const loadProfile = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const profile = await memberProfileService.getMyPhysicalProfile();
      setFormData(toFormData(profile));
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể tải hồ sơ thể chất.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
    setSuccess('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    const payload = Object.entries(formData).reduce((data, [field, value]) => {
      const normalizedValue = String(value).trim();
      return {
        ...data,
        [field]: normalizedValue === ''
          ? null
          : NUMBER_FIELDS.has(field)
            ? Number(normalizedValue)
            : normalizedValue,
      };
    }, {});
    if (estimatedBodyFat !== null) {
      payload.bodyFatPercentage = estimatedBodyFat;
    }

    try {
      const updatedProfile =
        await memberProfileService.updateMyPhysicalProfile(payload);
      setFormData(toFormData(updatedProfile));
      setSuccess('Đã cập nhật hồ sơ thể chất thành công.');
    } catch (err) {
      setError(err.response?.data?.message || 'Không thể cập nhật hồ sơ thể chất.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <MainLayout>
      <div className="member-physical-page">
        <header className="member-physical-header">
          <div className="member-physical-header-icon"><Activity size={27} /></div>
          <div>
            <h1>Hồ sơ thể chất</h1>
            <p>Cập nhật những thông tin bạn biết để PT có thể hỗ trợ phù hợp hơn.</p>
          </div>
        </header>

        <div className="physical-privacy-note">
          <ShieldCheck size={20} />
          <div>
            <strong>Thông tin thuộc quyền kiểm soát của bạn</strong>
            <span>Không có trường nào bắt buộc. Chỉ PT đang được phân công cho bạn mới có quyền xem và không thể chỉnh sửa.</span>
          </div>
        </div>

        {error && <div className="physical-form-alert is-error" role="alert">{error}</div>}
        {success && <div className="physical-form-alert is-success" role="status">{success}</div>}

        {loading ? (
          <div className="physical-page-loading">Đang tải hồ sơ thể chất...</div>
        ) : (
          <form className="member-physical-form" onSubmit={handleSubmit}>
            <section className="physical-form-section">
              <div className="physical-form-section-heading">
                <Ruler size={21} />
                <div><h2>Chỉ số cơ thể</h2><p>Nhập các số đo hiện tại nếu bạn biết.</p></div>
              </div>
              <div className="physical-form-grid">
                <NumberInput id="heightCm" label="Chiều cao" unit="cm" min="50" max="300" placeholder="VD: 170" value={formData.heightCm} onChange={handleChange} />
                <NumberInput id="weightKg" label="Cân nặng" unit="kg" min="20" max="500" placeholder="VD: 65" value={formData.weightKg} onChange={handleChange} />
                <div className="physical-form-group">
                  <label htmlFor="dateOfBirth">Ngày sinh <span>Không bắt buộc</span></label>
                  <input
                    id="dateOfBirth"
                    name="dateOfBirth"
                    type="date"
                    max={new Date().toISOString().slice(0, 10)}
                    value={formData.dateOfBirth}
                    onChange={handleChange}
                  />
                </div>
                <div className="physical-form-group">
                  <label htmlFor="biologicalSex">Giới tính sinh học <span>Để ước tính tỷ lệ mỡ</span></label>
                  <select id="biologicalSex" name="biologicalSex" value={formData.biologicalSex} onChange={handleChange}>
                    <option value="">Chưa chọn</option>
                    {BIOLOGICAL_SEX_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                  </select>
                </div>
                <NumberInput
                  id="bodyFatPercentage"
                  label="Tỷ lệ mỡ cơ thể"
                  unit="%"
                  min="0"
                  max="100"
                  placeholder="VD: 18"
                  value={estimatedBodyFat ?? formData.bodyFatPercentage}
                  onChange={handleChange}
                  readOnly={estimatedBodyFat !== null}
                  helperText={estimatedBodyFat !== null
                    ? 'Ước tính tự động theo BMI, tuổi và giới tính; không phải kết quả đo y khoa.'
                    : 'Có thể nhập kết quả đo thực tế, hoặc bổ sung chiều cao, cân nặng, ngày sinh và giới tính để tự tính.'}
                />
                <NumberInput id="chestCm" label="Vòng ngực" unit="cm" min="20" max="300" placeholder="VD: 92" value={formData.chestCm} onChange={handleChange} />
                <NumberInput id="waistCm" label="Vòng eo" unit="cm" min="20" max="300" placeholder="VD: 75" value={formData.waistCm} onChange={handleChange} />
                <NumberInput id="hipCm" label="Vòng hông" unit="cm" min="20" max="300" placeholder="VD: 95" value={formData.hipCm} onChange={handleChange} />
              </div>
            </section>

            <section className="physical-form-section">
              <div className="physical-form-section-heading">
                <Target size={21} />
                <div><h2>Vận động và mục tiêu</h2><p>Giúp PT hiểu nhu cầu tập luyện hiện tại của bạn.</p></div>
              </div>
              <div className="physical-form-grid physical-form-grid-two">
                <div className="physical-form-group">
                  <label htmlFor="activityLevel">Mức độ vận động hiện tại</label>
                  <select id="activityLevel" name="activityLevel" value={formData.activityLevel} onChange={handleChange}>
                    <option value="">Chưa chọn</option>
                    {ACTIVITY_LEVEL_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                  </select>
                </div>
                <div className="physical-form-group">
                  <label htmlFor="fitnessGoal">Mục tiêu tập luyện</label>
                  <select id="fitnessGoal" name="fitnessGoal" value={formData.fitnessGoal} onChange={handleChange}>
                    <option value="">Chưa chọn</option>
                    {FITNESS_GOAL_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                  </select>
                </div>
                <NumberInput id="targetWeightKg" label="Cân nặng mục tiêu" unit="kg" min="20" max="500" placeholder="VD: 60" value={formData.targetWeightKg} onChange={handleChange} />
                <div className="physical-form-group">
                  <label htmlFor="trainingExperience">Kinh nghiệm tập luyện <span>{formData.trainingExperience.length}/500</span></label>
                  <textarea id="trainingExperience" name="trainingExperience" value={formData.trainingExperience} onChange={handleChange} maxLength={500} rows={3} placeholder="VD: Mới bắt đầu hoặc đã tập khoảng 6 tháng..." />
                </div>
              </div>
            </section>

            <section className="physical-form-section">
              <div className="physical-form-section-heading">
                <HeartPulse size={21} />
                <div><h2>Sức khỏe và giới hạn vận động</h2><p>Thông tin này giúp PT thiết kế bài tập an toàn hơn.</p></div>
              </div>
              <div className="physical-form-grid physical-form-grid-two">
                <div className="physical-form-group">
                  <label htmlFor="injuryHistory">Tiền sử chấn thương <span>{formData.injuryHistory.length}/2000</span></label>
                  <textarea id="injuryHistory" name="injuryHistory" value={formData.injuryHistory} onChange={handleChange} maxLength={2000} rows={5} placeholder="VD: Từng chấn thương đầu gối trái..." />
                </div>
                <div className="physical-form-group">
                  <label htmlFor="medicalConditions">Bệnh lý hoặc hạn chế vận động <span>{formData.medicalConditions.length}/2000</span></label>
                  <textarea id="medicalConditions" name="medicalConditions" value={formData.medicalConditions} onChange={handleChange} maxLength={2000} rows={5} placeholder="VD: Huyết áp cao, hạn chế vận động cường độ mạnh..." />
                </div>
              </div>
            </section>

            <div className="physical-form-actions">
              <p>Bạn có thể để trống hoặc xóa bất kỳ thông tin nào rồi lưu lại.</p>
              <button type="submit" disabled={saving}>
                <Save size={18} /> {saving ? 'Đang lưu...' : 'Lưu hồ sơ'}
              </button>
            </div>
          </form>
        )}
      </div>
    </MainLayout>
  );
};

export default MemberPhysicalProfilePage;
