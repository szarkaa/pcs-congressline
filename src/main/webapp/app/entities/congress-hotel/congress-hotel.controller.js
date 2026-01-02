(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressHotelController', CongressHotelController);

    CongressHotelController.$inject = ['$scope', '$state', 'CongressHotel', 'CongressSelector', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function CongressHotelController ($scope, $state, CongressHotel, CongressSelector, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        $scope.dtOptions = DTOptionsBuilder.newOptions();
        $scope.dtOptions.withOption('order', [1, 'asc']);
        $scope.dtOptions.withOption('stateSave', true);
        $scope.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.congressHotels = [];

        loadAll();

        function loadAll() {
            CongressHotel.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.congressHotels = result;
            });
        }
    }
})();
