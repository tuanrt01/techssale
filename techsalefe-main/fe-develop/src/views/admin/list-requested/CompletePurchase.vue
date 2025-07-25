<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isCompleteModalOpen" size="modal-lg" @hidden="closeModal">
    <div class="modal-middle">
      <ModalHeader class="pb-4 pt-1">
      <h2 class="intro-y text-xl font-medium mt-4">{{$t('lang.DETAIL.DT23')}}</h2>
    </ModalHeader>
    <ModalBody>
      <Form enctype="multipart/form-data">
        <div class="flex flex-row justify-center gap-5 pl-5 pr-5 form-create">
          <div class="w-1/2 create-layout">
            <label class="form-label text-sm font-medium">
              {{ $t("lang.DETAIL.DT10") }}
              <span class="text-red-600">*</span>
            </label>
            <Field
              v-model="dataComplete.requestProductName"
              class="form-control pr-10"
              name="productName"
              type="text"
              @input="validateProductName"
            />
            <div class="text-red-500 text-sm mt-1">
              {{ errors.requestProductName }}
            </div>
          </div>
          <div class="w-1/2 create-layout">
            <label class="form-label text-sm font-medium">
              {{ $t("lang.DETAIL.DT27") }}
              <span class="text-red-600">*</span>
            </label>
            <Field
              v-model="formattedBroughtPrice"
              class="form-control pr-10"
              min="0"
              name="price"
              type="text"
              maxlength="14"
              @input="
                dataComplete.broughtPrice = parseInputPrice($event.target.value)
              "
              @keypress="validateNumericInput"
            />
            <div class="text-red-500 text-sm mt-1">
              {{ errors.broughtPrice }}
            </div>
          </div>
        </div>

        <div
          class="flex flex-row justify-center gap-5 pl-5 pr-5 form-create mt-5"
        >
          <div class="w-1/2 create-layout">
            <label class="form-label text-sm font-medium">
              {{ $t("lang.DETAIL.DT24") }}
              <span class="text-red-600">*</span>
            </label>
            <Field
              v-model="dataComplete.boughtPlace"
              class="form-control pr-10"
              name="placeBuy"
              type="text"
              @input="validatePlaceBuy"
            />
            <div class="text-red-500 text-sm mt-1">
              {{ errors.boughtPlace }}
            </div>
          </div>
          <div class="w-1/2 create-layout">
            <label class="form-label text-sm font-medium">
              {{ $t("lang.DETAIL.DT11") }}
               <span class="text-red-600">*</span>
            </label>
            <Field
              v-model="dataComplete.amount"
              class="form-control"
              min="0"
              name="quantity"
              type="number"
              @input="validateAmount"
              disabled
            />
            <div class="text-red-500 text-sm mt-1">{{ errors.amount }}</div>
          </div>
        </div>
        <div class="px-5 mt-2 relative">
          <label for="regular-form-1" class="form-label text-sm font-medium">{{
            $t("lang.DETAIL.DT32")
          }}</label>
          <button
            v-if="dataComplete.selectedFile"
            type="button"
            @click="deleteImage"
            class="absolute top-2 right-2 bg-primary text-white rounded-2xl w-6 h-6 flex justify-center items-center"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="w-3 h-3"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
          <label
            class="flex flex-col items-center justify-center w-64 border-4 border-dashed border-gray-400 cursor-pointer"
            for="fileInput"
            style="min-height: 150px; max-height: max-content"
          >
            <input
              type="file"
              class="hidden"
              id="fileInput"
              ref="fileInput"
              @change="handleFileChange"
            />
            <div v-if="dataComplete.selectedFile" class="w-full">
              <img
                :src="
                  dataComplete.selectedFile
                    ? createObjectURL(dataComplete.selectedFile)
                    : ''
                "
                alt="Selected Image"
                class="w-64 h-64 object-cover"
              />
            </div>
            <div v-if="!dataComplete.selectedFile">
              <div class="px-6">
                <p>{{ $t("lang.DETAIL.DT30") }}</p>
                <p>(JPEG, PNG, JPG. MAX. 1MB)</p>
              </div>
            </div>
          </label>

          <div class="text-red-500 text-sm mt-1">
            {{ errors.fileValidationError }}
          </div>
        </div>
        <div class="mt-4 mx-auto text-center">
          <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
            {{ $t("lang.BUTTON.BT1") }}
          </button>

          <button
            class="btn btn-success text-white w-24 mr-4 mb-2"
            type="button"
            :disabled="loadingAction"
            @click="onOK"
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
import { mapGetters } from "vuex";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi";
import Swal from "sweetalert2";
import dayjs from "dayjs";
import Loading from "@/components/loading/Loading.vue";

export default {
  name: "CompletePurchase",
  components: {Loading, Field, Form },
  data() {
    return {
      errors: {},
      dataApprove: {},
      selectedFile: null,
      fileValidationError: null,
      loadingAction: false
    };
  },
  props: ["isOpen", "onClose", "id"],
  computed: {
    ...mapGetters({
      dataComplete: "requestList/getDetails",
      isCompleteModalOpen: "requestList/getCompleteModalState",
      loadingAction: "general/getLoadingStatus"
    }),
    formattedBroughtPrice() {
      const formatter = new Intl.NumberFormat("it-IT", {
        style: "currency",
        currency: "VND",
      });

      const priceValue = this.dataComplete.broughtPrice || 0;

      if (!/^\d+$/.test(priceValue)) {
        this.errors.boughtPrice = "{{$t('lang.ERROR.ER12')}}";
      } else if (parseFloat(priceValue) <= 0) {
        this.errors.boughtPrice = "{{$t('lang.ERROR.ER13')}}";
      } else {
        this.errors.boughtPrice = "";
      }

      return formatter.format(priceValue).replace(/\D00(?=\D*$)/, "");
    },
  },
  methods: {
    deleteImage() {
      if (this.dataComplete.selectedFile) {
        this.dataComplete.selectedFile = "";
      }
    },
    createObjectURL(file) {
      return URL.createObjectURL(file);
    },
    handleFileChange(event) {
      const maxSize = 1000000;
      const allowedFileTypes = ["image/jpeg", "image/png", "image/jpg"];
      const selectedFileInput = this.$refs.fileInput.files[0];

      if (selectedFileInput) {
        if (!allowedFileTypes.includes(selectedFileInput.type)) {
          this.errors.fileValidationError =
            "Tệp tin không hợp lệ. Vui lòng nhập kiểu jpeg, png, jpg.";
          this.$refs.fileInput.value = null;

          this.dataComplete.selectedFile = false;
        } else if (selectedFileInput.size > maxSize) {
          this.errors.fileValidationError = "Vui lòng chọn tệp không quá 1MB.";
          this.$refs.fileInput.value = null;

          this.dataComplete.selectedFile = false;
        } else {
          this.errors.fileValidationError = null;

          this.dataComplete.selectedFile = selectedFileInput;
        }
      }
    },
    validateProductName() {
      this.errors.requestProductName = "";
    },
    validatePlaceBuy() {
      this.errors.boughtPlace = "";
    },
    validateAmount() {
      this.errors.amount = "";
    },
    parseInputPrice(value) {
      const numericValue = parseFloat(value.replace(/[^\d]/g, ""));
      return isNaN(numericValue) ? 0 : numericValue;
    },
    validateNumericInput(event) {
      const charCode = event.which ? event.which : event.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57)) {
        event.preventDefault();
      }
    },
    resetForm() {
      this.dataComplete = {};
      this.errors = {};
    },
    async onOK() {
      this.errors = {};

      const data = this.dataComplete;

      const validateField = (field, errorMessageKey, condition) => {
        if (!condition) {
          this.errors[field] = this.$t(`lang.ERROR.${errorMessageKey}`);
          return false; 
        }
        return true;
      };

      const isProductNameValid = validateField(
        "requestProductName",
        "ER1",
        !!data.requestProductName
      );
      const isBroughtPriceValid =
        validateField("broughtPrice", "ER13", !!data.broughtPrice) &&
        validateField("broughtPrice", "ER16", data.broughtPrice > 1) 
      const isBoughtPlaceValid = validateField(
        "boughtPlace",
        "ER14",
        !!data.boughtPlace
      );
      const isAmountValid = validateField("amount", "ER2", !!data.amount);

     
      if (
        !isProductNameValid ||
        !isBroughtPriceValid ||
        !isBoughtPlaceValid ||
        !isAmountValid
      ) {
        return; 
      }

      if (data.amount > 9999)   {
        this.errors.amount = this.$t("lang.ERROR.ER8");
        return;
      }
      this.$store.dispatch("general/activateLoading")
      this.$store.dispatch("requestList/closeCompleteModal");
      this.$store.dispatch("requestList/closeDetailModal");

      try {
        if (this.loadingAction) {
          return
        }
        this.loadingAction = true
        if (Object.keys(this.errors).length === 0) {
          let params = {
            boughtAmount: this.dataComplete.amount,
            boughtPlace: this.dataComplete.boughtPlace,
            boughtPrice: this.dataComplete.broughtPrice,
            boughtProductName: this.dataComplete.requestProductName,
            updatedAt: dayjs(this.dataComplete.updatedAt).format(
              "YYYY-MM-DDTHH:mm:ss"
            ),
            buyerAttachment: this.dataComplete.selectedFile,
          };
          const formData = this.$h.convertJsonToFormData(params);
          await ApproveAndRejectApi.buyerCompletion(
            this.dataComplete.id,
            formData
          );
          await Swal.fire({
            title: `<span class="text-modal-toasty">${this.$t(
              "lang.NOTY.NT6"
            )}</span>`,
            timerProgressBar: true,
            timer: 3000,
            icon: "success",
            didOpen: () => {
              const titleElement = document.querySelector(".swal2-title");
              titleElement.style.lineHeight = "1";
            },
          });
          await this.$store.dispatch("requestList/fetchListRequestAdmin");
          this.$store.dispatch("requestList/setResultDisplay");
        }
      } catch (error) {
        Swal.fire({
          icon: "error",
          title: `<span class="text-modal-toasty">${this.$t(
            "lang.NOTY.NT5"
          )}</span>`,
          confirmButtonColor: "#3085d6",
          confirmButtonText: this.$t("lang.BUTTON.BT7"),
          timerProgressBar: true,
          timer: 2000,
        });
      } finally {
        this.$store.dispatch("general/deactivateLoading")
        setTimeout(() => {
          this.loadingAction = false
        }, 1500)
      }
    },
    closeModal() {
      this.resetForm();
      this.$store.dispatch("requestList/closeCompleteModal");
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
/* delete button */

.relative {
  position: relative;
}

.absolute {
  position: absolute;
}

.top-2 {
  top: 2.2rem;
}

.right-2 {
  right: 18rem;
}

.modal-middle {
  margin-top: 20%;
}
</style>
