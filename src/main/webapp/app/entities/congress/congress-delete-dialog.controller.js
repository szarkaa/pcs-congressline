(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('CongressDeleteController',CongressDeleteController);

    CongressDeleteController.$inject = ['$uibModalInstance', 'entity', 'Congress'];

    function CongressDeleteController($uibModalInstance, entity, Congress) {
        var vm = this;

        vm.congress = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Congress.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
