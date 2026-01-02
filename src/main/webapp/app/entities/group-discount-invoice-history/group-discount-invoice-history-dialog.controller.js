(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountInvoiceHistoryDialogController', GroupDiscountInvoiceHistoryDialogController);

    GroupDiscountInvoiceHistoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'GroupDiscountInvoiceHistory', 'GroupDiscountInvoice'];

    function GroupDiscountInvoiceHistoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, GroupDiscountInvoiceHistory, GroupDiscountInvoice) {
        var vm = this;

        vm.groupDiscountInvoiceHistory = entity;
        vm.clear = clear;
        vm.save = save;
        vm.groupdiscountinvoices = GroupDiscountInvoice.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.groupDiscountInvoiceHistory.id !== null) {
                GroupDiscountInvoiceHistory.update(vm.groupDiscountInvoiceHistory, onSaveSuccess, onSaveError);
            } else {
                GroupDiscountInvoiceHistory.save(vm.groupDiscountInvoiceHistory, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:groupDiscountInvoiceHistoryUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
