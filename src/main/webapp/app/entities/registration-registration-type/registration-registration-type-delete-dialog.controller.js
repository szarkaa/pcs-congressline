(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationRegistrationTypeDeleteController',RegistrationRegistrationTypeDeleteController);

    RegistrationRegistrationTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'RegistrationRegistrationType'];

    function RegistrationRegistrationTypeDeleteController($uibModalInstance, entity, RegistrationRegistrationType) {
        var vm = this;

        vm.registrationRegistrationType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            RegistrationRegistrationType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
