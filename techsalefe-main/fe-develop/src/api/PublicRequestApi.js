import publicAxios from "@/api/PublicBaseAxios";

const ENDPOINT = "public-api/v1/purchase-history"; // Đây sẽ là endpoint public mới cần tạo ở backend

export const getPurchaseHistory = async (params = {}) => {
    const queryParams = new URLSearchParams(params).toString();
    return await publicAxios.get(`${ENDPOINT}?${queryParams}`);
};

export const getPurchaseHistoryDetail = async (id) => {
    return await publicAxios.get(`${ENDPOINT}/${id}`);
};

export default {
    getPurchaseHistory,
    getPurchaseHistoryDetail
} 