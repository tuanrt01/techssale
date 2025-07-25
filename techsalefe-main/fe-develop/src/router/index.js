import {createRouter, createWebHistory} from "vue-router";
import {isEmpty} from 'lodash'
import SideMenu from "../layouts/side-menu/Main.vue";
import TopMenu from "../layouts/top-menu/Main.vue";
import Login from "../views/login/Main.vue";
import Register from "../views/register/Main.vue";
import ErrorPage from "../views/error-page/Main.vue";
import ListRequests from "@/views/admin/list-requested/ListRequests.vue";
import ListRequestMe from "@/views/user/list-requested/ListRequestMe.vue";
import PurchaseHistory from "@/views/user/list-requested/PurchaseHistory.vue";
import ShoppingPurposes from "@/views/admin/shopping-purposes/ShoppingPurposes.vue";
import Permission from "@/views/admin/permission/Permission.vue";
import Dashboard from "@/views/admin/dash-board/Dashboard.vue";
import store from '../stores'
import {AuthUtils} from "@/utils/localStorageUtils";
import PermissionNameEnum from "@/common/PermissionEnum";

// const token = AuthUtils.getToken()
if (store.state.auth.token !== AuthUtils.getToken()) {
  store.dispatch('auth/setToken', AuthUtils.getToken())
}

const token = AuthUtils.getTokenFromCookies()
if (store.state.auth.tokenFormCookies !== token) {
  store.dispatch('auth/setTokenInCookies', token)
}

  const routes = [
    {
      path: "/admin",
      component: SideMenu,
      children: [
        {
          path: "/dashboard",
          name: "side-menu-dashboard",
          component: Dashboard,
          meta: {
            requiresAuth: true,
            id: 1
          },
        },
        {
          path: "/admin",
          name: "side-menu-list-request-management",
          component: ListRequests,
          meta: {
            requiresAuth: true,
            id: 1
          },
        },
        {
          path: "/shopping-purposes",
          name: "side-menu-shopping-purpose",
          component: ShoppingPurposes,
          meta: {
            requiresAuth: true,
            id: 1
          },
        },
        {
          path: "/permission",
          name: "side-menu-permission",
          component: Permission,
          meta: {
            requiresAuth: true,
            id: 1
          },
        }
      ],
    },
    {
      path: "/",
      component: TopMenu,
      children: [
        {
          path: "/",
          name: "top-menu-list-request-management",
          component: ListRequestMe,
          meta: {
            requiresAuth: true,
            id: 1
          },
        },
        {
          path: "/purchase-history",
          name: "top-menu-purchase-history",
          component: PurchaseHistory,
          meta: {
            requiresAuth: true,
            id: 1
          },
        }
      ]
    },
    {
      path: "/login",
      name: "login",
      component: Login,
    },
    {
      path: "/register",
      name: "register",
      component: Register,
    },
    {
      path: "/error-page",
      name: "error-page",
      component: ErrorPage,
    },
    {
      path: "/:pathMatch(.*)*",
      component: ErrorPage,
    },
  ];

  const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(to, from, savedPosition) {
      return savedPosition || {left: 0, top: 0};
    },
  });

  router.beforeEach(async (to, from, next) => {
    let token =  AuthUtils.getTokenFromCookies()
    let user = store.state.auth.user

    const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
    if (token) {
      await store.dispatch('auth/getAuthInfo', token)
      user = store.state.auth.user
    }

    if (!isEmpty(user)) {
      const privileges = user?.privileges
      const permissions = ['buy', 'approve'];

      const hasAllPermissions = permissions.some(permission =>
          privileges.includes(permission.toLowerCase())
      );

      if (!hasAllPermissions && user?.authorities[0] === PermissionNameEnum["1"] && to.path.startsWith('/admin')) {
        next({
          path: '/permission-page'
        });
        return;
      }
    }


    let checkMenu = true

    if (token && (to.path === '/login' || to.path === '/') && !isEmpty(user) && checkMenu) {
      next()
    } else if (!token && requiresAuth) {
      next({
        path: '/login'
      })
    } else if (token && requiresAuth && !checkMenu) {
      next({
        path: '/permission-page'
      })
    } else {
      next()
    }
  })
export default router;
