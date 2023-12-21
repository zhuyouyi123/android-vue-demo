import customNavBar from "@/views/components/custom/customNavBar";
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import customSwipe from "../custom/customSwipe.vue";
import customPicker from '@/views/components/custom/customPicker'
import deviceData from "@/store/deviceData";

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
                calories: 0,
                useTime: 0,
            },
            otherStepInfo: {
                stepNumber: 0,
                mileage: 0,
                calories: 0
            },
            initialSwipe: 0,
            // 达标天数
            complianceDays: 0,
            // 连续达标天数
            continueComplianceDays: 0,
            dateDatas: {
                currMonth: 0,
                currWeek: 0,
                lastMonth: 0,
                lastWeek: 0
            },


        };
    },

    mounted() {
        this.$deviceHolder.routerPath = "home";
        // 设置显示日期
        // this.setDateText();
        this.setDayData();
        this.queryComplianceDays();
        this.queryWeekAndMonthStepData();

    },

    methods: {
        onClickTab(e) {
            // 设置显示日期
            this.setDateText();
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
            this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive, chartSize - (e + 1));
            this.initialSwipe = e;
        },

        extendedInfoResponse(e) {
            this.stepInfo = {
                stepNumber: e.extendedInfo.stepNumber,
                mileage: e.extendedInfo.mileage,
                calories: e.extendedInfo.calories,
                useTime: e.extendedInfo.useTime,
            }
            // extendedInfo":{"calories":13,"mileage":681,"stepNumber":954,"useTime":0}}
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

        queryWeekAndMonthStepData() {
            deviceData.queryWeekAndMonthStepData().then(res => this.dateDatas = { ...res })
        },
    },
};
