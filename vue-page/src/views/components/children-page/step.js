import customNavBar from "@/views/components/custom/customNavBar";
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import customSwipe from "../custom/customSwipe.vue";
import customPicker from '@/views/components/custom/customPicker'

export default {
    components: {
        customNavBar,
        echartsComponent,
        customSwipe,
        customPicker
    },
    data() {
        return {
            pageWidth: window.innerWidth,
            timeActive: 0,
            // 日期显示
            dateText: "",
            // 步数
            stepInfo: {
                stepNumber: 0,
                mileage: 0,
                calories: 0
            },
            initialSwipe: 0,
            // 达标天数
            complianceDays: 0,
            // 连续达标天数
            continueComplianceDays: 0,
        };
    },

    mounted() {
        // 设置显示日期
        // this.setDateText();
        this.setDayData();
        this.queryComplianceDays();

    },

    methods: {
        onClickTab(e) {
            // 设置显示日期
            this.setDateText();
            console.log("onClickTab", e);
            this.$refs.customSwipe.changeDateType(e);
        },

        /**
         * 设置日期文本
         */
        setDateText() {
            this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive, 0);
        },

        setDayData() {
            this.stepInfo = this.$deviceHolder.deviceInfo.stepInfo;
        },

        vanSwipeChange(e, chartSize) {
            console.log(this.timeActive);
            this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive, chartSize - (e + 1));
            console.log(this.dateText);
            this.initialSwipe = e;
        },

        setTarget() {
            this.$refs.customPicker.open();
        },

        queryComplianceDays() {
            this.$androidApi.queryComplianceDays().then(res => {
                this.complianceDays = res.complianceDays
                this.continueComplianceDays = res.continueComplianceDays
            })
        },
    },
};
