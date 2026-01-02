(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('VatInfoDeleteController',VatInfoDeleteController);

    VatInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'VatInfo'];

    function VatInfoDeleteController($uibModalInstance, entity, VatInfo) {
        var vm = this;

        vm.vatInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            VatInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
