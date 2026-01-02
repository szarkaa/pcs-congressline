(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HotelDialogController', HotelDialogController);

    HotelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Hotel'];

    function HotelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Hotel) {
        var vm = this;

        vm.hotel = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.hotel.id !== null) {
                Hotel.update(vm.hotel, onSaveSuccess, onSaveError);
            } else {
                Hotel.save(vm.hotel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:hotelUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
