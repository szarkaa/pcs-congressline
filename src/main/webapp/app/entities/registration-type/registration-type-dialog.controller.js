(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationTypeDialogController', RegistrationTypeDialogController);

    RegistrationTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'RegistrationType', 'CongressSelector', 'VatInfo', 'Congress'];

    function RegistrationTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, RegistrationType, CongressSelector, VatInfo, Congress) {
        var vm = this;

        vm.registrationType = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.currencies = [];
        vm.vatInfos = [];

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.currencies;
        });

        VatInfo.queryForCongressAndItemType({ id: CongressSelector.getSelectedCongress().id, itemType: 'REGISTRATION' }, function(data) {
            vm.vatInfos = data;
        });


        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.registrationType.id !== null) {
                RegistrationType.update(vm.registrationType, onSaveSuccess, onSaveError);
            } else {
                RegistrationType.save(vm.registrationType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:registrationTypeUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.firstDeadline = false;
        vm.datePickerOpenStatus.secondDeadline = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
