(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('SendConfirmationToAllDialogController', SendConfirmationToAllDialogController);

    SendConfirmationToAllDialogController.$inject = ['$timeout', '$scope', '$uibModalInstance', 'confirmationSettings', 'Confirmation', 'OptionalText', 'CongressSelector'];

    function SendConfirmationToAllDialogController ($timeout, $scope, $uibModalInstance, confirmationSettings, Confirmation, OptionalText, CongressSelector) {
        var vm = this;

        vm.confirmationSettings = confirmationSettings.filter;
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
            Confirmation.sendConfirmationToAll(buildReportFilter(vm.confirmationSettings), onSaveSuccess, onSaveError);
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

        function buildReportFilter(reportFilter) {
            return {
                regId: reportFilter.regId,
                lastName: reportFilter.lastName,
                firstName: reportFilter.firstName,
                email: reportFilter.email,
                accPeopleLastName: reportFilter.accPeopleLastName,
                accPeopleFirstName: reportFilter.accPeopleFirstName,
                registrationType: reportFilter.registrationType ? reportFilter.registrationType.id : null,
                workplace: reportFilter.workplace ? reportFilter.workplace.id : null,
                payingGroup: reportFilter.payingGroup ? reportFilter.payingGroup.id : null,
                optionalService: reportFilter.optionalService ? reportFilter.optionalService.id : null,
                hotelId: reportFilter.congressHotel ? reportFilter.congressHotel.hotel.id : null,
                country: reportFilter.country ? reportFilter.country.id : null,
                countryNegation: reportFilter.countryNegation,
                presenter: reportFilter.presenter,
                etiquette: reportFilter.etiquette,
                closed: reportFilter.closed,
                onSpot: reportFilter.onSpot,
                cancelled: reportFilter.cancelled,
                congressId: reportFilter.congressId,
                sendAllEmail: reportFilter.sendAllEmail,
                language: reportFilter.language,
                optionalText: reportFilter.optionalText
            };
        }
    }
})();
