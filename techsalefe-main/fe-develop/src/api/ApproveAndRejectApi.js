import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1/request-application";

const updateStatus = async (approveRequest) => {
  return await baseAxios.put(`${ENDPOINT}`, approveRequest);
};
const buyerCompletion = async (requestId, params = {}) => {
  return await baseAxios.put(`${ENDPOINT}/${requestId}/buyer-completion`, params);
};
const buyerRejection = async (requestId, params = {}) => {
  return await baseAxios.put(`${ENDPOINT}/${requestId}/buyer-rejection`, params);
};

const buyerOrder = async (requestId, updateAt) => {
  return await baseAxios.put(`${ENDPOINT}/${requestId}/order?updateAt=${updateAt}`);
}

const confirmRequestByAssigner = async (id, params = {}) => {
  return await baseAxios.put(`${ENDPOINT}/${id}/approval`, params)
}

const rejectRequestByAssigner = async (id, params = {}) => {
  return await baseAxios.put(`${ENDPOINT}/${id}/rejection`, params)
}
export default {
  updateStatus,
  confirmRequestByAssigner,
  rejectRequestByAssigner,
  buyerCompletion,
  buyerRejection,
  buyerOrder
}
