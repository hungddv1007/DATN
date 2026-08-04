export const ACTIVITY_LEVEL_OPTIONS = [
  { value: 'SEDENTARY', label: 'Ít vận động' },
  { value: 'LIGHT', label: 'Vận động nhẹ' },
  { value: 'MODERATE', label: 'Vận động vừa' },
  { value: 'HIGH', label: 'Vận động nhiều' },
  { value: 'VERY_HIGH', label: 'Vận động cường độ rất cao' },
];

export const FITNESS_GOAL_OPTIONS = [
  { value: 'WEIGHT_LOSS', label: 'Giảm cân' },
  { value: 'MUSCLE_GAIN', label: 'Tăng cơ' },
  { value: 'MAINTENANCE', label: 'Duy trì vóc dáng' },
  { value: 'HEALTH_IMPROVEMENT', label: 'Cải thiện sức khỏe' },
];

export const BIOLOGICAL_SEX_OPTIONS = [
  { value: 'MALE', label: 'Nam' },
  { value: 'FEMALE', label: 'Nữ' },
];

export const getOptionLabel = (options, value) =>
  options.find((option) => option.value === value)?.label || 'Chưa cập nhật';

export const formatPhysicalMetric = (value, unit) =>
  value === null || value === undefined || value === ''
    ? 'Chưa cập nhật'
    : `${Number(value).toLocaleString('vi-VN', { maximumFractionDigits: 2 })} ${unit}`;

export const calculateAge = (dateOfBirth, referenceDate = new Date()) => {
  if (!dateOfBirth) return null;
  const birthDate = new Date(`${dateOfBirth}T00:00:00`);
  if (Number.isNaN(birthDate.getTime()) || birthDate > referenceDate) return null;

  let age = referenceDate.getFullYear() - birthDate.getFullYear();
  const birthdayHasPassed = referenceDate.getMonth() > birthDate.getMonth()
    || (referenceDate.getMonth() === birthDate.getMonth()
      && referenceDate.getDate() >= birthDate.getDate());
  if (!birthdayHasPassed) age -= 1;
  return age;
};

export const estimateBodyFatPercentage = ({
  heightCm,
  weightKg,
  dateOfBirth,
  biologicalSex,
}, referenceDate = new Date()) => {
  const height = Number(heightCm);
  const weight = Number(weightKg);
  const age = calculateAge(dateOfBirth, referenceDate);
  if (!height || !weight || age === null || age < 18
    || !['MALE', 'FEMALE'].includes(biologicalSex)) {
    return null;
  }

  const heightMeters = height / 100;
  const bmi = weight / (heightMeters ** 2);
  const sexCoefficient = biologicalSex === 'MALE' ? 1 : 0;
  const estimated = 1.2 * bmi + 0.23 * age - 10.8 * sexCoefficient - 5.4;
  if (!Number.isFinite(estimated) || estimated < 0 || estimated > 100) return null;
  return Math.round(estimated * 10) / 10;
};

export const formatDateOfBirth = (value) => value
  ? new Date(`${value}T00:00:00`).toLocaleDateString('vi-VN')
  : 'Chưa cập nhật';
