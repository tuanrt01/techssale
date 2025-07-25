<template>
  <template>
    <v-container
        fluid
        class="text-center"
    >
      <v-row
          class="flex"
          justify="space-between"
      >
        <v-col cols="12">
          <v-btn @click="show = !show">
            toggle
          </v-btn>
        </v-col>

        <v-col
            cols="12"
            class="mt-12"
        >
          <v-tooltip
              v-model="show"
              location="top"
          >
            <template v-slot:activator="{ props }">
              <v-btn
                  icon
                  v-bind="props"
              >
                <v-icon color="grey-lighten-1">
                  mdi-cart
                </v-icon>
              </v-btn>
            </template>
            <span>Programmatic tooltip</span>
          </v-tooltip>
        </v-col>
      </v-row>
    </v-container>
  </template>
<!--<div class="bg-white">-->
<!--  <ul>-->
<!--    <li>Tạo nhanh</li>-->
<!--    <li>Chỉnh sửa</li>-->
<!--    <li>Xóa</li>-->
<!--  </ul>-->
<!--</div>-->
<!--  <div>-->
<!--    <button @click="toggleBlock">Toggle Block</button>-->
<!--    <div class="block" v-if="showBlock">{{ blockContent }}</div>-->
<!--  </div>-->
</template>

<script>
export default {
  name: "Main",
  data() {
    return {
      showBlock: false,
      blockContent: "Block Content",

          show: false,

    };
  },
  methods: {
    toggleBlock() {
      this.showBlock = !this.showBlock;
      this.positionBlock();
    },
    positionBlock() {
      const button = this.$el.querySelector("button");
      const block = this.$el.querySelector(".block");
      const buttonRect = button.getBoundingClientRect();
      const blockRect = block.getBoundingClientRect();
      const screenWidth = window.innerWidth;

      if (screenWidth - buttonRect.right >= blockRect.width) {
        // Display the block to the right of the button
        block.style.left = `${buttonRect.right}px`;
      } else {
        // Display the block to the left of the button
        block.style.left = `${buttonRect.left - blockRect.width}px`;
      }
    },
  },
}
</script>

<style scoped>
.block {
  /*position: absolute;*/
  /*!*top: 50$;*!*/
  /*display: none;*/
  /*background-color: #ccc;*/
  /*padding: 10px;*/
}

button {
  margin-right: 20px;
}
</style>