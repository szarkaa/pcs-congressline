(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('HotelController', HotelController);

    HotelController.$inject = ['$scope', '$state', 'Hotel', 'DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder'];

    function HotelController ($scope, $state, Hotel, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withDOM('<"html5buttons"B>ltfgitp')
            .withButtons([
                {extend: 'excel', title: 'hotels'},
                {extend: 'pdf', title: 'hotels'}
            ]);

        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.hotels = [];

        loadAll();

        function loadAll() {
            Hotel.query(function(result) {
                vm.hotels = result;
            });
        }
    }
})();
