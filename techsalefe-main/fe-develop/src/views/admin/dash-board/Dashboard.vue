<template>
    <div class="p-3">
        <h1 class="font-bold text-2xl">{{ $t('lang.DASHBOARD.DB1') }}</h1>
        <div class="grid grid-cols-12 gap-6 mt-5">
            <div class="col-span-12 sm:col-span-6 xl:col-span-3 intro-y">
              <div class="report-box before:bg-[#FF8A4A] zoom-in cursor">
                <div class="box p-5 flex justify-between bg-[#EA5C0F] items-center">
                  <div>
                    <div class="flex">
                      <div class="text-lg text-[#FFF] mt-1 mr-2">{{ $t('lang.DASHBOARD.DB2') }} </div>
                      <VueDatePicker v-model="year.yearNow" auto-apply clearable class="w-24 h-8 text-center"
                          @update:model-value="handleYearSelectionHeader" 
                          @cleared="resetToCurrentYear('yearNow')"
                          year-picker>
                      </VueDatePicker>  
                    </div>
                    <div class="text-3xl text-[#FFF] font-medium leading-8 mt-3"> {{ formatCurrency(Header.total) }} {{ $t('lang.DASHBOARD.DB16') }} </div>
                  </div>
                  <div class="flex">
                    <img class="w-[50px] h-[50px]" src="../../../assets/images/total.png"/>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="col-span-12 sm:col-span-6 xl:col-span-3 intro-y">
              <div class="report-box before:bg-[#FFA262] zoom-in cursor">
                <div class="box p-5 flex justify-between bg-[#E97100] items-center">
                  <div>
                    <div class="text-lg text-[#FFF] mt-1">{{ $t('lang.DASHBOARD.DB3') }}</div>
                    <div class="text-2xl text-[#FFF] font-medium leading-8 mt-3"> {{ Header.reason }} </div>
                  </div>
                  <div class="flex">
                    <img class="w-[50px] h-[50px]" src="../../../assets/images/dashboard-2.png"/>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-span-12 sm:col-span-6 xl:col-span-3 intro-y">
              <div class="report-box before:bg-[#92D7D1] zoom-in cursor">
                <div class="box p-5 flex justify-between bg-[#0C9488] items-center">
                  <div>
                    <div class="text-lg text-[#FFF] mt-1">{{ $t('lang.DASHBOARD.DB4') }}</div>
                    <div class="text-3xl text-[#FFF] font-medium leading-8 mt-3">{{ $t('lang.DASHBOARD.DB10') }} {{ Header.month }} </div>
                  </div>
                  <div class="flex justify-between">
                    <img class="w-[50px] h-[50px]" src="../../../assets/images/dashboard-3.png"/>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-span-12 sm:col-span-6 xl:col-span-3 intro-y">
              <div class="report-box before:bg-[#87AFCB] zoom-in cursor">
                <div class="box p-5 flex justify-between bg-[#076CB5] items-center">
                  <div>
                    <div class="text-lg text-[#FFF] mt-1">{{ $t('lang.DASHBOARD.DB5') }}</div>
                    <div class="text-2xl text-[#FFF] font-medium leading-8 mt-3"> {{ Header.user }} </div>
                  </div>
                  <div class="flex">
                    <img class="w-[50px] h-[50px]" src="../../../assets/images/dashboard-4.png"/>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="flex w-full gap-6 mt-9">
            <div class="w-1/2 bg-[#FFF] rounded-xl chart-shadow p-7">
                <div class="flex justify-between">   
                    <h2 class="text-lg font-medium truncate">{{ $t('lang.DASHBOARD.DB6') }}</h2>
                    <VueDatePicker v-model="year.yearFilterMonth" auto-apply clearable class="w-28 text-center border-cus"
                         @update:model-value="handleYearSelection2"
                         @cleared="resetToCurrentYear('yearFilterMonth')"
                         year-picker>
                    </VueDatePicker> 
                </div>
                <VerticalBarChart 
                  :height="300" 
                  :chartLabels="totalByMonthData[0]" 
                  :chartData="totalByMonthData[1]"
                  @barClick="handleMonthBarClick"
                />
            </div>
            <div class="w-1/2 bg-[#FFF] rounded-xl chart-shadow p-7">
                <div class="flex justify-between">   
                    <h2 class="text-lg font-medium truncate">{{ $t('lang.DASHBOARD.DB7') }}</h2>
                    <VueDatePicker v-model="year.yearFilterQuarter" auto-apply clearable class="w-28 text-center border-cus"
                         @update:model-value="handleYearSelection3"
                         @cleared="resetToCurrentYear('yearFilterQuarter')"
                         year-picker>
                    </VueDatePicker> 
                </div>
                <VerticalBarChart 
                  :height="300" 
                  :chartLabels="totalByQuarterData[0]" 
                  :chartData="totalByQuarterData[1]"
                  @barClick="handleQuarterBarClick"
                />
            </div>
          </div>

          <div class="flex w-full gap-6 mt-6">
            <div class="w-1/2 bg-[#FFF] rounded-xl chart-shadow p-7">
                <div class="flex justify-between">   
                    <h2 class="text-lg font-medium truncate">{{ $t('lang.DASHBOARD.DB8') }}</h2>
                    <div class="flex items-center gap-2">
                        <div class="flex items-center">
                            <VueDatePicker v-model="year.yearFilterReason" auto-apply clearable class="w-28 text-center border-cus"
                                @update:model-value="handleYearSelection1"
                                @cleared="resetToCurrentYear('yearFilterReason')"
                                year-picker>
                            </VueDatePicker>
                        </div>
                        <div class="flex items-center">
                            <select v-model="monthFilterReason" @change="getListDashboardReason" class="h-10 px-3 py-2 rounded border border-gray-300 min-w-[120px]">
                                <option value="">{{ $t('lang.DASHBOARD.DB13') }}</option>
                                <option v-for="month in 12" :key="month" :value="month">{{ $t('lang.DASHBOARD.DB10') }} {{ month }}</option>
                            </select>
                        </div>
                    </div>
                </div>
                <PieChart 
                  :height="300" 
                  :chartLabels="totalByReasonData[0]" 
                  :chartData="totalByReasonData[1]"
                  @pieClick="handleReasonPieClick"
                />
            </div>
            <div class="w-1/2 bg-white rounded-xl p-2.5 col-span-12 xl:col-span-4 shadow-md chart-shadow p-7">
                <div class="intro-y flex justify-between items-center h-10">
                    <h2 class="text-lg font-medium truncate">{{ $t('lang.DASHBOARD.DB9') }}</h2>
                    <div class="flex items-center gap-2">
                        <div class="flex items-center">
                            <VueDatePicker v-model="year.yearFilterUser" auto-apply clearable class="w-28 text-center border-cus"
                                @update:model-value="handleYearSelection"
                                @cleared="resetToCurrentYear('yearFilterUser')"
                                year-picker>
                            </VueDatePicker>
                        </div>
                        <div class="flex items-center">
                            <select v-model="monthFilterUser" @change="getListDashboardUser" class="h-10 px-3 py-2 rounded border border-gray-300 min-w-[120px]">
                                <option value="">{{ $t('lang.DASHBOARD.DB13') }}</option>
                                <option v-for="month in 12" :key="month" :value="month">{{ $t('lang.DASHBOARD.DB10') }} {{ month }}</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="mt-2 border border-[#D2D2D2] rounded-md" style="max-height: 300px; overflow-y: auto; overflow-x: hidden">
                    <div class="intro-y " v-for="(item, index) in totalByUsers" style="z-index: 1;">
                        <div class="box px-8 py-2 mb-3 flex items-center zoom-in h-[60px] cursor-pointer" 
                            @click="handleUserRowClick(item)">
                            <h2 class="mr-4 text-lg font-medium"> {{ index + 1 }} </h2>  
                            <div class="w-10 h-10 flex-none image-fit rounded-md overflow-hidden">
                                <!-- <img alt="" :src="item.imageUrl" /> -->
                            </div>
                            <div class="ml-4 mr-auto">
                                <div class="font-medium"> {{ item.user }} </div>  
                            </div>
                            <div class="w-30 py-1 px-2 text-center rounded text-sm bg-[#FFF] border-[#0C9488] border text-[#131936]  font-medium">
                              {{ formatCurrency(item.total) }} {{ $t('lang.DASHBOARD.DB16') }}
                            </div>
                            <div class="w-28 py-1 px-2 text-center rounded text-sm bg-[#0C9488] border-[#0C9488] border text-white  font-medium ml-4">
                                {{ item.quantity }} {{ $t('lang.DASHBOARD.DB14') }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Chi tiết chi tiêu -->
        <ExpenseDetailModal
          :isOpen="isDetailModalOpen"
          :title="detailModalTitle"
          :subtitle="detailModalSubtitle"
          :items="detailItems"
          @close="closeDetailModal"
        />
    </div>
</template>

<script>
  import VerticalBarChart from "@/components/vertical-bar-chart/Main.vue";
  import PieChart from "@/components/pie-chart/Main.vue";
  import VueDatePicker from "@vuepic/vue-datepicker";
  import DashboardApi from "@/api/DashboardApi.js";
  import ExpenseDetailModal from "@/components/expense-detail-modal/Main.vue";

  export default {
    components: {
        VerticalBarChart,
        PieChart,
        VueDatePicker,
        ExpenseDetailModal
    },
    data() {
        return {
            theMonthFilter: {
                month: new Date().getMonth(),
                year: new Date().getFullYear()
            },
            theMonthFirst: new Date().getMonth() + 1,
            theYearFirst: new Date().getFullYear(),
            totalByUsers: [],
            totalByReasonData : [[],[]],
            totalByMonthData : [[],[]],
            totalByQuarterData : [[],[]],
            Header : {},
            year : {
              yearNow : new Date().getFullYear(),
              yearFilterUser: new Date().getFullYear(),
              yearFilterReason: new Date().getFullYear(),
              yearFilterMonth: new Date().getFullYear(),
              yearFilterQuarter: new Date().getFullYear(),
            },
            // Dữ liệu modal chi tiết
            isDetailModalOpen: false,
            detailModalTitle: "Chi tiết chi tiêu",
            detailModalSubtitle: "",
            detailItems: [],
            currentDetailType: null, // 'month', 'quarter', hoặc 'reason'
            currentDetailValue: null,
            selectedMonth: new Date().getMonth() + 1, // Default to current month
            monthFilterReason: "", // Empty string means all months
            monthFilterUser: "", // Empty string means all months
        }
    },
    created() {
      this.getListDashboardUser();
      this.getListDashboardReason()
      this.getListDashboardMoth()
      this.getListDashboardQuarter()
      this.getHeader()
    },
    mounted() {
      this.getListDashboardUser();
      this.getListDashboardReason()
      this.getListDashboardMoth()
      this.getListDashboardQuarter()
      this.getHeader()
    },
    methods: {
        async handleChangeMonth(newMonth) {
        let month = newMonth.month + 1
        this.theMonthFirst = month.toString().padStart(2, '0')
        this.getDates()
        let year = newMonth.year
        this.theYearFirst = year
        this.getDaysInMonth(this.theMonthFirst, this.theYearFirst)
        let param = {
            date: year + '-' + month.toString().padStart(2, '0')
        }
        this.init()
        },
        async getListDashboardUser(){
          const res = await DashboardApi.getBestUser(this.year.yearFilterUser, this.monthFilterUser || null);
          this.totalByUsers = res.data
        },
        async getListDashboardReason(){
          const res = await DashboardApi.getReason(this.year.yearFilterReason, this.monthFilterReason || null);
          this.totalByReasonData = [[],[]]
          this.updateDataArrays(this.totalByReasonData, res.data)
        },
        async getListDashboardMoth(){
          const res = await DashboardApi.getTotalByMonth(this.year.yearFilterMonth);
          this.totalByMonthData = [[],[]]
          this.updateDataArraysMonth(this.totalByMonthData , res.data)
        },
        async getListDashboardQuarter(){
          const res = await DashboardApi.getTotalByQuarter(this.year.yearFilterQuarter);
          this.totalByQuarterData = [[],[]]
          this.updateDataArraysQuarter(this.totalByQuarterData , res.data)
        },
        async getHeader(){
          const res = await DashboardApi.getHeader(this.year.yearNow);
          this.Header = {}
          this.Header = res.data
        },
        formatCurrency(value) {
          if (value == null){
            return 0
          }
          const formatter = new Intl.NumberFormat('vi-VN');
          return formatter.format(value);
        },
        handleYearSelection() {
          this.getListDashboardUser()
        },
        handleYearSelection1() {
          this.getListDashboardReason()
        },
        handleYearSelection2() {
          this.getListDashboardMoth()
        },
        handleYearSelection3() {
          this.getListDashboardQuarter()
        },
        handleYearSelectionHeader(){
          this.getHeader()
        },
        updateDataArrays(targetArray, sourceData) {
          for (let index = 0; index < sourceData.length; index++) {
            const element = sourceData[index];
            targetArray[0][index] = element.reason;
            targetArray[1][index] = element.total;
          }
        },
        updateDataArraysMonth(targetArray, sourceData) {
          for (let index = 0; index < 12; index++) {
            if (!sourceData[index]) {
              targetArray[1][index] = 0;
            } else {
              targetArray[1][index] = sourceData[index].total;
            }
          }
          targetArray[0] = ["Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"];
        },
        updateDataArraysQuarter(targetArray, sourceData) {
          for (let index = 0; index < 4; index++) {
            if (!sourceData[index]) {
              targetArray[1][index] = 0;
            } else {
              targetArray[1][index] = sourceData[index].total;
            }
          }
          targetArray[0] = ["Quý 1", "Quý 2", "Quý 3", "Quý 4"];
        },
        
        // Xử lý sự kiện click trên các chart
        async handleMonthBarClick(data) {
          const monthIndex = data.index;
          const monthNumber = monthIndex + 1;
          this.detailModalTitle = `${this.$t('lang.EXPENSE_MODAL.EM4')} ${monthNumber}`;
          this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${monthNumber} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterMonth}`;
          this.currentDetailType = 'month';
          this.currentDetailValue = monthNumber;
          
          // Mở modal trước khi tải dữ liệu
          this.detailItems = [];
          this.isDetailModalOpen = true;
          
          await this.loadDetailData();
        },
        
        async handleQuarterBarClick(data) {
          const quarterIndex = data.index;
          const quarterNumber = quarterIndex + 1;
          this.detailModalTitle = `${this.$t('lang.EXPENSE_MODAL.EM5')} ${quarterNumber}`;
          this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB11')} ${quarterNumber} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterQuarter}`;
          this.currentDetailType = 'quarter';
          this.currentDetailValue = quarterNumber;
          
          // Mở modal trước khi tải dữ liệu
          this.detailItems = [];
          this.isDetailModalOpen = true;
          
          await this.loadDetailData();
        },
        
        handleReasonPieClick(data) {
          if (!data || !data.id) {
            return;
          }
          
          const reasonName = data.id;
          
          this.detailModalTitle = `${this.$t('lang.EXPENSE_MODAL.EM3')}: ${reasonName}`;
          this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterReason}`;
          
          if (this.monthFilterReason) {
            this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${this.monthFilterReason} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterReason}`;
          }
          
          this.currentDetailType = 'reason';
          this.currentDetailValue = reasonName;
          
          // Mở modal trước khi tải dữ liệu
          this.detailItems = [];
          this.isDetailModalOpen = true;
          
          // Tải dữ liệu chi tiết
          this.loadDetailData();
        },
        
        async handleUserRowClick(item) {
          if (!item || !item.user) {
            return;
          }
          
          // Lấy tên người dùng
          const userName = item.user;
          
          // Tạo tiêu đề cho modal
          this.detailModalTitle = `${this.$t('lang.EXPENSE_MODAL.EM2')}: ${userName}`;
          this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterUser}`;
          
          // Thêm thông tin tháng nếu đang lọc theo tháng
          if (this.monthFilterUser) {
            this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${this.monthFilterUser} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterUser}`;
          }
          
          // Đặt loại chi tiết và giá trị
          this.currentDetailType = 'user';
          this.currentDetailValue = item.id;
          
          // Thông báo cho người dùng rằng đang tải dữ liệu
          this.detailItems = [];
          this.isDetailModalOpen = true;
          
          // Tải dữ liệu
          await this.loadDetailData();
        },
        
        async loadDetailData() {
          try {
            this.detailItems = [];
            let response = null;
            let isLoading = true;
            
            try {
              if (this.currentDetailType === 'month') {
                response = await DashboardApi.getMonthDetails(
                  this.year.yearFilterMonth, 
                  this.currentDetailValue
                );
              } else if (this.currentDetailType === 'quarter') {
                response = await DashboardApi.getQuarterDetails(
                  this.year.yearFilterQuarter, 
                  this.currentDetailValue
                );
              } else if (this.currentDetailType === 'reason') {
                response = await DashboardApi.getReasonDetails(
                  this.year.yearFilterReason, 
                  this.currentDetailValue
                );
              } else if (this.currentDetailType === 'user') {
                response = await DashboardApi.getUserDetails(
                  this.year.yearFilterUser, 
                  this.currentDetailValue
                );
              }
            } catch (error) {
              response = { data: [] };
            } finally {
              isLoading = false;
            }
            
            // Đảm bảo modal hiển thị ngay cả khi đang tải
            if (!this.isDetailModalOpen) {
              this.isDetailModalOpen = true;
            }
            
            if (response && response.data) {
              let processedData = response.data.map(item => {
                if (typeof item.amount === 'string') {
                  item.amount = parseFloat(item.amount.replace(/,/g, ''));
                }
                
                return item;
              });
              
              // LỌC DỮ LIỆU TRÊN FRONTEND
              if (this.currentDetailType === 'reason' && this.monthFilterReason) {
                processedData = this.filterDataByMonth(processedData, parseInt(this.monthFilterReason));
              } else if (this.currentDetailType === 'user' && this.monthFilterUser) {
                processedData = this.filterDataByMonth(processedData, parseInt(this.monthFilterUser));
              }
              
              // Cập nhật tiêu đề và tổng tiền
              this.updateTotalAmount(processedData);
              
              this.detailItems = processedData;
            } 
            
          } catch (error) {
            this.detailItems = [];
          }
        },
        
        // Thêm hàm mới để lọc dữ liệu theo tháng
        filterDataByMonth(data, month) {
          return data.filter(item => {
            if (!item.date) return false;
            
            try {
              const date = new Date(item.date);
              const itemMonth = date.getMonth() + 1; // JavaScript months are 0-based
              return itemMonth === month;
            } catch (e) {
              return false;
            }
          });
        },
        
        closeDetailModal() {
          this.isDetailModalOpen = false;
        },
        resetUserFilters() {
            this.year.yearFilterUser = new Date().getFullYear();
            this.monthFilterUser = "";
            this.getListDashboardUser();
        },
        resetReasonFilters() {
            this.year.yearFilterReason = new Date().getFullYear();
            this.monthFilterReason = "";
            this.getListDashboardReason();
        },
        resetToCurrentYear(yearField) {
            this.year[yearField] = new Date().getFullYear();
            
            // Gọi hàm cập nhật dữ liệu tương ứng dựa trên yearField đã được reset
            if (yearField === 'yearNow') {
                this.getHeader();
            } else if (yearField === 'yearFilterMonth') {
                this.getListDashboardMoth();
            } else if (yearField === 'yearFilterQuarter') {
                this.getListDashboardQuarter();
            } else if (yearField === 'yearFilterReason') {
                this.getListDashboardReason();
            } else if (yearField === 'yearFilterUser') {
                this.getListDashboardUser();
            }
        },
        // Hàm cập nhật tổng số tiền để hiển thị trong modal
        updateTotalAmount(items) {
          const total = items.reduce((sum, item) => sum + (Number(item.amount) || 0), 0);
          const formatter = new Intl.NumberFormat('vi-VN');
          
          // Cập nhật thông tin trong subtitle của modal
          if (this.currentDetailType === 'month') {
            // Mô tả "Tháng 1 năm 2024"
            this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${this.currentDetailValue} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterMonth}`;
          } else if (this.currentDetailType === 'quarter') {
            // Mô tả "Quý 1 năm 2024"
            this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB11')} ${this.currentDetailValue} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterQuarter}`;
          } else if (this.currentDetailType === 'reason') {
            // Mô tả "Tháng 1 năm 2024" nếu đang lọc theo tháng
            if (this.monthFilterReason) {
              this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${this.monthFilterReason} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterReason}`;
            } else {
              this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterReason}`;
            }
          } else if (this.currentDetailType === 'user') {
            // Mô tả "Tháng 1 năm 2024" nếu đang lọc theo tháng
            if (this.monthFilterUser) {
              this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB10')} ${this.monthFilterUser} ${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterUser}`;
            } else {
              this.detailModalSubtitle = `${this.$t('lang.DASHBOARD.DB12')} ${this.year.yearFilterUser}`;
            }
          }
        },
    }

  }
</script>

<style scoped>
.chart-shadow {
    box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.15);
}
.cursor {
  cursor: default;
}
.cursor-pointer {
  cursor: pointer;
}
.border-cus{
  border: 1px black solid;
  border-radius: 5px;
}
</style>