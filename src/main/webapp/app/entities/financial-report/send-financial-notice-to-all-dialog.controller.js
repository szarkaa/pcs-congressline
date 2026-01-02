(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendFinancialNoticeToAllDialogController', SendFinancialNoticeToAllDialogController);

    SendFinancialNoticeToAllDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'Confirmation', 'OptionalText', 'CongressSelector'];

    function SendFinancialNoticeToAllDialogController ($timeout, $scope, $uibModalInstance, Confirmation, OptionalText, CongressSelector) {
        var vm = this;

        vm.confirmationSettings = {
            sendAllEmail: '',
            language: 'hu',
            optionalText: '',
            congressId: CongressSelector.getSelectedCongress().id
        };

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
            Confirmation.sendFinancialNoticeToAll(vm.confirmationSettings, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:financialNoticeToAllSent', result);
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
