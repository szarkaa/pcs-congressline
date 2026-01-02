(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('InvoiceSetPaymentDateDialogController', InvoiceSetPaymentDateDialogController);

    InvoiceSetPaymentDateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Invoice'];

    function InvoiceSetPaymentDateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Invoice) {
        var vm = this;

        vm.invoice = entity;
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
            Invoice.savePaymentDate(vm.invoice, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:invoiceSetPaymentDate', result);
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
