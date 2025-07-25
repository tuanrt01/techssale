<template>
  <div id="loading-wrapper" v-if="showLoading">
    <div id="loading-text"></div>
    <div id="loading-content"></div>
  </div>
</template>

<script>
export default {
  name: "Loading",
  props: {
    showLoading: {
      default: false,
      type: Boolean
    }
  }
};
</script>

<style scoped>
#loading-wrapper {
  position: fixed;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  background-color: #e5e5e5;
  opacity: 0.2;
  z-index: 10000;
  border-radius: 20px;
}

#loading-text {
  display: block;
  position: absolute;
  top: 50%;
  left: 50%;
  color: #ffffff;
  width: 100px;
  height: 30px;
  margin: -7px 0 0 -45px;
  text-align: center;
  font-family: 'PT Sans Narrow', sans-serif;
  font-size: 20px;
}

#loading-content {
  display: block;
  position: relative;
  left: 50%;
  top: 50%;
  width: 170px;
  height: 170px;
  margin: -85px 0 0 -85px;
  border: 3px solid #C84C0A;
  opacity: 1 !important;
}

#loading-content {
  border: 10px solid transparent;
  border-top-color: #EA580B;
  border-bottom-color: #EA580B;
  border-radius: 50%;
  -webkit-animation: loader 2s linear infinite;
  -moz-animation: loader 2s linear infinite;
  -o-animation: loader 2s linear infinite;
  animation: loader 2s linear infinite;
}

@keyframes loader {
  0% {
    -webkit-transform: rotate(0deg);
    -ms-transform: rotate(0deg);
    transform: rotate(0deg);
  }

  100% {
    -webkit-transform: rotate(360deg);
    -ms-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
</style>