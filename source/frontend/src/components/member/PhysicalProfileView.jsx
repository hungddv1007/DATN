import { Activity, Dumbbell, HeartPulse, Ruler } from 'lucide-react';
import {
  ACTIVITY_LEVEL_OPTIONS,
  BIOLOGICAL_SEX_OPTIONS,
  FITNESS_GOAL_OPTIONS,
  formatPhysicalMetric,
  formatDateOfBirth,
  getOptionLabel,
} from '../../utils/physicalProfile';
import './PhysicalProfileView.css';

const ValueItem = ({ label, value, wide = false }) => (
  <div className={`physical-view-value${wide ? ' is-wide' : ''}`}>
    <span>{label}</span>
    <strong className={value === 'Chưa cập nhật' ? 'is-empty' : ''}>{value}</strong>
  </div>
);

const textValue = (value) => value?.trim() || 'Chưa cập nhật';

const PhysicalProfileView = ({ profile, loading, error }) => {
  if (loading) {
    return <div className="physical-view-state">Đang tải hồ sơ thể chất...</div>;
  }

  if (error) {
    return <div className="physical-view-state is-error">{error}</div>;
  }

  return (
    <div className="physical-profile-view">
      <section className="physical-view-section">
        <div className="physical-view-section-title">
          <Ruler size={19} />
          <div><h3>Chỉ số cơ thể</h3><p>Các số đo hiện tại do hội viên cung cấp.</p></div>
        </div>
        <div className="physical-view-grid">
          <ValueItem label="Chiều cao" value={formatPhysicalMetric(profile?.heightCm, 'cm')} />
          <ValueItem label="Cân nặng" value={formatPhysicalMetric(profile?.weightKg, 'kg')} />
          <ValueItem label="Ngày sinh" value={formatDateOfBirth(profile?.dateOfBirth)} />
          <ValueItem
            label="Giới tính sinh học"
            value={getOptionLabel(BIOLOGICAL_SEX_OPTIONS, profile?.biologicalSex)}
          />
          <ValueItem
            label={profile?.bodyFatSource === 'ESTIMATED' ? 'Tỷ lệ mỡ (ước tính)' : 'Tỷ lệ mỡ'}
            value={formatPhysicalMetric(profile?.bodyFatPercentage, '%')}
          />
          <ValueItem label="Vòng ngực" value={formatPhysicalMetric(profile?.chestCm, 'cm')} />
          <ValueItem label="Vòng eo" value={formatPhysicalMetric(profile?.waistCm, 'cm')} />
          <ValueItem label="Vòng hông" value={formatPhysicalMetric(profile?.hipCm, 'cm')} />
        </div>
      </section>

      <section className="physical-view-section">
        <div className="physical-view-section-title">
          <Activity size={19} />
          <div><h3>Vận động và mục tiêu</h3><p>Thông tin định hướng quá trình tập luyện.</p></div>
        </div>
        <div className="physical-view-grid">
          <ValueItem
            label="Mức độ vận động"
            value={getOptionLabel(ACTIVITY_LEVEL_OPTIONS, profile?.activityLevel)}
          />
          <ValueItem
            label="Mục tiêu tập luyện"
            value={getOptionLabel(FITNESS_GOAL_OPTIONS, profile?.fitnessGoal)}
          />
          <ValueItem
            label="Cân nặng mục tiêu"
            value={formatPhysicalMetric(profile?.targetWeightKg, 'kg')}
          />
          <ValueItem label="Kinh nghiệm tập luyện" value={textValue(profile?.trainingExperience)} />
        </div>
      </section>

      <section className="physical-view-section">
        <div className="physical-view-section-title">
          <HeartPulse size={19} />
          <div><h3>Sức khỏe và giới hạn vận động</h3><p>Lưu ý để xây dựng bài tập phù hợp và an toàn.</p></div>
        </div>
        <div className="physical-view-grid physical-view-grid-text">
          <ValueItem label="Tiền sử chấn thương" value={textValue(profile?.injuryHistory)} wide />
          <ValueItem label="Bệnh lý hoặc hạn chế vận động" value={textValue(profile?.medicalConditions)} wide />
        </div>
      </section>

      <div className="physical-view-readonly-note">
        <Dumbbell size={17} />
        Đây là thông tin chỉ đọc. Chỉ hội viên mới có quyền cập nhật hồ sơ này.
      </div>
    </div>
  );
};

export default PhysicalProfileView;
