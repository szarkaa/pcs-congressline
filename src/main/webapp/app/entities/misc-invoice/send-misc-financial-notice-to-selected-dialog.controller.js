(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendMiscFinancialNoticeToSelectedDialogController', SendMiscFinancialNoticeToSelectedDialogController);

    SendMiscFinancialNoticeToSelectedDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'MiscInvoice', 'OptionalText', 'CongressSelector', 'financialReportSettings'];

    function SendMiscFinancialNoticeToSelectedDialogController ($timeout, $scope, $uibModalInstance, MiscInvoice, OptionalText, CongressSelector, financialReportSettings) {
        var vm = this;
        vm.financialReportSettings = financialReportSettings;
        vm.financialReportSettings.congressId = CongressSelector.getSelectedCongress().id;

        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        vm.clear = clear;
        vm.send = send;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function send () {
            vm.isSaving = true;
            MiscInvoice.sendFinancialNoticeToSelected(vm.financialReportSettings, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:miscFinancialNoticeToSelectedSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function setOptionalTextMessage(text) {
            vm.financialReportSettings.optionalText = text;
        }
    }
})();
