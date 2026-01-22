(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscServiceDialogController', MiscServiceDialogController);

    MiscServiceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MiscService', 'VatInfo', 'Congress', 'CongressSelector'];

    function MiscServiceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MiscService, VatInfo,  Congress, CongressSelector) {
        var vm = this;

        vm.miscService = {
            id: entity.id,
            name: entity.name,
            description: entity.description,
            measure: entity.measure,
            price: entity.price,
            vatInfoId: entity.vatInfo ? entity.vatInfo.id : null,
            currencyId: entity.currency ? entity.currency.id : null,
            congressId: entity.congressId
        };
        vm.clear = clear;
        vm.save = save;
        vm.currencies = [];
        vm.vatInfos = [];

        VatInfo.queryForCongressAndItemType({ id: CongressSelector.getSelectedCongress().id, itemType: 'MISCELLANEOUS' } , function(result) {
            vm.vatInfos = result;
        });

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
            if (vm.miscService.id !== null) {
                MiscService.update(vm.miscService, onSaveSuccess, onSaveError);
            } else {
                MiscService.save(vm.miscService, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:miscServiceUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
