(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('OptionalTextDialogController', OptionalTextDialogController);

    OptionalTextDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'OptionalText', 'Congress'];

    function OptionalTextDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, OptionalText, Congress) {
        var vm = this;

        vm.optionalText = entity;
        vm.clear = clear;
        vm.save = save;
        vm.congresses = Congress.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.optionalText.id !== null) {
                OptionalText.update(vm.optionalText, onSaveSuccess, onSaveError);
            } else {
                OptionalText.save(vm.optionalText, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:optionalTextUpdate', result);
            $uibModalInstance.close(result);
            //vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
