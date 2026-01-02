(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('PayingGroupDialogController', PayingGroupDialogController);

    PayingGroupDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PayingGroup', 'Country', 'Congress', 'CongressSelector'];

    function PayingGroupDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PayingGroup, Country, Congress, CongressSelector) {
        var vm = this;

        vm.payingGroup = entity;
        vm.clear = clear;
        vm.save = save;
        vm.countries = Country.query();
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
            if (vm.payingGroup.id !== null) {
                PayingGroup.update(vm.payingGroup, onSaveSuccess, onSaveError);
            } else {
                PayingGroup.save(vm.payingGroup, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:payingGroupUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
