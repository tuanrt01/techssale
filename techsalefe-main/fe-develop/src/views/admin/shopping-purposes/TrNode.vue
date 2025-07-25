<template>
  <tr
    class="intro-x cursor-pointer line-table"
    :class="{'bg-[#DDE4FF]': isActive, 'bg-white': !isActive}"
    @click.stop="handleRowClick"
  >
    <td class="text-center max-w-50 w-[50px]">{{ index }}</td>
    <td class="">
      <div class="tree-node-content">
        <div v-for="n in level" :key="n" class="tree-indent"></div>
        <div v-if="hasChildren" class="tree-toggle" @click.stop="toggleNode">
          <i :class="item.expanded ? 'fa fa-caret-down' : 'fa fa-caret-right'"></i>
        </div>
        <div v-else class="tree-indent"></div>
        <span class="ml-1">{{ item.reason }}</span>
      </div>
    </td>
    <td class="">{{ item.createdAt }}</td>
    <td class="">{{ item.createdBy?.name }}</td>
    <td class="">{{ item.buyerUserId?.name }}</td>
    <td class="">
      <div class="flex gap-[20px] items-center justify-center">
        <div class="text-[#505050]" v-if="item.parent === null">
          <a @click.stop="handleCreate" class="flex items-center" href="javascript:;" title="Tạo mục đích con">
            <i class="fa-solid fa-folder-plus"></i>
          </a>
        </div>
        <div class="text-[#505050] text-opacity-30" v-else>
          <span class="flex items-center cursor-not-allowed">
            <i class="fa-solid fa-folder-plus"></i>
          </span>
        </div>
        <div class="text-[#7E7E7E]">
          <a @click.stop="handleEdit" class="flex items-center" href="javascript:;"  >
            <i class="fa-solid fa-pen-to-square"></i>
          </a>
        </div>
        <div class="text-[#E96C7B]">
          <a @click.stop="handleDelete" class="flex items-center" href="javascript:;">
            <i class="fa-solid fa-trash-can"></i>
          </a>
        </div>
      </div>
    </td>
  </tr>
  <template v-if="isExpanded && hasChildren">
    <tr-node
      v-for="(child, childIndex) in item.children"
      :key="child.id"
      :item="child"
      :index="getChildIndex(childIndex)"
      :level="level + 1"
      @edit-click="$emit('edit-click', $event)"
      @delete-click="$emit('delete-click', $event)"
      @create-click="$emit('create-click', $event)"
    />
  </template>
</template>

<script>
export default {
  name: "TrNode",
  props: {
    item: Object,
    index: {
      type: [Number, String],
      required: true
    },
    level: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      isActive: false,
      showAllBuyers: false
    };
  },
  computed: {
    hasChildren() {
      return this.item.children && this.item.children.length > 0;
    },
    isExpanded() {
      return this.item.expanded;
    }
  },
  created() {
    if (this.hasChildren && this.item.expanded === undefined) {
      this.item.expanded = false;
    }
  },
  methods: {
    handleRowClick() {
      this.isActive = true;
      setTimeout(() => {
        this.isActive = false;
      }, 200);
      this.$emit('edit-click', this.item.id);
    },
    toggleNode(e) {
      e.preventDefault();
      e.stopPropagation();
      if (this.hasChildren) {
        this.item.expanded = !this.item.expanded;
      }
    },
    handleEdit(e) {
      e.preventDefault();
      e.stopPropagation();
      this.$emit('edit-click', this.item.id);
    },
    handleDelete(e) {
      e.preventDefault();
      e.stopPropagation();
      this.$emit('delete-click', this.item.id);
    },
    handleCreate(e) {
      if (this.item.parent === null) {
        e.preventDefault();
        e.stopPropagation();
        this.$emit('create-click', { parentId: this.item.id, parentName: this.item.reason });
      }
    },
    getChildIndex(childIndex) {
      if (typeof this.index === 'number') {
        return `${this.index}.${childIndex + 1}`;
      } else {
        return `${this.index}.${childIndex + 1}`;
      }
    }
  }
};
</script>

<style scoped>
.tree-toggle {
  cursor: pointer;
  width: 20px;
  display: inline-block;
  text-align: center;
}

.tree-indent {
  display: inline-block;
  width: 20px;
}

.tree-node-content {
  display: flex;
  align-items: center;
}

tr.line-table:hover {
  background-color: #DDE4FF !important;
}

.buyer-tag {
  display: inline-block;
  background: #5cb85c;
  color: #fff;
  border-radius: 4px;
  padding: 2px 10px;
  margin: 2px 4px 2px 0;
  font-size: 13px;
  font-weight: 500;
}

.buyer-more {
  display: inline-block;
  background: #888;
  color: #fff;
  border-radius: 12px;
  padding: 2px 10px;
  margin: 2px 4px 2px 0;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  position: relative;
}

.buyer-popup {
  position: absolute;
  background: #fff;
  color: #333;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  padding: 8px 12px;
  z-index: 100;
  left: 50%;
  top: 28px;
  min-width: 160px;
  white-space: normal;
  transform: translateX(-50%);
}

.buyer-popup-wrapper {
  position: relative;
  display: inline-block;
}
</style> 