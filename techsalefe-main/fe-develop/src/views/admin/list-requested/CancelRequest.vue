<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isCancelModalOpen" @hidden="closeModal">
    <div class="modal-middle">
      <ModalHeader>
        <div class="text-lg">{{$t('lang.DETAIL.DT22')}}<span
          class="font-medium">{{ ` ${dataDetail.requestProductName}` }} </span></div>
      </ModalHeader>
      <ModalBody>
        <div>
          <Form @submit="rejectRequest" :validation-schema="schema" v-slot="{ errors }"
                enctype="multipart/form-data">
            <div class="w-full create-layout">
              <label for="regular-form-1" class="form-label font-medium text-sm">{{$t('lang.DETAIL.DT7')}} <span
                  class="text-red-600">*</span></label>
              <Field
                  as="textarea"
                  name="reasonRejection"
                  class="form-control text-area-scrollable"
                  v-model="reasonRejection"
              />
              <div class="invalid-feedback">{{ errors.reasonRejection }}</div>
            </div>
            <div class="mt-4 mx-auto text-center">
              <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                {{$t('lang.BUTTON.BT1')}}
              </button>
              <button
                  class="btn btn-danger text-white w-24 mr-4 mb-2"
                  type="submit"
              >
                {{$t('lang.BUTTON.BT7')}}
              </button>
            </div>
          </Form>
        </div>
      </ModalBody>
    </div>
  </Modal>
</template>

<script>
import {mapGetters} from "vuex";
import {Field, Form} from 'vee-validate';
import * as Yup from 'yup'
import {ModalHeader} from "@/global-components/modal";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi";
import dayjs from "dayjs";
import Swal from "sweetalert2";
import Loading from "@/components/loading/Loading.vue";

export default {
  name: "CancelRequest",
  components: {Loading, ModalHeader, Field, Form},
  data() {
    const schema = Yup.object().shape({
      reasonRejection: Yup.string().required(this.$t('lang.ERROR.ER4'))
    })
    return {
      schema,
      reasonRejection: ""
    }
  },
  computed: {
    ...mapGetters({
      isCancelModalOpen: "requestList/getCancelModalState",
      dataDetail: "requestList/getDetails",
      loadingAction: "general/getLoadingStatus"
    })
  },
  methods: {
    closeModal() {
      this.resetForm()
      this.$store.dispatch("requestList/closeCancelModal")
    },
    resetForm() {
      this.schema = {}
      this.reasonRejection = ""
    },
    async rejectRequest() {
      this.schema = Yup.object().shape({reasonRejection: Yup.string().required(this.$t('lang.ERROR.ER4'))})
      if (!this.reasonRejection) {
        return
      }
      let params = {
        rejectReason: this.reasonRejection,
        updatedAt: dayjs(this.dataDetail.updatedAt).format('YYYY-MM-DDTHH:mm:ss')
      }
      this.closeModal()
      this.$store.dispatch("requestList/closeDetailModal")
      this.$store.dispatch("general/activeLoading")
      try {
        const res = await ApproveAndRejectApi.rejectRequestByAssigner(this.dataDetail.id, params)
        if (res.status === 200) {
          Swal.fire({
            icon: 'success',
            title: `<span class="text-modal-toasty">${this.$t('lang.NOTY.NT4')}</span>`,
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'OK',
            timerProgressBar: true,
            timer: 1000
          });
        }
      } catch (e) {
        Swal.fire({
          icon: 'error',
          title: `<span class="text-modal-toasty">${this.$t('lang.NOTY.NT5')}</span>`,
          confirmButtonColor: '#3085d6',
          confirmButtonText: this.$t('lang.BUTTON.BT7'),
          timerProgressBar: true,
          timer: 1000
        });
      } finally {
        await this.$store.dispatch("requestList/fetchListRequestAdmin")
        this.$store.dispatch("requestList/setResultDisplay")
        this.$store.dispatch("general/deactivateLoading")
      }
    }
  }
}
</script>
<style scoped>
.modal-middle {
  margin-top: 50%;
}
</style>
