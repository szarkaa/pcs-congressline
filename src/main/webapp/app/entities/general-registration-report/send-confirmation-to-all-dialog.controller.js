(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendConfirmationToAllDialogController', SendConfirmationToAllDialogController);

    SendConfirmationToAllDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'confirmationSettings', 'Confirmation', 'OptionalText', 'CongressSelector'];

    function SendConfirmationToAllDialogController ($timeout, $scope, $uibModalInstance, confirmationSettings, Confirmation, OptionalText, CongressSelector) {
        var vm = this;

        vm.confirmationSettings = confirmationSettings;
        vm.confirmationSettings.congressId = CongressSelector.getSelectedCongress().id;
        vm.confirmationSettings.sendAllEmail = '';
        vm.confirmationSettings.language = 'hu';
        vm.confirmationSettings.optionalText = '';

        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        vm.clear = clear;
        vm.send = send;
        vm.setOptionalTextMessage = setOptionalTextMessage;
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function send () {
            vm.isSaving = true;
            Confirmation.sendConfirmationToAll(vm.confirmationSettings, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:confirmationToAllSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function setOptionalTextMessage(text) {
            vm.confirmationSettings.optionalText = text;
        }
    }
})();
