<template>
    <Modal :show="isOpen" size="modal-xl" @hidden="closeModal">
        <div class="flex">
            <ModalHeader class="flex flex-col border-none w-full p-2">
                <h2 class="intro-y text-lg font-medium">Thông tin phân quyền</h2>
            </ModalHeader>
            <button class="mx-auto flex items-center mr-3 pt-2 pr-3 justify-center h-10 w-10" @click="closeModal">
                <img class="w-4 h-4 mr-3 hover:transform hover:rotate-180 transition" src="../../../assets/images/closeModal.png"/>
            </button>
        </div>
	    <img class="w-[95%] h-[2px] flex m-auto" src="../../../assets/images/separate.png" alt="">

        <div class="flex flex-col mt-7">
            <div class="">
                <div class="flex flex-row justify-center gap-5 form-create px-8 w-full create-layout">
                    <!-- Tên mục đích -->
                    <div class="w-1/3 create-layout">
                        <label for="regular-form-1" class="text-sm text-[#505050] font-medium w-[30%]">Loại phân quyền</label>
                        <Field
                            id="tabulator-html-filter-value"
                            type="text"
                            class="form-control"
                            placeholder="Nhập tên mục đích"
                        />
                    </div>
                    <!-- Số lượng -->
                    <div class="w-1/3 create-layout">
                        <label for="regular-form-1" class="text-sm text-[#505050] font-medium w-[30%]">Số lượng</label>
                        <Field
                            id="tabulator-html-filter-value"
                            type="text"
                            class="form-control"
                            disabled
                        />
                    </div>
                    <!-- Số lượng tối đa -->
                    <div class="w-1/3 create-layout">
                        <label for="regular-form-1" class="text-sm text-[#505050] font-medium w-[30%]">Số lượng tối đa</label>
                        <Field
                            id="tabulator-html-filter-value"
                            type="text"
                            class="form-control"
                            disabled
                        />
                    </div>
                </div>
                <div class="flex flex-row gap-5 form-create px-8 mt-2">
                    <!-- Ngày tạo -->
                    <div class="w-1/3 create-layout">
                        <label for="regular-form-1" class="text-sm text-[#505050] font-medium w-[30%]">Ngày tạo</label>
                        <Field
                            id="tabulator-html-filter-value"
                            type="text"
                            class="form-control"
                            disabled
                        />
                    </div>
                    <!-- Người tạo -->
                    <div class="w-1/3 create-layout">
                        <label for="regular-form-1" class="text-sm text-[#505050] font-medium w-[30%]">Người tạo</label>
                        <Field
                            id="tabulator-html-filter-value"
                            type="text"
                            class="form-control"
                            disabled
                        />
                    </div>
                    <!-- Người tạo -->
                    <div class="w-1/3 create-layout"></div>
                </div>
                <img class="w-[95%] h-[2px] flex m-auto my-5" src="../../../assets/images/separate.png" alt="">

                <div class="flex flex-row gap-5 form-create px-8 mt-2">
                    <div class="w-1/3 create-layout">
                        <label class="w-full text-sm font-medium">Tìm kiếm</label>
                        <div class="flex justify-between">
                            <div class="flex relative justify-end w-10/12">
                            <input
                                id="tabulator-html-filter-value"
                                type="text"
                                class="form-control h-10"
                                placeholder="Tìm kiếm"
                            />
                            </div>
                            <button class="btn bg-[#EA5C0F] shadow-md h-10">
                                <i class="fa-solid fa-delete-left w-4 h-4 text-white"></i>
                            </button>
                        </div>
                    </div>

                    <div class="w-1/3 create-layout">

                    </div>
                    <div class="w-1/3 create-layout">
                        <label class="w-full text-sm font-medium">Thêm nhân viên</label>
                        <div class="flex justify-between">
                            <div class="flex relative justify-end w-10/12">
                                <input
                                    id="tabulator-html-filter-value"
                                    type="text"
                                    class="form-control h-10"
                                    placeholder="Chọn nhân viên để thêm"
                                />
                            </div>
                            <button class="btn bg-[#EA5C0F] shadow-md h-10">
                                <i class="fa-solid fa-user-plus w-4 h-4 text-white"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="intro-y col-span-12 overflow-auto table-style mt-2 rounded-md mx-8">
                <table class="table">
                    <thead>
                        <tr class="bg-[#999]">
                            <th class="whitespace-nowrap text-[#F6F6F6] text-center">STT</th>
                            <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Tên nhân viên</th>
                            <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Bộ phận</th>
                            <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Ngày tham gia</th>
                            <th class="whitespace-nowrap border-l text-[#F6F6F6]"></th>
                        </tr>
                    </thead>
                    <tbody class="p-5">
                        <tr class="intro-x cursor-pointer line-table" @click="openModalDetail" >
                            <td class="text-center">1</td>
                            <td class="">Lê Duy Linh</td>
                            <td class="">Tổng vụ</td>
                            <td class="">22/1/2024</td>
                            <td class="text-center text-[#E96C7B]">
                                <a @click.stop="openModalDelete" class="flex items-center" href="javascript:;">
                                    <i class="fa-solid fa-trash-can"></i>
                                </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="flex items-center mb-7 justify-center mt-4">
                <button class="btn w-24 mr-4 mb-2" type="button" @click="closeModal">
                    Hủy
                </button>
                <button class="bg-[#EA5C0F] btn text-white w-24 mr-4 mb-2" type="submit">
                    Lưu
                </button>
            </div>
        </div>
    </Modal>
</template>

<script>
import { Modal } from "@/global-components/modal";
import { Field } from "vee-validate";
export default {
    name: 'ModalDetail',
    components: { Modal, Field },
    props: ["isOpen", "onClose"],
    methods: {
        closeModal() {
            this.onClose()
        }
    }
}
</script>

<style scoped>

</style>
