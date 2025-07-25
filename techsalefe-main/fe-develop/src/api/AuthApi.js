import baseAxios from "@/api/BaseAxios";

const ENDPOINT = "auth"

const login = async (data) => {
  return await baseAxios.post(`${ENDPOINT}/login`, data)
}

const getAuthInfo = async () => {
  return await baseAxios.get(`${ENDPOINT}/me`)
}

const logout = async () => {
  return await baseAxios.get(`${ENDPOINT}/logout`)
}

export default {
  login,
  getAuthInfo,
  logout
}