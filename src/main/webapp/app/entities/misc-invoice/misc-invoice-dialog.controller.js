(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscInvoiceDialogController', MiscInvoiceDialogController);

    MiscInvoiceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MiscInvoice', 'Congress'];

    function MiscInvoiceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MiscInvoice, Congress) {
        var vm = this;

        vm.miscInvoice = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.congresses = Congress.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.miscInvoice.id !== null) {
                MiscInvoice.update(vm.miscInvoice, onSaveSuccess, onSaveError);
            } else {
                MiscInvoice.save(vm.miscInvoice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:miscInvoiceUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;
        vm.datePickerOpenStatus.paymentDeadline = false;
        vm.datePickerOpenStatus.dateOfGroupPayment = false;
        vm.datePickerOpenStatus.createdDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
