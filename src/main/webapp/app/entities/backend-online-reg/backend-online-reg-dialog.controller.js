(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegDialogController', BackendOnlineRegDialogController);

    BackendOnlineRegDialogController.$inject = ['$timeout', '$scope', '$state', '$stateParams', 'entity', 'BackendOnlineReg'];

    function BackendOnlineRegDialogController ($timeout, $scope, $state, $stateParams, entity, BackendOnlineReg) {
        var vm = this;

        vm.backendOnlineReg = entity;
        vm.cancel = cancel;

        BackendOnlineReg.queryCustomAnswers({id: entity.id}, function (result) {
            vm.customAnswers = result;
        });

        function cancel () {
            $state.go('^');
        }
    }
})();
