(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CountryController', CountryController);

    CountryController.$inject = ['$scope', '$state', 'Country', 'DTOptionsBuilder', 'DTColumnDefBuilder'];

    function CountryController ($scope, $state, Country, DTOptionsBuilder, DTColumnDefBuilder) {
        var vm = this;
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withDOM('<"html5buttons"B>ltfgitp')
            .withButtons([
                {extend: 'excel', title: 'countries'},
                {extend: 'pdf', title: 'countries'}
            ]);

        vm.dtOptions.withOption('stateSave', true);
        vm.dtOptions.withOption('order', [1, 'asc']);
        vm.dtColumnDefs = [
            // negative int calculated from the right of the columns
            DTColumnDefBuilder.newColumnDef(-1).notSortable()
        ];

        vm.countries = [];

        loadAll();

        function loadAll() {
            Country.query(function(result) {
                vm.countries = result;
            });
        }
    }
})();
