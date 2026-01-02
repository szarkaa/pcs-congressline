(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OrderedOptionalServiceDialogController', OrderedOptionalServiceDialogController);

    OrderedOptionalServiceDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'entity', 'registrationCurrency', 'OrderedOptionalService', 'OptionalService', 'PayingGroupItem', 'Registration', 'CongressSelector'];

    function OrderedOptionalServiceDialogController ($timeout, $scope, $uibModalInstance, entity, registrationCurrency, OrderedOptionalService, OptionalService, PayingGroupItem, Registration, CongressSelector) {
        var vm = this;

        vm.orderedOptionalService = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.resetParticipantsField = resetParticipantsField ;

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

        function resetParticipantsField () {
            vm.orderedOptionalService.participant = null;
        }

        vm.datePickerOpenStatus.dateOfGroupPayment = false;
        vm.datePickerOpenStatus.createdDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
