<template>
    <Modal :show="isOpen" class="test" size="modal-xl custom-modal-size" @hidden="closeModal(false)">
        <ModalHeader>
            <h2 class="intro-y text-lg font-medium">
                Các yêu cầu nằm trong mục đích mua sắm muốn xóa</h2>
            <button class="mx-auto flex items-center mr-3 justify-center h-10 w-10" @click="closeModal(false)">
                <svg aria-hidden="true" class="h-7 w-7 text-gray-600 hover:text-red-600" fill="none" stroke="currentColor"
                    viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
                </svg>
            </button>
        </ModalHeader>
        <div class="">
            <ModalBody>
                <!-- BEGIN: Data List -->
                <div class="justify-end ">
                    <div class="red">* Vui lòng thay đổi mục đích mua sắm của các yêu cầu bên dưới trước khi xóa </div>

                    <div class="cus flex w-1/2">
                        <div class="w-1/2">
                            <label for="regular-form-1" class="text-sm text-[#505050] font-medium">Mục đích mua sắm
                                mới</label>
                            <Field as="select" name="reason" class="form-select" v-model="reason"
                                @change="changeReason(reason)">
                                <option v-for="(item1, index) in result_reason" :key="index" :value="item1.id">{{
                                    item1.reason
                                }}
                                </option>
                            </Field>
                            <div class="invalid-feedback" v-if="errors">Vui lòng chọn mục đích mua sắm mới</div>
                        </div>
                    </div>
                </div>
                <div class="intro-y col-span-12 overflow-auto table-style style"
                    :class="{ 'scroll-view': listRequest.length > 10 }">
                    <table class="table">
                        <thead>
                            <tr class="bg-[#505050]">
                                <th class="whitespace-nowrap text-[#F6F6F6] text-center">STT</th>
                                <th class="whitespace-nowrap border-l text-[#F6F6F6]">Tên sản phẩm yêu cầu</th>
                                <th class="whitespace-nowrap border-l text-[#F6F6F6]">Người tạo yêu cầu</th>
                                <th class="whitespace-nowrap border-l text-[#F6F6F6]">Mục đích mua sắm</th>
                                <th class="whitespace-nowrap border-l text-[#F6F6F6]">Mục đích mua sắm mới</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="intro-x cursor line-table" v-for="(item, index) in listRequest" :key="index"
                                :class="{ 'bg-white': index % 2 === 0, 'bg-[#F6F6F6]': index % 2 !== 0 }">
                                <td class="text-center max-w-20 w-[20px]">{{ index + 1 }}
                                </td>
                                <td class="max-w-200 w-[220px] font-medium">{{ item.productName }}</td>
                                <td class="max-w-200 w-[220px] font-medium">{{ item.createBy }}</td>
                                <td class="max-w-200 w-[220px] font-medium">{{ findReason(item.requestReason) }}</td>
                                <td class="max-w-200 w-[220px] font-medium">
                                    <Field as="select" name="organization" class="form-select"
                                        :class="error[index] ? 'mgt' : ''" v-model="selectReason[item.id]"
                                        @change="changeReasonItem(selectReason[item.id], item.id)">
                                        <option v-for="(item1, index) in result_reason" :key="index" :value="item1.id">{{
                                            item1.reason
                                        }}
                                        </option>
                                    </Field>
                                    <div class="invalid-feedback" v-if="error[item.id]">Vui lòng chọn mục đích mua sắm mới
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="flex justify-center mt-5">
                    <button class="btn btn-secondary w-24 mr-4 mb-2" type="button" @click="closeModal(false)">
                        {{ $t('lang.BUTTON.BT3') }}
                    </button>
                    <button class="btn btn-primary w-24 mb-2" type="submit" @click="onSubmit">
                        Lưu
                    </button>
                </div>
            </ModalBody>
        </div>

    </Modal>

    <div id="success-create" class="toastify-content toastify-content-update hidden flex">
        <CheckCircleIcon class="text-success" />
        <div class="ml-1 mr-2 message-toast">
            <div class="text-slate-500">
                Đổi mục đích mua sắm thành công!
            </div>
        </div>
    </div>
    <div id="false-create" class="toastify-content toastify-content-update hidden flex">
        <CheckCircleIcon class="text-danger" />
        <div class="ml-1 mr-2 message-toast">
            <div class="text-slate-500">
                Có lỗi trong quá trình đổi!
            </div>
        </div>
    </div>
</template>
  
<script>
import Toastify from 'toastify-js'
import { Field, Form } from 'vee-validate'
import * as Yup from 'yup'
import { Modal } from "@/global-components/modal";
import ReasonApi from "@/api/ReasonApi.js";

export default {
    name: 'ModalConfim',
    components: { Modal, Field, Form },
    data() {
        return {
            result_reason: [],
            selectReason: {},
            errors: false,
            error: {},
            reason: "",
        }
    },
    props: ["isOpen", "onClose", "listRequest"],
    watch: {
        isOpen(newVal) {
            if (newVal) {
                this.listReason();
            }
        },
    },
    methods: {
        async listReason() {
            this.reason = this.listRequest[0].requestReason
            const res = await ReasonApi.getReasonAssign("")
            this.result_reason = res.data
            for (let i = 0; i < this.listRequest.length; i++) {
                const request = this.listRequest[i];
                this.selectReason[request.id] = request.requestReason;
            }
        },
        async onSubmit() {
            try {
                let flag = false;
                for (let i = 0; i < this.listRequest.length; i++) {
                    const request = this.listRequest[i];
                    if (this.selectReason[request.id] == request.requestReason){
                        flag = true
                        this.error[request.id] = true
                    }
                }
                if (flag == true){
                    return
                }
                const res = await ReasonApi.updateListRequestByReason(this.selectReason)
                Toastify({
                    node: dom('#success-create').clone().removeClass('hidden')[0],
                    duration: 3000,
                    newWindow: true,
                    close: true,
                    gravity: 'top',
                    position: 'right',
                    stopOnFocus: true
                }).showToast()
                this.closeModal(true);
            } catch (error) {
                console.warn(error);
                Toastify({
                    node: dom('#false-create').clone().removeClass('hidden')[0],
                    duration: 3000,
                    newWindow: true,
                    close: true,
                    gravity: 'top',
                    position: 'right',
                    stopOnFocus: true
                }).showToast()
                this.closeModal(false);
            }
        },
        changeReason(reason) {
            for (let i = 0; i < this.listRequest.length; i++) {
                const request = this.listRequest[i];
                this.selectReason[request.id] = reason;
            }
            if (reason == this.listRequest[0].requestReason) {
                this.errors = true
            } else {
                this.errors = false
                for (let i = 0; i < this.listRequest.length; i++) {
                    const request = this.listRequest[i];
                    this.error[request.id] = false;
                }
            }
        },
        changeReasonItem(reason, id) {
            if (reason == this.listRequest[0].requestReason) {
                this.error[id] = true
            } else {
                this.error[id] = false
            }
        },
        findReason(reason) {
            const item = this.result_reason.find(item => item.id == reason);
            return item ? item.reason : '';
        },
        closeModal(flag) {
            this.errors = false
            this.error = {}
            this.onClose();
            if (flag) {
                this.$emit('update-success');
            }
            this.selectReason ={}
        }
    },
}
</script>
<style scoped>
.invalid-feedback {
    color: red;
    margin-top: 8px;
}

.default-class.scroll-view {
    min-height: 660px;
    max-height: 650px;
}

.modal-header {
    padding-top: 0.25rem;
    padding-bottom: 0.25rem;
}

.overflow-y-auto.overflow-x-hidden.default-class.scroll-view {
    max-height: none;
}

.scroll-view {
    max-height: 400px;
    /* Điều chỉnh chiều cao tối đa của khu vực cuộn */
}

.cus {
    width: 40%;
    margin-left: 78.5%;
    margin-bottom: 5%;
}

.mgt {
    margin-top: 27px;
}

.red {
    font-size: 2.5vh;
    color: red;
    margin-left: 15%;
    margin-bottom: 2%;
}

.div-1 {
    margin-top: 30px;
}

.style {
    margin-top: -30px;
}

.ml-cus {
    margin-left: 8%;
}
</style>
  
<style>
@media (min-width: 960px) {
    .modal .modal-dialog.modal-xl {
        width: 900px !important;
    }
}

@media (min-width: 1200px) {
    .modal .modal-dialog.modal-xl {
        width: 1200px !important;
    }
}
</style>
  