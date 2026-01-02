(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceSetPaymentDateDialogController', MiscInvoiceSetPaymentDateDialogController);

    MiscInvoiceSetPaymentDateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MiscInvoice'];

    function MiscInvoiceSetPaymentDateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MiscInvoice) {
        var vm = this;

        vm.miscInvoice = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            MiscInvoice.savePaymentDate(vm.miscInvoice, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:miscInvoiceSetPaymentDate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.paymentDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
