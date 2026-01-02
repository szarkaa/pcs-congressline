(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalServiceController', OptionalServiceController);

    OptionalServiceController.$inject = ['$scope', '$state', 'OptionalService', 'CongressSelector'];

    function OptionalServiceController ($scope, $state, OptionalService, CongressSelector) {
        var vm = this;
        
        vm.optionalServices = [];

        loadAll();

        function loadAll() {
            OptionalService.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.optionalServices = result;
            });
        }
    }
})();
