<template>
    <Modal :show="isOpen" size="modal-lg" @hidden="closeModal">
        <div class="flex">
            <ModalHeader class="flex flex-col border-none w-full p-2">
                <h2 class="intro-y text-lg font-medium">Xác nhận</h2>
            </ModalHeader>
            <button class="mx-auto flex items-center mr-3 pt-2 pr-3 justify-center h-10 w-10" @click="closeModal">
                <img class="w-4 h-4 mr-3 hover:transform hover:rotate-180 transition" src="../../../assets/images/closeModal.png"/>
            </button>
        </div>
	    <img class="w-[95%] h-[2px] flex m-auto" src="../../../assets/images/separate.png" alt="">

        <div class="flex flex-col w-1/2 m-auto mt-7">
            <div class="">
                <div class="flex flex-col items-center">
                    <label class="text-[#505050] text-base font-medium">Mục này sẽ bị xóa</label>
                    <label class="text-[#505050] text-base font-medium">Bạn chắc chắn muốn xóa?</label>
                </div>
            </div>

            <div class="flex items-center mb-7 justify-center mt-4">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                    Hủy
                </button>
                <button class="bg-[#C62D3F] btn text-white w-24 mr-4 mb-2" type="submit" @click="onSubmit">
                    Xóa
                </button>
            </div>
        </div>
    </Modal>
</template>

<script>
import { Modal } from "@/global-components/modal";
import ReasonApi from "@/api/ReasonApi.js"
import Swal from "sweetalert2";
export default {
    name: 'ModalDelete',
    components: { Modal },
    props: ["isOpen", "onClose","idReason"],
    methods: {
        closeModal() {
            this.onClose()
        },
        async onSubmit(){
            try {
                const res = await ReasonApi.deleteReasonAssign(this.idReason)
                if (res.status === 200) {
                Swal.fire({
                    icon: "success",
                    title: "Xóa thành công",
                    confirmButtonColor: "#3085d6",
                    confirmButtonText: this.$t("lang.BUTTON.BT7"),
                    timerProgressBar: true,
                    timer: 1000,
                });
                }
            } catch {
                Swal.fire({
                icon: "error",
                title: "Xóa thất bại",
                confirmButtonColor: "#3085d6",
                confirmButtonText: this.$t("lang.BUTTON.BT7"),
                timerProgressBar: true,
                timer: 1000,
                });
            }
            this.onClose()
            this.$emit('list-updated');
        },
    }
}
</script>

<style scoped>

</style>
