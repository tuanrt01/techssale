<template>
  <div class="tree-node">
    <div class="node-content" :class="{ 'selected': isSelected }">
      <div class="flex items-center justify-between w-full">
        <div class="tree-node-content flex-1">
          <div v-for="n in level" :key="n" class="tree-indent"></div>
          <div v-if="hasChildren" class="tree-toggle" @click.stop="toggleExpand">
            <i :class="isExpanded ? 'fa fa-caret-down' : 'fa fa-caret-right'"></i>
          </div>
          <div v-else class="tree-indent"></div>
          <span class="ml-1">{{ node.organizationName || node.name }}</span>
        </div>
        <div class="flex items-center justify-center" style="width: 120px">
          <input
              type="radio"
              name="organization-radio"
              :checked="isChecked"
              @change="handleSelect"
              class="form-radio h-4 w-4 text-blue-600 border-gray-300"
          />
        </div>
      </div>
    </div>
    <div v-if="isExpanded && hasChildren">
      <TreeNodeSearch
          v-for="child in node.children"
          :key="child.id"
          :node="child"
          :level="level + 1"
          :selected-rows="selectedRows"
          @node-selected="handleChildNodeSelected"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'TreeNodeSearch',
  props: {
    node: {
      type: Object,
      required: true
    },
    level: {
      type: Number,
      default: 0
    },
    selectedRows: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      isExpanded: true
    };
  },
  computed: {
    isChecked() {
      return this.selectedRows.some(row => row.id === this.node.id);
    },
    hasChildren() {
      return this.node.children && this.node.children.length > 0;
    },
    isSelected() {
      return this.selectedRows.some(row => row.id === this.node.id);
    },
    hasSelectedChild() {
      if (!this.hasChildren) return false;
      return this.hasAnySelectedChildRecursive(this.node);
    }
  },
  watch: {
    selectedRows: {
      immediate: true,
      deep: true,
      handler() {
        if (this.hasSelectedChild || this.isSelected) {
          this.isExpanded = true;
        }
      }
    }
  },
  methods: {
    toggleExpand() {
      this.isExpanded = !this.isExpanded;
    },
    handleSelect(event) {
      const isChecked = event.target.checked;
      this.$emit('node-selected', this.node, isChecked);
    },
    handleChildNodeSelected(childNode, isChecked) {
      this.$emit('node-selected', childNode, isChecked);
    },
    hasAnySelectedChildRecursive(node) {
      if (!node.children || !node.children.length) return false;

      for (const child of node.children) {
        if (this.selectedRows.some(row => row.id === child.id)) {
          return true;
        }
        if (this.hasAnySelectedChildRecursive(child)) {
          return true;
        }
      }
      return false;
    }
  },
  mounted() {
    if (this.isSelected || this.hasSelectedChild) {
      this.isExpanded = true;
    }
  }
};
</script>

<style scoped>
.tree-node {
  width: 100%;
}

.node-content {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  border-bottom: 1px solid #e2e8f0;
}

.node-content:hover {
  background-color: #F8FAFC;
}

.node-content.selected {
  background-color: #E8EDF9;
}

.tree-node-content {
  display: flex;
  align-items: center;
}

.tree-toggle {
  cursor: pointer;
  width: 20px;
  display: inline-block;
  text-align: center;
  color: #64748b;
}

.tree-indent {
  display: inline-block;
  width: 20px;
}

.form-radio {
  cursor: pointer;
}

.form-radio:hover {
  border-color: #2563eb;
}
</style> 