(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('EmailDialogController', EmailDialogController);

    EmailDialogController.$inject = ['$timeout', '$scope', 'registrationEmail', '$uibModalInstance', 'Email', 'CongressSelector'];

    function EmailDialogController ($timeout, $scope, registrationEmail, $uibModalInstance, Email, CongressSelector) {
        var vm = this;

        vm.mail = { congressId: CongressSelector.getSelectedCongress().id, lastName: registrationEmail.lastName, firstName: registrationEmail.firstName, email: registrationEmail.email, body: null };

        vm.summerNoteConfig = {
            height: 300,
            focus: true,
            toolbar: []
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
            Email.send(vm.mail, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:emailSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
