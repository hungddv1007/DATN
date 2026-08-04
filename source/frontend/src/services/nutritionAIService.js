import api from './api';

export const analyzeNutrition = async (mealsData) => {
  const response = await api.post('/nutrition/analyze', mealsData);
  return response.data;
};

export const generateDietFromPhysicalProfile = async (memberId, dayType) => {
  const response = await api.post('/nutrition/generate-diet', {
    memberId: Number(memberId),
    dayType,
  });
  return response.data;
};

export default {
  analyzeNutrition,
  generateDietFromPhysicalProfile,
};
