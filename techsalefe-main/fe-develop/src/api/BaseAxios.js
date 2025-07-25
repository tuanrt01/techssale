import axios from 'axios'
import {AuthUtils} from "@/utils/localStorageUtils";
const API_ROOT = import.meta.env.VITE_API_ROOT

const defaultAxiosConfig = {
  baseURL: API_ROOT,
  headers: {
    'Cache-Control': 'no-cache',
    'Pragma': 'no-cache',
    'Expires': '0'
  }
}
const baseAxios = axios.create(defaultAxiosConfig)
baseAxios.interceptors.request.use(
  config => {
    config.headers.Authorization = `Bearer ${AuthUtils.getTokenFromCookies()}`
    return config;
  },
  Promise.reject
);
baseAxios.interceptors.response.use(
  response => {
    return response;
  },
  (error) => Promise.reject(error.response)
);
export default baseAxios;