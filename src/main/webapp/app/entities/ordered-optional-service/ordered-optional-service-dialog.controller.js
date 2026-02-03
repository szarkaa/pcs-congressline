(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OrderedOptionalServiceDialogController', OrderedOptionalServiceDialogController);

    OrderedOptionalServiceDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'registrationCurrency', 'OrderedOptionalService', 'OptionalService', 'PayingGroupItem', 'Registration', 'CongressSelector'];

    function OrderedOptionalServiceDialogController ($timeout, $scope, $uibModalInstance, entity, registrationCurrency, OrderedOptionalService, OptionalService, PayingGroupItem, Registration, CongressSelector) {
        var vm = this;
        vm.selectedOptionalService = null;
        vm.orderedOptionalService = {
            id: entity.id,
            participant: entity.participant,
            optionalServiceId: entity.optionalServiceId,
            payingGroupItemId: entity.payingGroupItemId,
            registrationId: entity.registrationId
        };
        vm.clear = clear;
        vm.save = save;
        vm.optionalServiceSelectionChanged = optionalServiceSelectionChanged;

        vm.optionalServices = OptionalService.queryByCongress({id: CongressSelector.getSelectedCongress().id});
        vm.payingGroupItems = PayingGroupItem.queryByCongressAndItemType({id: CongressSelector.getSelectedCongress().id, itemType: 'OPTIONAL_SERVICE'});
        vm.registrationCurrency = registrationCurrency;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.orderedOptionalService.id !== null) {
                OrderedOptionalService.update(vm.orderedOptionalService, onSaveSuccess, onSaveError);
            } else {
                OrderedOptionalService.save(vm.orderedOptionalService, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:orderedOptionalServiceUpdate', result);
            $uibModalInstance.close(result);
            /*vm.isSaving = false;*/
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function optionalServiceSelectionChanged () {
            vm.orderedOptionalService.participant = null;
            for (var i = 0; i < vm.optionalServices.length; i++) {
                if (vm.optionalServices[i].id === vm.orderedOptionalService.optionalServiceId) {
                    vm.selectedOptionalService = vm.optionalServices[i];
                    break
                }
            }
        }
    }
})();
