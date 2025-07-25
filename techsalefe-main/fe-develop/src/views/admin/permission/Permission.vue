<template>
  <div class="p-3">
      <h1 class="font-bold text-2xl">Phân quyền</h1>
      <div class="flex justify-between mt-2">
          <div class="flex w-1/5 relative justify-end">
              <i class="fa-solid fa-magnifying-glass absolute mt-3 mr-4 text-[#999] w-5 h-5"></i>
              <input
                  id="tabulator-html-filter-value"
                  type="text"
                  class="form-control h-10"
                  placeholder="Tìm kiếm"
              />
          </div>
          <div class="flex items-center justify-center gap-[10px] text-base">
              <button class="bg-[#EA5C0F] rounded-md text-white btn w-28 mr-4 mb-2" @click="openModalDetail" type="submit">
                  <i class="fa-solid fa-folder-plus mr-2"></i>
                  Tạo mới
              </button>
          </div>
      </div>

      <div class="intro-y col-span-12 overflow-auto table-style mt-2 rounded-md">
          <table class="table">
              <thead>
                  <tr class="bg-[#999]">
                      <th class="whitespace-nowrap text-[#F6F6F6] text-center">STT</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Loại phân quyền</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Số lượng</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Số lượng tối đa</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Ngày tạo</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer">Người tạo</th>
                      <th class="whitespace-nowrap border-l text-[#F6F6F6]"></th>
                  </tr>
              </thead>
              <tbody class="p-5">
                  <tr class="intro-x cursor-pointer line-table" @click="openModalDetail" >
                      <td class="text-center max-w-50 w-[50px]">{{ (currentPage - 1) * perPage + index + 1 }}</td>
                      <td class="">Người dùng</td>
                      <td class="">20</td>
                      <td class="">40</td>
                      <td class="">22/1/2024</td>
                      <td class="">Lê Duy Linh</td>
                      <td class="">
                          <div class="flex gap-[20px] items-center justify-center">
                              <div class="text-[#7E7E7E]">
                                  <a @click="openModalDetail" class="flex items-center" href="javascript:;">
                                      <i class="fa-solid fa-pen-to-square"></i>
                                  </a>
                              </div>
                              <div class="text-[#E96C7B]">
                                  <a @click.stop="openModalDelete" class="flex items-center" href="javascript:;">
                                      <i class="fa-solid fa-trash-can"></i>
                                  </a>
                              </div>
                          </div>
                      </td>
                  </tr>
              </tbody>
          </table>
      </div>
      <ModalDetail :is-open="isModalDetailOpen" :on-close="closeModalDetail"/>
      <ModalDelete :is-open="isModalDeleteOpen" :on-close="closeModalDelete"/>
  </div>
</template>
<script>
import ModalDetail from "@/views/admin/permission/ModalDetail.vue";
import ModalDelete from "@/views/admin/permission/ModalDelete.vue";

export default {
  name: "Permission",
  components: {
      ModalDetail,
      ModalDelete
  },
  data() {
      return {
          isModalDeleteOpen: false,
          isModalDetailOpen: false,
      }
  },
  methods: {
      openModalDelete() {
          this.isModalDeleteOpen = true;
      },
      closeModalDelete() {
          this.isModalDeleteOpen = false;
      },
      openModalDetail() {
          this.isModalDetailOpen = true;
      },
      closeModalDetail() {
          this.isModalDetailOpen = false;
      },
  }
}
</script>

<style scoped>

</style>
