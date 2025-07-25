<template>
  <div>
    <Chart 
      type="pie" 
      :width="width" 
      :height="height" 
      :data="data" 
      :options="chartOptions" 
      :plugins="plugins" 
      ref="chartRef"
    />
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useDarkModeStore } from "@/stores/dark-mode";
import { useColorSchemeStore } from "@/stores/color-scheme";
import { colors } from "@/utils/colors";

const props = defineProps(["width", "height", "chartLabels", "chartData"]);

const emit = defineEmits(['pieClick']);

const chartRef = ref(null);

// Xử lý click trong options của Chart
const handleDataPointClick = (event, elements, chart) => {
  
  if (elements && elements.length > 0) {
    const index = elements[0].index;
    const id = props.chartLabels[index];
    const label = props.chartLabels[index];
    const value = props.chartData[index];

    emit('pieClick', {
      id,
      label, 
      value,
      index
    });
  }
};

const darkMode = computed(() => useDarkModeStore().darkMode);
const colorScheme = computed(() => useColorSchemeStore().colorScheme);

const chartColors = () => [
  colors.pending(0.9),
  colors.warning(0.9),
  colors.success(0.9),
  colors.danger(0.9),
  colors.primary(0.9),
  colors.secondary(0.9),
  colors.info(0.9),
];

const data = computed(() => {
  return {
    labels: props.chartLabels,
    datasets: [
      {
        data: props.chartData,
        backgroundColor: colorScheme.value ? chartColors() : "",
        hoverBackgroundColor: colorScheme.value ? chartColors() : "",
        borderWidth: 3,
        borderColor: darkMode.value ? colors.darkmode[700]() : colors.white,
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
            const value = context.parsed || 0;
            const formatter = new Intl.NumberFormat('vi-VN');
            const percentage = value / context.dataset.data.reduce((a, b) => a + b, 0) * 100;
            return label + ': ' + formatter.format(value) + ' (' + percentage.toFixed(2) + '%)';
          }
        }
      }
    },
    onClick: handleDataPointClick,
    onHover: (event, chartElement) => {
      if (event && event.native && event.native.target) {
        event.native.target.style.cursor = chartElement.length > 0 ? 'pointer' : 'default';
      }
    },
  };
});

const plugins = computed(() => [{}]);
</script>