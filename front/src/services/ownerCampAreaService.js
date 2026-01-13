import axiosapi from '@/plugins/axios.js'; // 你現在有的axios設定

// 營地API

export const getOwnerCampAreas = () => {
  return axiosapi.get('/camp-areas');
};

export const getCampAreaById = (id) => {
  return axiosapi.get(`/api/owner/camp-area/${id}`);
};

export const createCampArea = (data) => {
  return axiosapi.post('/api/owner/camp-area', data);
};

export const updateCampArea = (id, data) => {
  return axiosapi.put(`/api/owner/camp-area/${id}`, data);
};

export const deleteCampArea = (id) => {
  return axiosapi.delete(`/api/owner/camp-area/${id}`);
};

// 營位API

export const getCampSpotsByCampAreaId = (id) => {
  return axiosapi.get(`/api/owner/camp-area/${id}/spots`);
};
