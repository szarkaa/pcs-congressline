(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceSetPaymentDateDialogController', GroupDiscountInvoiceSetPaymentDateDialogController);

    GroupDiscountInvoiceSetPaymentDateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'GroupDiscountInvoice'];

    function GroupDiscountInvoiceSetPaymentDateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, GroupDiscountInvoice) {
        var vm = this;

        vm.groupDiscountInvoice = entity;
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
            GroupDiscountInvoice.savePaymentDate(vm.groupDiscountInvoice, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:groupDiscountInvoiceSetPaymentDate', result);
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
