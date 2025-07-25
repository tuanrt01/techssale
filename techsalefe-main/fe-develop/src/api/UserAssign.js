import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1";

export const getUserAssign = async () => {
    return await baseAxios.get(`${ENDPOINT}/user-assign`);
};

export const getUserBuyer = async () => {
    return await baseAxios.get(`${ENDPOINT}/buyer-list`)
}
export default {
    getUserAssign,
    getUserBuyer
}