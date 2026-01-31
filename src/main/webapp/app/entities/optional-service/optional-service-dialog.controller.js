(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalServiceDialogController', OptionalServiceDialogController);

    OptionalServiceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OptionalService', 'Currency', 'VatInfo', 'Congress', 'CongressSelector'];

    function OptionalServiceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OptionalService, Currency, VatInfo, Congress, CongressSelector) {
        var vm = this;

        vm.optionalService = {
            id: entity.id,
            code: entity.code,
            name: entity.name,
            startDate: entity.startDate,
            endDate: entity.endDate,
            price: entity.price,
            maxPerson: entity.maxPerson,
            reserved: entity.reserved,
            onlineLabel: entity.onlineLabel,
            onlineOrder: entity.onlineOrder,
            onlineVisibility: entity.onlineVisibility,
            onlineType: entity.onlineType,
            currencyId: entity.currency ? entity.currency.id : null,
            vatInfoId: entity.vatInfo ? entity.vatInfo.id : null,
            congressId: CongressSelector.getSelectedCongress().id
        };

        vm.vatInfos = [];
        vm.currencies = [];
        vm.datePickerOpenStatus = {};
        vm.clear = clear;
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.onlineVisibilityChanged = onlineVisibilityChanged;

        Congress.get({id: CongressSelector.getSelectedCongress().id}, function(data) {
            vm.currencies = data.currencies;
        });

        VatInfo.queryForCongressAndItemType({ id: CongressSelector.getSelectedCongress().id, itemType: 'OPTIONAL_SERVICE' }, function(data) {
            vm.vatInfos = data;
        });

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function onlineVisibilityChanged() {
            if (vm.optionalService.onlineVisibility !== 'VISIBLE') {
                vm.optionalService.onlineLabel = null;
                vm.optionalService.onlineOrder = null;
                vm.optionalService.onlineType = 'NORMAL';
            }
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.optionalService.id !== null) {
                OptionalService.update(vm.optionalService, onSaveSuccess, onSaveError);
            } else {
                OptionalService.save(vm.optionalService, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:optionalServiceUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
