 (function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HotelSummarySelectionDialogController', HotelSummarySelectionDialogController);

    HotelSummarySelectionDialogController.$inject = ['$timeout', '$state', '$scope', '$stateParams', '$uibModalInstance', 'listFilter', 'CongressHotel', 'CongressSelector', 'HotelSummaryFilter'];

    function HotelSummarySelectionDialogController ($timeout, $state, $scope, $stateParams, $uibModalInstance, listFilter, CongressHotel, CongressSelector, HotelSummaryFilter) {
        var vm = this;

        vm.listFilter = listFilter;
        vm.clear = clear;
        vm.select = select;
        CongressHotel.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.congressHotels = result;
        });

/*
        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });
*/

        function clear () {
            $uibModalInstance.dismiss('cancel');
            $state.go('hotel-summary');
        }

        function select () {
            vm.isSaving = true;
            HotelSummaryFilter.setHotelSummaryFilter(vm.listFilter);
            $uibModalInstance.close();
            $state.go('hotel-summary', null, {reload: 'hotel-summary'});
            vm.isSaving = false;
        }

    }
})();
