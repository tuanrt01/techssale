<template>
    <Modal :show="isOpen" size="modal-lg" @hidden="closeModal">
        <div class="flex">
            <ModalHeader class="flex flex-col border-none w-full p-2">
                <h2 class="intro-y text-lg font-medium">{{ $t('lang.PURPOSES.PP10') }}</h2>
            </ModalHeader>
            <button class="mx-auto flex items-center mr-3 pt-2 pr-3 justify-center h-10 w-10" @click="closeModal">
                <img class="w-4 h-4 mr-3 hover:transform hover:rotate-180 transition" src="../../../assets/images/closeModal.png"/>
            </button>
        </div>
	    <img class="w-[95%] h-[2px] flex m-auto" src="../../../assets/images/separate.png" alt="">

        <div class="flex flex-col mt-7">
            <div class="flex flex-row gap-4 px-8 w-full">
                <!-- Tên mục đích -->
                <div class="w-1/2">
                    <label class="text-sm text-[#505050] font-medium">{{ $t('lang.PURPOSES.PP7') }}</label>
                    <label class="text-[#E96C7B] text-base font-medium">*</label>
                    <input
                        id="tabulator-html-filter-value"
                        type="text"
                        class="form-control"
                        placeholder="Nhập tên mục đích"
                        v-model = "RequestReason.reason"
                        @input="input"
                    />
                    <div class="text-red-500 text-sm mt-1" v-if="errors.reason">
                        {{ $t('lang.ERROR.ER22') }}
                    </div>
                </div>
                <!-- Người mua chính -->
                <div class="w-1/2">
                    <label class="text-[#505050] text-base font-medium">{{ $t('lang.PURPOSES.PP4') }}</label>
                    <multiselect 
                        v-model="mainBuyer" 
                        placeholder="Chọn người mua chính"
                        selectLabel="" 
                        deselectLabel="" 
                        selectedLabel=""
                        :searchable="true" 
                        :close-on-select="true"
                        :show-labels="false"
                        label="name" 
                        track-by="id" 
                        :options="filteredMainBuyerList" 
                        :multiple="false"
                        class="custom-multiselect">
                    </multiselect>
                </div>
            </div>
            <div class="flex flex-row gap-4 px-8 w-full mt-2">
                <!-- Ngày tạo -->
                <div class="flex-1">
                    <label class="text-sm text-[#505050] font-medium">{{ $t('lang.PURPOSES.PP2') }}</label>
                    <input
                        id="tabulator-html-filter-value"
                        type="text"
                        class="form-control"
                        v-model= "RequestReason.createdAt"
                        disabled
                    />
                </div>
                <!-- Người tạo -->
                <div class="flex-1">
                    <label class="text-sm text-[#505050] font-medium">{{ $t('lang.PURPOSES.PP3') }}</label>
                    <input
                        id="tabulator-html-filter-value"
                        type="text"
                        class="form-control"
                        v-model = "RequestReason.createdBy.name"
                        disabled
                    />
                </div>
            </div>
            <div class="flex items-center mb-7 justify-center mt-4">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                    {{ $t('lang.BUTTON.BT1') }}
                </button>
                <button class="bg-[#EA5C0F] btn text-white w-24 mr-4 mb-2" type="submit" @click="onSubmit">
                    {{ $t('lang.BUTTON.BT13') }}
                </button>
            </div>
        </div>
    </Modal>
</template>

<script>
import { Modal } from "@/global-components/modal";
import ReasonApi from "@/api/ReasonApi.js"
import Swal from "sweetalert2";
import Multiselect from 'vue-multiselect';
import { getUserBuyer } from "@/api/UserAssign";
export default {
    name: 'ModalDetail',
    components: { Modal, Multiselect },
    props: ["isOpen", "onClose", "idReason"],
    data() {
        return {
            RequestReason: {
                reason : "",
                createdAt: "",
                createdBy : "",
                mainBuyer: "",
                buyer: []
            },
            errors: {
                reason : false
            },
            mainBuyer: null,
            secondaryBuyers: [],
            userBuyerList: []
        }
    },
    computed: {
        filteredMainBuyerList() {
            const secondaryIds = this.secondaryBuyers.map(b => b.id);
            return this.userBuyerList.filter(user => !secondaryIds.includes(user.id));
        },
        filteredSecondaryBuyerList() {
            return this.userBuyerList.filter(user => !this.mainBuyer || user.id !== this.mainBuyer.id);
        }
    },
    methods: {
        closeModal() {
            this.onClose()
        },
        async detailReason(){
            try {
                const res = await ReasonApi.getDetailReasonAssign(this.idReason)
                this.RequestReason = res.data
                this.mainBuyer = this.userBuyerList.find(u => u.id === res.data.mainBuyer) || null;
                this.secondaryBuyers = this.userBuyerList.filter(u => res.data.buyer && res.data.buyer.includes(u.id));
            } catch (error) {
                console.log(error)
            } 
        },
        async fetchUserBuyers() {
            try {
                const response = await getUserBuyer();
                if (response && response.data) {
                    this.userBuyerList = response.data;
                }
            } catch (error) {
                console.error("Error fetching buyers:", error);
            }
        },
        input(){
            if (this.RequestReason.reason !== ""){
                this.errors.reason = false
                return
            } else {
                this.errors.reason = true
                return
            }
        },
        async onSubmit(){
            if(this.RequestReason.reason.trim() === ""){
                this.errors.reason = true
                return
            }
            this.errors.reason = false
            this.RequestReason.mainBuyer = this.mainBuyer ? this.mainBuyer.id : "";
            this.RequestReason.buyer = this.secondaryBuyers.map(b => b.id);
            try {
                const res = await ReasonApi.updateReasonAssign(this.RequestReason);
                if (res.status === 200) {
                Swal.fire({
                    icon: "success",
                    title: this.$t('lang.NOTY.NT13'),
                    confirmButtonColor: "#3085d6",
                    confirmButtonText: this.$t("lang.BUTTON.BT7"),
                    timerProgressBar: true,
                    timer: 1000,
                });
                }
            } catch {
                Swal.fire({
                icon: "error",
                title: this.$t('lang.NOTY.NT14'),
                confirmButtonColor: "#3085d6",
                confirmButtonText: this.$t("lang.BUTTON.BT7"),
                timerProgressBar: true,
                timer: 1000,
                });
            }
            this.onClose()
            this.$emit('list-updated');
        },
    },
    watch: {
        isOpen(newVal) {
            if (newVal) {
                this.fetchUserBuyers().then(() => {
                    this.detailReason();
                });
            }
        },
    },
}
</script>

<style scoped>

</style>
