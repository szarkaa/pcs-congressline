(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendGeneralEmailToAllDialogController', SendGeneralEmailToAllDialogController);

    SendGeneralEmailToAllDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'GeneralRegistrationReport', 'registrationSettings'];

    function SendGeneralEmailToAllDialogController ($timeout, $scope, $uibModalInstance, GeneralRegistrationReport, registrationSettings) {
        var vm = this;

        vm.registrationSettings = registrationSettings.filter;
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
                regId: vm.registrationSettings.regId,
                lastName: vm.registrationSettings.lastName,
                firstName: vm.registrationSettings.firstName,
                email: vm.registrationSettings.email,
                accPeopleLastName: vm.registrationSettings.accPeopleLastName,
                accPeopleFirstName: vm.registrationSettings.accPeopleFirstName,
                registrationType: vm.registrationSettings.registrationType ? vm.registrationSettings.registrationType.id : null,
                workplace: vm.registrationSettings.workplace ? vm.registrationSettings.workplace.id : null,
                payingGroup: vm.registrationSettings.payingGroup ? vm.registrationSettings.payingGroup.id : null,
                optionalService: vm.registrationSettings.optionalService ? vm.registrationSettings.optionalService.id : null,
                hotelId: vm.registrationSettings.congressHotel ? vm.registrationSettings.congressHotel.hotel.id : null,
                country: vm.registrationSettings.country ? vm.registrationSettings.country.id : null,
                countryNegation: vm.registrationSettings.countryNegation,
                presenter: vm.registrationSettings.presenter,
                etiquette: vm.registrationSettings.etiquette,
                closed: vm.registrationSettings.closed,
                onSpot: vm.registrationSettings.onSpot,
                cancelled: vm.registrationSettings.cancelled,
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
