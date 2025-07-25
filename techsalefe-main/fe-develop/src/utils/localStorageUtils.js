import Cookies from "js-cookie";
export const setToken = (token) => localStorage.setItem("token", token);
export const removeToken = () => localStorage.removeItem("token");
export const getToken = () => localStorage.getItem("token");
export const hasToken = () => !!localStorage.getItem("token");
export const setTokenInCookies = (value) =>
  Cookies.set("token", value, {
    path: "/",
    domain: `${import.meta.env.VITE_DOMAIN_COOKIE}`,
    expires: 3650,
  });
export const getTokenFromCookies = () => Cookies.get("token");
export const removeTokenFromCookie = () =>
  Cookies.remove("token", {
    path: "/",
    domain: `${import.meta.env.VITE_DOMAIN_COOKIE}`,
    expires: 3650,
  });

export const AuthUtils = {
  setToken,
  removeToken,
  getToken,
  hasToken,
  setTokenInCookies,
  getTokenFromCookies,
  removeTokenFromCookie,
};
