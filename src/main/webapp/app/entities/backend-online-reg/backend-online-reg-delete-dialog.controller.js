(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('BackendOnlineRegDeleteController',BackendOnlineRegDeleteController);

    BackendOnlineRegDeleteController.$inject = ['$uibModalInstance', 'entity', 'BackendOnlineReg'];

    function BackendOnlineRegDeleteController($uibModalInstance, entity, BackendOnlineReg) {
        var vm = this;

        vm.backendOnlineReg = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BackendOnlineReg.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
