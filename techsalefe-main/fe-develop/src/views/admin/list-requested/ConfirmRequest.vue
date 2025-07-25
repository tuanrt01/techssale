<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isConfirmModalOpen" @hidden="closeModal">
    <div class="modal-middle">
      <div class="stepper">
			  <div class="step" :class="{'step-active' : step === 1, 'step-done': step > 1}"><span class="step-number">1</span></div>
			  <div class="step" :class="{'step-active' : step === 2, 'step-done': step > 2}"><span class="step-number">2</span></div>
		  </div>
      <section v-if="step === 1">
        <ModalHeader>
          <div class="text-xl">{{$t('lang.TITLE.TT3')}} <span class="font-medium">{{ dataDetail.requestProductName }}</span></div>
        </ModalHeader>
        <ModalBody>
          <div>
            <Form @submit="goToStep(2)" :validation-schema="schema" v-slot="{ errors }" enctype="multipart/form-data">
              <div class="w-full create-layout">
                <label for="regular-form-1" class="form-label font-medium text-sm">{{$t('lang.DETAIL.DT25')}} <span class="text-red-600">*</span></label>
                <div class="flex flex-col">
                  <multiselect 
                    v-model="selectedBuyer"
                    :options="listUserBuyer"
                    label="name"
                    track-by="id"
                    :internal-search="false"
                    @search-change="onSearchChange"
                    :show-labels="false"
                    :placeholder="$t('lang.DETAIL.DT25')"
                    class="form-control"
                    :multiple="false"
                  >
                    <template #singleLabel="{ option }">
                      {{ option.name }}
                    </template>
                  </multiselect>
                  <Field 
                    v-show="false"
                    name="buyerUser"
                    v-model="buyerUserAssign"
                  />
                  <div class="invalid-feedback">{{ errors.buyerUser }}</div>
                </div>
              </div>
              <div class="mt-4 mx-auto text-center">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                  {{$t('lang.BUTTON.BT1')}}
                </button>
                <button
                  class="link_text btn btn-success text-white w-24 mr-4 mb-2"
                  type="submit"
                >
                  {{$t('lang.BUTTON.BT7')}}
                </button>
              </div>
            </Form>
          </div>
        </ModalBody>
      </section>

      <section v-if="step === 2">
        <ModalHeader>
          <div class="text-xl">{{$t('lang.DETAIL.DT21')}} <span class="font-medium">{{ dataDetail.requestProductName }}</span></div>
        </ModalHeader>
        <ModalBody>
          <div>
            <Form @submit="confirmRequest" enctype="multipart/form-data">
              <div class="mt-4 mx-auto text-center">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="goToStep(1)">
                  {{$t('lang.BUTTON.BT10')}}
                </button>
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                  {{$t('lang.BUTTON.BT1')}}
                </button>
                <button
                    class="link_text btn btn-success text-white w-24 mr-4 mb-2"
                    type="submit"
                >
                  {{$t('lang.BUTTON.BT7')}}
                </button>
              </div>
            </Form>
          </div>
        </ModalBody>
      </section>
    </div>
  </Modal>
</template>

<script>
import {mapGetters} from "vuex";
import {Field, Form} from 'vee-validate';
import * as Yup from 'yup'
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi";
import dayjs from "dayjs";
import Swal from "sweetalert2";
import Loading from "@/components/loading/Loading.vue";
import Multiselect from 'vue-multiselect';

export default {
  name: "ConfirmRequest",
  components: {Loading, Field, Form, Multiselect},
  data() {
    const schema = Yup.object().shape({
      buyerUser: Yup.string().required(this.$t('lang.ERROR.ER15'))
    })
    return {
      schema,
      buyerUserAssign: null,
      selectedBuyer: null,
      step: 1,
      searchQuery: ''
    }
  },
  computed: {
    ...mapGetters({
      isConfirmModalOpen: "requestList/getConfirmModalState",
      dataDetail: "requestList/getDetails",
      listUserBuyer: 'users/getUserBuyer',
      loadingAction: "general/getLoadingStatus",
      buyerUserId: "requestList/getBuyerUserId"
    })
  },
  methods: {
    goToStep(targetStep) {
      if (!this.buyerUserAssign && targetStep === 2) {
        this.step = 1
        this.schema = Yup.object().shape({buyerUser: Yup.string().required(this.$t('lang.ERROR.ER15'))})
      } else {
        this.step = targetStep;
      }
    },
    closeModal() {
      this.resetForm()
      this.$store.dispatch("requestList/closeConfirmModal")
    },
    resetForm() {
      this.buyerUserAssign = null
      this.selectedBuyer = null
      this.step = 1
    },
    onSearchChange(query) {
      this.searchQuery = query
      this.$store.dispatch("users/searchUserBuyer", { query });
    },
    async confirmRequest() {
      let params = {
        buyerUserId: this.buyerUserAssign,
        updateAt: dayjs(this.dataDetail.updatedAt).format('YYYY-MM-DDTHH:mm:ss')
      }
      this.closeModal()
      this.$store.dispatch("general/activeLoading")
      this.$store.dispatch("requestList/closeDetailModal")

      try {
        const res = await ApproveAndRejectApi.confirmRequestByAssigner(this.dataDetail.id, params)
        if (res.status === 200) {
          Swal.fire({
            icon: 'success',
            title: `<span class="text-modal-toasty">${this.$t('lang.NOTY.NT7')}</span>`,
            confirmButtonColor: '#3085d6',
            confirmButtonText: this.$t('lang.BUTTON.BT7'),
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
  },
  watch: {
    isConfirmModalOpen(newVal) {
      if (newVal) {
        this.buyerUserAssign = this.buyerUserId || null;
        this.selectedBuyer = this.listUserBuyer.find(u => u.id === this.buyerUserAssign) || null;
      } else {
        this.schema = {};
      }
    },
    selectedBuyer(newVal) {
      if (newVal) {
        this.buyerUserAssign = newVal.id;
      } else {
        this.buyerUserAssign = null;
      }
    }
  }
}
</script>

<style scoped>
.stepper {
  display: flex;
  justify-content: space-between;
  width: 80%;
  position: relative;
  margin: 0 auto 1.5em;
  padding-top: 1rem;
}
.stepper::before {
  z-index: 0;
  content: "";
  display: block;
  position: absolute;
  height: 2px;
  top: calc(50% + 5px);
  background: #cecece;
  width: 100%;
}
.step {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
  border: 2px solid #cecece;
  color: #cecece;
  background-color: #fff;
  border-radius: 50%;
  min-width: 25px;
  min-height: 25px;
  line-height: 20px;
  font-size: 16px;
}
.step-active {
  color: #00c4b5;
  background-color: #fff;
  border-color: #00c4b5;
}
.step-done {
  color: #a7e4b5;
  border-color: #a7e4b5;
}
.step-number {
  font-family: "Montserrat", sans-serif;
  font-weight: 800;
  line-height: 1;
  vertical-align: middle;
}

.modal-middle {
  margin-top: 50%;
}
</style>
<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>