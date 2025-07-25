import baseAxios from "@/api/BaseAxios";


export const getReasonAssign = async (params = {}) => {
    return await baseAxios.get(`/reason/list?reason=${params}`);
};

export const createReasonAssign = async (params = {}) => {
    return await baseAxios.post('/reason/create', params);
};

export const updateReasonAssign = async (params = {}) => {
    return await baseAxios.put('/reason/update', params)
}

export const deleteReasonAssign = async (params = {}) => {
    return await baseAxios.delete(`/reason/delete/${params}`);
};

export const getDetailReasonAssign = async (params = {}) => {
    return await baseAxios.get(`/reason/detail?idReason=${params}`);
};
export const getListRequestByReason = async (id) => {
    return await baseAxios.get(`/reason/list-request-application?reason=${id}`);
};
export const updateListRequestByReason = async (params = {}) => {
    return await baseAxios.put('/reason/list-request-application',params);
};
export default {
    getReasonAssign,
    createReasonAssign,
    deleteReasonAssign,
    updateReasonAssign,
    getDetailReasonAssign,
    getListRequestByReason,
    updateListRequestByReason
}