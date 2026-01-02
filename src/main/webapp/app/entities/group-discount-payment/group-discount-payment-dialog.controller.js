(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('GroupDiscountPaymentDialogController', GroupDiscountPaymentDialogController);

    GroupDiscountPaymentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity',
        'GroupDiscountPayment', 'Currency', 'Congress', 'CongressSelector', 'PayingGroup'];

    function GroupDiscountPaymentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity,
        GroupDiscountPayment, Currency, Congress, CongressSelector, PayingGroup) {
        var vm = this;

        vm.groupDiscountPayment = entity;
        vm.payingGroups = PayingGroup.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.clear = clear;
        vm.save = save;
        vm.currencies = [];

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.currencies;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.groupDiscountPayment.id !== null) {
                GroupDiscountPayment.update(vm.groupDiscountPayment, onSaveSuccess, onSaveError);
            } else {
                GroupDiscountPayment.save(vm.groupDiscountPayment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:groupDiscountPaymentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateOfPayment = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
