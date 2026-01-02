(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('MiscServiceController', MiscServiceController);

    MiscServiceController.$inject = ['$scope', '$state', 'MiscService', 'CongressSelector'];

    function MiscServiceController ($scope, $state, MiscService, CongressSelector) {
        var vm = this;
        
        vm.miscServices = [];

        loadAll();

        function loadAll() {
            MiscService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.miscServices = result;
            });
        }
    }
})();
