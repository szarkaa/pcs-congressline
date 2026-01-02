(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendConfirmationDialogController', SendConfirmationDialogController);

    SendConfirmationDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'confirmationSettings', 'Confirmation', 'OptionalText', 'CongressSelector'];

    function SendConfirmationDialogController ($timeout, $scope, $uibModalInstance, confirmationSettings, Confirmation, OptionalText, CongressSelector) {
        var vm = this;

        vm.confirmationEmail = confirmationSettings.email;
        vm.language = 'hu';
        vm.optionalText = '';
        vm.regId = confirmationSettings.regId;
        OptionalText.queryByCongress({id: CongressSelector.getSelectedCongress().id}, function(result) {
            vm.optionalTexts = result;
        });

        vm.clear = clear;
        vm.sendAndPrintConfirmation = sendAndPrintConfirmation;
        vm.setOptionalTextMessage = setOptionalTextMessage;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function sendAndPrintConfirmation() {
            vm.isSaving = true;
            Confirmation.sendAndPrintConfirmation(createConfirmationForPrinting(), onSaveSuccess, onSaveError);
            vm.isSaving = false;
            $uibModalInstance.close();
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:confirmationSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function setOptionalTextMessage(text) {
            vm.optionalText = text;
        }

        function createConfirmationForPrinting() {
            var conf = {};
            conf.language = vm.language;
            conf.confirmationTitleType = 'CONFIRMATION';
            conf.optionalText = vm.optionalText;
            conf.registrationId = vm.regId;
            conf.congressId = CongressSelector.getSelectedCongress().id;
            conf.customConfirmationEmail = vm.confirmationEmail;
            conf.ignoredChargeableItemIdList = [];
            conf.ignoredChargedServiceIdList = [];
            return conf;
        }
    }
})();
