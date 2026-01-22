(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('WorkplaceDialogController', WorkplaceDialogController);

    WorkplaceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Workplace', 'Country', 'Congress'];

    function WorkplaceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Workplace, Country, Congress) {
        var vm = this;

        vm.workplace = {
            id: null,
            name: entity.name,
            vatRegNumber: null,
            countryId: entity.countryId,
            department: entity.department,
            zipCode: entity.zipCode,
            city: entity.city,
            street: entity.street,
            phone: entity.phone,
            fax: entity.fax,
            email: entity.email,
            congressId: entity.congressId
        };
        vm.clear = clear;
        vm.save = save;
        vm.countries = Country.query();
        vm.congresses = Congress.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.workplace.id !== null) {
                Workplace.update(vm.workplace, onSaveSuccess, onSaveError);
            } else {
                Workplace.save(vm.workplace, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:workplaceUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
