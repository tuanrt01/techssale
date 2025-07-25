import axios from 'axios'
const API_ROOT = import.meta.env.VITE_API_ROOT

const defaultAxiosConfig = {
  baseURL: API_ROOT
}

// Tạo instance axios riêng cho các API call không yêu cầu xác thực
const publicAxios = axios.create(defaultAxiosConfig)

// Có thể thêm một API key hoặc token cố định nếu backend yêu cầu
// publicAxios.interceptors.request.use(
//   config => {
//     config.headers['X-API-Key'] = 'public-api-key-here' // Thay thế bằng API key public nếu có
//     return config
//   },
//   Promise.reject
// )

publicAxios.interceptors.response.use(
  response => {
    return response
  },
  (error) => Promise.reject(error.response)
)

export default publicAxios 