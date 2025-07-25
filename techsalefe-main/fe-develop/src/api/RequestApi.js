import baseAxios from "@/api/BaseAxios";
import {AuthUtils} from "@/utils/localStorageUtils";

const ENDPOINT = "client-api/v1/request-application";

export const getAllRequest = async (params = {}) => {
    const queryParams = new URLSearchParams(params).toString();
    return await baseAxios.get(`${ENDPOINT}?${queryParams}`);
};
export const createRequest = async (params = {}) => {
    return await baseAxios.post(`${ENDPOINT}`, params);
};

export const updateRequest = async (requestId, params = {}) => {
    return await baseAxios.put(`${ENDPOINT}/${requestId}`, params)
};

export const getRequestHistory = async (params = {}) => {
    const queryParams = new URLSearchParams(params).toString();
    return await baseAxios.get(`${ENDPOINT}/history?${queryParams}`);
};

export default {
    getAllRequest,
    createRequest,
    updateRequest,
    getRequestHistory
}