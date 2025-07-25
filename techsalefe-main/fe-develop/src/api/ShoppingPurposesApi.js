import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1/request-application";

export const getAllReasonList = async (params = {}) => {
    return await baseAxios.get(`${ENDPOINT}?${queryParams}`);
};

export default {
    getAllReasonList
}