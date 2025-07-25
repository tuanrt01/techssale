<template>
    <div class="p-3">
        <h1 class="font-bold text-2xl">Mục đích mua hàng</h1>
        <div class="flex justify-between mt-2">
            <div class="flex w-1/3 relative">
                <i class="fa-solid fa-magnifying-glass absolute mt-3 ml-4 text-[#999] w-5 h-5"></i>
                <input
                    id="tabulator-html-filter-value"
                    type="text"
                    class="form-control h-10 pl-10"
                    placeholder="Tìm kiếm theo mục đích hoặc người tạo"
                    v-model="search"
                    @input="onSearch"
                />
                <button 
                    v-if="search" 
                    @click="resetSearch"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 cursor-pointer"
                >
                    <i class="fa-solid fa-times text-[#999]"></i>
                </button>
            </div>
            <div class="flex items-center justify-center gap-[10px] text-base">
                <button class="bg-[#EA5C0F] rounded-md text-white btn w-28 mr-4 mb-2" @click="openModalCreate()" type="submit">
                    <i class="fa-solid fa-folder-plus mr-2"></i>
                    Tạo mới
                </button>
            </div>
        </div>

        <div class="intro-y col-span-12 overflow-auto table-style mt-2 rounded-md table-container-1">
            <table class="table">
                <thead>
                    <tr class="bg-[#505050] header-fixed">
                        <th class="whitespace-nowrap text-[#F6F6F6] text-center">STT</th>
                        <th 
                            class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer"
                            @click="handleSortTable('reason')"
                        >
                            <div class="flex items-center">
                                {{ $t('lang.PURPOSES.PP1') }}
                            </div>
                        </th>
                        <th 
                            class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer"
                            @click="handleSortTable('createdAt')"
                        >
                            <div class="flex items-center">
                                {{ $t('lang.PURPOSES.PP2') }}
                                <span v-if="params.sort === 'createdAt'" 
                                    :class="params.direction === 'DESC' ? 'fa fa-caret-up ml-3' : 'fa fa-caret-down ml-3'">
                                </span>
                            </div>
                        </th>
                        <th 
                            class="whitespace-nowrap border-l text-[#F6F6F6] cursor-pointer"
                            @click="handleSortTable('createdBy')"
                        >
                            <div class="flex items-center">
                                {{ $t('lang.PURPOSES.PP3') }}
                                <span v-if="params.sort === 'createdBy'" 
                                    :class="params.direction === 'DESC' ? 'fa fa-caret-up ml-3' : 'fa fa-caret-down ml-3'">
                                </span>
                            </div>
                        </th>
                        <th class="whitespace-nowrap border-l text-[#F6F6F6] text-center">{{ $t('lang.PURPOSES.PP4') }}</th>
                        <th class="whitespace-nowrap border-l text-[#F6F6F6]"></th>
                    </tr>
                </thead>
                <tbody class="p-5">
                    <template v-if="sortedReasons && sortedReasons.length > 0">
                        <tr-node
                            v-for="(item, idx) in sortedReasons"
                            :key="item.id"
                            :item="item"
                            :index="idx + 1"
                            :level="0"
                            @edit-click="openModalDetail"
                            @delete-click="openModalDelete"
                            @create-click="openModalCreateChild"
                        >

                        </tr-node>
                    </template>
                    <tr class="intro-x cursor line-table" v-else>
                        <td class="text-center max-w-50" colspan="5">
                            {{ search ? 'Không tìm thấy kết quả' : 'Không có dữ liệu' }}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <ModalCreate :is-open="isModalCreateOpen" :on-close="closeModalCreate" v-on:list-updated="fetchReasonList" :parent-id="parentId" :parent-name="parentName"/>
        <ModalDelete :is-open="isModalDeleteOpen" :on-close="closeModalDelete" :id-reason="idReason" v-on:list-updated="fetchReasonList"/>
        <ModalDetail :is-open="isModalDetailOpen" :on-close="closeModalDetail" :id-reason="idReason" v-on:list-updated="fetchReasonList"/>
        <ModalReason :isOpen="flagReason" @close="flagReason = false" :listRequest="listRequest" @update-success="handleUpdateSuccess" />
    </div>
</template>
<script>
import ModalCreate from "@/views/admin/shopping-purposes/ModalCreate.vue";
import ModalDelete from "@/views/admin/shopping-purposes/ModalDelete.vue";
import ModalDetail from "@/views/admin/shopping-purposes/ModalDetail.vue";
import ReasonApi from "@/api/ReasonApi.js"
import ModalReason from "@/views/admin/shopping-purposes/ModalReason.vue";
import TrNode from "@/views/admin/shopping-purposes/TrNode.vue";

export default {
    name: "ShoppingPurposes",
    components: {
        ModalCreate,
        ModalDelete,
        ModalDetail,
        ModalReason,
        TrNode
    },
    data() {
        return {
            isModalCreateOpen: false,
            isModalDeleteOpen: false,
            isModalDetailOpen: false,
            RequestReason: [],
            idReason: "",
            search:"",
            listRequest: [],
            flagReason: false,
            params: {
                sort: "reason",
                direction: "ASC"
            },
            allReasonData: [],
            searchTimeout: null,
            parentId: null,
            parentName: ""
        }
    },
    computed: {
        sortedReasons() {
            if (!this.RequestReason || !this.RequestReason.length) return [];
            return this.RequestReason;
        }
    },
    methods: {
        removeVietnameseAccents(str) {
            if (!str) return '';
            return str
                .normalize('NFD')
                .replace(/[\u0300-\u036f]/g, '')
                .replace(/đ/g, 'd')
                .replace(/Đ/g, 'D');
        },
        onSearch() {
            clearTimeout(this.searchTimeout);
            this.searchTimeout = setTimeout(() => {
                this.applyFiltersAndSort();
            }, 300);
        },
        resetSearch() {
            this.search = "";
            this.applyFiltersAndSort();
        },
        applyFiltersAndSort() {
            if (!this.allReasonData || !this.allReasonData.length) {
                this.RequestReason = [];
                return;
            }

            let result = JSON.parse(JSON.stringify(this.allReasonData));

            if (this.search && this.search.trim() !== '') {
                const searchText = this.search.trim().toLowerCase();
                const searchTextNoAccent = this.removeVietnameseAccents(searchText);
                
                result = this.filterTreeData(result, searchText, searchTextNoAccent);
            }
            
            if (this.params.sort && this.params.direction) {
                result = this.sortTreeData(result, this.params.sort, this.params.direction);
            }
            
            this.RequestReason = result;
        },
        filterTreeData(treeData, searchText, searchTextNoAccent) {
            const hasMatchInBranch = (node) => {
                const nodeReason = node.reason?.toLowerCase() || '';
                const nodeReasonNoAccent = this.removeVietnameseAccents(nodeReason);
                
                const nodeCreator = node.createdBy?.name?.toLowerCase() || '';
                const nodeCreatorNoAccent = this.removeVietnameseAccents(nodeCreator);
                
                if (nodeReason.includes(searchText) || 
                    nodeReasonNoAccent.includes(searchTextNoAccent) ||
                    nodeCreator.includes(searchText) ||
                    nodeCreatorNoAccent.includes(searchTextNoAccent)) {
                    return true;
                }
                
                if (node.children && node.children.length) {
                    return node.children.some(child => hasMatchInBranch(child));
                }
                
                return false;
            };

            const filterNode = (node) => {
                const newNode = { ...node };
                
                if (hasMatchInBranch(node)) {
                    if (node.children && node.children.length) {
                        newNode.children = node.children
                            .map(child => {
                                const filteredChild = filterNode(child);
                                if (filteredChild) {
                                    filteredChild.expanded = true;
                                }
                                return filteredChild;
                            })
                            .filter(child => child !== null);
                    }
                    
                    newNode.expanded = true;
                    
                    return newNode;
                }
                
                return null;
            };

            const result = treeData
                .map(node => filterNode(node))
                .filter(node => node !== null);
            
            return result;
        },
        sortTreeData(treeData, sortField, sortDirection) {
            const getSortValue = (node, field) => {
                switch(field) {
                    case 'reason':
                        return node.reason || '';
                    case 'createdAt':
                        return new Date(node.createdAt || 0);
                    case 'createdBy':
                        return node.createdBy?.name || '';
                    default:
                        return node.id || 0;
                }
            };
            
            const sortedData = [...treeData].sort((a, b) => {
                const valueA = getSortValue(a, sortField);
                const valueB = getSortValue(b, sortField);
                
                if (typeof valueA === 'string' && typeof valueB === 'string') {
                    if (sortDirection === 'ASC') {
                        return valueA.localeCompare(valueB, 'vi');
                    } else {
                        return valueB.localeCompare(valueA, 'vi');
                    }
                } else {
                    if (sortDirection === 'ASC') {
                        return valueA > valueB ? 1 : -1;
                    } else {
                        return valueA < valueB ? 1 : -1;
                    }
                }
            });
            
            sortedData.forEach(node => {
                if (node.children && node.children.length) {
                    node.children = this.sortTreeData(node.children, sortField, sortDirection);
                }
            });
            
            return sortedData;
        },
        async fetchReasonList(reason = "") {
            try {
                if (reason) {
                    const res = await ReasonApi.getReasonAssign(reason);
                    this.processTreeData(res.data);
                    return;
                }

                const res = await ReasonApi.getReasonAssign("");
                this.allReasonData = res.data;
                this.processTreeData(res.data);
            } catch (error) {
                console.error("Error fetching reason list:", error);
                this.RequestReason = [];
                this.allReasonData = [];
            }
        },
        handleSortTable(sort) {
            if (this.params.sort === sort) {
                this.params.direction = this.params.direction === 'ASC' ? 'DESC' : 'ASC';
            } else {
                this.params.sort = sort;
                this.params.direction = 'ASC';
            }
            this.applyFiltersAndSort();
        },
        openModalCreate() {
            this.parentId = null;
            this.parentName = "";
            this.isModalCreateOpen = true;
        },
        openModalCreateChild(data) {
            this.parentId = data.parentId;
            this.parentName = data.parentName;
            this.isModalCreateOpen = true;
        },
        closeModalCreate() {
            this.isModalCreateOpen = false;
            this.parentId = null;
            this.parentName = "";
        },
        async openModalDelete(id) {
            this.idReason = id
            const res = await ReasonApi.getListRequestByReason(id);
            this.listRequest = res.data
            if (this.listRequest.length > 0){
                this.flagReason = true
            } else {
                this.isModalDeleteOpen = true;
            }
        },
        handleUpdateSuccess() {
            this.isModalDeleteOpen = true;
        },
        closeModalDelete() {
            this.isModalDeleteOpen = false;
        },
        openModalDetail(id) {
            this.idReason = id
            this.isModalDetailOpen = true;
        },
        closeModalDetail() {
            this.isModalDetailOpen = false;
        },
        processTreeData(data) {
            if (!data || !Array.isArray(data)) {
                this.RequestReason = [];
                return;
            }
            
            const processedData = JSON.parse(JSON.stringify(data));
            
            processedData.forEach(item => {
                if (item.children && item.children.length > 0) {
                    item.expanded = false;
                    this.processChildrenNodes(item.children);
                }
            });
            
            this.RequestReason = processedData;
        },
        processChildrenNodes(children) {
            if (!children || !Array.isArray(children)) return;
            
            children.forEach(child => {
                if (child.children && child.children.length > 0) {
                    child.expanded = false;
                    this.processChildrenNodes(child.children);
                }
            });
        }
    },
    mounted() {
        this.fetchReasonList();
    }
}
</script>

<style scoped>
.table-style {
  border-radius: 8px;
  background: #FFF;
  box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.15);
}

.table td,
.table th {
  padding: 0.75rem;
}

.line-table:hover {
  background: #DDE4FF;
}

.header-fixed {
  position: sticky;
  top: 0; 
  z-index: 999;  
}

.table-container-1 {
  max-height: 750px;
  overflow-y: auto; 
}
</style>
