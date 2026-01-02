'use strict';

angular.module('pcsApp').controller('SharedRoomReservationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'roomReservations', 'RoomReservation', 'registrationCurrency', 'CongressSelector', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder',
        function($scope, $stateParams, $uibModalInstance, roomReservations, RoomReservation, registrationCurrency, CongressSelector, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions();
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.roomReservations = roomReservations;
        vm.registrationCurrency = registrationCurrency;

        var onSaveSuccess = function (result) {
            $scope.$emit('pcsApp:sharedRoomReservationUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        };

        var onSaveError = function (result) {
            vm.isSaving = false;
        };

        vm.save = function (rrId) {
            vm.isSaving = true;
            var sharedRoomReservation = {
                registrationId: $stateParams.registrationId,
                rrId: rrId
            };
            RoomReservation.saveShared(sharedRoomReservation, onSaveSuccess, onSaveError);
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

}]);
