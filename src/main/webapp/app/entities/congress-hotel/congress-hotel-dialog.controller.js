(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressHotelDialogController', CongressHotelDialogController);

    CongressHotelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'CongressHotel', 'Congress', 'Hotel'];

    function CongressHotelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, CongressHotel, Congress, Hotel) {
        var vm = this;

        vm.congressHotel = entity;
        vm.clear = clear;
        vm.save = save;
        vm.congresses = Congress.query();
        vm.hotels = Hotel.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.congressHotel.id !== null) {
                CongressHotel.update(vm.congressHotel, onSaveSuccess, onSaveError);
            } else {
                CongressHotel.save(vm.congressHotel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:congressHotelUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
