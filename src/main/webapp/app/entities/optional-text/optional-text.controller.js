(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalTextController', OptionalTextController);

    OptionalTextController.$inject = ['$scope', '$state', 'OptionalText', 'CongressSelector'];

    function OptionalTextController ($scope, $state, OptionalText, CongressSelector) {
        var vm = this;
        
        vm.optionalTexts = [];

        loadAll();

        function loadAll() {
            OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
                vm.optionalTexts = result;
            });
        }
    }
})();
