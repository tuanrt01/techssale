<template>
  <Loading :show-loading="loadingAction" />
  <ConfirmRequest :data="dataDetail" :onClose="closeConfirmDelete" />
  <Modal :show="isOpen" size="modal-lg" @hidden="closeModal">
    <ModalHeader class="pb-4 pt-1">
      <h2 v-if="action == 'DETAIL'" class="text-xl font-medium mt-2">
        {{ $t("lang.DETAIL.DT9") }}
      </h2>
      <h2 v-if="action == 'UPDATE'" class="text-xl font-medium mt-2">
        {{ $t("lang.DETAIL.DT17") }}
      </h2>
      <div class="mt-2 ml-2">
        <button
          type="button"
          @click="openCreateModal"
          class="text-gray-900 bg-gray-100 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 font-medium rounded-lg text-sm px-5 py-2 text-center inline-flex items-center dark:focus:ring-gray-500"
        >
          <CopyIcon class="w-5 h-5" />
          <div class="ml-2">{{ $t("lang.DETAIL.DT28") }}</div>
        </button>
      </div>

      <button
        class="mx-auto flex items-center mr-1 pt-4 pr-3 justify-center h-10 w-10"
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
            <label class="form-label font-medium text-sm mb-1">
              {{ $t("lang.DETAIL.DT33") }}
              <span class="text-red-600">*</span>
            </label>
            <div>
              <Field
                type="text"
                name="productName"
                class="form-control pr-10"
                v-model.trim="dataDetail.requestProductName"
                :disabled="!dataDetail.isFieldsEditable"
              />
              <div class="invalid-feedback">
                {{ theErrors.requestProductName }}
              </div>
            </div>
          </div>
          <div class="mx-5">
            <label class="form-label font-medium text-sm mb-1">
              {{ $t("lang.DETAIL.DT45") }} <span class="text-red-600">*</span>
            </label>
            <div v-if="!dataDetail.isFieldsEditable">
              <div class="form-control pr-10 link-readonly">
                <span v-html="linkify(dataDetail.productLink)"></span>
              </div>
            </div>
            <div v-else>
              <Field
                type="text"
                name="productLink"
                class="form-control pr-10"
                v-model.trim="dataDetail.productLink"
                :disabled="!dataDetail.isFieldsEditable"
                @input="validateProductLink"
              />
              <div class="invalid-feedback">
                {{ theErrors.productLink }}
              </div>
            </div>
          </div>
          <div
            class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create"
          >
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1"
                >{{ $t("lang.DETAIL.DT1") }}
              </label>
              <Field
                type="text"
                name="status"
                class="form-control pr-10"
                v-model="DisplayName"
                :disabled="isDefaultFieldDisable"
              />
            </div>
            <div class="w-1/2 create-layout" v-if="dataDetail.status === 'WAITING'">
              <label class="form-label mb-1 font-medium text-sm">{{ $t("lang.DETAIL.DT6") }}
                <span class="text-red-600">*</span></label>
              <Field as="select" name="approverName" class="form-control pr-10" v-model="dataDetail.approverUserId"
                     :disabled="!dataDetail.isFieldsEditable">
                <option v-for="(item, index) in listUserAssigner" :key="index" :value="item.id">
                  {{ item.name }}
                </option>
              </Field>
            </div>
            <div class="w-1/2 create-layout" v-else>
              <label class="form-label font-medium text-sm"
                >{{ $t("lang.DETAIL.DT8") }}
                <span class="text-red-600">*</span></label
              >
              <Field
                as="select"
                name="buyerName"
                class="form-control pr-10"
                v-model="dataDetail.buyerUserId"
                :disabled="!dataDetail.isFieldsEditable"
              >
                <option
                  v-for="(item, index) in listUserBuyer"
                  :key="index"
                  :value="item.id"
                >
                  {{ item.name }}
                </option>
              </Field>
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
                  name="amount"
                  class="form-control"
                  v-model.trim="dataDetail.amount"
                  min="0"
                  :disabled="!dataDetail.isFieldsEditable"
                />
                <div class="invalid-feedback">
                  {{ this.theErrors.requestAmount }}
                </div>
              </div>
            </div>
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">
                {{ $t("lang.DETAIL.DT46") }}<span class="text-red-600">*</span>
                (<input type="checkbox" :checked="showOrganizationField" @change="toggleOrganizationField" :disabled="!dataDetail.isFieldsEditable"> {{ $t("lang.DETAIL.DT47") }})
              </label>
              <div v-if="!showOrganizationField">
                <Field
                  as="select"
                  v-model="dataDetail.personnelUsedId"
                  class="form-select input__width"
                  name="personnelUsedId"
                  :disabled="!dataDetail.isFieldsEditable"
                >
                  <option v-for="(userItem, index) in listUserBuyer" :key="index" :value="userItem.id">
                    {{ userItem.name }}
                  </option>
                </Field>
              </div>
              <div v-else>
                <input
                  type="text"
                  class="form-control"
                  :value="dataDetail.organizationUsedName"
                  readonly
                  :disabled="!dataDetail.isFieldsEditable"
                  @click="dataDetail.isFieldsEditable && openOrganizationModalWithData()"
                  style="cursor: pointer;"
                  placeholder="Chọn bộ phận"
                />
              </div>
            </div>
          </div>

          <div class="flex justify-start gap-5 pl-5 pr-5 mt-2 form-create">
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT3")
              }}</label>
              <VueDatePicker
                v-model="dataDetail.createAt"
                class="form-control text-sm"
                auto-apply
                format="dd-MM-yyyy"
                :disabled="isDefaultFieldDisable"
              />
            </div>
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1"
                >{{ $t("lang.DETAIL.DT4") }}
                <span class="text-red-600">*</span></label
              >
              <VueDatePicker
                v-model="dataDetail.expectReceiveDate"
                class="form-control text-sm"
                auto-apply
                format="dd-MM-yyyy"
                :disabled="!dataDetail.isFieldsEditable"
              />
              <div class="invalid-feedback">
                {{ theErrors.expectReceiveDate }}
              </div>
            </div>
          </div>
          <div class="mx-5">
            <label class="form-label font-medium text-sm"
              >{{ $t("lang.DETAIL.DT42") }} <span class="text-red-600">*</span>
            </label>
            <div class="flex gap-3 reason-container">
              <div class="w-1/2">
                <Field
                  as="select"
                  name="requestReason"
                  class="form-control w-full"
                  v-model.trim="parentReasonId"
                  @input="handleParentReasonChange"
                  :disabled="!dataDetail.isFieldsEditable"
                >
                  <option value="" selected>{{ $t("lang.DETAIL.DT48") }}</option>
                  <option
                    v-for="item in parentReasons"
                    :key="item.id"
                    :value="item.id"
                  >
                    {{ $t(item.reason) }}
                  </option>
                </Field>
              </div>
              
              <div class="w-1/2">
                <Field
                  as="select"
                  name="requestReasonChild"
                  class="form-control w-full"
                  v-model=" dataDetail.requestReason"
                  @input="handleChildReasonChange"
                  :disabled="isChildReasonDisabled || !dataDetail.isFieldsEditable"
                >
                  <option value="" selected>{{ isChildReasonDisabled ? $t("lang.DETAIL.DT44") : $t("lang.DETAIL.DT43") }}</option>
                  <option
                    v-for="item in childReasons"
                    :key="item.id"
                    :value="item.id"
                  >
                    {{ $t(item.reason) }}
                  </option>
                </Field>
              </div>
            </div>
          </div>

          <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{ $t("lang.DETAIL.DT12") }}</label>
              <input
                type="text"
                name="price"
                class="form-control"
                v-model.trim="formattedEstimatePrice"
                :disabled="!dataDetail.isFieldsEditable"
                maxlength="14"
                @input="
                  dataDetail.estimatePrice = parseInputPrice(
                    $event.target.value
                  )
                "
              />
            </div>
            <div class="w-1/2 create-layout">
              <!-- empty for layout alignment -->
            </div>
          </div>
          <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
            <div class="w-1/2 create-layout">
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT13")
              }}</label>
              <Field
                type="text"
                name="reasonDetail"
                class="form-control pr-10 text-area-scrollable"
                as="textarea"
                rows="4"
                v-model.trim="dataDetail.requestReasonDetail"
                :disabled="!dataDetail.isFieldsEditable"
              />
              <div class="invalid-feedback">
                {{ this.theErrors.requestReasonDetail }}
              </div>
            </div>
            <div
              class="w-1/2 create-layout"
              :class="{ 'overflow-auto': !isEditable }"
            >
              <label class="form-label font-medium text-sm mb-1">{{
                $t("lang.DETAIL.DT14")
              }}</label>
              <Field
                type="text"
                name="description"
                class="form-control pr-10 text-area-scrollable"
                as="textarea"
                rows="4"
                v-model.trim="dataDetail.description"
                v-if="isEditable"
              />
              <div
                class="pl-3 pt-2 pb-2 overflow-auto text-area-scrollable"
                style="
                  background-color: rgb(
                    var(--color-slate-100) / var(--tw-bg-opacity)
                  );
                  height: 75%;
                  border: 1px solid rgba(0, 0, 0, 0.1);
                  border-radius: 0.375rem;
                "
                v-html="'' || dataDetail.description"
                v-if="!isEditable"
              ></div>
            </div>
          </div>
          
          <div
            v-if="dataDetail.status === 'REJECTED'"
            class="flex justify-start gap-5 pl-5 pr-5 form-create"
          >
            <div class="w-full create-layout">
              <label
                for="detailReason"
                class="form-label font-medium text-sm mb-1"
                >{{ $t("lang.DETAIL.DT26") }}</label
              >
              <Field
                type="text"
                name="reason"
                class="form-control pr-10 text-area-scrollable"
                as="textarea"
                rows="4"
                v-model="rejectReason"
                :disabled="!dataDetail.isFieldsEditable"
              />
              <div class="invalid-feedback">
                {{ this.theErrors.requestReasonDetail }}
              </div>
            </div>
          </div>

          <!-- Status History Timeline Section -->
          <div v-if="action === 'DETAIL' && dataDetail.status !== 'WAITING'" class="mx-5 mt-5">
            <div class="border-b border-gray-200 pb-3">
              <h3 class="text-lg font-medium">{{ $t('lang.DETAIL.DT1') }} {{ $t('lang.DETAIL.DT3') }}</h3>
            </div>
            <div class="relative pb-5 mt-5">
              <div class="flex items-center w-full">
                <!-- Timeline bar -->
                <div class="w-full bg-gray-200 h-1 flex items-center justify-between">
                  <div v-for="(step, stepIndex) in filteredStatusSteps" :key="stepIndex"
                      class="relative flex flex-col items-center"
                      :class="{'opacity-50': !isStepPassed(step.status)}">
                    <!-- Timeline node -->
                    <div :class="{
                        'bg-green-500': isStepPassed(step.status),
                        'bg-gray-300': !isStepPassed(step.status),
                        'w-6 h-6 rounded-full z-10 flex items-center justify-center': true
                      }"
                      :title="$t(step.tooltip)">
                      <i v-if="isStepPassed(step.status)" class="fas fa-check text-white text-xs"></i>
                    </div>
                    <!-- Status label -->
                    <div class="absolute top-8 transform -translate-x-1/2 text-xs font-medium">
                      {{ $t(step.label) }}
                    </div>
                    <!-- Date under status if available -->
                    <div v-if="getStatusDate(step.status)" class="absolute top-14 transform -translate-x-1/2 text-xs text-gray-500">
                      {{ formatDate(getStatusDate(step.status)) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Purchased Product Information Section -->
          <div v-if="dataDetail.status === 'SUCCESS'" class="mx-5 mt-5">
            <div class="border-b border-gray-200 pb-3">
              <h3 class="text-lg font-medium">{{ $t('lang.DETAIL.DT41') }}</h3>
            </div>
            <div class="mt-4 grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT33') }}</p>
                <p class="mt-1">{{ dataDetail.boughtProductName || '-' }}</p>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT38') }}</p>
                <p class="mt-1">{{ dataDetail.boughtAmount || '-' }}</p>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT40') }}</p>
                <p class="mt-1">{{ formatCurrency(dataDetail.boughtPrice) }}</p>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT39') }}</p>
                <p class="mt-1">{{ dataDetail.boughtPlace || '-' }}</p>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT4') }}</p>
                <p class="mt-1">{{ formatDate(dataDetail.completedAt) || '-' }}</p>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-500">{{ $t('lang.DETAIL.DT8') }}</p>
                <p class="mt-1">{{ dataDetail.buyerName || '-' }}</p>
              </div>
            </div>
          </div>

          <div
            class="flex justify-start gap-5 pl-5 pr-5 form-create"
            v-if="
              (action == 'DETAIL' && dataDetail.requestUserAttachment) ||
              action === 'UPDATE'
            "
          >
            <div class="w-1/2 create-layout relative">
              <label
                for="regular-form-1"
                class="form-label font-medium text-sm mb-1"
              >
                {{ $t("lang.DETAIL.DT32") }}</label
              >
              <button
                v-if="
                  dataDetail.requestUserAttachment && dataDetail.isDeleteImage
                "
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

              <div class="flex items-center justify-center">
                <label
                  for="editInput"
                  class="flex flex-col items-center justify-center w-full border-4 border-dashed border-gray-400 cursor-pointer"
                  style="min-height: 150px; max-height: max-content"
                >
                  <input
                    v-if="dataDetail.isEditingUserImage"
                    type="file"
                    class="hidden"
                    id="editInput"
                    ref="editInput"
                    @change="handleFileChange"
                  />

                  <div class="w-full" v-if="dataDetail.requestUserAttachment">
                    <img
                      :src="
                        dataDetail.requestUserAttachment
                          ? createObjectURFunc(dataDetail.requestUserAttachment)
                          : ''
                      "
                      alt="Image"
                      class="w-full h-64 object-cover"
                      :class="{ 'cursor-default': action === 'DETAIL' }"
                    />
                  </div>

                  <div v-if="!dataDetail.requestUserAttachment">
                    <div class="px-6">
                      <p>{{ $t("lang.DETAIL.DT30") }}</p>
                      <p>(JPEG, PNG, JPG. MAX. 1MB)</p>
                    </div>
                  </div>
                </label>
              </div>
              <div class="text-red-500 text-sm mt-1">
                {{ theErrors.fileValidationError }}
              </div>
            </div>
          </div>
        </Form>
      </ModalBody>
    </div>
    <div class="mt-4 mx-auto text-center">
      <button
        class="btn w-26 mr-4 mb-2"
        :class="dataDetail.status === 'WAITING' ? 'btn btn-danger' : ''"
        type="button"
        @click="
          dataDetail.status === 'WAITING'
            ? openConfirmModalDelete()
            : closeModal()
        "
      >
        {{
          dataDetail.status === "WAITING"
            ? $t("lang.BUTTON.BT11")
            : $t("lang.BUTTON.BT1")
        }}
      </button>
      <button
        v-if="action === 'DETAIL' && dataDetail.status === 'WAITING'"
        class="btn w-24 btn-primary mr-4 mb-2"
        type="button"
        @click="changeActionToUpdate"
      >
        {{ $t("lang.BUTTON.BT2") }}
      </button>
      <button
        v-if="action === 'UPDATE' && dataDetail.status === 'WAITING'"
        class="btn w-24 btn-primary mr-4 mb-2"
        type="submit"
        :disabled="!isParamsChanged"
        @click="updateRequest"
      >
        {{ $t("lang.BUTTON.BT3") }}
      </button>
      <!-- Add Order Button -->
      <button
        v-if="canOrder"
        class="btn w-32 btn-success mr-4 mb-2"
        type="button"
        @click="handleOrder"
      >
        {{ $t("lang.BUTTON.ORDER") }}
      </button>
    </div>
  </Modal>
  <ModalOrganization
    :is-open="isOrganizationModalOpen"
    :selected-rows="selectedOrganizations"
    :organization-id="dataDetail.organizationUsedId"
    @update:is-open="isOrganizationModalOpen = $event"
    @save-selection="handleOrganizationSelection"
  />
</template>

<script>
import VueDatePicker from "@vuepic/vue-datepicker";
import { Field, Form } from "vee-validate";
import requestAdminApi from "@/api/RequestAdminApi";
import { RequestStatus } from "@/common/StatusEnum";
import RequestApi from "@/api/RequestApi";
import dayjs from "dayjs";
import { mapGetters } from "vuex";
import Swal from "sweetalert2";
import * as Yup from "yup";
import { RequestReason } from "@/common/RequestReasonEnum";
import ConfirmRequest from "@/views/user/list-requested/ConfirmDelete.vue";
import { getAttachment } from "@/api/Attachments";
import Loading from "@/components/loading/Loading.vue";
import ReasonApi from "@/api/ReasonApi.js";
import ModalOrganization from "@/common/ModalOrganization.vue";
import ApproveAndRejectApi from "@/api/ApproveAndRejectApi.js";

export default {
  name: "DetailMyRequest",
  components: {Loading, ConfirmRequest, Field, Form, VueDatePicker, ModalOrganization },
  data() {
    const schema = Yup.object().shape({
      productName: Yup.string().required(this.$t("lang.ERROR.ER1")),
      amount: Yup.string().required(this.$t("lang.ERROR.ER2")),
    });
    return {
      schema,
      rejectReason: "",
      dataDetail: {
        isFieldsEditable: false,
        isDeleteImage: false,
        isEditingUserImage: false,
      },
      action: "DETAIL",
      isDefaultFieldDisable: true,
      theErrors: {},
      currentDate: new Date(),
      isHasError: false,
      dataDetailInit: {},
      fileValidationError: null,
      buyerAttachment: null,
      isInputDisabled: false,
      isEditable: false,
      description: "",
      reasonList: [],
      parentReasonId: "",
      parentReasons: [],
      childReasons: [],
      showOrganizationField: false,
      isOrganizationModalOpen: false,
      selectedOrganizations: [],
      allUser: {},
    };
  },
  props: ["isOpen", "onClose", "id"],
  computed: {
    ...mapGetters({
      dataComplete: "requestList/getDetails",
      loadingAction: "general/getLoadingStatus",
      userInfo: "auth/getUserInfo"  // Fix: change from users/getUserInfo to auth/getUserInfo
    }),
    buyerRejectReason() {
      return this.dataDetail.buyerRejectReason;
    },
    approverRejectReason() {
      return this.dataDetail.approverRejectReason;
    },
    rejectReasonComputed() {
      return this.approverRejectReason || this.buyerRejectReason;
    },
    RequestReason() {
      return RequestReason;
    },
    RequestStatus() {
      return RequestStatus;
    },
    DisplayName() {
      return (
        this.$t(`${RequestStatus[this.dataDetail.status]}`) ||
        this.$t(`${this.dataDetail.status}`)
      );
    },
    filteredStatusSteps() {
      return [
        {
          status: 'WAITING',
          label: 'lang.STATUS.STT1',
          tooltip: 'lang.STATUSDESCRIPTION.STTD1',
          icon: 'fa fa-clock'
        },
        {
          status: 'PROCESSING',
          label: 'lang.STATUS.STT2',
          tooltip: 'lang.STATUSDESCRIPTION.STTD2',
          icon: 'fa fa-cog'
        },
        {
          status: 'ORDERING',
          label: 'lang.STATUS.STT5',
          tooltip: 'lang.STATUSDESCRIPTION.STTD5',
          icon: 'fa fa-shopping-cart'
        },
        {
          status: 'SUCCESS',
          label: 'lang.STATUS.STT3',
          tooltip: 'lang.STATUSDESCRIPTION.STTD3',
          icon: 'fa fa-check'
        }
      ];
    },
    ...mapGetters({
      listUserAssigner: "users/getUserAssign",
      listUserBuyer: "users/getUserBuyer",
      myDetailData: "requestList/getDetails",
    }),
    formattedEstimatePrice() {
      const formatter = new Intl.NumberFormat("it-IT", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(this.dataDetail.estimatePrice || 0);

      if (!/^[0-9]+$/.test(this.dataDetail.estimatePrice)) {
        this.theErrors.estimatePrice = "";
      } else {
        this.theErrors.estimatePrice = "";
      }
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    isParamsChanged() {
      return (
        this.dataDetailInit.approverUserId !== this.dataDetail.approverUserId ||
        this.dataDetailInit.buyerUserId !== this.dataDetail.buyerUserId ||
        this.dataDetailInit.description !== this.dataDetail.description ||
        this.dataDetailInit.estimatePrice !== this.dataDetail.estimatePrice ||
        dayjs(this.dataDetailInit.expectReceiveDate).format(
          "YYYY-MM-DDTHH:mm:ss"
        ) !==
          dayjs(this.dataDetail.expectReceiveDate).format(
            "YYYY-MM-DDTHH:mm:ss"
          ) ||
        this.dataDetailInit.amount !== this.dataDetail.amount ||
        this.dataDetailInit.requestProductName !==
          this.dataDetail.requestProductName ||
        this.dataDetailInit.requestReason !== this.dataDetail.requestReason ||
        this.dataDetailInit.requestReasonDetail !==
          this.dataDetail.requestReasonDetail ||
        this.dataDetailInit.requestUserAttachment !==
          this.dataDetail.requestUserAttachment ||
        this.dataDetailInit.productLink !== this.dataDetail.productLink ||
            this.dataDetailInit.organizationUsedId !== this.dataDetail.organizationUsedId ||
            this.dataDetailInit.personnelUsedId !== this.dataDetail.personnelUsedId
      );
    },
    isChildReasonDisabled() {
      return !this.childReasons || this.childReasons.length === 0;
    },
    canOrder() {
      console.log('User Info:', this.userInfo); // Add debug log
      console.log('Request Status:', this.dataDetail?.status); // Add debug log
      return this.userInfo && 
             this.userInfo.privileges && 
             (this.userInfo.privileges.includes('buy') || 
              this.userInfo.authorities.includes('NGUOI_MUA')) &&
             this.dataDetail.status === 'PROCESSING';
    }
  },
  async created() {
    this.$store.dispatch("users/fetchListAssigners");
    this.$store.dispatch("users/fetchListBuyers");
    this.fetchListReason()
  },
  methods: {
    createObjectURFunc(file) {
      const blob = URL.createObjectURL(file);
      return blob;
    },
    handleFileChange(event) {
      const maxSize = 1000000;
      const allowedFileTypes = ["image/jpeg", "image/png", "image/jpg"];
      const selectedFileInput = this.$refs.editInput.files[0];
      if (selectedFileInput) {
        if (!allowedFileTypes.includes(selectedFileInput.type)) {
          this.theErrors.fileValidationError =
            "Tệp tin không hợp lệ. Vui lòng nhập kiểu jpeg, png, jpg.";
          this.$refs.editInput.value = "";
          this.dataDetail.requestUserAttachment = false;
        } else if (selectedFileInput.size > maxSize) {
          this.theErrors.fileValidationError =
            "Vui lòng chọn tệp không quá 1MB.";
          this.$refs.editInput.value = "";
          this.dataDetail.requestUserAttachment = false;
        } else {
          this.theErrors.fileValidationError = null;
          this.dataDetail.requestUserAttachment = selectedFileInput;
        }
      }
    },

    deleteImage() {
      if (this.dataDetail.requestUserAttachment) {
        this.dataDetail.requestUserAttachment = null;
      }
    },
    parseInputPrice(value) {
      const numericValue = parseFloat(value.replace(/[^\d]/g, ""));
      return isNaN(numericValue) ? 0 : numericValue;
    },

    async detailRequest() {
      try {
        const res = await requestAdminApi.getDetailRequest(this.id);
        const responseData = res.data;

        await this.getImageUserData(responseData.userAttachment?.id);
        const dataDetailDuplicate = responseData;
        const description = responseData.description;
        this.description = description;
        if (
          description &&
          (description.includes("https") || description.includes("http"))
        ) {
          const regex =
            /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;

          const replacedText = description.replace(regex, function (match) {
            return (
              '<a target="_blank" style="text-decoration: underline;color: blue;" href="' +
              match +
              '">' +
              match +
              "</a>"
            );
          });
          responseData.description = replacedText;
        }
        this.dataDetail = { ...this.dataDetail, ...responseData };
        this.dataDetailInit = { ...this.dataDetail, ...responseData };
        dataDetailDuplicate.description = description;
        this.$store.dispatch("requestList/setDataForDup", dataDetailDuplicate);
        this.initializeReasonSelection();
        this.showOrganizationField = !!this.dataDetail.organizationUsedId;
        this.dataDetail.organizationUsedId = res.data.organizationUsedId
      } catch (error) {
        console.error(error);
      }
    },

    async getImageUserData(userAttachmentId) {
      if (userAttachmentId) {
        const imgUserRes = await getAttachment(userAttachmentId);
        this.dataDetail.requestUserAttachment = imgUserRes.data;
      } else {
        this.dataDetail.requestUserAttachment = null;
      }
    },

    closeModal() {
      this.onClose();
      this.schema = {};
      this.theErrors = {};
      this.action = "DETAIL";
      this.dataDetail.isFieldsEditable = false;
      this.dataDetail.isDeleteImage = false;
      this.dataDetail.isEditingUserImage = false;
      this.isEditable = false;
    },
    changeActionToUpdate() {
      this.dataDetail.isFieldsEditable = true;
      this.dataDetail.isDeleteImage = true;
      this.dataDetail.isEditingUserImage = true;
      this.action = "UPDATE";
      this.isEditable = true;
      this.dataDetail.description = this.description;
      this.dataDetailInit.description = this.description;
    },
    openConfirmModalDelete() {
      this.$store.dispatch("requestList/openDeleteModal");
    },
    closeConfirmDelete() {
      this.$store.dispatch("requestList/closeDeleteModal");
      this.closeModal();
    },
    openConfirmModal() {
      this.$store.dispatch("requestList/openConfirmModal");
    },

    async updateRequest() {
      this.isHasError = false;
      this.validateProductLink();
      
      if (!this.showOrganizationField && !this.dataDetail.personnelUsed) {
        this.theErrors.personnelUsed = this.$t("lang.ERROR.ER20");
      }
      if (this.showOrganizationField && !this.dataDetail.organizationId) {
        this.theErrors.personnelUsed = this.$t("lang.ERROR.ER21");
      }
      
      const link = this.dataDetail.productLink;
      if (!link) {
        this.theErrors.productLink = this.$t("lang.ERROR.ER18");
        return;
      } else {
        const urlPattern = /^(https?:\/\/)?([\w\-]+\.)+[\w\-]{2,}(\/\S*)?$/;
        if (!urlPattern.test(link)) {
          this.theErrors.productLink = this.$t("lang.ERROR.ER19");
          return;
        }
      }

      let params = {
        approverUserId: this.dataDetail.approverUserId,
        buyerUserId: this.dataDetail.buyerUserId,
        description: this.dataDetail.description,
        estimatePrice: this.dataDetail.estimatePrice,
        expectReceiveDate: dayjs(this.dataDetail.expectReceiveDate).format(
          "YYYY-MM-DDTHH:mm:ss"
        ),
        requestAmount: this.dataDetail.amount,
        requestProductName: this.dataDetail.requestProductName,
        productLink: this.dataDetail.productLink,
        requestReason: this.getSelectedReason(),
        requestReasonDetail: this.dataDetail.requestReasonDetail,
        updatedAt: dayjs(this.dataDetail.updatedAt).format(
          "YYYY-MM-DDTHH:mm:ss"
        ),
      };

      // Bổ sung truyền dữ liệu đối tượng sử dụng
      if (this.showOrganizationField) {
        params.organizationUsed = this.dataDetail.organizationUsedId;
        params.personnelUsed = null;
      } else {
        params.personnelUsed = this.dataDetail.personnelUsedId;
        params.organizationUsed = null;
      }

      const formData = this.$h.convertJsonToFormData(params);
      if (this.dataDetail.requestUserAttachment) {
        const fileName =
          this.dataDetail.userAttachment?.fileName ||
          this.dataDetail.requestUserAttachment?.name;

        var fileOfBlob = new File(
          [this.dataDetail.requestUserAttachment],
          fileName
        );
        formData.append("requestUserAttachment", fileOfBlob);
      }

      if (this.isHasError) {
        return;
      }
      this.theErrors = {}
      this.closeModal();
      this.$store.dispatch("general/activeLoading")
      try {
        const res = await RequestApi.updateRequest(this.id, formData);
        if (res.status === 204) {
          Swal.fire({
            icon: "success",
            title: `<span class="text-modal-toasty">${this.$t(
              "lang.NOTY.NT11"
            )}</span>`,
            confirmButtonColor: "#3085d6",
            confirmButtonText: this.$t("lang.BUTTON.BT7"),
            timerProgressBar: true,
            timer: 1000,
          });
        }
      } catch {
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
    },
    validation() {
      const inputExpectedDate = new Date(this.dataDetail.expectReceiveDate);
      if (inputExpectedDate <= this.currentDate) {
        this.theErrors.expectReceiveDate = this.$t("lang.ERROR.ER6");
        return (this.isHasError = true);
      }

      if (
        this.dataDetail.requestReason === 'OTHER' &&
        !this.dataDetail.requestReasonDetail
      ) {
        this.theErrors.requestReasonDetail = this.$t("lang.ERROR.ER9");
        return (this.isHasError = true);
      }

      if (this.dataDetail.amount > 9999) {
        this.theErrors.requestAmount = this.$t("lang.ERROR.ER8");
        return (this.isHasError = true);
      }

      if (!this.dataDetail.amount) {
        this.theErrors.requestAmount = this.$t("lang.ERROR.ER2");
        return (this.isHasError = true);
      }

      if (!this.dataDetail.requestProductName) {
        this.theErrors.requestProductName = this.$t("lang.ERROR.ER1");
        return (this.isHasError = true);
      }
    },
    async deleteRequest() {
      try {
        const res = await requestAdminApi.deleteRequest(this.id);
        this.dataDetail = { ...this.dataDetail, ...res.data };
        this.dataDetailInit = { ...this.dataDetail, ...res.data };
      } catch (error) {}
    },
    openCreateModal() {
      this.$store.dispatch("requestList/openCreateModal");
      this.$store.dispatch("requestList/switchToDuplicate");
      this.$store.dispatch("requestList/closeMyDetailModal");
    },
    async fetchListReason(){
      let params = "";
      const res = await ReasonApi.getReasonAssign(params);
      this.reasonList = res.data;
      this.parentReasons = res.data.filter(item => item.parent === null);
    },
    handleParentReasonChange(event) {
      const selectedParentId = parseInt(event.target.value);
      this.dataDetail.requestReason = "";
      this.childReasons = [];
      
      if (selectedParentId) {
        this.parentReasonId = selectedParentId;
        const selectedParent = this.reasonList.find(item => item.id === selectedParentId);
        if (selectedParent && selectedParent.children && Array.isArray(selectedParent.children) && selectedParent.children.length > 0) {
          this.childReasons = selectedParent.children;
        } else {
          this.dataDetail.requestReason = selectedParentId.toString();
        }
      } else {
        this.parentReasonId = "";
        this.dataDetail.requestReason = "";
      }
    },
    handleChildReasonChange(event) {
      if (event && event.target) {
        this.dataDetail.requestReason = event.target.value;
      }
    },
    findParentId(childId) {
      if (!childId) return null;
      
      const childIdNum = parseInt(childId);
      for (const parent of this.reasonList) {
        if (parent.id === childIdNum) {
          return null; 
        }
        
        if (parent.children) {
          for (const child of parent.children) {
            if (child.id === childIdNum) {
              return parent.id;
            }
          }
        }
      }
      return null;
    },
    initializeReasonSelection() {
      if (!this.dataDetail.requestReason || this.reasonList.length === 0) return;
      
      const reasonId = parseInt(this.dataDetail.requestReason);
      const parentId = this.findParentId(reasonId);
      
      if (parentId) {
        this.parentReasonId = parentId;
        const parent = this.reasonList.find(item => item.id === parentId);
        if (parent && parent.children) {
          this.childReasons = parent.children;
        }
      } else {
        this.parentReasonId = reasonId;
        const parent = this.reasonList.find(item => item.id === reasonId);
        if (parent && parent.children) {
          this.childReasons = parent.children;
        }
      }
    },
    getSelectedReason() {
      if (this.dataDetail.requestReason) {
        return this.dataDetail.requestReason;
      }
      else if (this.parentReasonId) {
        return this.parentReasonId;
      }
      return null;
    },
    linkify(text) {
      if (!text) return '';
      const urlRegex = /(https?:\/\/[^\s]+)/g;
      return text.replace(urlRegex, function(url) {
        return `<a href="${url}" target="_blank" rel="noopener noreferrer" title="${url}" style="color: #1976d2; text-decoration: underline;">${url}</a>`;
      });
    },
    validateProductLink() {
      this.theErrors.productLink = "";
    
    },
    toggleOrganizationField() {
      if (!this.dataDetail.isFieldsEditable) return;
      this.showOrganizationField = !this.showOrganizationField;
    },
    openOrganizationModalWithData() {
      this.isOrganizationModalOpen = true;
    },
    handleOrganizationSelection(selectedOrganizations) {
      this.selectedOrganizations = selectedOrganizations;
      this.dataDetail.organizationUsedId = selectedOrganizations[0].id;
      this.dataDetail.organizationUsedName = selectedOrganizations[0].organizationName;
    },
    isStepPassed(status) {
      const statusOrder = {
        'WAITING': 0,
        'PROCESSING': 1,
        'ORDERING': 2,
        'SUCCESS': 3,
        'REJECTED': -1
      };
      
      // If rejected, only show the status where it was rejected
      if (this.dataDetail.status === 'REJECTED') {
        return false;
      }
      
      return statusOrder[status] <= statusOrder[this.dataDetail.status];
    },
    getStatusDate(status) {
      if (status === 'WAITING') {
        return this.dataDetail.createdAt;
      } else if (status === 'PROCESSING') {
        return this.dataDetail.updatedAt;
      } else if (status === 'ORDERING') {
        return this.dataDetail.updatedAt;
      } else if (status === 'SUCCESS') {
        return this.dataDetail.completedAt;
      }
      return null;
    },
    formatDate(dateString) {
      if (!dateString) return '';
      return dayjs(dateString).format('DD/MM/YYYY HH:mm');
    },
    formatCurrency(value) {
      if (!value) return '-';
      const formatter = new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(value || 0);
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    async handleOrder() {
      try {
        this.$store.dispatch("general/activeLoading");
        const updateAt = new Date().toISOString();
        await ApproveAndRejectApi.buyerOrder(this.dataDetail.id, updateAt);
        this.$toast.success(this.$t("lang.TOAST.ORDER_SUCCESS"));
        this.$store.dispatch("requestList/fetchListRequest");
        this.closeModal();
      } catch (error) {
        console.error("Error ordering request:", error);
        this.$toast.error(this.$t("lang.TOAST.ORDER_ERROR"));
      } finally {
        this.$store.dispatch("general/deactivateLoading");
      }
    }
  },
  watch: {
    isOpen(newVal) {
      if (newVal) {
        this.detailRequest();
        this.showOrganizationField = !!this.dataDetail.organizationUsedId;
      }
    },
    rejectReasonComputed(newValue) {
      this.rejectReason = newValue;
    },
    'dataDetail.requestReason'() {
      this.initializeReasonSelection();
    },
    reasonList() {
      this.initializeReasonSelection();
    },
    'dataDetail.organizationUsedId'(val) {
      this.showOrganizationField = !!val;
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

.reason-container {
  display: flex;
  gap: 12px;
}

.form-control:disabled {
  background-color: rgb(var(--color-slate-100) / var(--tw-bg-opacity));
  opacity: 0.7;
  cursor: not-allowed;
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
  right: 0.2rem;
}

.link-readonly {
  padding-left: 0.75rem;
  padding-right: 2.5rem;
  background-color: rgb(var(--color-slate-100) / var(--tw-bg-opacity));
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 1rem;
  color: #6b7280;
  min-height: 38px;
  display: flex;
  align-items: center;
  width: 100%;
}

.link-readonly span,
.link-readonly a {
  white-space: nowrap;
  overflow: hidden;
  display: block;
  width: 100%;
}
</style>
<style>
.dp__button {
  display: none !important;
}

.dp__disabled {
  background: rgb(var(--color-slate-100) / var(--tw-bg-opacity));
  cursor: not-allowed !important;
}

.invalid-feedback {
  color: red;
  margin-top: 8px;
  font-size: 13px;
}

.dp__input,
.dp__input:hover {
  border: 1px solid rgb(var(--color-slate-200) / var(--tw-border-opacity));
  font-size: 0.875rem;
}
</style>
