import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1";

export const getListOrganization = async () => {
    return await baseAxios.get(`${ENDPOINT}/list-organization`);
};

export default {
    getListOrganization
}