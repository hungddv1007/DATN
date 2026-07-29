import api from './api';

export const analyzeNutrition = async (mealsData) => {
  const response = await api.post('/nutrition/analyze', mealsData);
  return response.data;
};

export default {
  analyzeNutrition,
};
