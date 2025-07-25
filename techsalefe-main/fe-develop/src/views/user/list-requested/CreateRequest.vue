<template>
  <Loading :show-loading="loadingAction" />
  <Modal :show="isCreateOpen" size="modal-lg" @hidden="closeModals">
    <ModalHeader class="pb-4 pt-1">
      <h2 class="text-xl font-medium mt-2">
        {{ $t("lang.DETAIL.DT15") }}
      </h2>
      <button
        class="mx-auto flex items-center mr-2 pt-4 pr-3 justify-center h-10 w-10"
        @click="closeModal"
      >
        <svg
          aria-hidden="true"
          class="h-7 w-7 text-gray-600 hover:text-red-600"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M6 18L18 6M6 6l12 12"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
          />
        </svg>
      </button>
    </ModalHeader>
    <div class="overflow-y-auto overflow-x-hidden default-class scroll-view">
      <ModalBody>
        <Form enctype="multipart/form-data">
          <div class="mx-5">
            <label class="form-label font-medium text-sm mb-1"
              >{{ $t("lang.DETAIL.DT33") }}
              <span class="text-red-600">*</span></label
            >
            <div>
              <Field
                type="text"
                name="productName"
                class="form-control pr-10"
                v-model.trim="dataCreate.requestProductName"
                @input="validateProductName"
              />
              <div class="text-red-500 text-sm mt-1">
                {{ errors.requestProductName }}
              </div>
            </div>
          </div>

          <div class="mx-5">
            <label class="form-label font-medium text-sm mb-1">
              {{ $t("lang.DETAIL.DT45") }} <span class="text-red-600">*</span>
            </label>
            <div>
              <Field
                type="text"
                name="productLink"
                class="form-control pr-10"
                v-model="dataCreate.productLink"
                @input="validateProductLink"
              />
              <div class="text-red-500 text-sm mt-1">
                {{ errors.productLink }}
              </div>
            </div>
          </div>

          <div  class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
            <div class="w-1/2 create-layout">
            <label class="form-label font-medium text-sm mb-1"
              >{{ $t("lang.DETAIL.DT6") }}
              <span class="text-red-600">*</span></label
            >
            <Field
              v-model.trim="dataCreate.approverUserId"
              id="tabulator-html-filter-field"
              class="form-select input__width"
              @input="validateApproverName"
              name="approverUserId"
              as="select"
            >
              <option
                v-for="(userItem, index) in user"
                :key="index"
                :value="userItem.id"
              >
                {{ userItem.name }}
              </option>
            </Field>
            <div class="text-red-500 text-sm mt-1">
              {{ errors.approverUserId }}
            </div>
            </div>
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">
                {{ $t("lang.DETAIL.DT4") }}
                <span class="text-red-600">*</span></label
              >
              <VueDatePicker
                  v-model.trim="dataCreate.expectReceiveDate"
                  class="form-control"
                  auto-apply
                  format="dd-MM-yyyy"
                  @update:model-value="validateExpectReceiveDate"
              />
              <div class="text-red-500 text-sm mt-1">
                {{ errors.expectReceiveDate }}
              </div>
            </div>
          </div>

          <div
            class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create"
          >
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1"
                >{{ $t("lang.DETAIL.DT11") }}
                <span class="text-red-600">*</span></label
              >
              <div>
                <Field
                  type="number"
                  min="1"
                  class="form-control"
                  v-model.trim="dataCreate.requestAmount"
                  @input="validateQuantity"
                  name="requestAmount"
                />
                <div class="text-red-500 text-sm mt-1">
                  {{ errors.requestAmount }}
                </div>
              </div>
            </div>
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">
                {{ $t("lang.DETAIL.DT46") }}<span class="text-red-600">*</span>
                (<input type="checkbox" :checked="!showPersonnelField" @change="togglePersonnelField"> {{ $t("lang.DETAIL.DT47") }})
              </label>
             <Field
             v-if="showPersonnelField"
             v-model.trim="dataCreate.personnelUsed"
             id="tabulator-html-filter-field"
             class="form-select input__width"
             @input="validateApproverName"
             name="personnelUsed"
             as="select"
           >
             <option
               v-for="(userItem, index) in allUser"
               :key="index"
               :value="userItem.id"
             >
               {{ userItem.name }}
             </option>
           </Field>

              <div class="relative w-full" v-if="!showPersonnelField">
                <input
                  type="text"
                  name="organization"
                  :value="organization"
                  :class="{ 'is-invalid': errors.organization }"
                  class="w-full px-3 border rounded cursor-pointer"
                  style="height: 38px;"
                  readonly
                  @click="openOrganizationModalWithData"
                  :placeholder="$t('lang.DETAIL.DT49')"
                />
              </div>

              <div class="text-red-500 text-sm mt-1">
                {{ errors.personnelUsed }}
              </div>
            </div>
          </div>
          <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
            <div class="w-full create-layout">
              <label class="form-label font-medium text-sm mb-1"
                >{{ $t("lang.DETAIL.DT42") }} <span class="text-red-600">*</span>
              </label>
              <div class="flex gap-3 reason-container">
                <div class="w-1/2">
                  <Field
                    as="select"
                    name="requestReason"
                    class="form-control w-full"
                    @input="handleParentReasonChange"
                    v-model.trim="dataCreate.requestReason"
                  >
                    <option
                      v-for="item in parentReasons"
                      :key="item.id"
                      :value="item.id"
                    >
                      {{ item.reason }}
                    </option>
                  </Field>
                </div>
                
                <div class="w-1/2">
                  <Field
                    as="select"
                    name="requestReasonChild"
                    class="form-control w-full"
                    @input="handleChildReasonChange"
                    v-model.trim="dataCreate.requestReasonChild"
                    :disabled="isChildReasonDisabled"
                  >
                    <option value="" selected>{{ isChildReasonDisabled ? $t("lang.DETAIL.DT44") : $t("lang.DETAIL.DT43") }}</option>
                    <option
                      v-for="item in childReasons"
                      :key="item.id"
                      :value="item.id"
                    >
                      {{ item.reason }}
                    </option>
                  </Field>
                </div>
              </div>
              <div class="text-red-500 text-sm mt-1">
                {{ errors.requestReason }}
              </div>
            </div>
             
          </div>


          <div
            class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create"
          >
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT12")
              }}</label>
              <input
                type="text"
                name="estimate-price"
                class="form-control"
                v-model.trim="formattedEstimatePrice"
                maxlength="14"
                @input="
                  dataCreate.estimatePrice = parseInputPrice(
                    $event.target.value
                  )
                "
              />
              <div class="text-red-500 text-sm mt-1">
                {{ errors.estimatePrice }}
              </div>
            </div>
            <div class="w-1/2 create-layout">
            </div>
          </div>
          <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT13")
              }}</label>
              <Field
                type="text"
                name="reason"
                class="form-control pr-10 text-area-scrollable"
                as="textarea"
                rows="4"
                v-model.trim="dataCreate.requestReasonDetail"
                @input="validateDetailReason"
              />
              <div class="text-red-500 text-sm mt-1">
                {{ errors.requestReasonDetail }}
              </div>
            </div>
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT14")
              }}</label>
              <Field
                type="text"
                name="description"
                class="form-control pr-10 text-area-scrollable"
                as="textarea"
                rows="4"
                v-model.trim="dataCreate.description"
              />
            </div>
          </div>
          <div class="px-5 relative">
            <label
              for="regular-form-1"
              class="form-label font-medium text-sm mb-1"
              >{{ $t("lang.DETAIL.DT32") }}</label
            >

            <button
              v-if="dataCreate.selectedFile"
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
              <div v-if="dataCreate.selectedFile || imageForDup" class="w-full">
                <img
                  :src="
                    dataCreate.selectedFile
                      ? createObjectURLFunc(dataCreate.selectedFile)
                      : imageForDup
                      ? imageForDup
                      : ''
                  "
                  alt="Selected Image"
                  class="w-64 h-64 object-cover"
                />
              </div>

              <div v-if="!dataCreate.selectedFile && !imageForDup">
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
        </Form>
      </ModalBody>
    </div>
    <ModalFooter>
      <div class="mt-4 mx-auto text-center flex justify-center">
        <LoadingIcon  v-if="loadingAction" class="w-10 h-10" icon="three-dots"/>
        <button v-else
            class="btn btn-primary w-24 mr-4 mb-2"
            type="button"
            @click="createRequest"
        >
          {{ $t("lang.BUTTON.BT3") }}
        </button>
      </div>
    </ModalFooter>
  </Modal>
  <ModalOrganization
    :is-open="isOrganizationModalOpen"
    :selected-rows="selectedOrganizations"
    :organization-id="dataCreate.organizationId"
    @update:is-open="isOrganizationModalOpen = $event"
    @save-selection="handleOrganizationSelection"
  />
</template>

<script>
import VueDatePicker from "@vuepic/vue-datepicker";
import { Field, Form } from "vee-validate";
import { createRequest } from "@/api/RequestApi";
import UserAssign from "@/api/UserAssign";
import Swal from "sweetalert2";
import { mapGetters } from "vuex";
import dayjs from "dayjs";
import { getAttachment } from "@/api/Attachments";
import Loading from "@/components/loading/Loading.vue";
import ReasonApi from "@/api/ReasonApi.js"
import Multiselect from "vue-multiselect";
import ModalOrganization from "@/common/ModalOrganization.vue";

export default {
  name: "CreateRequest",
  components: {Multiselect, Loading, Field, Form, VueDatePicker, ModalOrganization },
  data() {
    return {
      errors: {},
      dataCreate: {
        requestAmount: 1,
        isDeleteImage: false,
        requestReason: "",
        requestReasonChild: "",
        productLink: "",
        personnelUsed: "",
        organizationId: ""
      },
      user: {},
      allUser: {},
      fieldFocused: false,
      selectedFile: null,
      fileValidationError: null,
      imageForDup: null,
      RequestReason: [],
      parentReasons: [],
      childReasons: [],
      selectedReason: null,
      showPersonnelField: true,
      showPersonnelButton: false,
      isOrganizationModalOpen: false,
      selectedOrganizations: [],
      selectedMainOrganization: null,
      organization: ""
    };
  },
  created() {
    this.userAssign();
  },
  methods: {
    deleteImage() {
      if (this.dataCreate.selectedFile || this.imageForDup) {
        this.dataCreate.selectedFile = null;
        this.imageForDup = null;
      }
    },
    createObjectURLFunc(file) {
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
          this.$refs.fileInput.value = "";
          this.dataCreate.selectedFile = false;
        } else if (selectedFileInput.size > maxSize) {
          this.errors.fileValidationError = "Vui lòng chọn tệp không quá 1MB.";
          this.$refs.fileInput.value = "";
          this.dataCreate.selectedFile = false;
        } else {
          this.errors.fileValidationError = null;
          this.dataCreate.selectedFile = selectedFileInput;
        }
      }
    },
    parseInputPrice(value) {
      const numericValue = parseFloat(value.replace(/[^\d]/g, ""));
      return isNaN(numericValue) ? 0 : numericValue;
    },
    validateProductName() {
      this.errors.requestProductName = "";
    },
    validateApproverName() {
      this.errors.approverUserId = "";
    },
    validateQuantity() {
      this.errors.requestAmount = "";
      if (
        this.dataCreate.requestAmount <= 0 &&
        !this.dataCreate.requestReasonDetail
      ) {
        this.errors.requestAmount = this.$t("lang.ERROR.ER10");
      } else {
        this.errors.requestAmount = "";
      }
    },
    validateReason() {
      this.errors.requestReason = "";
    },
    validateExpectReceiveDate() {
      this.errors.expectReceiveDate = "";
    },
    validateDetailReason() {
      this.errors.requestDetailReason = "";

      if ( !this.dataCreate.requestReasonDetail.trim()) {
        this.errors.requestReasonDetail = this.$t("lang.ERROR.ER11");
      } else {
        this.errors.requestReasonDetail = "";
      }
    },
    validateProductLink() {
      this.errors.productLink = "";
    },
    resetForm() {
      this.dataCreate = {};
      this.errors = {};
      this.fieldFocused = false;
      this.imageForDup = null;
      this.$refs.fileInput.value = "";
    },
    closeModals() {
      this.errors = {};
      this.imageForDup = null;
      this.dataCreate.isDeleteImage = true;
      this.$store.dispatch("requestList/closeCreateModal");
    },
    closeModal() {
      this.errors = {};
      this.resetForm();
      this.$store.dispatch("requestList/closeCreateModal");
    },
    async createRequest() {
      this.errors = {};
      if (!this.dataCreate.approverUserId) {
        this.errors.approverUserId = this.$t("lang.ERROR.ER3");
      }
      if (!this.selectedReason) {
        this.errors.requestReason = this.$t("lang.ERROR.ER4");
      }
      if (!this.dataCreate.requestProductName) {
        this.errors.requestProductName = this.$t("lang.ERROR.ER1");
      }
      if (!this.dataCreate.expectReceiveDate) {
        this.errors.expectReceiveDate = this.$t("lang.ERROR.ER5");
      } else if (this.dataCreate.expectReceiveDate < new Date()) {
        this.errors.expectReceiveDate = this.$t("lang.ERROR.ER6");
      }
      if (!this.dataCreate.requestAmount) {
        this.errors.requestAmount = this.$t("lang.ERROR.ER7");
      }

      if (this.dataCreate.requestAmount > 9999) {
        this.errors.requestAmount = this.$t("lang.ERROR.ER8");
      }

      if (
        this.dataCreate.requestReason === "OTHER" &&
        !this.dataCreate.requestReasonDetail
      ) {
        this.errors.requestReasonDetail = this.$t("lang.ERROR.ER9");
      }

      if (!this.dataCreate.productLink) {
        this.errors.productLink = "";
      }
      if (this.showPersonnelField && !this.dataCreate.personnelUsed) {
        this.errors.personnelUsed = this.$t("lang.ERROR.ER20");
      }
      if (!this.showPersonnelField && !this.dataCreate.organizationId) {
        this.errors.personnelUsed = this.$t("lang.ERROR.ER21");
      }

      const link = this.dataCreate.productLink;
      if (!link) {
        this.errors.productLink = this.$t("lang.ERROR.ER18");
      } else {
        const urlPattern = /^(https?:\/\/)?([\w\-]+\.)+[\w\-]{2,}(\/\S*)?$/;
        if (!urlPattern.test(link)) {
          this.errors.productLink = this.$t("lang.ERROR.ER19");
        }
      }      
      if (Object.keys(this.errors).length === 0) {
        let params = {
          approverUserId: this.dataCreate.approverUserId,
          requestReason: this.selectedReason,
          requestReasonDetail: this.dataCreate.requestReasonDetail,
          requestProductName: this.dataCreate.requestProductName,
          productLink: this.dataCreate.productLink,
          expectReceiveDate: dayjs(this.dataCreate.expectReceiveDate).format(
            "YYYY-MM-DDTHH:mm:ss"
          ),
          description: this.dataCreate.description,
          requestAmount: this.dataCreate.requestAmount,
          estimatePrice: this.dataCreate.estimatePrice,
          requestUserAttachment: this.dataCreate.selectedFile
            ? this.dataCreate.selectedFile
            : this.imageForDup,
        };

        if (this.showPersonnelField) {
          params.personnelUsed = this.dataCreate.personnelUsed;
        } else {
          params.organizationUsed = this.dataCreate.organizationId;
        }

        const formData = this.$h.convertJsonToFormData(params);
        this.$store.dispatch("requestList/closeMyDetailModal");
        this.closeModal();

        try {
          this.$store.dispatch("general/activeLoading")
          const res = await createRequest(formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });
          if (res.status === 201) {
            Swal.fire({
              icon: "success",
              title: `<span class="text-modal-toasty">${this.$t(
                "lang.NOTY.NT1"
              )}</span>`,
              confirmButtonColor: "#3085d6",
              confirmButtonText: this.$t("lang.BUTTON.BT7"),
              timerProgressBar: true,
              timer: 1000,
            });
          }
        } catch (error) {
          Swal.fire({
            icon: "error",
            title: `<span class="text-modal-toasty">${this.$t(
              "lang.NOTY.NT2"
            )}</span>`,
            confirmButtonColor: "#3085d6",
            confirmButtonText: this.$t("lang.BUTTON.BT7"),
            timerProgressBar: true,
            timer: 1000,
          });
        } finally {
          this.$store.dispatch("general/deactivateLoading")
          this.$store.dispatch("requestList/fetchListRequest");
        }
      }
    },
    async userAssign() {
      const res = await UserAssign.getUserAssign();
      this.user = res.data;
      const response = await UserAssign.getUserBuyer();
      this.allUser = response.data;
    },
    handleParentReasonChange(event) {
      this.errors.requestReason = "";
      this.dataCreate.requestReasonChild = "";
      this.childReasons = [];
      
      const selectedParentId = parseInt(event.target.value);
      if (selectedParentId) {
        const selectedParent = this.RequestReason.find(item => item.id === selectedParentId);
        if (selectedParent && selectedParent.children && Array.isArray(selectedParent.children) && selectedParent.children.length > 0) {
          this.childReasons = selectedParent.children;
        }
        this.selectedReason = selectedParentId;
      } else {
        this.selectedReason = null;
      }
    },
    handleChildReasonChange(event) {
      this.errors.requestReason = "";
      const selectedChildId = parseInt(event.target.value);
      if (selectedChildId) {
        this.selectedReason = selectedChildId;
      } else {
        this.selectedReason = this.dataCreate.requestReason;
      }
    },
    findParentId(childId) {
      for (const parent of this.RequestReason) {
        if (parent.children) {
          for (const child of parent.children) {
            if (child.id === childId) {
              return parent.id;
            }
            if (child.children) {
              for (const grandChild of child.children) {
                if (grandChild.id === childId) {
                  return parent.id;
                }
              }
            }
          }
        }
      }
      return null;
    },
    togglePersonnelField() {
      this.showPersonnelField = !this.showPersonnelField;
    },
    openOrganizationModalWithData() {
      this.isOrganizationModalOpen = true;
    },
    handleOrganizationSelection(org) {
      this.selectedOrganizations = [org];
      this.selectedMainOrganization = org;
      this.organization = org[0]?.organizationName;
      this.dataCreate.organizationId = org[0]?.id;
    }
  },
  computed: {
    formattedEstimatePrice() {
      const formatter = new Intl.NumberFormat("it-IT", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(this.dataCreate.estimatePrice || 0);
      if (!/^[0-9]+$/.test(this.dataCreate.estimatePrice)) {
        this.errors.estimatePrice = "";
      } else {
        this.errors.estimatePrice = "";
      }
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    isChildReasonDisabled() {
      return !this.childReasons || this.childReasons.length === 0;
    },
    ...mapGetters({
      isCreateOpen: "requestList/getCreateModalState",
      requestAction: "requestList/getRequestActionState",
      dataDetailForDup: "requestList/getDataForDup",
      loadingAction: "general/getLoadingStatus"
    }),
  },
  watch: {
    async isCreateOpen() {
      if (this.requestAction === "DUPLICATE") {
        this.dataCreate.requestProductName =
          this.dataDetailForDup.requestProductName;
        this.dataCreate.estimatePrice = this.dataDetailForDup.estimatePrice;
        this.dataCreate.approverUserId = this.dataDetailForDup.approverUserId;
        this.dataCreate.requestAmount = this.dataDetailForDup.amount;
        this.dataCreate.requestReason = this.dataDetailForDup.requestReason;
        this.selectedReason = this.dataDetailForDup.requestReason;
        this.dataCreate.expectReceiveDate =
          this.dataDetailForDup.expectReceiveDate;
        this.dataCreate.requestReasonDetail =
          this.dataDetailForDup.requestReasonDetail;
        this.dataCreate.description = this.dataDetailForDup.description;
        this.dataCreate.productLink = this.dataDetailForDup.productLink;

        const attachment = this.dataDetailForDup.userAttachment?.id;
        if (attachment) {
          const imageForDup = await getAttachment(
            this.dataDetailForDup.userAttachment?.id
          );

          this.imageForDup = URL.createObjectURL(imageForDup.data);
          this.dataCreate.selectedFile = new File(
            [imageForDup.data],
            this.dataDetailForDup.userAttachment?.fileName
          );
        } else {
          this.imageForDup = null;
        }
      } else {
        this.dataCreate = { 
          requestAmount: 1,
          requestReason: "",
          requestReasonChild: "",
          productLink: ""
        };
        this.selectedReason = null;
        this.imageForDup = null;
      }
      
      try {
        const res = await ReasonApi.getReasonAssign("");
        this.RequestReason = res.data;
        this.parentReasons = res.data.filter(item => item.parent === null);
        
        if (this.requestAction === "DUPLICATE" && this.dataCreate.requestReason) {
          const parentId = this.findParentId(this.dataCreate.requestReason);
          if (parentId) {
            this.dataCreate.requestReason = parentId;
            const parent = this.RequestReason.find(item => item.id === parentId);
            if (parent && parent.children) {
              this.childReasons = parent.children;
              this.dataCreate.requestReasonChild = this.selectedReason;
            }
          }
        }
      } catch (error) {
        console.error("Error loading reasons:", error);
      }
    }
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

.placeholder-option {
  color: #e5e7eb;
  font-style: italic;
}
/* delete button */

.relative {
  position: relative;
}

.absolute {
  position: absolute;
}

.top-2 {
  top: 1.8rem;
}

.right-2 {
  right: 17.7rem;
}
</style>

