import {defineStore} from "pinia";

export const useTopMenuStore = defineStore("topMenu", {
  state: () => ({
    menu: [
      {
        icon: "GitPullRequestIcon",
        pageName: "top-menu-list-request-management",
        title: "lang.TITLE.TT4"
      },
      {
        icon: "PackageIcon",
        pageName: "top-menu-purchase-history",
        title: "lang.TITLE.TT_PURCHASE_HISTORY"
      }
    ],
  }),
});
