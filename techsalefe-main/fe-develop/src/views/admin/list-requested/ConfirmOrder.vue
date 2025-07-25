<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isConfirmOrderModalOpen" @hidden="closeModal">
    <ModalHeader>
      <h2 class="font-medium text-base mr-auto">{{ $t('lang.TITLE.TT8') }}</h2>
    </ModalHeader>
    <ModalBody class="p-0">
      <div class="p-5">
        <div class="text-center">{{ $t('lang.DETAIL.DT41') }}</div>
      </div>
    </ModalBody>
    <ModalFooter class="text-right">
      <button type="button" class="btn btn-outline-secondary w-24 mr-1" @click="closeModal">
        {{ $t('lang.BUTTON.ORDER_DETAIL') }}
      </button>
      <button type="button" class="btn btn-primary w-24" @click="handleConfirm">
        {{ $t('lang.BUTTON.BT7') }}
      </button>
    </ModalFooter>
  </Modal>
</template>

<script>
import { mapGetters } from "vuex";
import { Modal } from "@/global-components/modal";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi";
import { Form } from "vee-validate";

export default {
  name: "AcceptOrder",
  components: { Modal, Form },
  data() {
    return {
      disableAction: false
    }
  },
  computed: {
    ...mapGetters({
      isConfirmOrderModalOpen: "requestList/getConfirmOrder",
      dataDetail: "requestList/getDetails",
      loadingAction: "general/getLoadingStatus",
      userInfo: "auth/getUserInfo"
    }),
  },
  methods: {
    async handleConfirm() {
      try {
        // Kiểm tra nếu action đã bị disable
        if (this.disableAction) {
          return;
        }

        // Kiểm tra quyền truy cập
        if (!this.userInfo || !this.userInfo.privileges || !this.userInfo.privileges.includes('buy')) {
          throw new Error(this.$t("lang.ERROR.ER1")); // Không có quyền truy cập
        }

        // Disable action và hiển thị loading
        this.disableAction = true;
        this.$store.dispatch("general/activeLoading");

        // Kiểm tra dữ liệu đơn hàng
        if (!this.dataDetail || !this.dataDetail.id) {
          throw new Error("Không tìm thấy thông tin đơn hàng");
        }

        // Kiểm tra trạng thái đơn hàng
        if (this.dataDetail.status !== 'PROCESSING') {
          throw new Error("Trạng thái đơn hàng không hợp lệ");
        }

        try {
          // Lấy thời gian hiện tại
          const updateAt = new Date().toISOString();

          // Gọi API đặt hàng
          const orderResponse = await ApproveAndRejectApi.buyerOrder(
            this.dataDetail.id,
            updateAt
          );

          if (!orderResponse || orderResponse.status !== 200) {
            throw new Error("Lỗi khi đặt hàng");
          }

          // Cập nhật trạng thái
          const updateRequest = {
            id: this.dataDetail.id,
            status: 'ORDERING',
            updateAt: updateAt
          };
          
          const statusResponse = await ApproveAndRejectApi.updateStatus(updateRequest);

          if (!statusResponse || statusResponse.status !== 200) {
            throw new Error("Lỗi khi cập nhật trạng thái");
          }

          // Hiển thị thông báo thành công
          this.$toast.success(this.$t("lang.TOAST.TS3"));

          // Đóng các modal
          this.closeModal();
          this.$store.dispatch("requestList/closeDetailModal");

          // Refresh dữ liệu
          await this.$store.dispatch("requestList/fetchListRequestAdmin");
          this.$store.dispatch("requestList/setResultDisplay");

        } catch (apiError) {
          console.error('API Error:', apiError);
          if (apiError.status === 403) {
            throw new Error(this.$t("lang.ERROR.ER1")); // Không có quyền truy cập
          }
          throw new Error(apiError.data?.message || this.$t("lang.TOAST.TS2"));
        }

      } catch (error) {
        console.error('Error in handleConfirm:', error);
        this.$toast.error(error.message || this.$t("lang.TOAST.TS2"));
      } finally {
        // Reset trạng thái
        this.disableAction = false;
        this.$store.dispatch("general/deactivateLoading");
      }
    },

    closeModal() {
      this.disableAction = false;
      this.$store.dispatch("requestList/closeConfirmOrderModal");
    },
  },
};
</script>

<style scoped>
.modal-middle {
  margin-top: 50%;
}
</style>
