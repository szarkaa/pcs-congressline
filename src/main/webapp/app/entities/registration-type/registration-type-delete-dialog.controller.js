(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationTypeDeleteController',RegistrationTypeDeleteController);

    RegistrationTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'RegistrationType'];

    function RegistrationTypeDeleteController($uibModalInstance, entity, RegistrationType) {
        var vm = this;

        vm.registrationType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            RegistrationType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
