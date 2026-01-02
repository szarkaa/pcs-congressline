(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RateController', RateController);

    RateController.$inject = ['$scope', '$state', 'Rate', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function RateController ($scope, $state, Rate, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withDOM('<"html5buttons"B>ltfgitp')
            .withButtons([
                {extend: 'excel', title: 'rates'},
                {extend: 'pdf', title: 'rates'}
            ]);

        vm.dtOptions.withOption('order', [1, 'desc']);
        vm.dtOptions.withOption('stateSave', true);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.rates = [];

        loadAll();

        function loadAll() {
            Rate.query(function(result) {
                vm.rates = result;
            });
        }
    }
})();
