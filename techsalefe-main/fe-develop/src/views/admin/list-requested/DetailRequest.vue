<template>
  <Modal :loginValue="LoginValue" :show="isDetailModalOpen" size="modal-xl" @hidden="closeModal">
    <ModalHeader >
      <h2 class="intro-y text-xl font-medium mt-2">
        {{ $t("lang.DETAIL.DT9") }}
      </h2>
      <div class="flex items-center justify-between mt-3">
        <div class="flex items-center w-full">
          <div class="flex items-center w-full px-2">
            <div class="flex items-center justify-between w-full ">
              <div  class="relative w-full flex items-center justify-between ">
                <div class="absolute top-5 left-5 right-5 flex h-1 z-0">
                  <template v-for="(step, index) in filteredStatusSteps.slice(0, filteredStatusSteps.length - 1)" :key="'line-'+step.status">
                    <div
                        class="flex-1 h-full transition-colors duration-300 "
                        :class="isStepPassedOrRejected(filteredStatusSteps[index+1].status) ? 'bg-blue-500' : 'bg-gray-300'"
                    ></div>
                  </template>
                </div>
                <template v-for="(step, index) in filteredStatusSteps" :key="step.status">
                  <div class="flex flex-col items-center z-10 w-40" v-tooltip="{ content: $t(step.tooltip), placement: 'top' }">
                    <div
                        class="w-10 h-10 rounded-full flex items-center justify-center border-2 text-xl cursor-pointer"
                        :class="[
              rejectedStep === step.status
                ? ['bg-red-500', 'text-white', 'border-red-500']
                : isStepSuccessed(step.status)
                  ? ['bg-green-500', 'text-white', 'border-green-500']
                : isStepActive(step.status)
                  ? [colorClassMap[step.status].bg, 'text-white', colorClassMap[step.status].border]
                  : 'bg-white text-gray-400 border-gray-300'
            ]"
                        @click="isStepActive(step.status) || isStepPassedOrRejected(step.status) ? openStatusModal(step.status) : null"
                        :style="{ cursor: isStepActive(step.status) || isStepPassedOrRejected(step.status) ? 'pointer' : 'default' }"
                    >
                      <i v-if="rejectedStep === step.status" class="fa fa-times"></i>
                      <i v-else :class="getStatusIcon(step.status)"></i>
                    </div>
                    <span
                        class="mt-2 text-xs text-center font-semibold"
                        :class="{
            'text-red-500': rejectedStep === step.status,
            'text-green-500': isStepSuccessed(step.status) && rejectedStep !== step.status,
            [colorClassMap[step.status].text]: isStepActive(step.status) && !isStepSuccessed(step.status) && rejectedStep !== step.status,
            'text-gray-400': !isStepActive(step.status) && !isStepSuccessed(step.status) && rejectedStep !== step.status
          }"
                    >
                      <template v-if="rejectedStep === step.status">{{ $t('lang.STATUS.STT4') }}</template>
                      <template v-else>{{ $t(step.label) }}</template>
                    </span>
                    <div class="mt-1 text-xs text-center w-full px-1 h-11">
                      <template v-if="isStepActive(step.status) || isStepPassedOrRejected(step.status)">
                        <template v-if="step.status === 'WAITING'">
                          <div class="font-semibold truncate">{{ $t('lang.DETAIL.DT19') }}</div>
                          <div
                              class="truncate"
                              v-tooltip="{ content: dataDetail.requestUsername, placement: 'bottom' }"
                          >
                            {{ dataDetail.requestUsername }}
                          </div>
                        </template>
                        <template v-else-if="step.status === 'PROCESSING'">
                          <div class="font-semibold truncate">{{ $t('lang.DETAIL.DT6') }}</div>
                          <div
                              class="truncate"
                              v-tooltip="{ content: dataDetail.approverUsername || '-', placement: 'bottom' }"
                          >
                            {{ dataDetail.approverUsername || '-' }}
                          </div>
                        </template>

                        <template v-else-if="step.status === 'ORDERING'">
                          <div class="font-semibold truncate">{{ $t('lang.DETAIL.DT8') }}</div>
                          <div
                              class="truncate"
                              v-tooltip="{ content: dataDetail.buyerUsername || '-', placement: 'bottom' }"
                          >
                            {{ dataDetail.buyerUsername || '-' }}
                          </div>
                        </template>

                        <template v-else-if="step.status === 'SUCCESS'">
                          <div class="font-semibold truncate">{{ $t('lang.DETAIL.DT39') }}</div>
                          <div
                              class="truncate"
                              v-tooltip="{ content: dataDetail.boughtPlace || '-', placement: 'bottom' }"
                          >
                            {{ dataDetail.boughtPlace || '-' }}
                          </div>
                        </template>
                      </template>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>

      </div>
      <span class="intro-y text-sm-gray font-medium mt-2 mb-0">
    {{ $t("lang.DETAIL.DT3") }}: {{ formatDate(dataDetail.createAt) }}
  </span>
    </ModalHeader>
 <div class="flex">
   <div :class="{'w-2/3': dataDetail.status === 'SUCCESS', 'w-full': dataDetail.status !== 'SUCCESS'}" >
     <div class="overflow-y-auto overflow-x-hidden default-class scroll-view">
       <ModalBody>
         <Form enctype="multipart/form-data">
           <div class="flex gap-5 mx-5">
             <div class="w-1/2">
               <label class="form-label font-medium text-sm mb-1">{{ $t('lang.DETAIL.DT33') }}</label>
               <Field v-model="dataDetail.requestProductName" :disabled="!dataDetail.isFieldsEditable"
                      class="form-control pr-4" name="productName" type="text">
               </Field>
             </div>
             <div class="w-1/2">
               <label class="form-label font-medium text-sm mb-1">{{ $t('lang.DETAIL.DT45') }} <span class="text-red-600">*</span></label>
               <div v-if="!dataDetail.isFieldsEditable">
                 <div class="form-control pr-10 link-readonly">
                   <a
                     :href="dataDetail.productLink"
                     target="_blank"
                     rel="noopener noreferrer"
                     :title="dataDetail.productLink"
                     class="link-text"
                   >{{ dataDetail.productLink }}</a>
                 </div>
               </div>
               <div v-else>
                 <Field
                   type="text"
                   name="productLink"
                   class="form-control pr-10"
                   v-model.trim="dataDetail.productLink"
                   :disabled="!dataDetail.isFieldsEditable"
                 />
               </div>
             </div>
           </div>
           <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT19")
                 }}</label>
               <Field v-model="dataDetail.requestUsername" :disabled="!dataDetail.isFieldsEditable"
                      class="form-control pr-10" name="name" type="text" />
             </div>
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   displayLabelApprover
                 }}</label>
               <Field v-model="dataDetail.approverUsername" :disabled="!dataDetail.isFieldsEditable"
                      class="form-control pr-10" min="0" name="approverName" type="text" />
             </div>
           </div>

           <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT1")
                 }}</label>
               <Field v-model="DisplayName" :disabled="!dataDetail.isFieldsEditable" class="form-control pr-10"
                      name="status" type="text" />
             </div>
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT4")
                 }}</label>
               <DatePicker v-model="dataDetail.expectReceiveDate" :disabled="!dataDetail.isFieldsEditable" auto-apply
                           class="form-control" format="dd-MM-yyyy" />
             </div>
           </div>
           <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT11")
                 }}</label>
               <Field v-model="dataDetail.amount" :disabled="!dataDetail.isFieldsEditable" class="form-control pr-4"
                      name="quantity" type="number">
               </Field>
             </div>
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT12")
                 }}</label>
               <Field type="text" name="price" class="form-control" v-model="formattedEstimatePrice"
                      :disabled="!dataDetail.isFieldsEditable" maxlength="16" @input="
                  dataDetail.estimatePrice = parseInputPrice(
                    $event.target.value
                  )
                  " />
             </div>
           </div>

           <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
             <div class="w-1/2 create-layout">
              <div class="flex gap-3 reason-container">

              <div class="w-1/2">
                <label class="form-label font-medium text-sm mb-1">
                 {{ displayLabelBuyer }}
               </label>
               <Field v-model="dataDetail.buyerUsername" :disabled="!dataDetail.isFieldsEditable"
                      class="form-control pr-10" min="0" name="buyerName" type="text" />
              </div>
              <div class="w-1/2">
                <label class="form-label font-medium text-sm mb-1">
                 {{ $t('lang.DETAIL.DT46') }} <span class="text-red-600">*</span>
                 (<input
                   type="checkbox"
                   :checked="!dataDetail.personnelUsedId"
                   disabled
                 > {{ $t('lang.DETAIL.DT47') }})
               </label>
               <input
                 type="text"
                 class="form-control"
                 :value="dataDetail.personnelUsedId ? dataDetail.personnelUsedName : dataDetail.organizationUsedName"
                 readonly
                 disabled
                 :placeholder="$t('lang.DETAIL.DT50')"
               />
              </div>
 
              </div>
             </div>
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1" for="requestReason">{{ $t("lang.DETAIL.DT7") }}</label>
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
                      <option value="" selected>{{ $t('lang.DETAIL.DT48') }}</option>
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
                      v-model.trim="dataDetail.requestReason"
                      @input="handleChildReasonChange"
                      :disabled="isChildReasonDisabled || !dataDetail.isFieldsEditable"
                    >
                      <option value="" selected>{{ isChildReasonDisabled ? "" : $t("lang.DETAIL.DT43") }}</option>
                      <option
                        v-for="item in childReasons"
                        :key="item.id"
                        :value="item.id"
                        v-if="!isChildReasonDisabled"
                      >
                        {{ $t(item.reason) }}
                      </option>
                    </Field>
                  </div>
                </div>
             </div>
           </div>
           <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
             <div class="w-1/2 create-layout">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT13")
                 }}</label>
               <Field type="text" name="reason" class="form-control pr-10 text-area-scrollable" as="textarea" rows="4"
                      v-model="dataDetail.requestReasonDetail" :disabled="!dataDetail.isFieldsEditable" />
             </div>
             <div class="w-1/2 create-layout overflow-auto">
               <label class="form-label font-medium text-sm mb-1">{{
                   $t("lang.DETAIL.DT14")
                 }}</label>
               <div class="pl-3 pt-2 pb-2 overflow-scroll"
                    style="background-color: rgb(var(--color-slate-100) / var(--tw-bg-opacity));height: 75%; border: 1px solid rgba(0, 0, 0, 0.1); border-radius: 0.375rem;"
                    v-html="'' || displayDescription"></div>
             </div>
           </div>
           <div class="flex justify-start gap-5 pl-5 pr-5 mt-3 form-create">
             <div v-if="dataDetail.status === 'REJECTED'" class="w-full create-layout">
               <label for="detailReason" class="form-label font-medium text-sm mb-1">Lý do từ chối</label>
               <Field type="text" name="reasonReject" class="form-control pr-10 text-area-scrollable" as="textarea"
                      rows="4" v-model="rejectReason" :disabled="!dataDetail.isFieldsEditable" />
             </div>
           </div>

           <div class="flex justify-start gap-5 pl-5 pr-5 form-create">
             <div class="w-1/2 create-layout" v-if="dataDetail.userAttachment">
               <label for="regular-form-1" class="form-label font-medium text-sm mb-1">{{$t('lang.DETAIL.DT34')}}</label>
               <div class="flex items-center justify-center">
                 <label
                     class="flex flex-col items-center justify-center w-full border-4 border-dashed border-gray-400 cursor-pointer"
                     style="min-height: 150px; max-height: max-content">
                   <div class="w-full">
                     <img :src="imageUserData" alt="Image" class="w-full h-64 object-cover cursor-default" />
                   </div>
                 </label>
               </div>
             </div>
           </div>
         </Form>
       </ModalBody>
     </div>
     <div class="mt-4 mx-auto text-center" v-if="dataDetail.status !== 'SUCCESS'">
       <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
         {{ $t("lang.BUTTON.BT1") }}
       </button>

       <!-- Nút Từ chối khi đơn đang chờ duyệt -->
       <button v-if="dataDetail.status === 'WAITING'" 
               class="btn btn-danger w-24 mr-4 mb-2"
               type="button" 
               @click="openCancelModal">
         {{ $t("lang.BUTTON.BT5") }}
       </button>

       <!-- Nút Duyệt khi đơn đang chờ duyệt -->
       <button v-if="dataDetail.status === 'WAITING'"
               class="btn btn-success text-white w-24 mr-4 mb-2" 
               type="button" 
               @click="openConfirmModal">
         {{ $t("lang.BUTTON.BT8") }}
       </button>

       <!-- Nút Từ chối khi đơn đang xử lý -->
       <button v-if="dataDetail.status === 'PROCESSING'" 
               class="btn btn-danger w-24 mr-4 mb-2"
               type="button" 
               @click="openRejectModal">
         {{ $t('lang.BUTTON.BT5') }}
       </button>

       <!-- Nút Hoàn thành khi đơn đang đặt hàng -->
       <button v-if="dataDetail.status === 'ORDERING'"
               class="btn btn-success text-white w-26 mr-4 mb-2"
               type="button"
               @click="openCompleteModal">
         {{ $t("lang.BUTTON.BT6") }}
       </button>
     </div>
   </div>
   <div v-if="dataDetail.status === 'SUCCESS'" class="1/3 overflow-y-auto overflow-x-hidden default-class scroll-view">
     <div>
       <h2 class="font-medium text-md px-3 pt-3 rounded-lg text-center mx-5">Thông tin mua hàng</h2>
       <div class="h-0.5 bg-primary mx-16"></div>
     </div>
     <div>
       <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
         <div class="w-full create-layout">
           <label class="form-label font-medium text-sm mb-1">{{
               $t("lang.DETAIL.DT37")
             }}</label>
           <Field v-model="dataDetail.buyerUsername" :disabled="!dataDetail.isFieldsEditable"
                  class="form-control pr-10" name="name" type="text" />
         </div>
       </div>
       <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
         <div class="w-full create-layout">
           <label class="form-label font-medium text-sm mb-1">{{
               $t("lang.DETAIL.DT38")
             }}</label>
           <Field v-model="dataDetail.boughtAmount" :disabled="!dataDetail.isFieldsEditable"
                  class="form-control pr-10" name="name" type="text" />
         </div>
       </div>
       <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
         <div class="w-full create-layout">
           <label class="form-label font-medium text-sm mb-1">{{
               $t("lang.DETAIL.DT39")
             }}</label>
           <Field v-model="dataDetail.boughtPlace" :disabled="!dataDetail.isFieldsEditable"
                  class="form-control pr-10" name="name" type="text" />
         </div>
       </div>
       <div class="flex flex-row justify-center gap-5 pl-5 pr-5 mt-3 form-create">
         <div class="w-full create-layout">
           <label class="form-label font-medium text-sm mb-1">{{
               $t("lang.DETAIL.DT40")
             }}</label>
           <Field v-model="dataDetail.boughtPrice" :disabled="!dataDetail.isFieldsEditable"
                  class="form-control pr-10" name="name" type="text" />
         </div>
       </div>
       <div class="flex flex-col justify-center pl-5 pr-5 mt-3 form-create"  v-if="dataDetail.buyerAttachment">
         <label for="regular-form-1" class="form-label font-medium text-sm mb-1">{{$t('lang.DETAIL.DT35')}}</label>
         <div class="flex items-center justify-center">
           <label
               class="flex flex-col items-center justify-center w-64 border-4 border-dashed border-gray-400 cursor-pointer"
               style="min-height: 150px; max-height: max-content">
             <div class="w-full">
               <img :src="imageBuyerData" alt="Image" class="w-64 h-64 object-cover cursor-default" />
             </div>
           </label>
         </div>
       </div>
     </div>
   </div>
 </div>
    <div class="mt-4 mx-auto text-center" v-if="dataDetail.status === 'SUCCESS'">
      <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
        {{ $t("lang.BUTTON.BT1") }}
      </button>
    </div>
  </Modal>

  <!-- Status Detail Modals -->
  <Modal :show="statusModalOpen" @hidden="closeStatusModal" size="modal-md">
    <template v-if="currentStatusModal === 'WAITING'">
      <ModalHeader class="pb-4 pt-1">
        <h2 class="intro-y text-xl font-medium mt-2">{{ $t('lang.STATUSMODAL.STTM1')  }}</h2>
      </ModalHeader>
      <ModalBody>
        <div class="grid grid-cols-2 gap-4">
          <div class="font-medium">{{ $t('lang.DETAIL.DT19')  }}</div>
          <div>{{ dataDetail.requestUsername }}</div>

          <div class="font-medium">{{ $t('lang.STATUSMODAL_DETAIL.STTMDT1') }}</div>
          <div>{{ formatDate(dataDetail.createAt) }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT33') }}</div>
          <div>{{ dataDetail.requestProductName }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT7')}}</div>
          <div>{{ DisplayReason }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT13')  }}</div>
          <div>{{ dataDetail.requestReasonDetail || '-' }}</div>
        </div>
      </ModalBody>
    </template>

    <template v-else-if="currentStatusModal === 'PROCESSING'">
      <ModalHeader class="pb-4 pt-1">
        <h2 class="intro-y text-xl font-medium mt-2">{{ $t('lang.STATUSMODAL.STTM2') }}</h2>
      </ModalHeader>
      <ModalBody>
        <div class="grid grid-cols-2 gap-4">
          <div class="font-medium">{{ $t('lang.DETAIL.DT6') }}</div>
          <div>{{ dataDetail.approverUsername }}</div>

          <div class="font-medium">{{ $t('lang.STATUSMODAL_DETAIL.STTMDT2')}}</div>
          <div>{{ formatDate(dataDetail.approvedAt) }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT1') }}</div>
          <div>{{ $t('lang.STATUS.STT2') || 'Đã duyệt' }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT33') }}</div>
          <div>{{ dataDetail.requestProductName }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT11')}}</div>
          <div>{{ dataDetail.amount }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT12') }}</div>
          <div>{{ formattedEstimatePrice }}</div>
        </div>
      </ModalBody>
    </template>

    <template v-else-if="currentStatusModal === 'ORDERING'">
      <ModalHeader class="pb-4 pt-1">
        <h2 class="intro-y text-xl font-medium mt-2">{{ $t('lang.STATUSMODAL.STTM3')}}</h2>
      </ModalHeader>
      <ModalBody>
        <div class="grid grid-cols-2 gap-4">
          <div class="font-medium">{{ $t('lang.DETAIL.DT8')}}</div>
          <div>{{ dataDetail.buyerUsername }}</div>

          <div class="font-medium">{{ $t('lang.STATUSMODAL_DETAIL.STTMDT3')}}</div>
          <div>{{ formatDate(dataDetail.updatedAt) }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT33')}}</div>
          <div>{{ dataDetail.requestProductName }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT11')}}</div>
          <div>{{ dataDetail.amount }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT12')}}</div>
          <div>{{ formattedEstimatePrice }}</div>
        </div>
      </ModalBody>
    </template>

    <template v-else-if="currentStatusModal === 'SUCCESS'">
      <ModalHeader class="pb-4 pt-1">
        <h2 class="intro-y text-xl font-medium mt-2">{{ $t('lang.STATUSMODAL.STTM4') }}</h2>
      </ModalHeader>
      <ModalBody>
        <div class="grid grid-cols-2 gap-4">
          <div class="font-medium">{{ $t('lang.DETAIL.DT39')}}</div>
          <div>{{ dataDetail.boughtPlace }}</div>

          <div class="font-medium">{{ $t('lang.STATUSMODAL_DETAIL.STTMDT4')}}</div>
          <div>{{ formatDate(dataDetail.updatedAt) }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT19')}}</div>
          <div>{{ dataDetail.requestUsername }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT33')}}</div>
          <div>{{ dataDetail.boughtProductName }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT38')}}</div>
          <div>{{ dataDetail.boughtAmount }}</div>

          <div class="font-medium">{{ $t('lang.DETAIL.DT40')}}</div>
          <div>{{ formatCurrency(dataDetail.boughtPrice) }}</div>
        </div>
      </ModalBody>
    </template>

    <ModalFooter>
      <button class="btn w-24 mr-2" type="button" @click="closeStatusModal">
        {{ $t("lang.BUTTON.BT1")}}
      </button>
    </ModalFooter>
  </Modal>
</template>

<script>
import DatePicker from "@vuepic/vue-datepicker";
import VueDatePicker from "@vuepic/vue-datepicker";
import { Field, Form } from "vee-validate";
import { Modal } from "@/global-components/modal";
import moment from "moment/moment";
import { RequestStatus } from "@/common/StatusEnum";
import { mapGetters } from "vuex";
import { RequestReason } from "@/common/RequestReasonEnum";
import { getAttachment } from "@/api/Attachments";
import ReasonApi from "@/api/ReasonApi.js";
import Tooltip from 'primevue/tooltip';

export default {
  data() {
    return {
      imageUserData: null,
      imageBuyerData: null,
      reasonList: [],
      parentReasons: [],
      childReasons: [],
      parentReasonId: "",
      rejectReason: "",
      statusModalOpen: false,
      currentStatusModal: null,
    };
  },
  props: {
    LoginValue: String,
  },
  name: "DetailRequest",
  components: { VueDatePicker, Modal, Field, Form, DatePicker,Tooltip  },
  created() {
    this.$store.dispatch("users/fetchListBuyers");
    this.fetchListReason()
  },
  computed: {
    colorClassMap() {
      return {
        WAITING: { bg: 'bg-yellow-500', text: 'text-yellow-500', border: 'border-yellow-500' },
        PROCESSING: { bg: 'bg-blue-500', text: 'text-blue-500', border: 'border-blue-500' },
        ORDERING: { bg: 'bg-orange-500', text: 'text-orange-500', border: 'border-orange-500' },
        SUCCESS: { bg: 'bg-green-500', text: 'text-green-500', border: 'border-green-500' },
        REJECTED: { bg: 'bg-red-500', text: 'text-red-500', border: 'border-red-500' }
      }
    },
    statusSteps() {
      return [
        {
          status: 'WAITING',
          label: 'lang.STATUS.STT1',
          colorClass: 'yellow',
          tooltip: 'lang.STATUSDESCRIPTION.STTD1',
          icon: 'fa fa-clock'
        },
        {
          status: 'PROCESSING',
          label: 'lang.STATUS.STT2',
          colorClass: 'blue',
          tooltip: 'lang.STATUSDESCRIPTION.STTD2',
          icon: 'fa fa-cog'
        },
        {
          status: 'ORDERING',
          label: 'lang.STATUS.STT5',
          colorClass: 'orange',
          tooltip: 'lang.STATUSDESCRIPTION.STTD5',
          icon: 'fa fa-shopping-cart'
        },
        {
          status: 'SUCCESS',
          label: 'lang.STATUS.STT3',
          colorClass: 'green',
          tooltip: 'lang.STATUSDESCRIPTION.STTD3',
          icon: 'fa fa-check'
        },
        {
          status: 'REJECTED',
          label: 'lang.STATUS.STT4',
          colorClass: 'red',
          tooltip: 'lang.STATUSDESCRIPTION.STTD4',
          icon: 'fa fa-times'
        }
      ];
    },
    buyerRejectReason() {
      return this.dataDetail.buyerRejectReason;
    },
    approverRejectReason() {
      return this.dataDetail.approverRejectReason;
    },
    rejectReasonComputed() {
      return this.approverRejectReason || this.buyerRejectReason;
    },
    LoginValue() {
      return this.getUserInfo.authorities[0];
    },
    privilegesBuy() {
      return ["buy"].some((ele) =>
        this.userInfo.privileges.includes(ele.toLowerCase())
      );
    },
    privilegesApprove() {
      return ["approve"].some((ele) =>
        this.userInfo.privileges.includes(ele.toLowerCase())
      );
    },
    ...mapGetters("auth", ["getUserInfo"]),
    DisplayName() {
      return this.$t(`${RequestStatus[this.dataDetail.status]}`) || this.$t(`${this.dataDetail.status}`)
    },

    ...mapGetters({
      isDetailModalOpen: "requestList/getDetailModalState",
      dataDetail: "requestList/getDetails",
      userInfo: "auth/getUserInfo",
    }),

    displayDescription() {
      const description = this.dataDetail.description;
      let replacedText = '';
      if (description && (description.includes("https") || description.includes("http"))) {
        const regex = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g;

        replacedText = description.replace(regex, function (match) {
          return '<a style="text-decoration: underline;color: blue;" href="' + match + '">' + match + '</a>';
        });
      }
      return replacedText ? replacedText : description;
    },
    displayLabelApprover() {
      if (this.dataDetail.status === "REJECTED" && !this.dataDetail.buyerUsername) {
        return this.$t(`lang.DETAIL.DT36`)
      } else {
        return this.$t(`lang.DETAIL.DT6`)
      }
    },
    displayLabelBuyer() {
      if (this.dataDetail.status === "REJECTED" && this.dataDetail.buyerUsername) {
        return this.$t(`lang.DETAIL.DT36`)
      } else {
        return this.$t(`lang.DETAIL.DT8`)
      }
    },

    DisplayReason() {
      const match = this.reasonList.find(item => item.id == this.dataDetail.requestReason);
      return match ? match.reason : '';    },
    formattedEstimatePrice() {
      const formatter = new Intl.NumberFormat("it-IT", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(this.dataDetail.estimatePrice || 0);
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    isChildReasonDisabled() {
      return !this.childReasons || this.childReasons.length === 0;
    },
    filteredStatusSteps() {
      return this.statusSteps.filter(step => step.status !== 'REJECTED');
    },
    rejectedStep() {
      return this.dataDetail.status === 'REJECTED' ? this.dataDetail.statusReject : null;
    },
  },
  methods: {

    isStepPassedOrRejected(stepStatus) {
      if (this.dataDetail.status === 'REJECTED') {
        const rejectIndex = this.statusSteps.findIndex(s => s.status === this.dataDetail.statusReject);
        const stepIndex = this.statusSteps.findIndex(s => s.status === stepStatus);
        return stepIndex <= rejectIndex;
      } else {
        const currentIndex = this.statusSteps.findIndex(s => s.status === this.dataDetail.status);
        const stepIndex = this.statusSteps.findIndex(s => s.status === stepStatus);
        return stepIndex <= currentIndex;
      }
    },
    isStepActive(stepStatus) {
      return this.dataDetail.status === stepStatus;
    },
    async getImageUserData(userAttachmentId) {
      if (userAttachmentId) {
        const imgUserRes = await getAttachment(userAttachmentId);
        this.imageUserData = URL.createObjectURL(imgUserRes.data);
      } else {
        this.imageUserData = null;
      }
    },

    async getImageBuyerData(buyerAttachmentId) {
      if (buyerAttachmentId) {
        const imageBuyerRes = await getAttachment(buyerAttachmentId);
        this.imageBuyerData = URL.createObjectURL(imageBuyerRes.data);
      } else {
        this.imageBuyerData = null;
      }
    },
    parseInputPrice(value) {
      const numericValue = parseFloat(value.replace(/[^\d]/g, ""));
      return isNaN(numericValue) ? 0 : numericValue;
    },
    formatDate(date) {
      if (!date) return "";
      return moment(date).format("DD-MM-YYYY");
    },
    closeModal() {
      this.$store.dispatch("requestList/closeDetailModal");
    },
    openCompleteModal() {
      this.$store.dispatch("requestList/openCompleteModal");
    },
    openRejectModal() {
      this.$store.dispatch("requestList/openRejectModal");
    },
    openConfirmModal() {
      this.$store.dispatch("requestList/openConfirmModal", {
        buyerUserId: this.dataDetail.buyerUserId || null
      });
    },
    openCancelModal() {
      this.$store.dispatch("requestList/openCancelModal")
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
          // If no child reasons, use the parent reason
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
          return null; // It's already a parent
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
    
    async fetchListReason(){
      let params = ""
      const res = await ReasonApi.getReasonAssign(params)
      this.reasonList = res.data
      this.parentReasons = res.data.filter(item => item.parent === null);
      
      this.initializeReasonSelection();
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
    linkify(text) {
      if (!text) return '';
      const urlRegex = /(https?:\/\/[^\s]+)/g;
      return text.replace(urlRegex, function(url) {
        return `<a href="${url}" target="_blank" rel="noopener noreferrer" title="${url}" style="color: #1976d2; text-decoration: underline;">${url}</a>`;
      });
    },
    getStatusIcon(status) {
      const step = this.statusSteps.find(s => s.status === status);
      return step ? step.icon : '';
    },
    openStatusModal(status) {
      this.currentStatusModal = status;
      this.statusModalOpen = true;
    },
    closeStatusModal() {
      this.statusModalOpen = false;
      this.currentStatusModal = null;
    },
    formatCurrency(price) {
      if (!price) return "-";
      const formatter = new Intl.NumberFormat("it-IT", {
        style: "currency",
        currency: "VND",
      });
      const priceString = formatter.format(price);
      return priceString.replace(/\D00(?=\D*$)/, "");
    },
    isStepSuccessed(stepStatus) {
      if (this.dataDetail.status === 'REJECTED') {
        const rejectIndex = this.statusSteps.findIndex(s => s.status === this.dataDetail.statusReject);
        const stepIndex = this.statusSteps.findIndex(s => s.status === stepStatus);
        return stepIndex < rejectIndex;
      }
      const currentIndex = this.statusSteps.findIndex(s => s.status === this.dataDetail.status);
      const stepIndex = this.statusSteps.findIndex(s => s.status === stepStatus);
      return stepIndex < currentIndex;
    },
  },
  watch: {
    rejectReasonComputed(newValue) {
      this.rejectReason = newValue;
    },
    async dataDetail() {
      await this.getImageUserData(this.dataDetail.userAttachment?.id);
      await this.getImageBuyerData(this.dataDetail.buyerAttachment?.id);
      if (this.reasonList.length > 0) {
        this.initializeReasonSelection();
      }
    },
    reasonList() {
      this.initializeReasonSelection();
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

.dp__disabled {
  background: rgb(var(--color-slate-100) / var(--tw-bg-opacity));
  cursor: not-allowed !important;
}

.default-class.scroll-view {
  min-height: 560px;
  max-height: 550px;
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
</style>

<style>
.text-area-scrollable {
  max-height: 250px;
}

.default-class.scroll-view {
  min-height: 560px;
  max-height: 550px;
}

.link-readonly {
  padding-left: 0.75rem;
  padding-right: 2.5rem;
  background-color: #f1f5f9;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  font-size: 1rem;
  color: #6b7280;
  min-height: 38px;
  height: 40px;
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}
.link-text {
  white-space: nowrap;
  overflow: hidden;
  /* text-overflow: ellipsis; */
  display: block;
  width: 100%;
  color: #1976d2;
  text-decoration: underline;
}
</style>
