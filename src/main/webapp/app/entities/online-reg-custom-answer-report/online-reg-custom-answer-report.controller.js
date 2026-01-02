(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OnlineRegCustomAnswerReportController', OnlineRegCustomAnswerReportController);

    OnlineRegCustomAnswerReportController.$inject = ['$scope', '$state', 'OnlineRegCustomAnswerReport', 'Congress', 'reportFilter',
        'DTOptionsBuilder', 'DTColumnDefBuilder', 'CongressSelector'];

    function OnlineRegCustomAnswerReportController ($scope, $state, OnlineRegCustomAnswerReport, Congress, reportFilter,
       DTOptionsBuilder, DTColumnDefBuilder, CongressSelector) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.onlineRegCurrencies;
        });
        vm.reportFilter = reportFilter;

        vm.search = search;
        vm.clear = clear;
        vm.isAnyFilterSet = isAnyFilterSet;
        vm.downloadReportXls = downloadReportXls;

        vm.reportList = [];

        function clear() {
            vm.reportList = [];
            vm.reportFilter.currency = null;
        }

        function search() {
            OnlineRegCustomAnswerReport.getCustomAnswers({meetingCode: CongressSelector.getSelectedCongress().meetingCode, currency: vm.reportFilter.currency.currency},
                function (result) {
                    vm.reportList = result;
                }
            );
        }

        function isAnyFilterSet() {
            return vm.reportFilter.regType;
        }

        function downloadReportXls () {
            window.location.href = 'api/online-reg-custom-answer-report/' + CongressSelector.getSelectedCongress().meetingCode + '/' + vm.reportFilter.currency.currency + '/download-report';
        }
    }
})();
