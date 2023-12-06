import customNavBar from "../custom/customNavBar.vue";
import customProgress from '@/views/components/custom/customProgress'
import echartsComponent from "@/components/Charts/echartsComponent.vue";
export default {
    components: { customNavBar, customProgress, echartsComponent },

    data() {
        return {
            timeActive: 1,
            sleepTimeStatistics: [
                { name: '深睡', value: 3, color: '#5C44C7' },
                { name: '浅睡', value: 2, color: '#8977E0' },
                { name: '快速眼动睡眠', value: 7, color: '#1DA772' },
                { name: '清醒时长', value: 0, color: '#FBBF03' },
            ],
        };
    },


    mounted() { },

    methods: { onClickTab() { }, },
};