<template>
  <Modal :show="isRejectModalOpen" @hidden="closeModal">
    <div class="modal-middle">
      <ModalHeader>
        <h2 class="text-lg">
          {{ $t("lang.TITLE.TT5") }}
          <span class="font-medium">{{ ` ${dataReject.requestProductName}` }} </span>
        </h2>
      </ModalHeader>
      <ModalBody>
        <Form
          @submit="onOK"
          :validation-schema="schema"
          v-slot="{ errors }"
          enctype="multipart/form-data"
        >
          <div class="flex justify-start gap-5 form-create">
            <div class="w-full create-layout">
              <label class="form-label font-medium text-sm" for="requestReason"
                >{{ $t("lang.DETAIL.DT7") }}:
                <span class="text-red-600">*</span></label
              >
              <Field
                v-model.trim="dataReject.rejectReason"
                as="textarea"
                class="form-control text-area-scrollable"
                :style="{ maxHeight: +'px' }"
                name="rejectReason"
                rows="4"
                type="text"
              />
              <div class="invalid-feedback">{{ errors.rejectReason }}</div>
            </div>
          </div>
          <div class="mt-4 mx-auto text-center">
            <button
              class="btn w-24 mr-4 mb-2"
              type="button"
              @click="closeModal"
            >
              {{ $t("lang.BUTTON.BT1") }}
            </button>

            <button
              class="btn btn-danger text-white w-24 mr-4 mb-2"
              type="submit"
              :disabled="disableAction"
            >
              {{ $t("lang.BUTTON.BT7") }}
            </button>
          </div>
        </Form>
      </ModalBody>
    </div>
  </Modal>
</template>

<script>
import { Field, Form } from "vee-validate";
import { Modal } from "@/global-components/modal";
import { mapGetters } from "vuex";
import dayjs from "dayjs";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi";
import Swal from "sweetalert2";
import * as Yup from "yup";

export default {
  name: "RejectPurchase",
  components: { Modal, Field, Form },
  data() {
    const schema = Yup.object().shape({
      rejectReason: Yup.string().required(this.$t("lang.ERROR.ER4")),
    });
    return {
      schema,
      disableAction: false
    };
  },

  props: ["isOpen", "onClose", "id"],
  computed: {
    ...mapGetters({
      dataReject: "requestList/getDetails",
      isRejectModalOpen: "requestList/getRejectModalState",
    }),
  },
  methods: {
    async onOK() {
        this.schema = Yup.object().shape({
          rejectReason: Yup.string().required(this.$t("lang.ERROR.ER4")),
        });
      if (!this.dataReject.rejectReason) {
        return
      }
      let params = {
        rejectReason: this.dataReject.rejectReason,
        updatedAt: dayjs(this.dataReject.updatedAt).format(
            "YYYY-MM-DDTHH:mm:ss"
        ),
      };
      try {
        if (this.disableAction) {
          return
        }
        this.disableAction = true
        await ApproveAndRejectApi.buyerRejection(this.dataReject.id, params);
        this.$store.dispatch("requestList/closeRejectModal");
        await Swal.fire({
          title: `<span class="text-modal-toasty">${this.$t(
            "lang.NOTY.NT4"
          )}</b> <span style="font-weight: normal"></span>`,
          timerProgressBar: true,
          timer: 3000,
          icon: "success",
          didOpen: () => {
            const titleElement = document.querySelector(".swal2-title");
            titleElement.style.lineHeight = "1";
            this.$store.dispatch("requestList/closeDetailModal");
          },
        });
      } catch (error) {
        Swal.fire({
          icon: "error",
          title: `<span class="text-modal-toasty">${this.$t(
            "lang.NOTY.NT5"
          )}</span>`,
          confirmButtonColor: "#3085d6",
          confirmButtonText: this.$t("lang.BUTTON.BT7"),
          timerProgressBar: true,
          timer: 1000,
        });
        this.$store.dispatch("requestList/closeDetailModal");
      } finally {
        await this.$store.dispatch("requestList/fetchListRequestAdmin");
        this.$store.dispatch("requestList/setResultDisplay");
        setTimeout(() => {
          this.disableAction = false
        }, 1500)
      }
    },
    closeModal() {
      this.schema = {}
      this.$store.dispatch("requestList/closeRejectModal");
      this.$store.dispatch("requestList/openDetailModal");
    },
  },
};
</script>

<style scoped>
.intro-y.text-sm-gray {
  font-size: 14px;
  color: #888;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-middle {
  margin-top: 50%;
}
</style>
<style>
.text-modal-toasty {
  font-size: 24px;
  font-weight: bold;
}
</style>
