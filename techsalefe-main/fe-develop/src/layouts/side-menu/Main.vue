<template>
  <div class="py-2 -mt-2">
    <MobileMenu />
    <TopBar />
    <div class="wrapper">
      <div class="wrapper-box">
        <!-- BEGIN: Side Menu -->
<!--        <nav class="side-nav" :style="{width: widthEnable + 'px'}" @click="changeVisible" :class="{'hidden': visibleIconSidebar}">-->
<!--          <div class="icon_menu__wrapper">-->
<!--            <HomeIcon class="side-menu__icon icon-display text-white" />-->
<!--          </div></nav>-->
        <nav class="side-nav" :style="{width: widthEnable + 'px'}">
          <ul>
            <!-- BEGIN: First Child -->
            <template v-for="(menu, menuKey) in formattedMenu">
              <li
                v-if="menu == 'devider'"
                :key="menu + menuKey"
                class="side-nav__devider my-6"
              ></li>
              <li v-else :key="menu + menuKey">
                <SideMenuTooltip
                  tag="a"
                  :content="menu.title"
                  :href="
                    menu.subMenu
                      ? 'javascript:;'
                      : router.resolve({ name: menu.pageName }).path
                  "
                  class="side-menu"
                  :class="{
                    'side-menu--active': menu.active,
                    'side-menu--open': !menu.activeDropdown,
                  }"
                  @click="linkTo(menu, router, $event)"
                >
                  <div class="side-menu__icon" @click="changeVisible">
                    <component :is="menu.icon"/>
                  </div>
                  <div class="side-menu__title" v-if="widthEnable === 280">
                    {{ menu.title }}
                    <div
                      v-if="menu.subMenu"
                      class="side-menu__sub-icon"
                      :class="{ 'transform rotate-180': menu.activeDropdown }"
                    >
                      <ChevronUpIcon />
                    </div>
                  </div>
                </SideMenuTooltip>
                <!-- BEGIN: Second Child -->
                <transition @enter="enter" @leave="leave" class="pl-4" >
                  <ul v-if="menu.subMenu && widthEnable === 280">
                    <li
                      v-for="(subMenu, subMenuKey) in menu.subMenu"
                      :key="subMenuKey"
                    >
                      <SideMenuTooltip
                        tag="a"
                        :content="subMenu.title"
                        :href="
                          subMenu.subMenu
                            ? 'javascript:;'
                            : router.resolve({ name: subMenu.pageName }).path
                        "
                        class="side-menu"
                        :class="{ 'side-menu--active': subMenu.active }"
                        @click="linkTo(subMenu, router, $event)"
                      >
                        <div class="side-menu__icon">
                          <component :is="subMenu.icon" class="h-5"/>
                        </div>
                        <div class="side-menu__title" v-if="!visibleSidebar">
                          {{ subMenu.title }}
                          <div
                            v-if="subMenu.subMenu"
                            class="side-menu__sub-icon"
                            :class="{
                              'transform rotate-180': subMenu.activeDropdown,
                            }"
                          >
                            <ChevronUpIcon />
                          </div>
                        </div>
                      </SideMenuTooltip>
                      <!-- BEGIN: Third Child -->
                      <transition @enter="enter" @leave="leave">
                        <ul v-if="subMenu.subMenu && subMenu.activeDropdown">
                          <li
                            v-for="(
                              lastSubMenu, lastSubMenuKey
                            ) in subMenu.subMenu"
                            :key="lastSubMenuKey"
                          >
                            <SideMenuTooltip
                              tag="a"
                              :content="lastSubMenu.title"
                              :href="
                                lastSubMenu.subMenu
                                  ? 'javascript:;'
                                  : router.resolve({
                                      name: lastSubMenu.pageName,
                                    }).path
                              "
                              class="side-menu"
                              :class="{
                                'side-menu--active': lastSubMenu.active,
                              }"
                              @click="linkTo(lastSubMenu, router, $event)"
                            >
                              <div class="side-menu__icon">
                                <ZapIcon />
                              </div>
                              <div class="side-menu__title"  v-if="widthEnable === 280">
                                {{ lastSubMenu.title }}
                              </div>
                            </SideMenuTooltip>
                          </li>
                        </ul>
                      </transition>
                      <!-- END: Third Child -->
                    </li>
                  </ul>
                </transition>
                <!-- END: Second Child -->
              </li>
            </template>
            <!-- END: First Child -->
          </ul>
        </nav>
        <!-- END: Side Menu -->
        <!-- BEGIN: Content -->
        <div class="content">
          <router-view />
        </div>
        <!-- END: Content -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, provide, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { helper as $h } from "@/utils/helper";
import { useSideMenuStore } from "@/stores/side-menu";
import TopBar from "@/components/top-bar/Main.vue";
import MobileMenu from "@/components/mobile-menu/Main.vue";
import SideMenuTooltip from "@/components/side-menu-tooltip/Main.vue";
import { linkTo, nestedMenu, enter, leave } from "./index";
import dom from "@left4code/tw-starter/dist/js/dom";

const route = useRoute();
const router = useRouter();
const formattedMenu = ref([]);
const sideMenuStore = useSideMenuStore();
const sideMenu = computed(() => nestedMenu(sideMenuStore.menu, route));

provide("forceActiveMenu", (pageName) => {
  route.forceActiveMenu = pageName;
  formattedMenu.value = $h.toRaw(sideMenu.value);
});

watch(
  computed(() => route.path),
  () => {
    delete route.forceActiveMenu;
    formattedMenu.value = $h.toRaw(sideMenu.value);
  }
);

onMounted(() => {
  dom("body").removeClass("error-page").removeClass("login").addClass("main");
  formattedMenu.value = $h.toRaw(sideMenu.value);
});

</script>
<script>
export default {
  data() {
    return {
      widthEnable: 100,
      visibleSidebar: true,
      visibleIconSidebar: false
    }
  },
  methods: {
    changeVisible() {
      this.visibleSidebar = !this.visibleSidebar
      if(!this.visibleSidebar) {
        const targetValue = 280;
        const duration = 200;
        const interval = 10;
        const increment = (targetValue - this.widthEnable) * (interval / duration);
        const timer = setInterval(() => {
          this.widthEnable += increment;
          if (this.widthEnable >= targetValue) {
            clearInterval(timer);
          }
        }, interval);
      } else {
        const targetValue = 100;
        const duration = 200;
        const interval = 10;
        const increment = (this.widthEnable - targetValue) * (interval / duration);
        const timer = setInterval(() => {
          this.widthEnable -= increment;
          if (this.widthEnable <= targetValue) {
            clearInterval(timer);
          }
        }, interval);
      }
    }
  }
}
</script>
<style scoped>
.icon-display {
  margin: 12px 15px 10px 20px;
}
</style>
