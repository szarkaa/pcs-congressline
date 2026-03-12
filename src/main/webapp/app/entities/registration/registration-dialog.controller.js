(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationDialogController', RegistrationDialogController);

    RegistrationDialogController.$inject = ['$timeout', '$scope', '$state', 'registration', 'Registration',
        'workplaces', 'countries', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices', 'chargedServices'];

    function RegistrationDialogController ($timeout, $scope, $state, registration, Registration,
       workplaces, countries, registrationRegistrationTypes, roomReservations, orderedOptionalServices, chargedServices) {
        var vm = this;
        vm.registration = registration;

        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.setShortName = setShortName;
        vm.copyWorkplace = copyWorkplace;
        vm.copyInvoiceDataFromReg = copyInvoiceDataFromReg;
        vm.copyInvoiceDataFromWorkplace = copyInvoiceDataFromWorkplace;
        vm.workplaces = workplaces;
        vm.countries = countries;
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.chargedServices = chargedServices;


        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function save () {
            vm.isSaving = true;
            transferRegistrationToViewModel();
            if (vm.registration.id !== null) {
                Registration.update(vm.registration, onSaveSuccess, onSaveError);
            } else {
                Registration.save(vm.registration, onSaveSuccess, onSaveError);
            }
        }

        function clear() {
            $state.go('registration');
        }

        function onSaveSuccess (result) {
            $scope.$emit('pcsApp:registrationUpdate', result);
            //vm.isSaving = false;
            $state.go('registration', {registrationId: result.id}, { reload: true });
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function transferRegistrationToViewModel() {
            vm.registration.workplaceId = vm.registration.workplace ? vm.registration.workplace.id : null;
            vm.registration.countryId = vm.registration.country ? vm.registration.country.id : null;
            vm.registration.invoiceCountryId = vm.registration.invoiceCountry ? vm.registration.invoiceCountry.id : null;
            delete vm.registration.workplace;
            delete vm.registration.country;
            delete vm.registration.invoiceCountry;
        }

        function copyWorkplace() {
            if (vm.registration.workplace) {
                var workplace = vm.registration.workplace;
                vm.registration.country = workplace.country ? workplace.country.id : null;
                vm.registration.zipCode = workplace.zipCode;
                vm.registration.city = workplace.city;
                vm.registration.street = workplace.street;
                vm.registration.email = workplace.email;
            }
        }

        vm.datePickerOpenStatus.dateOfApp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function setShortName() {
            if (!vm.registration.shortName) {
                vm.registration.shortName = vm.registration.firstName.charAt(0).toUpperCase() + '.';
            }
        }

        function copyInvoiceDataFromReg() {
            vm.registration.invoiceName = vm.registration.lastName + ' ' + vm.registration.firstName;
            vm.registration.invoiceCountry = vm.registration.country;
            vm.registration.invoiceZipCode = vm.registration.zipCode;
            vm.registration.invoiceCity = vm.registration.city;
            vm.registration.invoiceAddress = vm.registration.street;
        }

        function copyInvoiceDataFromWorkplace() {
            if (vm.registration.workplace) {
                var workplace = vm.registration.workplace;
                vm.registration.invoiceName = workplace.name;
                vm.registration.invoiceCountry = workplace.country ? workplace.country : null;
                vm.registration.invoiceZipCode = workplace.zipCode;
                vm.registration.invoiceCity = workplace.city;
                vm.registration.invoiceAddress = workplace.street;
                vm.registration.invoiceTaxNumber = workplace.vatRegNumber;
            }
        }
    }
})();
