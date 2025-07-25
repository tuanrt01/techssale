<template>
  <!-- BEGIN: Top Bar -->
  <div
    class="top-bar-boxed h-[70px] z-[51] relative border-b border-white/[0.08] mt-12 md:-mt-5 -mx-3 sm:-mx-8 px-3 sm:px-8 md:pt-0 mb-12"
  >
    <div class="h-full flex items-center">
      <!-- BEGIN: Logo -->
      <a href="" class="-intro-x hidden md:flex">
        <img
          alt="theLogo"
          class="w-40 "
          src="/theLogo.png"
        />
      </a>
      <!-- END: Logo -->
      <!-- BEGIN: Breadcrumb -->
      <nav aria-label="breadcrumb" class="-intro-x h-full mr-auto breadcrumb breadcrumb-light text-lg">
<!--        Mạch lạc quy trình, định hình đẳng cấp-->
        {{$t('lang.TITLE.TT2')}}
      </nav>
      <!-- END: Breadcrumb -->
      <!-- BEGIN: Notifications -->
      <Dropdown class="multiple-language intro-x w-8 h-8">
        <DropdownToggle class="w-8 h-8 flex items-center overflow-hidden" role="button" tag="div"
                        @click="showLang = !showLang">{{ languageItem[indexLanguage].name }}
        </DropdownToggle>
        <DropdownMenu class="w-40">
          <DropdownContent
              class="bg-primary/80 before:block before:absolute before:bg-black before:inset-0 before:rounded-md before:z-[-1] text-white">
            <template v-for="(itemLang, indexLang) in languageItem">
              <p v-if="indexLang != indexLanguage" :key="indexLang"
                 class="text-center cursor-pointer text-base-1 font-medium uppercase"
                 @click="changeLang(itemLang.value, indexLang)">
                {{ itemLang.name }}</p>
            </template>
          </DropdownContent>
        </DropdownMenu>
      </Dropdown>
      <!-- END: Notifications -->
      <!-- BEGIN: Account Menu -->
      <Dropdown v-if="getTokenFromCookies()" class="intro-x w-8 h-8">
        <DropdownToggle
          tag="div"
          role="button"
          class="w-8 h-8 rounded-full overflow-hidden shadow-lg image-fit zoom-in scale-110"
        >
          <img
            alt="Icewall Tailwind HTML Admin Template"
            :src="getUserInfo.avtUrl"
          />
        </DropdownToggle>
        <DropdownMenu class="w-56">
          <DropdownContent
            class="bg-primary/80 before:block before:absolute before:bg-black before:inset-0 before:rounded-md before:z-[-1] text-white"
          >
            <DropdownHeader tag="div" class="!font-normal">
              <div class="font-medium">
                {{ getUserInfo.name }}
              </div>
              <div class="text-xs text-white/60 mt-0.5 dark:text-slate-500">
                {{ wrapText(getUserInfo.email) }}
              </div>
            </DropdownHeader>
            <DropdownItem  v-if="(isRoute== true && (LoginValue === 'NGUOI_MUA' || LoginValue === 'QUAN_LY'
             || LoginValue === 'NGUOI_DUYEN' || privilegesApprove || privilegesBuy)) " @click="handleRoute" class="dropdown-item hover:bg-white/5">
              <LogOutIcon class="w-4 h-4 mr-2" />   {{ getDropdownLabel }}
            </DropdownItem>
            <DropdownItem @click="handleLogout" class="dropdown-item hover:bg-white/5">
              <ToggleRightIcon class="w-4 h-4 mr-2" /> Đăng xuất</DropdownItem
            >
          </DropdownContent>
        </DropdownMenu>
      </Dropdown>
      <!-- END: Account Menu -->
    </div>
  </div>
  <!-- END: Top Bar -->
</template>

<script>
import {mapActions, mapGetters} from "vuex";
import {getTokenFromCookies} from "@/utils/localStorageUtils";

export default {
  name: 'Main',
  data: () => ({
    isRoute: true,
    name: '',
    showLang: false,
    indexLanguage: 0,
    languageItem: [
      // {
      //   name: "en",
      //   value: 'en'
      // },
      {
        name: "vi",
        value: 'vi'
      },
      {
        name: "ja",
        value: 'ja'
      }
    ]

  }),

  props:{
    LoginValue: String
  },
  computed: {
    ...mapGetters('auth', ['getUserInfo']),
    LoginValue() {
      return this.getUserInfo.authorities[0];
    },
    getDropdownLabel() {
      if (this.$route.fullPath === '/admin' || this.$route.fullPath === '/shopping-purposes' || this.$route.fullPath === '/permission' || this.$route.fullPath === '/dashboard') {
        return 'Trang người dùng';
      }
        if (this.$route.fullPath === '/') {
        return 'Trang quản lý';
      }
    },
    privilegesBuy() {
      return ['buy'].some(ele => this.getUserInfo.privileges.includes(ele.toLowerCase()))
    },
    privilegesApprove() {
      return ['approve'].some(ele => this.getUserInfo.privileges.includes(ele.toLowerCase()))
    }
  },
  created() {
    let language = localStorage.getItem("language") == undefined ? "vi" : localStorage.getItem("language");
    for (const key in this.languageItem) {
      if (this.languageItem[key].value == language) {
        this.indexLanguage = key;
      }
    }},
  methods: {
    getTokenFromCookies,
    changeLang(value, index) {
      this.indexLanguage = index;
      this.showLang = false;
      localStorage.setItem("language", value);
      location.reload();
    },
    ...mapActions('auth', ['logout']),
    handleLogout() {
      const isLogout = this.logout();
      if (isLogout) {
        this.$router.push('/login');
      } else {
        window.alert("Đăng xuất không thành công")
      }
    },
    handleRoute() {
      if (this.$route.fullPath === '/admin' || this.$route.fullPath === '/shopping-purposes' || this.$route.fullPath === '/permission' || this.$route.fullPath === '/dashboard') {
        this.$router.replace('/');
      }
      if ((this.$route.fullPath === '/')){
        this.$router.push('/admin');
      }
    },
    wrapText(text) {
      if (text.length > 32) {
        return text.slice(0, 32) + "\n" + text.slice(32);
      }
      return text;
    },
  },
}
</script>
<style scoped>
.multiple-language {
    font-size: 15px;
    font-weight: 500;
    color: white;
    text-transform: uppercase;
    cursor: pointer;
}
</style>