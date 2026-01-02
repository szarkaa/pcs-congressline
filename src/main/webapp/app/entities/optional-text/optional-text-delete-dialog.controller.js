(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalTextDeleteController',OptionalTextDeleteController);

    OptionalTextDeleteController.$inject = ['$uibModalInstance', 'entity', 'OptionalText'];

    function OptionalTextDeleteController($uibModalInstance, entity, OptionalText) {
        var vm = this;

        vm.optionalText = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            OptionalText.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
