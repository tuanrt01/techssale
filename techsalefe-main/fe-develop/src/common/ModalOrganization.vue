<template>
  <Modal :show="isOpen" size="modal-lg" @click.stop @hidden="onClose">
    <ModalHeader>
      <h3 class="intro-y text-lg font-medium">{{ $t('lang.DETAIL.DT49') }}</h3>
      <button class="mx-auto flex items-center mr-1 justify-center" @click="closeModal">
        <svg aria-hidden="true" class="h-7 w-7 text-gray-600 hover:text-red-600" fill="none" stroke="currentColor"
             viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
        </svg>
      </button>
    </ModalHeader>
    <ModalBody>
      <div class="tree-container">
        <div class="org-table">
          <div class="org-header">
            <div class="org-col">{{ $t('lang.DETAIL.DT47') }}</div>
            <div class="org-col text-center">{{ $t('lang.DETAIL.DT49') }}</div>
          </div>
          <div class="org-body">
            <template v-for="org in origanizationList" :key="org.id">
              <TreeNodeSearch
                  :node="org"
                  :level="0"
                  :selected-rows="localSelectedRows"
                  @node-selected="handleCheckboxChange"
              />
            </template>
          </div>
        </div>
      </div>
    </ModalBody>
    <ModalFooter>
      <div class="mx-auto text-center">
        <button @click="closeModal" class="btn btn-outline-secondary w-24 mr-1"> {{ $t('lang.BUTTON.BT1') }} </button>
        <button class="btn btn-primary w-24" @click="saveSelection"> {{ $t('lang.BUTTON.BT13') }} </button>
      </div>
    </ModalFooter>
  </Modal>
</template>

<script>
import { Modal, ModalHeader, ModalBody, ModalFooter } from "@/global-components/modal";
import OrganizationApi from '@/api/OrganizationApi';
import Toastify from 'toastify-js';
import TreeNodeSearch from '@/components/trees/TreeNodeSearch.vue';

export default {
  name: 'ModalSelectOrganization',
  components: {
    Modal,
    ModalHeader,
    ModalBody,
    ModalFooter,
    TreeNodeSearch
  },
  props: {
    isOpen: Boolean,
    selectedRows: {
      type: Array,
      default: () => []
    },
    organizationId: {
      type: [String, Number],
      default: ''
    }
  },
  data() {
    return {
      origanizationList: [],
      localSelectedRows: [],
    };
  },
  watch: {
    isOpen: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.fetchOrganizationList();
        }
        this.localSelectedRows = this.selectedRows ? [...this.selectedRows] : [];
      }
    },
    selectedRows: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.localSelectedRows = [...newVal];
        } else {
          this.localSelectedRows = [];
        }
      }
    }
  },
  methods: {
    async fetchOrganizationList() {
      try {
        const res = await OrganizationApi.getListOrganization();
        this.origanizationList = res.data.data;

        if (this.organizationId) {
          this.autoSelectOrganization(this.origanizationList);
        }
      } catch (error) {
        console.error('Error fetching organizations:', error);
      }
    },

    autoSelectOrganization(organizations) {
      for (const org of organizations) {
        if (org.id === this.organizationId) {
          this.handleCheckboxChange(org, true);
          return true;
        }
        if (org.children && org.children.length > 0) {
          const found = this.autoSelectOrganization(org.children);
          if (found) return true;
        }
      }
      return false;
    },

    handleCheckboxChange(node, isSelected) {
      if (isSelected) {
        this.localSelectedRows = [node];
      } else {
        this.localSelectedRows = [];
      }
    },
    closeModal() {
      this.$emit('update:isOpen', false);
    },
    saveSelection() {
      if (this.localSelectedRows.length === 0) {
        Toastify({
          node: dom('#required-modal').clone().removeClass('hidden')[0],
          duration: 3000,
          newWindow: true,
          close: true,
          gravity: 'top',
          position: 'right',
          stopOnFocus: true
        }).showToast();
        return;
      }
      const selectedNode = this.localSelectedRows[0];
      this.$emit('save-selection', [selectedNode], selectedNode);
      this.$emit('update:isOpen', false);
    },
    onClose() {
      this.$emit('update:isOpen', false);
    }
  }
};
</script>

<style scoped>
.tree-container {
  max-height: 60vh;
  overflow-y: auto;
  padding: 1rem;
  background-color: white;
  border-radius: 0.375rem;
}

.org-table {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  overflow: hidden;
}

.org-header {
  display: flex;
  background-color: #2F3640;
  padding: 0.75rem 1rem;
  color: white;
  font-weight: 500;
}

.org-body {
  background-color: white;
}

.org-col {
  flex: 1;
}

.org-col:last-child {
  width: 120px;
  flex: none;
}
</style>