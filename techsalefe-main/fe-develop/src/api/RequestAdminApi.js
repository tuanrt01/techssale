import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1/request-application";

const getDetailRequest = async (id) => {
  // Add timestamp to prevent caching
  const timestamp = new Date().getTime();
  return await baseAxios.get(`${ENDPOINT}/${id}?_=${timestamp}`);
};

export const getAllRequest = async (params = {}) => {
  const queryParams = new URLSearchParams(params).toString();
  return await baseAxios.get(`${ENDPOINT}?${queryParams}`);
};
export const deleteRequest = async (id) =>{
  return await baseAxios.delete(`${ENDPOINT}/${id}/delete`)
}
export default {
  getDetailRequest,
  deleteRequest
}