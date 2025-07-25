<template>
    <div>
      <Chart
        type="bar"
        :width="width"
        :height="height"
        :data="data"
        :options="chartOptions"
        ref="chartRef"
      />
    </div>
  </template>
  
  <script setup>
  import { computed, ref, onMounted } from "vue";
  import { useDarkModeStore } from "@/stores/dark-mode";
  import { useColorSchemeStore } from "@/stores/color-scheme";
  import { colors } from "@/utils/colors";
  
  const props = defineProps({
    width: {
      type: [Number, String],
      default: "auto",
    },
    height: {
      type: [Number, String],
      default: "auto",
    },
    chartLabels : [],
    chartData: [],
  });
  
  const emit = defineEmits(['barClick']);
  
  const chartRef = ref(null);
  
  // Xử lý click trong options của Chart
  const handleDataPointClick = (event, elements, chart) => {
    
    if (elements && elements.length > 0) {
      const index = elements[0].index;
      const label = props.chartLabels[index];
      const value = props.chartData[index];
      
      emit('barClick', { 
        label, 
        value,
        index
      });
    }
  };
  
  const darkMode = computed(() => useDarkModeStore().darkMode);
  const colorScheme = computed(() => useColorSchemeStore().colorScheme);

  const data = computed(() => {
    return {
      labels: props.chartLabels,
      datasets: [
        {
          label: "Chi phí",
          barPercentage: 0.8,
          barThickness: 30,
          maxBarThickness: 30,
          minBarLength: 2,
          data: props.chartData,
          backgroundColor: colorScheme.value ? colors.primary() : "",
        },
      ],
    };
  });
  
  const chartOptions = computed(() => {
    return {
      maintainAspectRatio: false,
      plugins: {
        legend: {
          labels: {
            color: colors.slate["500"](0.8),
          },
        },
        tooltip: {
          callbacks: {
            label: function (context) {
              const label = context.label || '';
              const dataIndex = context.dataIndex;
              const value = props.chartData[dataIndex] || 0;
              const formatter = new Intl.NumberFormat('vi-VN');
              const percentage = value / props.chartData.reduce((a, b) => a + b, 0) * 100;
              return label + ': ' + formatter.format(value) + ' (' + percentage.toFixed(2) + '%)';
            }
          }
        }
      },
      scales: {
        x: {
          ticks: {
            font: {
              size: 12,
            },
            color: colors.slate["500"](0.8),
          },
          grid: {
            display: false,
            drawBorder: false,
          },
        },
        y: {
          ticks: {
            font: {
              size: 12,
            },
            color: colors.slate["500"](0.8),
            callback: function (value) {
              if (value == null){
                return 0
              }
              const formatter = new Intl.NumberFormat('vi-VN');
              return formatter.format(value);
            },
          },
          grid: {
            color: darkMode.value
              ? colors.slate["500"](0.3)
              : colors.slate["300"](),
            borderDash: [2, 2],
            drawBorder: false,
          },
        },
      },
      onClick: handleDataPointClick,
      onHover: (event, chartElement) => {
        if (event && event.native && event.native.target) {
          event.native.target.style.cursor = chartElement.length > 0 ? 'pointer' : 'default';
        }
      },
    };
  });
  </script>
  