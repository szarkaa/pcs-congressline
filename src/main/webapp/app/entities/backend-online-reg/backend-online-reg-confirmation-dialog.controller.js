(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegConfirmationDialogController',BackendOnlineRegConfirmationDialogController);

    BackendOnlineRegConfirmationDialogController.$inject = ['$uibModalInstance', 'entity', 'BackendOnlineReg'];

    function BackendOnlineRegConfirmationDialogController($uibModalInstance, entity, BackendOnlineReg) {
        var vm = this;

        vm.backendOnlineReg = entity;
        vm.clear = clear;
        vm.confirm = confirm;

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function confirm() {
            BackendOnlineReg.accept(vm.backendOnlineReg, function () {
                $uibModalInstance.close(true);
            });
        }
    }

})();
