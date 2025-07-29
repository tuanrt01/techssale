// Import only the necessary components for the purchase history page
import Loading from "@/components/loading/Loading.vue"
import { Modal } from "@/global-components/modal"
import Pagination from "@/components/pagination/pagination.vue"
import VTooltip from 'v-tooltip'
import Lucide from '@/global-components/lucide'
import Tippy from '@/global-components/tippy/Main.vue'

export default (app) => {
  app.component("Loading", Loading)
  app.component("Pagination", Pagination)
  app.component("Tippy", Tippy)
  
  // Register Lucide icons
  app.component("LucideEye", Lucide.Eye)
  app.component("LucideMoreHorizontal", Lucide.MoreHorizontal)
  app.component("LucideSearch", Lucide.Search)
  app.component("LucidePlus", Lucide.Plus)
  app.component("LucideCheck", Lucide.Check)
  app.component("LucideX", Lucide.X)
  app.component("LucideChevronDown", Lucide.ChevronDown)
  app.component("LucideChevronLeftIcon", Lucide.ChevronLeft)
  app.component("LucideChevronRightIcon", Lucide.ChevronRight)

  // Register directive
  app.directive('tooltip', VTooltip)

  // Register Modal components
  app.use(Modal)
} 