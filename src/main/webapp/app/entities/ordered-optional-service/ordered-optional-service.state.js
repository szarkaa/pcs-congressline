(function() {
    'use strict';

    angular
        .module('pcsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('ordered-optional-service', {
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
            .state('ordered-optional-service.new', {
                parent: 'ordered-optional-service',
                url: '/optional-service/new',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', 'registration', '$uibModal', 'registrationCurrency', function($stateParams, $state, registration, $uibModal, registrationCurrency) {
                    $uibModal.open({
                        templateUrl: 'app/entities/ordered-optional-service/ordered-optional-service-dialog.html',
                        controller: 'OrderedOptionalServiceDialogController',
                        controllerAs: 'vm',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null,
                                    participant: null,
                                    optionalServiceId: null,
                                    payingGroupItemId: null,
                                    registrationId: registration.id
                                };
                            },
                            registrationCurrency: function () {
                                return registrationCurrency;
                            }
                        }
                    }).result.then(function(result) {
                            $state.go('registration', {registrationId: registration.id}, { reload: true });
                        }, function() {
                            $state.go('registration', {registrationId: registration.id});
                        })
                }]
            })
            .state('ordered-optional-service.edit', {
                parent: 'ordered-optional-service',
                url: '/optional-service/{optionalServiceId}/edit',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', 'registration', '$uibModal', 'registrationCurrency', function($stateParams, $state, registration, $uibModal, registrationCurrency) {
                    $uibModal.open({
                        templateUrl: 'app/entities/ordered-optional-service/ordered-optional-service-dialog.html',
                        controller: 'OrderedOptionalServiceDialogController',
                        controllerAs: 'vm',
                        size: 'lg',
                        resolve: {
                            entity: ['OrderedOptionalService', function(OrderedOptionalService) {
                                return OrderedOptionalService.get({id : $stateParams.optionalServiceId}).$promise;
                            }],
                            registrationCurrency: function () {
                                return registrationCurrency;
                            }
                        }
                    }).result.then(function(result) {
                            $state.go('registration', {registrationId: registration.id}, { reload: true });
                        }, function() {
                            $state.go('registration', {registrationId: registration.id});
                        })
                }]
            })
            .state('ordered-optional-service.delete', {
                parent: 'ordered-optional-service',
                url: '/optional-service/{optionalServiceId}/delete',
                data: {
                    authorities: ['ROLE_USER','ROLE_ADVANCED_USER','ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', 'registration', '$uibModal', function($stateParams, $state, registration, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/ordered-optional-service/ordered-optional-service-delete-dialog.html',
                        controller: 'OrderedOptionalServiceDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['OrderedOptionalService', function(OrderedOptionalService) {
                                return OrderedOptionalService.get({id : $stateParams.optionalServiceId}).$promise;
                            }]
                        }
                    }).result.then(function(result) {
                            $state.go('registration', {registrationId: registration.id}, { reload: true });
                        }, function() {
                            $state.go('registration', {registrationId: registration.id});
                        })
                }]
            });
    }

})();
