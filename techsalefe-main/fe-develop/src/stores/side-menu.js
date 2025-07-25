import { defineStore } from "pinia";

export const useSideMenuStore = defineStore("sideMenu", {
  state: () => ({
    menu: [
      // Thống kê
      {
        icon: 'PhoneIcon',
        pageName: 'dashboard',
        title: 'Thống kê',
        id: 0,
        subMenu: [
          {
            icon: "GitPullRequestIcon",
            pageName: "side-menu-dashboard",
            title: "Dashboard"
          },
        ]
      },
      {
        icon: "HomeIcon",
        pageName: "side-menu-dashboard",
        id: 0,
        title: "Quản lí yêu cầu",
        subMenu: [
          {
            icon: "GitPullRequestIcon",
            pageName: "side-menu-list-request-management",
            title: "Danh sách yêu cầu"
          },
          {
            icon: "GitPullRequestIcon",
            pageName: "side-menu-shopping-purpose",
            title: "Mục đích mua sắm"
          },
          {
            icon: "UserIcon",
            pageName: "side-menu-permission",
            title: "Phân quyền"
          }
        ],
      },
    ],
  }),
});
