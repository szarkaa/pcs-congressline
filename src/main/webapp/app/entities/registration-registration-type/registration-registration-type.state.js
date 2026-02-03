(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('registration-registration-type', {
            abstract: true,
            parent: 'registration',
            resolve: {
                registrationCurrency: ['registrationRegistrationTypes', 'roomReservations', 'orderedOptionalServices', function (registrationRegistrationTypes, roomReservations, orderedOptionalServices) {
                    if (registrationRegistrationTypes.length) {
                        return registrationRegistrationTypes[0].chargeableItemCurrency;
                    }

                    if (roomReservations.length) {
                        return roomReservations[0].chargeableItemCurrency;
                    }

                    if (orderedOptionalServices.length) {
                        return orderedOptionalServices[0].chargeableItemCurrency;
                    }
                    return null;
                }]
            }
        })
        .state('registration-registration-type.new', {
            parent: 'registration-registration-type',
            url: '/registration-type/new',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
                onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'registrationCurrency', function($stateParams, $state, $uibModal, registration, registrationCurrency) {
                    $uibModal.open({
                        templateUrl: 'app/entities/registration-registration-type/registration-registration-type-dialog.html',
                        controller: 'RegistrationRegistrationTypeDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null,
                                    regFee: null,
                                    currency: null,
                                    createdDate: new Date(),
                                    accPeople: null,
                                    registrationTypeId: null,
                                    payingGroupItemId: null,
                                    registrationId: registration ? registration.id : null
                                };
                            },
                            registrationCurrency: function () {
                                return registrationCurrency;
                            }
                        }
                    }).result.then(function (result) {
                            $state.go('registration', {registrationId: registration.id}, {reload: true});
                        }, function () {
                            $state.go('registration', {registrationId: registration.id});
                        });
                }]
        })
        .state('registration-registration-type.edit', {
            parent: 'registration-registration-type',
            url: '/registration-type/{regTypeId}/edit',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', 'registrationCurrency', function($stateParams, $state, $uibModal, registration, registrationCurrency) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration-registration-type/registration-registration-type-dialog.html',
                    controller: 'RegistrationRegistrationTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['RegistrationRegistrationType', function (RegistrationRegistrationType) {
                            return RegistrationRegistrationType.get({id: $stateParams.regTypeId}).$promise;
                        }],
                        registrationCurrency: function () {
                            return registrationCurrency;
                        }
                    }
                }).result.then(function (result) {
                        $state.go('registration', {registrationId: registration.id}, {reload: true});
                    }, function () {
                        $state.go('registration', {registrationId: registration.id});
                    });
            }]
        })
        .state('registration-registration-type.delete', {
            parent: 'registration-registration-type',
            url: '/registration-type/{regTypeId}/delete',
            data: {
                authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', 'registration', function($stateParams, $state, $uibModal, registration) {
                $uibModal.open({
                    templateUrl: 'app/entities/registration-registration-type/registration-registration-type-delete-dialog.html',
                    controller: 'RegistrationRegistrationTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['RegistrationRegistrationType', function (RegistrationRegistrationType) {
                            return RegistrationRegistrationType.get({id: $stateParams.regTypeId}).$promise;
                        }]
                    }
                }).result.then(function (result) {
                        $state.go('registration', {registrationId: registration.id}, {reload: true});
                    }, function () {
                        $state.go('registration', {registrationId: registration.id});
                    });
            }]
        });
    }

})();
