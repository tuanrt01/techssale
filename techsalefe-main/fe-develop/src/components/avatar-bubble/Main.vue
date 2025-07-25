<template>
  <div
      class="inline-flex items-center justify-center flex-shrink-0 w-10 h-10 bg-gray-200 rounded-full cursor-pointer relative">
    <img :class="{'border-transparent': !isShowTooltip,'border-primary': isShowTooltip}"
         :src="adminData?.avatarUrl"
         alt="Rounded avatar"
         class="w-10 h-10 rounded-full border-2 object-cover"
    >
    <Tippy
        :key="adminData?.id"
        tag="div"
        :content="adminData?.name"
        class="w-10 z-10 h-10 flex items-center justify-center absolute rounded-full text-white bg-transparent right-0 top-0"
    />
  </div>
</template>

<script>
export default {
  name: "Main",
  props: ["admin", "isShowTooltip", "toggleTooltip", "onClick", "groupId"],
  computed: {
    adminData() {
      return this.admin;
    }
  }
}
</script>

<style scoped>

</style>