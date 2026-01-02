(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('AccPeopleDeleteController',AccPeopleDeleteController);

    AccPeopleDeleteController.$inject = ['$uibModalInstance', 'entity', 'AccPeople'];

    function AccPeopleDeleteController($uibModalInstance, entity, AccPeople) {
        var vm = this;

        vm.accPeople = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AccPeople.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
