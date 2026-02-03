(function() {
    'use strict';

    angular
        .module('pcsApp')
        .filter('searchRegistrationFilter', function() {
            return searchRegistrationFilter;
        });

    angular
        .module('pcsApp')
        .controller('RegistrationController', RegistrationController);

    RegistrationController.$inject = ['$scope', '$rootScope', '$stateParams', 'registration', 'registrationRegistrationTypes',
        'roomReservations', 'orderedOptionalServices', 'chargedServices', 'Registration', 'registrationArray'];

    function RegistrationController ($scope, $rootScope, $stateParams, registration, registrationRegistrationTypes,
                                     roomReservations, orderedOptionalServices, chargedServices, Registration, registrationArray) {
        var vm = this;
        vm.registration = registration;
        vm.registrationArray = registrationArray;
        vm.registrationRegistrationTypes = registrationRegistrationTypes;
        vm.roomReservations = roomReservations;
        vm.orderedOptionalServices = orderedOptionalServices;
        vm.chargedServices = chargedServices;
        vm.searchBoxVisible = false;
        vm.isSaving = false;

        vm.saveCheckboxProperties = saveCheckboxProperties;
        vm.save = save;
        vm.displayRRTAccPeople = displayRRTAccPeople;
        vm.firstRegId = firstRegId;
        vm.prevRegId = prevRegId;
        vm.nextRegId = nextRegId;
        vm.lastRegId = lastRegId;
        vm.hasPrevRegId = hasPrevRegId;
        vm.hasNextRegId = hasNextRegId;

        function saveCheckboxProperties(propertyName) {
            vm.isSaving = true;
            Registration.update(vm.registration,
                function onSaveSuccess(result) {
                    vm.isSaving = false;
                },
                function onSaveError(result) {
                    vm.isSaving = false;
                    vm.registration[propertyName] = !vm.registration[propertyName];
                }
            );
        }

        function save() {
            Registration.update(vm.registration);
        }

        function displayRRTAccPeople(rrt) {
            return rrt.registrationTypeType === 'ACCOMPANYING_FEE' ? rrt.accPeople : '';
        }

        function firstRegId() {
            return vm.registrationArray[0] && vm.registrationArray[0].id;
        }

        function prevRegId() {
            return vm.registration && vm.registrationArray[indexOfRegistrationId(vm.registration.id) - 1] && vm.registrationArray[indexOfRegistrationId(vm.registration.id) - 1].id;
        }

        function nextRegId() {
            if (!vm.registration && vm.registrationArray.length) {
                return vm.registrationArray[0].id;
            } else {
                return vm.registration && vm.registrationArray[indexOfRegistrationId(vm.registration.id) + 1] && vm.registrationArray[indexOfRegistrationId(vm.registration.id) + 1].id;
            }
        }

        function lastRegId() {
            return vm.registrationArray[vm.registrationArray.length - 1] && vm.registrationArray[vm.registrationArray.length - 1].id;
        }

        function hasPrevRegId() {
            return vm.registration && indexOfRegistrationId(vm.registration.id) > 0;
        }

        function hasNextRegId() {
            return (!vm.registration && vm.registrationArray.length) || (vm.registration && indexOfRegistrationId(vm.registration.id) < vm.registrationArray.length - 1);
        }

        function indexOfRegistrationId(id) {
            var i, idx = -1;
            for (i = 0; i < vm.registrationArray.length; i++) {
                if (id === vm.registrationArray[i].id) {
                    idx = i;
                    break;
                }
            }
            return idx;
        }

    }

    function searchRegistrationFilter(registrations, searchText) {
        if (!searchText) {
            return registrations;
        }
        var re = new RegExp(searchText);
        var out = [];
        for (var i = 0; i < registrations.length; i++) {
            if (re.test('' + registrations[i].regId) || re.test(registrations[i].firstName) || re.test(registrations[i].lastName)) {
                out.push(registrations[i]);
            }
        }
        return out;
    }

})();
