(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationTypeController', RegistrationTypeController);

    RegistrationTypeController.$inject = ['$scope', '$state', 'RegistrationType', 'CongressSelector'];

    function RegistrationTypeController ($scope, $state, RegistrationType, CongressSelector) {
        var vm = this;
        
        vm.registrationTypes = [];

        loadAll();

        function loadAll() {
            RegistrationType.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.registrationTypes = result;
            });
        }
    }
})();
