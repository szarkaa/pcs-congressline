(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendGeneralEmailToAllDialogController', SendGeneralEmailToAllDialogController);

    SendGeneralEmailToAllDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'GeneralRegistrationReport', 'CongressSelector', 'registrationSettings'];

    function SendGeneralEmailToAllDialogController ($timeout, $scope, $uibModalInstance, GeneralRegistrationReport, CongressSelector, registrationSettings) {
        var vm = this;

        vm.registrationSettings = registrationSettings;
        vm.registrationSettings.congressId = CongressSelector.getSelectedCongress().id;
        vm.registrationSettings.topic = null;
        vm.registrationSettings.emailBody = null;

        vm.clear = clear;
        vm.send = send;

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function send () {
            vm.isSaving = true;
            GeneralRegistrationReport.sendGeneralEmailToAll(createGeneralEmailToAllFilter(), onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:sendGeneralEmailToAllSent', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function createGeneralEmailToAllFilter() {
            return {
                registrationIds: vm.registrationSettings.registrationIds,
                congressId: vm.registrationSettings.congressId,
                topic: vm.registrationSettings.topic,
                emailBody: vm.registrationSettings.emailBody
            };
        }

        vm.summerNoteConfig = {
            height: 150,
            toolbar: [
                ['style', ['bold', 'italic', 'underline', 'superscript', 'subscript', 'strikethrough', 'clear']],
                ['alignment', ['ul', 'ol']]
            ]
        };
    }
})();
