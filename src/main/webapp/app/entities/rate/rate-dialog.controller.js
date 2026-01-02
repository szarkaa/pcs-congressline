(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RateDialogController', RateDialogController);

    RateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Rate', 'Currency'];

    function RateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Rate, Currency) {
        var vm = this;

        vm.rate = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.currencies = Currency.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.rate.id !== null) {
                Rate.update(vm.rate, onSaveSuccess, onSaveError);
            } else {
                Rate.save(vm.rate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:rateUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.valid = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
