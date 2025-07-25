<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isConfirmModalOpen" @hidden="closeModal">
    <div class="modal-middle">
      <ModalBody>
        <div class="mt-2 text-center">
          <div class="text-xl text-bold">{{$t('lang.TITLE.TT6')}} <br>
            <span class="font-bold">{{ this.data.requestProductName }}</span>
          </div>
          <h2 class="text-sm text-gray-500 mt-2">{{$t('lang.NOTY.NT8')}}</h2>
          <div class="mt-4 text-md">
            <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
              {{$t('lang.BUTTON.BT1')}}
            </button>
            <button class="btn btn-danger text-white w-24 mr-4 mb-2" @click="confirmRequest">
              {{$t('lang.BUTTON.BT7')}}
            </button>
          </div>
        </div>
      </ModalBody>
    </div>
  </Modal>
</template>
<script>
import {mapGetters} from "vuex";
import Swal from "sweetalert2";
import requestAdminApi from "@/api/RequestAdminApi";
import Loading from "@/components/loading/Loading.vue";

export default {
  name: "ConfirmRequest",
  components: {Loading},
  computed: {
    ...mapGetters({
      isConfirmModalOpen: "requestList/getConfirmDelete",
      loadingAction: "general/getLoadingStatus"
    }),
  },
  props: ['data', 'onClose'],
  methods: {
    closeModal() {
      this.onClose()
    },
    async confirmRequest() {
      this.closeModal();
      this.$store.dispatch("general/activeLoading")
      try {
        const res = await requestAdminApi.deleteRequest(this.data.id);
        if (res.status === 204) {
          Swal.fire({
            icon: "success",
            title: `<span class="text-modal-toasty">${this.$t('lang.NOTY.NT9')}</span>`,
            confirmButtonColor: "#3085d6",
            confirmButtonText: this.$t('lang.BUTTON.BT7'),
            timerProgressBar: true,
            timer: 1000,
          });
        }
      } catch {
        Swal.fire({
          icon: "error",
          title: `<span class="text-modal-toasty">${this.$t('lang.NOTY.NT10')}</span>`,
          confirmButtonColor: "#3085d6",
          confirmButtonText: this.$t('lang.BUTTON.BT7'),
          timerProgressBar: true,
          timer: 1000,
        });
      } finally {
        this.$store.dispatch("general/deactivateLoading")
        this.$store.dispatch("requestList/fetchListRequest");
      }
    },
  },
};
</script>

<style scoped>
.modal-middle {
  margin-top: 50%;
}
</style>