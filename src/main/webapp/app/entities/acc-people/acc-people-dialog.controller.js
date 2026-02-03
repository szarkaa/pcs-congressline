(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('AccPeopleDialogController', AccPeopleDialogController);

    AccPeopleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AccPeople'];

    function AccPeopleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AccPeople) {
        var vm = this;

        vm.accPeople = {
            id: entity.id,
            title: entity.title,
            lastName: entity.lastName,
            firstName: entity.firstName,
            registrationRegistrationTypeId: entity.registrationRegistrationTypeId
        };
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.accPeople.id !== null) {
                AccPeople.update(vm.accPeople, onSaveSuccess, onSaveError);
            } else {
                AccPeople.save(vm.accPeople, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:accPeopleUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
