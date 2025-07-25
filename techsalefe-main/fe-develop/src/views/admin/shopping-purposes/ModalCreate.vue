<template>
    <Modal :show="isOpen" size="modal-lg" @hidden="closeModal">
        <div class="flex">
            <ModalHeader class="flex flex-col border-none w-full p-2">
                <h2 class="intro-y text-lg font-medium">{{ parentId ? $t('lang.PURPOSES.PP5') + ' ' + parentName : $t('lang.PURPOSES.PP6') }}</h2>
            </ModalHeader>
            <button class="mx-auto flex items-center mr-3 pt-2 pr-3 justify-center h-10 w-10" @click="closeModal">
                <img class="w-4 h-4 mr-3 hover:transform hover:rotate-180 transition" src="../../../assets/images/closeModal.png"/>
            </button>
        </div>
	    <img class="w-[95%] h-[2px] flex m-auto" src="../../../assets/images/separate.png" alt="">

        <div class="flex flex-col mt-7">
            <div class="flex flex-row gap-4 px-8 w-full">
                <div class="w-1/2">
                    <label class="text-[#505050] text-base font-medium">{{ $t('lang.PURPOSES.PP7') }}</label>
                    <label class="text-[#E96C7B] text-base font-medium">*</label>
                    <input
                        id="tabulator-html-filter-value"
                        type="text"
                        class="form-control"
                        placeholder="Nhập tên mục đích"
                        v-model="RequestReason.reason"
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

            <div class="flex items-center mb-7 justify-center mt-4">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                    {{ $t('lang.BUTTON.BT1') }}
                </button>
                <button class="bg-[#EA5C0F] btn text-white w-24 mb-2" type="submit" @click="onSubmit">
                    {{ $t('lang.BUTTON.BT14') }}
                </button>
            </div>
        </div>
    </Modal>
</template>

<script>
import { Modal } from "@/global-components/modal";
import ReasonApi from "@/api/ReasonApi.js"
import Swal from "sweetalert2";
import { getUserBuyer } from "@/api/UserAssign";
import Multiselect from 'vue-multiselect';

export default {
    name: 'ModalCreate',
    components: { Modal, Multiselect },
    props: {
        isOpen: Boolean,
        onClose: Function,
        parentId: {
            type: Number, 
            default: null
        },
        parentName: {
            type: String,
            default: ""
        }
    },
    data() {
        return {
            RequestReason: {
                reason: "",
                parent: null,
                mainBuyer: "",
                buyer: []
            },
            errors: {
                reason: false
            },
            mainBuyer: null,
            secondaryBuyers: [],
            userBuyerList: []
        }
    },
    watch: {
        parentId: {
            immediate: true,
            handler(newVal) {
                this.RequestReason.parent = newVal;
            }
        },
        isOpen(val) {
            if (val) {
                this.RequestReason.reason = "";
                this.RequestReason.parent = this.parentId;
                this.mainBuyer = null;
                this.secondaryBuyers = [];
                this.errors.reason = false;
                this.fetchUserBuyers();
            }
        },
        mainBuyer(val) {
            if (val) {
                this.RequestReason.mainBuyer = val.id;
            } else {
                this.RequestReason.mainBuyer = "";
            }
        },
        secondaryBuyers: {
            deep: true,
            handler(val) {
                if (val && val.length > 0) {
                    this.RequestReason.buyer = val.map(buyer => buyer.id);
                } else {
                    this.RequestReason.buyer = [];
                }
            }
        }
    },
    methods: {
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
        closeModal() {
            this.RequestReason.reason = "";
            this.mainBuyer = null;
            this.secondaryBuyers = [];
            this.onClose();
            this.errors.reason = false;
        },
        async onSubmit(){
            if(this.RequestReason.reason.trim() === ""){
                this.errors.reason = true;
                return;
            }
            this.errors.reason = false;
            try {
                const res = await ReasonApi.createReasonAssign(this.RequestReason);
                if (res.status === 200) {
                    this.RequestReason.reason = "";
                    this.mainBuyer = null;
                    this.secondaryBuyers = [];
                    this.onClose();
                    await Swal.fire({
                        icon: "success",
                        title: this.parentId ? this.$t('lang.PURPOSES.PP8') : this.$t('lang.PURPOSES.PP9'),
                        confirmButtonColor: "#3085d6",
                        confirmButtonText: this.$t("lang.BUTTON.BT7"),
                        timerProgressBar: true,
                        timer: 1000,
                    });
                    this.$emit('list-updated');
                }
            } catch {
                await Swal.fire({
                    icon: "error",
                    title: "Thêm mới thất bại",
                    confirmButtonColor: "#3085d6",
                    confirmButtonText: this.$t("lang.BUTTON.BT7"),
                    timerProgressBar: true,
                    timer: 1000,
                });
                this.onClose();
            }
        },
        input(){
            if (this.RequestReason.reason !== ""){
                this.errors.reason = false;
                return;
            } else {
                this.errors.reason = true;
                return;
            }
        }
    },
    computed: {
        filteredMainBuyerList() {
            const secondaryIds = this.secondaryBuyers.map(b => b.id);
            return this.userBuyerList.filter(user => !secondaryIds.includes(user.id));
        },
    }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.css"></style>
<style>
.custom-multiselect {
  margin-bottom: 1rem;
}

.custom-multiselect .multiselect__tags {
  min-height: 40px;
  padding: 8px 40px 0 8px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  background: #fff;
}

.custom-multiselect.multiselect--active .multiselect__tags {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

.custom-multiselect .multiselect__tag {
  background: #5cb85c;
  margin-bottom: 3px;
}

.custom-multiselect .multiselect__tag-icon:after {
  color: white;
}

.custom-multiselect .multiselect__tag-icon:hover {
  background-color: #4cae4c;
}

.custom-multiselect .multiselect__option--highlight {
  background: #5cb85c;
}

.custom-multiselect .multiselect__option--highlight:after {
  background: #5cb85c;
}

.custom-multiselect .multiselect__option--selected.multiselect__option--highlight {
  background: #5cb85c;
  color: white;
}

.custom-multiselect .multiselect__placeholder {
  margin-bottom: 6px;
  padding-top: 0;
  padding-left: 5px;
  color: #6b7280;
}

.custom-multiselect .multiselect__input {
  padding-left: 5px;
}

.custom-multiselect .multiselect__select {
  height: 38px;
}

.custom-multiselect .multiselect__content-wrapper {
  border: 1px solid #e2e8f0;
  border-top: none;
}
</style>
