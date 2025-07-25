import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "client-api/v1/attachments";

export const getAttachment = async (id) => {
    return await baseAxios.get(`${ENDPOINT}/${id}`,  {responseType: "blob"});
};
export default {
    getAttachment,
    namespaced: true,
};