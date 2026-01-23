(function() {
    'use strict';

    angular
        .module('pcsApp')
        .controller('RegistrationDialogController', RegistrationDialogController);

    RegistrationDialogController.$inject = ['$timeout', '$scope', '$state', 'registration', 'Registration',
        'workplaces', 'countries', 'registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices', 'chargedServices', 'CongressSelector'];

    function RegistrationDialogController ($timeout, $scope, $state, registration, Registration,
       workplaces, countries, registrationRegistrationTypes, roomReservations, orderedOptionalServices, chargedServices, CongressSelector) {
        var vm = this;
        vm.registration = {
            id: registration.id,
            regId: registration.regId,
            lastName: registration.lastName,
            firstName: registration.firstName,
            shortName: registration.shortName,
            title: registration.title,
            position: registration.position,
            otherData: registration.otherData,
            department: registration.department,
            countryId: registration.country ? registration.country.id : (CongressSelector.getSelectedCongress().defaultCountry ? CongressSelector.getSelectedCongress().defaultCountry.id : null),
            zipCode: registration.zipCode,
            city: registration.city,
            street: registration.street,
            phone: registration.phone,
            email: registration.email,
            fax: registration.fax,
            invoiceName: registration.invoiceName,
            invoiceCountryId: registration.invoiceCountry ? registration.invoiceCountry.id : null,
            invoiceZipCode: registration.invoiceZipCode,
            invoiceCity: registration.invoiceCity,
            invoiceAddress: registration.invoiceAddress,
            invoiceTaxNumber: registration.invoiceTaxNumber,
            dateOfApp: registration.dateOfApp,
            remark: registration.remark,
            onSpot: registration.onSpot,
            cancelled: registration.cancelled,
            presenter: registration.presenter,
            closed: registration.closed,
            etiquette: registration.etiquette,
            workplaceId: registration.workplace ? registration.workplace.id : null,
            congressId: registration.congressId
        };

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

        function copyWorkplace() {
            if (vm.registration.workplaceId) {
                var workplace = getSelectedWorkplace();
                console.log('workplace:', workplace);
                vm.registration.countryId = workplace.country ? workplace.country.id : null;
                vm.registration.zipCode = workplace.zipCode;
                vm.registration.city = workplace.city;
                vm.registration.street = workplace.street;
            }
        }

        vm.datePickerOpenStatus.dateOfApp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }

        function getSelectedWorkplace() {
            if (vm.registration.workplaceId) {
                for (var i = 0; i < vm.workplaces.length; i++) {
                    if (vm.workplaces[i].id === vm.registration.workplaceId) {
                        return vm.workplaces[i];
                    }
                }
            }
        }

        function setShortName() {
            if (!vm.registration.shortName) {
                vm.registration.shortName = vm.registration.firstName.charAt(0).toUpperCase() + '.';
            }
        }

        function copyInvoiceDataFromReg() {
            vm.registration.invoiceName = vm.registration.lastName + ' ' + vm.registration.firstName;
            vm.registration.invoiceCountryId = vm.registration.countryId;
            vm.registration.invoiceZipCode = vm.registration.zipCode;
            vm.registration.invoiceCity = vm.registration.city;
            vm.registration.invoiceAddress = vm.registration.street;
        }

        function copyInvoiceDataFromWorkplace() {
            if (vm.registration.workplaceId) {
                var workplace = getSelectedWorkplace();
                vm.registration.invoiceName = workplace.name;
                vm.registration.invoiceCountryId = workplace.country ? workplace.country.id : null;
                vm.registration.invoiceZipCode = workplace.zipCode;
                vm.registration.invoiceCity = workplace.city;
                vm.registration.invoiceAddress = workplace.street;
                vm.registration.invoiceTaxNumber = workplace.vatRegNumber;
            }
        }
    }
})();
